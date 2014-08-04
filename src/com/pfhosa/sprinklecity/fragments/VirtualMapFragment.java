package com.pfhosa.sprinklecity.fragments;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.database.CustomHttpClient;
import com.pfhosa.sprinklecity.database.WriteToRemoteAsyncTask;
import com.pfhosa.sprinklecity.model.DrawableObject;
import com.pfhosa.sprinklecity.model.HumanAvatar;
import com.pfhosa.sprinklecity.model.VirtualLocation;
import com.pfhosa.sprinklecity.ui.HomeActivity;

public class VirtualMapFragment extends Fragment {

	// Constants
	public static final int PIXELS_PER_METER = 10;
	public static final int AVATAR_EDGE = 300;
	public static final int AVATAR_EDGE_MARGIN = 0;
	public static final int LOCATION_EDGE = 300;
	public static final int ARROWS_EDGE = 600;
	public static final int CORNER_EDGE = 300;

	// Location
	static float distance;
	static Location previousLocation;
	LocationReceiver locationReceiver;

	// View
	private MapSurfaceView mapSurfaceView;
	HumanAvatar humanAvatar;
	DrawableObject arrows, home, inventory;
	ArrayList<VirtualLocation> virtualLocations= new ArrayList<VirtualLocation>();

	// Open new Fragment listener
	OnLocationSelectedListener locationSelectedListener;

	String username;

	public void onAttach (Activity activity) {
		super.onAttach(activity);
		locationSelectedListener = (OnLocationSelectedListener) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		if(getArguments() != null) {
			username = getArguments().getString("Username");
			int avatar = getArguments().getInt("Avatar");

			makeNewAvatar(username, avatar);
		}

		if(mapSurfaceView == null)
			mapSurfaceView = new MapSurfaceView(getActivity());

		LocationReceiver locationReceiver = new LocationReceiver();
		getActivity().registerReceiver(locationReceiver, new IntentFilter("newLocationIntent"));

		new GetAvatarAsyncTask().execute();
		new GetLocationsAsyncTask().execute();

		return mapSurfaceView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mapSurfaceView != null){
			mapSurfaceView.surfaceRestart();
		}
	}


	@Override
	public void onPause() {
		super.onPause();
		if (mapSurfaceView != null){
			mapSurfaceView.surfaceDestroyed(mapSurfaceView.getHolder());
		}

		if(locationReceiver != null) {
			getActivity().unregisterReceiver(locationReceiver);
			locationReceiver = null;
		}


		updateAvatar();
	}

	public class LocationReceiver extends BroadcastReceiver {

		double currentLatitude, currentLongitude;
		Location currentLocation;

		@Override
		public void onReceive(Context context, Intent intent) {

			try {
				currentLatitude = intent.getExtras().getDouble("latitude");
				currentLongitude = intent.getExtras().getDouble("longitude");

				currentLocation = new Location("currentLocation");

				currentLocation.setLatitude(currentLatitude);
				currentLocation.setLongitude(currentLongitude);

				setNewDistance(currentLocation);

			} catch(NullPointerException e) {
				e.printStackTrace();
			}	
		}
	}

	public void makeNewAvatar(String username, int avatar) {
		humanAvatar = new HumanAvatar(username, avatar, AVATAR_EDGE, 0, 0, -1, true);
	}

	public void populateMap(ArrayList<VirtualLocation> virtualLocations) {
		for(VirtualLocation vl : virtualLocations) 
			Log.d("Username location", "" + vl.getOwner());
	}

	public void setNewDistance(Location currentLocation) {

		if(previousLocation == null) 
			distance = 0;
		else 
			distance = previousLocation.distanceTo(currentLocation);		

		previousLocation = currentLocation;

		Log.d("Raw distance",Float.toString(distance));

		DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
		float pxHeight = displayMetrics.heightPixels;
		//float pxWidth = displayMetrics.widthPixels;

		if(0 < humanAvatar.getPositionY() + (int)(PIXELS_PER_METER * distance * humanAvatar.getDirection()) &&
				pxHeight - humanAvatar.getEdge() > humanAvatar.getPositionY() + (int)(PIXELS_PER_METER * distance * humanAvatar.getDirection()) &&
				distance < 20) {

			humanAvatar.setPositionY(humanAvatar.getPositionY() + (int)(PIXELS_PER_METER * distance * humanAvatar.getDirection()));

			Log.d("Adjusted distance",Float.toString(distance));
		}

	}

	private class MapSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

		private SurfaceHandler surfaceHandler;
		private SurfaceHolder surfaceHolder;

		int touchX, touchY;

		public MapSurfaceView(Context context) {
			super(context);
			setFocusable(true);

			surfaceHolder = getHolder();
			surfaceHolder.addCallback(this);

			surfaceHandler = new SurfaceHandler(this);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			surfaceHandler.restartSurface();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			surfaceHandler.terminateSurface();
		}

		public void surfaceRestart(){
			if (surfaceHandler != null){
				surfaceHandler.restartSurface(); 
			}
		}


		@Override 
		public boolean onTouchEvent(MotionEvent event){
			if(event.getAction() == MotionEvent.ACTION_DOWN) {

				touchX = (int)event.getX();
				touchY = (int)event.getY();

				if(humanAvatar.isTouchOnObject(touchX, touchY))
					arrows.setVisibility(true);
				if(home.isTouchOnObject(touchX, touchY)) {
					Intent openHome = new Intent(getActivity(), HomeActivity.class);
					startActivity(openHome);
				}
				if(inventory.isTouchOnObject(touchX, touchY)) {
					//TODO open new activity
					locationSelectedListener.onLocationSelected("inventory");
					Toast.makeText(getActivity(), "Inventory", Toast.LENGTH_SHORT).show();
				}
			}

			if(event.getAction() == MotionEvent.ACTION_UP) {
				touchX = (int)event.getX();
				touchY = (int)event.getY();

				if(humanAvatar.isTouchOnObject(touchX, touchY))
					arrows.setVisibility(false);

				if(humanAvatar.swipeArrowUp(touchX, touchY)) {
					arrows.setVisibility(false);
					humanAvatar.setDirection(-1);
					Toast.makeText(getActivity(), "Up", Toast.LENGTH_SHORT).show();
				}

				if(humanAvatar.swipeArrowDown(touchX, touchY)) {
					arrows.setVisibility(false);
					humanAvatar.setDirection(1);
					Toast.makeText(getActivity(), "Down", Toast.LENGTH_SHORT).show();
				}

				if(humanAvatar.swipeArrowLeft(touchX, touchY)) {
					arrows.setVisibility(false);
					Toast.makeText(getActivity(), "Left", Toast.LENGTH_SHORT).show();
					if("noLocation" != surfaceHandler.locationToLeftExists()){
						Log.d("Location", surfaceHandler.locationToRightExists().toString());
						locationSelectedListener.onLocationSelected(surfaceHandler.locationToLeftExists());
					}
				}

				if(humanAvatar.swipeArrowRight(touchX, touchY)) {
					arrows.setVisibility(false);
					Toast.makeText(getActivity(), "Right", Toast.LENGTH_SHORT).show();
					if("noLocation" != surfaceHandler.locationToRightExists()){
						Log.d("Location", surfaceHandler.locationToRightExists().toString());
						locationSelectedListener.onLocationSelected(surfaceHandler.locationToRightExists());						
					}
				}
			}
			return true;   
		}
	}

	private class SurfaceHandler implements Runnable {
		// Class fields
		private SurfaceHolder mSurfaceHolder;
		private MapSurfaceView mMapSurfaceView;
		private Canvas mCanvas = null;
		private final Handler mHandler;			

		// View
		Bitmap backgroundBitmap, characterBitmap, arrowsBitmap, inventoryBitmap, homeBitmap;
		Bitmap farmBitmap, bakeryBitmap, postOfficeBitmap, parkBitmap, locationBitmap;
		Bitmap scaledBackgroundBitmap, scaledCharacterBitmap, scaledArrowsBitmap, scaledInventoryBitmap, scaledHomeBitmap;
		Bitmap scaledFarmBitmap, scaledBakeryBitmap, scaledPostOfficeBitmap, scaledParkBitmap, scaledLocationBitmap;
		int newWidth, newHeight;

		public SurfaceHandler(MapSurfaceView mapSurfaceView){
			mSurfaceHolder = mapSurfaceView.getHolder();
			mMapSurfaceView = mapSurfaceView;
			mCanvas = new Canvas();

			mHandler=new Handler();
			mHandler.post(this);

			doPrepare();
		}

		public void terminateSurface(){
			mHandler.removeCallbacks(this);
		}

		public void restartSurface() {
			mHandler.post(this);
		}

		@Override
		public void run() {
			mHandler.postDelayed(this,500);	
			try {
				mCanvas = mSurfaceHolder.lockCanvas(null);
				if (mCanvas == null)
					mSurfaceHolder = mMapSurfaceView.getHolder();
				else 
					doDraw(mCanvas);				
			} 
			finally {
				if (mCanvas != null) 
					mSurfaceHolder.unlockCanvasAndPost(mCanvas);
			}

		}

		public void doPrepare() {
			synchronized (mSurfaceHolder) {			
				DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
				float pxHeight = displayMetrics.heightPixels;
				float pxWidth = displayMetrics.widthPixels;

				arrows = new DrawableObject(0, 0, R.drawable.arrows, ARROWS_EDGE, false);
				inventory = new DrawableObject((int)pxWidth - LOCATION_EDGE, 0, R.drawable.button_inventory, LOCATION_EDGE, true);
				home = new DrawableObject(0, 0, R.drawable.button_home, LOCATION_EDGE, true);

				backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.street_view);
				characterBitmap = BitmapFactory.decodeResource(getResources(), humanAvatar.getImage());
				arrowsBitmap = BitmapFactory.decodeResource(getResources(), arrows.getImage());
				inventoryBitmap = BitmapFactory.decodeResource(getResources(), inventory.getImage());
				homeBitmap = BitmapFactory.decodeResource(getResources(), home.getImage());

				farmBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location_farm);
				bakeryBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location_shop);
				postOfficeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location_storage);
				parkBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location_park);
				locationBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location_temple);

				float scaleHeight = (float)backgroundBitmap.getHeight()/(float)pxHeight;
				float scaleWidth = (float)backgroundBitmap.getWidth()/(float)pxWidth;

				newHeight = Math.round(backgroundBitmap.getHeight()/scaleHeight);
				newWidth = Math.round(backgroundBitmap.getWidth()/scaleWidth);

				scaledBackgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, newWidth, newHeight, true);
				scaledCharacterBitmap = Bitmap.createScaledBitmap(characterBitmap, humanAvatar.getEdge(), humanAvatar.getEdge(), true);
				scaledArrowsBitmap = Bitmap.createScaledBitmap(arrowsBitmap, ARROWS_EDGE, ARROWS_EDGE, true);
				scaledInventoryBitmap = Bitmap.createScaledBitmap(inventoryBitmap, inventory.getEdge(), inventory.getEdge(), true);
				scaledHomeBitmap = Bitmap.createScaledBitmap(homeBitmap, home.getEdge(), home.getEdge(), true);

				scaledFarmBitmap = Bitmap.createScaledBitmap(farmBitmap, LOCATION_EDGE, LOCATION_EDGE, true);
				scaledBakeryBitmap = Bitmap.createScaledBitmap(bakeryBitmap, LOCATION_EDGE, LOCATION_EDGE, true);
				scaledPostOfficeBitmap = Bitmap.createScaledBitmap(postOfficeBitmap, LOCATION_EDGE, LOCATION_EDGE, true);
				scaledParkBitmap = Bitmap.createScaledBitmap(parkBitmap, LOCATION_EDGE, LOCATION_EDGE, true);
				scaledLocationBitmap = Bitmap.createScaledBitmap(locationBitmap, LOCATION_EDGE, LOCATION_EDGE, true);

				if(humanAvatar.getPositionX() == 0 && humanAvatar.getPositionY() == 0) {
					humanAvatar.setPosition(
							(int)((pxWidth - humanAvatar.getEdge()) / 2), 
							(int)(pxHeight * 0.8));
				}

			}
		}

		public Bitmap getLocationBitmap(String locationType) {			
			switch(locationType) {
			case "farmer": return scaledFarmBitmap;
			case "postman": return scaledPostOfficeBitmap;
			case "baker": return scaledBakeryBitmap;
			case "park": return scaledParkBitmap;
			}

			return scaledLocationBitmap;
		}

		public void doDraw(Canvas canvas){

			// Order of Bitmaps is important (they are drawn in this order).

			canvas.drawBitmap(scaledBackgroundBitmap, 0,  0, null);

			for(VirtualLocation vl : virtualLocations) 
				canvas.drawBitmap(getLocationBitmap(vl.getLocationType()), vl.getPositionX(), vl.getPositionY(), null);			

			canvas.drawBitmap(scaledHomeBitmap, home.getPositionX(), home.getPositionY(), null);
			canvas.drawBitmap(scaledInventoryBitmap, inventory.getPositionX(), inventory.getPositionY(), null);
			canvas.drawBitmap(scaledCharacterBitmap, humanAvatar.getPositionX(), humanAvatar.getPositionY(), null);

			if(arrows.getVisibility())
				canvas.drawBitmap(scaledArrowsBitmap, humanAvatar.getPositionX() - 150, humanAvatar.getPositionY() - 200, null);

		}

		public String locationToLeftExists() {

			for(VirtualLocation vl : virtualLocations) { 
				if(vl.isObjectOnLeftFrom(humanAvatar.getPositionX(),
						humanAvatar.getPositionY(), 
						humanAvatar.getEdge(), 
						AVATAR_EDGE_MARGIN))
					return vl.getLocationType();
			}

			return "noLocation";
		}

		public String locationToRightExists() {

			for(VirtualLocation vl : virtualLocations) { 
				if(vl.isObjectOnRightFrom(humanAvatar.getPositionX(),
						humanAvatar.getPositionY(), 
						humanAvatar.getEdge(), 
						AVATAR_EDGE_MARGIN))
					return vl.getLocationType();
			}

			return "noLocation";
		}
	}


	public void updateAvatar() {
		String url = "http://www2.macs.hw.ac.uk/~ph109/DBConnect/updateHumanAvatar.php";

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("Username", humanAvatar.getUsername()));
		postParameters.add(new BasicNameValuePair("PositionX", Integer.toString(humanAvatar.getPositionX())));
		postParameters.add(new BasicNameValuePair("PositionY", Integer.toString(humanAvatar.getPositionY())));
		postParameters.add(new BasicNameValuePair("Direction", Integer.toString(humanAvatar.getDirection())));

		WriteToRemoteAsyncTask updateHumanAvatar = new WriteToRemoteAsyncTask(url, postParameters, getActivity());

		updateHumanAvatar.execute();
		Log.d("Update avatar", "executing");
	}

	public class GetLocationsAsyncTask extends AsyncTask<Void, Void, Void> {

		String username, locationType;
		int locationX, locationY;

		protected Void doInBackground(Void... params) {
			// declare parameters that are passed to PHP script i.e. the name "birthyear" and its value submitted by user   
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

			//Looper.prepare();

			String response = null;

			// call executeHttpPost method passing necessary parameters 
			try {
				response = CustomHttpClient.executeHttpPost("http://www2.macs.hw.ac.uk/~ph109/DBConnect/getLocations.php", postParameters);

				// store the result returned by PHP script that runs MySQL query
				String result = response.toString();  

				//parse json data
				try{
					JSONArray jArray = new JSONArray(result);		

					for(int i = 0; i < jArray.length(); ++i){
						JSONObject json_data = jArray.getJSONObject(i);

						//TODO get image from remote
						int image = 0;
						//Get an output to the screen
						username = json_data.getString("Username");
						locationType = json_data.getString("Job");
						Log.d("Character job", "" + locationType);
						locationX = Integer.parseInt(json_data.getString("LocationX"));
						locationY = Integer.parseInt(json_data.getString("LocationY"));

						virtualLocations.add(new VirtualLocation(username, locationType, locationX, locationY, image, LOCATION_EDGE, true));
					}

					populateMap(getLocationsArray());

				} 
				catch(JSONException e){
					Log.e("log_tag", "Error parsing data "+ e.toString());
				}   

			} 
			catch (Exception e) {
				Log.e("log_tag","Error in http connection!!" + e.toString());

			}  
			return null;
		}

		public void setLocationsArray(ArrayList<VirtualLocation> vl) {
			virtualLocations = vl;
		}

		public ArrayList<VirtualLocation> getLocationsArray() {
			return virtualLocations;
		}
	}

	public class GetAvatarAsyncTask extends AsyncTask<Void, Void, Void> {

		String username, owner;
		int positionX, positionY, direction;

		protected Void doInBackground(Void... params) {
			// declare parameters that are passed to PHP script i.e. the name "birthyear" and its value submitted by user   
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("Username", humanAvatar.getUsername()));

			//Looper.prepare();

			String response = null;

			// call executeHttpPost method passing necessary parameters 
			try {
				response = CustomHttpClient.executeHttpPost("http://www2.macs.hw.ac.uk/~ph109/DBConnect/getHumanAvatar.php", postParameters);

				// store the result returned by PHP script that runs MySQL query
				String result = response.toString();  

				//parse json data
				try{
					JSONArray jArray = new JSONArray(result);		

					for(int i = 0; i < jArray.length(); ++i){
						JSONObject json_data = jArray.getJSONObject(i);

						//Get an output to the screen
						positionX = Integer.parseInt(json_data.getString("PositionX"));
						positionY = Integer.parseInt(json_data.getString("PositionY"));
						direction = Integer.parseInt(json_data.getString("Direction"));

						humanAvatar.setPosition(positionX, positionY);
						humanAvatar.setDirection(direction);
					}
				} 
				catch(JSONException e){
					Log.e("log_tag", "Error parsing data "+ e.toString());
				}   

			} 
			catch (Exception e) {
				Log.e("log_tag","Error in http connection!!" + e.toString());

			}  
			return null;
		}

		protected void onPostExecute(Void result) {

		}
	}

	public interface OnLocationSelectedListener {
		public void onLocationSelected(String location);
	}
}