package com.pfhosa.sprinklecity.fragments;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.database.CustomHttpClient;
import com.pfhosa.sprinklecity.database.WriteToRemoteAsyncTask;
import com.pfhosa.sprinklecity.model.DrawableObject;
import com.pfhosa.sprinklecity.model.HumanAvatar;
import com.pfhosa.sprinklecity.model.VirtualLocation;
import com.pfhosa.sprinklecity.ui.GameLocationActivity;
import com.pfhosa.sprinklecity.ui.HomeActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
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

public class VirtualMapFragment extends Fragment {

	private MapSurfaceView mapSurfaceView;

	// Constants
	public static final int PIXELS_PER_METER = 50;
	public static final int AVATAR_EDGE = 300;
	public static final int AVATAR_EDGE_MARGIN = 0;
	public static final int LOCATION_EDGE = 300;
	public static final int ARROWS_EDGE = 600;
	public static final int CORNER_EDGE = 300;
	public static final double DISTANCE_FACTOR = 0.5;

	// Location
	static float distance, distanceGlobal;
	static double currentLatitude, currentLongitude;
	static Location previousLocation, currentLocation = null, seenLocation = null;

	OnLocationSelectedListener locationSelectedListener;

	HumanAvatar humanAvatar;
	int avatarGlobal;

	String usernameGlobal;
	int touchX, touchY;

	DrawableObject arrows;

	WriteToRemoteAsyncTask updateHumanAvatar;
	
	volatile Thread thread;

	ArrayList<VirtualLocation> virtualLocations= new ArrayList<VirtualLocation>();

	LocationReceiver locationReceiver;

	@Override
	public void onResume() {
		if (mapSurfaceView != null){
			mapSurfaceView.surfaceRestart();
		}
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(mapSurfaceView == null)
			mapSurfaceView = new MapSurfaceView(getActivity());

		LocationReceiver locationReceiver = new LocationReceiver();
		getActivity().registerReceiver(locationReceiver, new IntentFilter("newLocationIntent"));

		if(getArguments() != null) {
			usernameGlobal = getArguments().getString("Username");
			avatarGlobal = getArguments().getInt("Avatar");
		}
		
		makeNewAvatar();
		
		mapSurfaceView.doPrepare();

		return mapSurfaceView;
	}


	@Override
	public void onPause() {
		if (mapSurfaceView != null){
			mapSurfaceView.surfaceDestroyed(mapSurfaceView.getHolder());
		}
		super.onPause();
	}

	public class LocationReceiver extends BroadcastReceiver {

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
	
	public void makeNewAvatar() {
		humanAvatar = new HumanAvatar(
				usernameGlobal, 
				avatarGlobal,
				AVATAR_EDGE,
				0,
				0,
				-1);

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

		distanceGlobal = distance;
		previousLocation = currentLocation;

		humanAvatar.setPositionY(humanAvatar.getPositionY() + (int)(PIXELS_PER_METER * distance * humanAvatar.getDirection()));

	}

	private class MapSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
		
		int newWidth, newHeight;
		
		Bitmap backgroundBitmap, characterBitmap, arrowsBitmap,  locationBitmap;
		Bitmap scaledBackgroundBitmap, scaledCharacterBitmap, scaledArrowsBitmap, scaledLocationBitmap;

		private SurfaceThread surfaceThread;

		public MapSurfaceView(Context context) {
			super(context);
			setFocusable(true);
			surfaceThread = new SurfaceThread(this);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			surfaceThread.restartSurface();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			surfaceThread.terminateSurface();
		}

		public void surfaceRestart(){
			if (surfaceThread != null){
				surfaceThread.restartSurface(); 
			}
		}
		
		public void doPrepare() {
			

				arrows = new DrawableObject(0, 0, R.drawable.arrows, false);

				DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
				float pxHeight = displayMetrics.heightPixels;
				float pxWidth = displayMetrics.widthPixels;

				backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.street_view);
				characterBitmap = BitmapFactory.decodeResource(getResources(), humanAvatar.getAvatarImage());
				arrowsBitmap = BitmapFactory.decodeResource(getResources(), arrows.getImage());
				locationBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location_temple);

				float scaleHeight = (float)backgroundBitmap.getHeight()/(float)pxHeight;
				float scaleWidth = (float)backgroundBitmap.getWidth()/(float)pxWidth;

				newHeight = Math.round(backgroundBitmap.getHeight()/scaleHeight);
				newWidth = Math.round(backgroundBitmap.getWidth()/scaleWidth);

				scaledBackgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, newWidth, newHeight, true);
				scaledCharacterBitmap = Bitmap.createScaledBitmap(characterBitmap, humanAvatar.getAvatarEdge(), humanAvatar.getAvatarEdge(), true);
				scaledArrowsBitmap = Bitmap.createScaledBitmap(arrowsBitmap, ARROWS_EDGE, ARROWS_EDGE, true);
				scaledLocationBitmap = Bitmap.createScaledBitmap(locationBitmap, LOCATION_EDGE, LOCATION_EDGE, true);

				//canvas.drawBitmap(scaledBackgroundBitmap, 0, 0, null);
				//canvas.drawBitmap(scaledCharacterBitmap, humanAvatar.getPositionX(), humanAvatar.getPositionY(), null);

				humanAvatar.setPosition(
						(int)((pxWidth - humanAvatar.getAvatarEdge()) / 2), 
						(int)(pxHeight * 0.8));
		}
		
		public void doDraw(Canvas canvas){

			Log.d("Drawing", "...");

			// Order of Bitmaps is important (they are drawn in this order).

			canvas.drawBitmap(scaledBackgroundBitmap, 0,  0, null);

			for(VirtualLocation vl : virtualLocations) 
				canvas.drawBitmap(scaledLocationBitmap, vl.getLocationX(), vl.getLocationY(), null);			

			//canvas.drawBitmap(scaledCharacterBitmap, characterX, characterY, null);
			canvas.drawBitmap(scaledCharacterBitmap, humanAvatar.getPositionX(), humanAvatar.getPositionY(), null);

			if(arrows.getVisibility())
				canvas.drawBitmap(scaledArrowsBitmap, humanAvatar.getPositionX() - 150, humanAvatar.getPositionY() - 200, null);

		}

		@Override 
		public boolean onTouchEvent(MotionEvent event){
			if(event.getAction() == MotionEvent.ACTION_DOWN) {

				touchX = (int)event.getX();
				touchY = (int)event.getY();

				if(humanAvatar.isTouchOnAvatar(touchX, touchY))
					//thread.setDrawArrows(true);
					arrows.setVisibility(true);
			}

			if(event.getAction() == MotionEvent.ACTION_UP) {

				touchX = (int)event.getX();
				touchY = (int)event.getY();

				if(surfaceThread.isTouchOnUpperLeft(touchX, touchY)) {
					Intent openHome = new Intent(getActivity(), HomeActivity.class);
					startActivity(openHome);
				}					
			}

			if(event.getAction() == MotionEvent.ACTION_UP) {
				touchX = (int)event.getX();
				touchY = (int)event.getY();

				if(humanAvatar.isTouchOnAvatar(touchX, touchY))
					//thread.setDrawArrows(false);

					arrows.setVisibility(false);

				if(humanAvatar.swipeArrowUp(touchX, touchY)) {
					//thread.setDrawArrows(false);
					arrows.setVisibility(false);
					humanAvatar.setDirection(-1);
					Toast.makeText(getActivity(), "Up", Toast.LENGTH_SHORT).show();
				}

				if(humanAvatar.swipeArrowDown(touchX, touchY)) {
					//thread.setDrawArrows(false);
					arrows.setVisibility(false);
					humanAvatar.setDirection(1);
					Toast.makeText(getActivity(), "Down", Toast.LENGTH_SHORT).show();
				}

				if(humanAvatar.swipeArrowLeft(touchX, touchY)) {
					//thread.setDrawArrows(false);
					arrows.setVisibility(false);
					Toast.makeText(getActivity(), "Left", Toast.LENGTH_SHORT).show();
					if("noLocation" != surfaceThread.locationToLeftExists()){
						locationSelectedListener.onLocationSelected(1);
					}
				}

				if(humanAvatar.swipeArrowRight(touchX, touchY)) {
					//thread.setDrawArrows(false);
					arrows.setVisibility(false);
					Toast.makeText(getActivity(), "Right", Toast.LENGTH_SHORT).show();
					if("noLocation" != surfaceThread.locationToRightExists()){
						Intent openFarm = new Intent(getActivity(), GameLocationActivity.class);
						getActivity().startActivity(openFarm);	
					}
				}

			}
			return true;   
		}


	}

	private class SurfaceThread implements Runnable {
		private SurfaceHolder mSurfaceHolder;
		private MapSurfaceView mMapSurfaceView;
		private Canvas mCanvas;

		public SurfaceThread(MapSurfaceView mapSurfaceView){
			mSurfaceHolder = mapSurfaceView.getHolder();
			mMapSurfaceView = mapSurfaceView;
			mCanvas = new Canvas();

			thread = new Thread(this);
			thread.start();
		}

		public void terminateSurface(){
			thread.interrupt();
		}

		public void restartSurface() {
			if (thread.getState() == Thread.State.TERMINATED){
				thread = new Thread(this);
				thread.start();  // Start a new thread
			} 
		}

		public SurfaceHolder getSurfaceHolder() {
			return mSurfaceHolder;
		}

		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					mCanvas = mSurfaceHolder.lockCanvas(null);
					if (mCanvas == null){
						mSurfaceHolder = mMapSurfaceView.getHolder();
					} else {
						synchronized (mSurfaceHolder) {
							mapSurfaceView.doDraw(mCanvas);
						}
					}   
				} finally {
					if (mCanvas != null) {
						mSurfaceHolder.unlockCanvasAndPost(mCanvas);
					}
				}
			}
		}
		
		public boolean isTouchOnUpperLeft(int touchX, int touchY) {

			return 	(CORNER_EDGE > touchX) &&
					(CORNER_EDGE > touchY);
		}

		public String locationToLeftExists() {

			for(VirtualLocation vl : virtualLocations) { 
				if(vl.isLocationOnLeft(humanAvatar.getPositionX(),
						humanAvatar.getPositionY(), 
						humanAvatar.getAvatarEdge(), 
						AVATAR_EDGE_MARGIN))
					return vl.getOwner();
			}

			return "noLocation";
		}

		public String locationToRightExists() {

			for(VirtualLocation vl : virtualLocations) { 
				if(vl.isLocationOnRight(humanAvatar.getPositionX(),
						humanAvatar.getPositionY(), 
						humanAvatar.getAvatarEdge(), 
						AVATAR_EDGE_MARGIN))
					return vl.getOwner();
			}

			return "noLocation";
		}
	}

	public class GetLocationsAsyncTask extends AsyncTask<Void, Void, Void> {

		String username, owner;
		int locationX, locationY;

		protected Void doInBackground(Void... params) {
			// declare parameters that are passed to PHP script i.e. the name "birthyear" and its value submitted by user   
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

			Looper.prepare();

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

						//Get an output to the screen
						owner = json_data.getString("Username");
						locationX = Integer.parseInt(json_data.getString("LocationX"));
						locationY = Integer.parseInt(json_data.getString("LocationY"));

						virtualLocations.add(new VirtualLocation(owner, locationX, locationY, LOCATION_EDGE));
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

			Looper.prepare();

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
		public void onLocationSelected(int fragment);
	}
}