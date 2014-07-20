package com.pfhosa.sprinklecity.fragments;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
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

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.database.CustomHttpClient;
import com.pfhosa.sprinklecity.database.WriteToRemoteAsyncTask;
import com.pfhosa.sprinklecity.model.HumanAvatar;
import com.pfhosa.sprinklecity.model.VirtualLocation;
import com.pfhosa.sprinklecity.ui.GameLocationActivity;
import com.pfhosa.sprinklecity.ui.HomeActivity;

public class VirtualMapFragment extends Fragment {

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

	MapSurfaceView mapSurfaceView;
	HumanAvatar humanAvatar;
	int avatarGlobal;

	String usernameGlobal;


	WriteToRemoteAsyncTask updateHumanAvatar;

	ArrayList<VirtualLocation> virtualLocations= new ArrayList<VirtualLocation>();

	LocationReceiver locationReceiver;
	
	 @Override
	public void onResume() {
	  if (mapSurfaceView != null){
	   mapSurfaceView.surfaceRestart();
	  }
	  super.onResume();
	 }

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Register BroadcastReceiver
		LocationReceiver locationReceiver = new LocationReceiver();
		getActivity().registerReceiver(locationReceiver, new IntentFilter("newLocationIntent"));

		if(getArguments() != null) {
			usernameGlobal = getArguments().getString("Username");
			avatarGlobal = getArguments().getInt("Avatar");
		}

		makeNewAvatar();

		new GetLocationsAsyncTask().execute();
		new GetAvatarAsyncTask().execute();

		Log.d("SurfaceView created", "true");

		mapSurfaceView = new MapSurfaceView(getActivity());
		return mapSurfaceView;
	}

	@Override
	public void onPause() {
		super.onPause();
		updateAvatar();

		if(mapSurfaceView != null){
			mapSurfaceView.surfaceDestroyed(mapSurfaceView.getHolder());
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

	public void updateAvatar() {
		String url = "http://www2.macs.hw.ac.uk/~ph109/DBConnect/updateHumanAvatar.php";

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("Username", humanAvatar.getUsername()));
		postParameters.add(new BasicNameValuePair("PositionX", Integer.toString(humanAvatar.getPositionX())));
		postParameters.add(new BasicNameValuePair("PositionY", Integer.toString(humanAvatar.getPositionY())));
		postParameters.add(new BasicNameValuePair("Direction", Integer.toString(humanAvatar.getDirection())));

		updateHumanAvatar = new WriteToRemoteAsyncTask(url, postParameters, getActivity());

		updateHumanAvatar.execute();
		Log.d("Update avatar", "executing");
	}

	/**
	 * This method takes the current Location and calculates the distance between the current and old one. 
	 */
	public void setNewDistance(Location currentLocation) {

		if(previousLocation == null) 
			distance = 0;
		else 
			distance = previousLocation.distanceTo(currentLocation);		

		distanceGlobal = distance;
		previousLocation = currentLocation;

		humanAvatar.setPositionY(humanAvatar.getPositionY() + (int)(PIXELS_PER_METER * distance * humanAvatar.getDirection()));

		Log.d("distance", Float.toString(distance));
	}

	/**
	 * This method receives new latitude and longitude that were broadcast and passes the new Location to
	 * setNewDistance.
	 */
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

	/**
	 * @author Piotr
	 * This class creates SurfaceView and makes new thread for it. It also listens to MotionEvents.
	 */

	public class MapSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

		@SuppressWarnings("unused")
		private int canvasWidth = 200;
		private int canvasHeight = 400;

		Bitmap backgroundBitmap, characterBitmap, arrowsBitmap,  locationBitmap;
		Bitmap scaledBackgroundBitmap, scaledCharacterBitmap, scaledArrowsBitmap, scaledLocationBitmap;

		float distance = 0;
		int newWidth, newHeight;
		boolean drawArrows = false;

		private MapThread thread;

		Context context;
		SurfaceHolder surfaceHolder;

		int touchX, touchY;

		public MapSurfaceView(Context context) {
			super(context);
			setFocusable(true);

			thread = new MapThread(mapSurfaceView);
		}

		public MapThread getThread() {
			return thread;
		}

		public void surfaceCreated(SurfaceHolder holder) {     

		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {      
			thread.setSurfaceSize(width, height);
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			thread.terminateSurface();
		}

		public void surfaceRestart(){
			if (thread != null){
				thread.restartSurface(); 
			}
		}

		/**
		 * Handles tapping on avatar and swiping arrows that set direction.
		 */
		@Override
		public boolean onTouchEvent(MotionEvent event) {

			if(event.getAction() == MotionEvent.ACTION_DOWN) {

				touchX = (int)event.getX();
				touchY = (int)event.getY();

				if(humanAvatar.isTouchOnAvatar(touchX, touchY))
					thread.setDrawArrows(true);
			}

			if(event.getAction() == MotionEvent.ACTION_UP) {

				touchX = (int)event.getX();
				touchY = (int)event.getY();

				if(thread.isTouchOnUpperLeft(touchX, touchY)) {
					Intent openHome = new Intent(getActivity(), HomeActivity.class);
					startActivity(openHome);
				}					
			}

			if(event.getAction() == MotionEvent.ACTION_UP) {
				touchX = (int)event.getX();
				touchY = (int)event.getY();

				if(humanAvatar.isTouchOnAvatar(touchX, touchY))
					thread.setDrawArrows(false);

				if(humanAvatar.swipeArrowUp(touchX, touchY)) {
					thread.setDrawArrows(false);
					humanAvatar.setDirection(-1);
					Toast.makeText(getActivity(), "Up", Toast.LENGTH_SHORT).show();
				}

				if(humanAvatar.swipeArrowDown(touchX, touchY)) {
					thread.setDrawArrows(false);
					humanAvatar.setDirection(1);
					Toast.makeText(getActivity(), "Down", Toast.LENGTH_SHORT).show();
				}

				if(humanAvatar.swipeArrowLeft(touchX, touchY)) {
					thread.setDrawArrows(false);
					Toast.makeText(getActivity(), "Left", Toast.LENGTH_SHORT).show();
					if("noLocation" != thread.locationToLeftExists()){
						locationSelectedListener.onLocationSelected(1);
					}
				}

				if(humanAvatar.swipeArrowRight(touchX, touchY)) {
					thread.setDrawArrows(false);
					Toast.makeText(getActivity(), "Right", Toast.LENGTH_SHORT).show();
					if("noLocation" != thread.locationToRightExists()){
						Intent openFarm = new Intent(getActivity(), GameLocationActivity.class);
						getActivity().startActivity(openFarm);	
					}
				}

			}
			return true;  
		}

		public void onPrepare(Canvas canvas) {
			synchronized (surfaceHolder) {

				DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
				float pxHeight = displayMetrics.heightPixels;
				float pxWidth = displayMetrics.widthPixels;

				backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.street_view);
				characterBitmap = BitmapFactory.decodeResource(getResources(), humanAvatar.getAvatarImage());
				arrowsBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrows);
				locationBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location_temple);

				float scaleHeight = (float)backgroundBitmap.getHeight()/(float)pxHeight;
				float scaleWidth = (float)backgroundBitmap.getWidth()/(float)pxWidth;

				newHeight = Math.round(backgroundBitmap.getHeight()/scaleHeight);
				newWidth = Math.round(backgroundBitmap.getWidth()/scaleWidth);

				scaledBackgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, newWidth, newHeight, true);
				scaledCharacterBitmap = Bitmap.createScaledBitmap(characterBitmap, humanAvatar.getAvatarEdge(), humanAvatar.getAvatarEdge(), 
						true);
				scaledArrowsBitmap = Bitmap.createScaledBitmap(arrowsBitmap, ARROWS_EDGE, ARROWS_EDGE, true);
				scaledLocationBitmap = Bitmap.createScaledBitmap(locationBitmap, LOCATION_EDGE, LOCATION_EDGE, true);

				canvas.drawBitmap(scaledBackgroundBitmap, 0, 0, null);
				canvas.drawBitmap(scaledCharacterBitmap, humanAvatar.getPositionX(), humanAvatar.getPositionY(), null);

				humanAvatar.setPosition(
						(int)((pxWidth - humanAvatar.getAvatarEdge()) / 2), 
						(int)(canvasHeight * 0.8));
			}
		}

		public void onDraw(Canvas canvas) {
			canvas.drawBitmap(scaledBackgroundBitmap, 0,  0, null);

			for(VirtualLocation vl : virtualLocations) 
				canvas.drawBitmap(scaledLocationBitmap, vl.getLocationX(), vl.getLocationY(), null);			

			//canvas.drawBitmap(scaledCharacterBitmap, characterX, characterY, null);
			canvas.drawBitmap(scaledCharacterBitmap, humanAvatar.getPositionX(), humanAvatar.getPositionY(), null);

			if(drawArrows)
				canvas.drawBitmap(scaledArrowsBitmap, humanAvatar.getPositionX() - 150, humanAvatar.getPositionY() - 200, null);
		}


	}

	/**
	 * @author Piotr
	 * This class is a new thread for SurfaceView. All drawing and canvas measurements are done here.
	 */

	private class MapThread implements Runnable {
		private SurfaceHolder surfaceHolder;
		private MapSurfaceView mMapSurfaceView;
		private Canvas canvas;
		volatile Thread thread;
		
		boolean drawArrows;
		int canvasHeight, canvasWidth;

		public MapThread(MapSurfaceView mapSurfaceView){
			surfaceHolder = mapSurfaceView.getHolder();
			mMapSurfaceView = mapSurfaceView;
			canvas = new Canvas();

			thread = new Thread(this);
			thread.start();
		}
		public void terminateSurface(){
			this.thread.interrupt();
		}
		public void restartSurface() {
			if (thread.getState() == Thread.State.TERMINATED){
				thread = new Thread(this);
				thread.start();  // Start a new thread
			} 
		}
		
		@SuppressLint("WrongCall")
		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					canvas = surfaceHolder.lockCanvas(null);
					if (canvas == null){
						surfaceHolder = mMapSurfaceView.getHolder();
					} else {
						synchronized (surfaceHolder) {
							mMapSurfaceView.onDraw(canvas);
						}
					}   
				} finally {
					if (canvas != null) {
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}

		public void setSurfaceSize(int width, int height) {

			synchronized (surfaceHolder) {
				canvasWidth = width;
				canvasHeight = height;
				mMapSurfaceView.onPrepare(canvas);
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

		public void setDrawArrows(boolean set) {
			drawArrows = set;
		}
	}

	public void populateMap(ArrayList<VirtualLocation> virtualLocations) {

		for(VirtualLocation vl : virtualLocations) 
			Log.d("Username location", "" + vl.getOwner());
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

		protected void onPostExecute(Void result) {

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
