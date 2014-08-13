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
	public static final float DEFAULT_SCREEN_HEIGHT = 1920f;
	public static final float AVATAR_EDGE = 300f / DEFAULT_SCREEN_HEIGHT;
	public static final float AVATAR_EDGE_MARGIN = 0f / DEFAULT_SCREEN_HEIGHT;
	public static final float LOCATION_EDGE = 300f / DEFAULT_SCREEN_HEIGHT;
	public static final float ARROWS_EDGE = 600f / DEFAULT_SCREEN_HEIGHT;
	public static final float CORNER_EDGE = 300f / DEFAULT_SCREEN_HEIGHT;

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

	float height = 10;

	public void onAttach (Activity activity) {
		super.onAttach(activity);
		locationSelectedListener = (OnLocationSelectedListener) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
		height = displayMetrics.heightPixels;

		if(getArguments() != null) {
			username = getArguments().getString("Username");
			int avatar = getArguments().getInt("Avatar");

			makeNewAvatar(username, avatar);
		}

		if(mapSurfaceView == null) mapSurfaceView = new MapSurfaceView(getActivity());

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

	public void setNewDistance(Location currentLocation) {

		if(previousLocation == null) distance = 0;
		else distance = previousLocation.distanceTo(currentLocation);		

		Log.d("New distance", Float.toString(distance));
		
		previousLocation = currentLocation;

		DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
		float pxHeight = displayMetrics.heightPixels;

		if(0 < humanAvatar.getPositionY() + (float)(PIXELS_PER_METER * distance * humanAvatar.getDirection() / DEFAULT_SCREEN_HEIGHT) &&
				(pxHeight - humanAvatar.getEdge() * height) / DEFAULT_SCREEN_HEIGHT > humanAvatar.getPositionY() + (float)(PIXELS_PER_METER * distance * humanAvatar.getDirection() / DEFAULT_SCREEN_HEIGHT) &&
				distance < 20) {

			humanAvatar.setPositionY(humanAvatar.getPositionY() + (float)(PIXELS_PER_METER * distance * humanAvatar.getDirection() / DEFAULT_SCREEN_HEIGHT));
		}

	}

	private class MapSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

		private SurfaceHandler surfaceHandler;
		private SurfaceHolder surfaceHolder;

		float touchX, touchY;

		public MapSurfaceView(Context context) {
			super(context);
			setFocusable(true);

			surfaceHolder = getHolder();
			surfaceHolder.addCallback(this);

			surfaceHandler = new SurfaceHandler(this);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {surfaceHandler.restartSurface();}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {surfaceHandler.terminateSurface();}

		public void surfaceRestart(){if (surfaceHandler != null)	surfaceHandler.restartSurface();}

		@Override 
		public boolean onTouchEvent(MotionEvent event){

			touchX = (float)event.getX() / height;
			touchY = (float)event.getY() / height;

			if(event.getAction() == MotionEvent.ACTION_DOWN) {
				if(humanAvatar.isTouchOnObject(touchX, touchY))
					arrows.setVisibility(true);
				if(home.isTouchOnObject(touchX, touchY)) {
					Intent openHome = new Intent(getActivity(), HomeActivity.class);
					startActivity(openHome);
				}
				if(inventory.isTouchOnObject(touchX, touchY)) {
					locationSelectedListener.onLocationSelected("inventory");
					Toast.makeText(getActivity(), "Inventory", Toast.LENGTH_SHORT).show();
				}
			}

			if(event.getAction() == MotionEvent.ACTION_UP) {

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
						locationSelectedListener.onLocationSelected(surfaceHandler.locationToLeftExists());
					}
				}

				if(humanAvatar.swipeArrowRight(touchX, touchY)) {
					arrows.setVisibility(false);
					Toast.makeText(getActivity(), "Right", Toast.LENGTH_SHORT).show();
					if("noLocation" != surfaceHandler.locationToRightExists()){
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

		public void terminateSurface(){mHandler.removeCallbacks(this);}

		public void restartSurface() {mHandler.post(this);}

		@Override
		public void run() {
			mHandler.postDelayed(this,500);	
			try {
				mCanvas = mSurfaceHolder.lockCanvas(null);
				if (mCanvas == null) mSurfaceHolder = mMapSurfaceView.getHolder();
				else doDraw(mCanvas);				
			} finally {
				if (mCanvas != null) mSurfaceHolder.unlockCanvasAndPost(mCanvas);
			}
		}

		/**
		 * Reference screen size is 1920 x 1080. A scaling factor is determined when DisplayMetrics are obtained.
		 */
		public void doPrepare() {
			synchronized (mSurfaceHolder) {			
				DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
				float pxHeight = displayMetrics.heightPixels;
				float pxWidth = displayMetrics.widthPixels;

				arrows = new DrawableObject(0, 0, R.drawable.arrows, ARROWS_EDGE, false);
				inventory = new DrawableObject((float)(pxWidth / height - LOCATION_EDGE), 0, R.drawable.button_inventory, LOCATION_EDGE, true);
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
				scaledCharacterBitmap = Bitmap.createScaledBitmap(characterBitmap, (int)(humanAvatar.getEdge() * height), (int)(humanAvatar.getEdge()* height), true);
				scaledArrowsBitmap = Bitmap.createScaledBitmap(arrowsBitmap, (int)(ARROWS_EDGE * height), (int)(ARROWS_EDGE * height), true);
				scaledInventoryBitmap = Bitmap.createScaledBitmap(inventoryBitmap, (int)(inventory.getEdge() * height), (int)(inventory.getEdge() * height), true);
				scaledHomeBitmap = Bitmap.createScaledBitmap(homeBitmap, (int)(home.getEdge() * height), (int)(home.getEdge() * height), true);

				int adjustedEdge = (int)(LOCATION_EDGE * height);
				scaledFarmBitmap = Bitmap.createScaledBitmap(farmBitmap, adjustedEdge, adjustedEdge, true);
				scaledBakeryBitmap = Bitmap.createScaledBitmap(bakeryBitmap, adjustedEdge, adjustedEdge, true);
				scaledPostOfficeBitmap = Bitmap.createScaledBitmap(postOfficeBitmap, adjustedEdge, adjustedEdge, true);
				scaledParkBitmap = Bitmap.createScaledBitmap(parkBitmap, adjustedEdge, adjustedEdge, true);
				scaledLocationBitmap = Bitmap.createScaledBitmap(locationBitmap, adjustedEdge, adjustedEdge, true);

				if(humanAvatar.getPositionX() == 0 && humanAvatar.getPositionY() == 0)
					humanAvatar.setPosition(0.5f - AVATAR_EDGE_MARGIN, 0.8f);		
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
				canvas.drawBitmap(getLocationBitmap(vl.getLocationType()), vl.getPositionX() * height, vl.getPositionY() * height, null);			

			canvas.drawBitmap(scaledHomeBitmap, home.getPositionX() * height, home.getPositionY() * height, null);
			canvas.drawBitmap(scaledInventoryBitmap, inventory.getPositionX() * height, inventory.getPositionY() * height, null);
			canvas.drawBitmap(scaledCharacterBitmap, humanAvatar.getPositionX() * height, humanAvatar.getPositionY() * height, null);

			if(arrows.getVisibility())
				canvas.drawBitmap(scaledArrowsBitmap, (humanAvatar.getPositionX() - 150 / DEFAULT_SCREEN_HEIGHT) * height, (humanAvatar.getPositionY() - 200 / DEFAULT_SCREEN_HEIGHT) * height, null);

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
		postParameters.add(new BasicNameValuePair("PositionX", Float.toString(humanAvatar.getPositionX())));
		postParameters.add(new BasicNameValuePair("PositionY", Float.toString(humanAvatar.getPositionY())));
		postParameters.add(new BasicNameValuePair("Direction", Integer.toString(humanAvatar.getDirection())));

		WriteToRemoteAsyncTask updateHumanAvatar = new WriteToRemoteAsyncTask(url, postParameters, getActivity());

		updateHumanAvatar.execute();
	}

	public class GetLocationsAsyncTask extends AsyncTask<Void, Void, Void> {

		String username, locationType;
		float locationX, locationY;

		protected Void doInBackground(Void... params) {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

			String response = null;

			try {
				response = CustomHttpClient.executeHttpPost("http://www2.macs.hw.ac.uk/~ph109/DBConnect/getLocations.php", postParameters);

				String result = response.toString();  

				try{
					JSONArray jArray = new JSONArray(result);		

					for(int i = 0; i < jArray.length(); ++i) {
						JSONObject json_data = jArray.getJSONObject(i);
						//TODO get image from remote
						int image = 0;

						username = json_data.getString("Username");
						locationType = json_data.getString("Job");
						locationX = Float.parseFloat(json_data.getString("LocationX"));
						locationY = Float.parseFloat(json_data.getString("LocationY"));

						virtualLocations.add(new VirtualLocation(username, locationType, locationX, locationY, image, LOCATION_EDGE, true));
					}
				} 
				catch(JSONException e){Log.e("log_tag", "Error parsing data "+ e.toString());}   
			} catch (Exception e) {Log.e("log_tag","Error in http connection!!" + e.toString());}  

			return null;
		}

		public void setLocationsArray(ArrayList<VirtualLocation> vl) {virtualLocations = vl;}

		public ArrayList<VirtualLocation> getLocationsArray() {return virtualLocations;}
	}

	public class GetAvatarAsyncTask extends AsyncTask<Void, Void, Void> {

		String username, owner;
		float positionX, positionY;
		int direction;

		protected Void doInBackground(Void... params) {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("Username", humanAvatar.getUsername()));

			String response = null;

			try {
				response = CustomHttpClient.executeHttpPost("http://www2.macs.hw.ac.uk/~ph109/DBConnect/getHumanAvatar.php", postParameters);

				String result = response.toString();  

				try{
					JSONArray jArray = new JSONArray(result);		

					for(int i = 0; i < jArray.length(); ++i){
						JSONObject json_data = jArray.getJSONObject(i);

						positionX = Float.parseFloat(json_data.getString("PositionX"));
						positionY = Float.parseFloat(json_data.getString("PositionY"));
						direction = Integer.parseInt(json_data.getString("Direction"));

						humanAvatar.setPosition(positionX, positionY);
						humanAvatar.setDirection(direction);
					}
				} catch(JSONException e){Log.e("log_tag", "Error parsing data "+ e.toString());}   
			} catch (Exception e) {Log.e("log_tag","Error in http connection!!" + e.toString());}  

			return null;
		}
		protected void onPostExecute(Void result) {}
	}

	public interface OnLocationSelectedListener {public void onLocationSelected(String location);}
}