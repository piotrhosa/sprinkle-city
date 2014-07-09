package com.pfhosa.sprinklecity.fragments;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class VirtualMapFragment extends Fragment {

	static float distance, distanceGlobal;
	static double currentLatitude, currentLongitude;
	static Location previousLocation, currentLocation = null, seenLocation = null;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Register BroadcastReceiver
		getActivity().registerReceiver(new LocationReceiver(), new IntentFilter("newLocationIntent"));
		
		new GetLocationsAsyncTask().execute();

		return new MapSurfaceView(getActivity());
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

		private MapThread thread;
		Context context;
		SurfaceHolder surfaceHolder;

		int touchX, touchY;

		public MapSurfaceView(Context context) {

			super(context);

			surfaceHolder = getHolder();
			surfaceHolder.addCallback(this);

			thread = new MapThread(surfaceHolder, context, new Handler());
			setFocusable(true);
		}

		public MapThread getThread() {
			return thread;
		}

		public void surfaceCreated(SurfaceHolder holder) {
			thread.setRunning(true);
			thread.start();
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {      
			thread.setSurfaceSize(width, height);
		}

		public void surfaceDestroyed(SurfaceHolder holder) {

			boolean retry = true;

			thread.setRunning(false);

			while (retry) {
				try {
					thread.join();
					retry = false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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

				if(thread.isTouchOnAvatar(touchX, touchY))
					thread.setDrawArrows(true);
			}
			
			if(event.getAction() == MotionEvent.ACTION_UP) {
				touchX = (int)event.getX();
				touchY = (int)event.getY();

				if(thread.isTouchOnAvatar(touchX, touchY))
					thread.setDrawArrows(false);
				
				if(thread.swipeArrowUp(touchX, touchY)) {
					thread.setDrawArrows(false);
					Toast.makeText(getActivity(), "Up", Toast.LENGTH_SHORT).show();
				}
				
				if(thread.swipeArrowLeft(touchX, touchY)) {
					thread.setDrawArrows(false);
					Toast.makeText(getActivity(), "Left", Toast.LENGTH_SHORT).show();
				}
				
				if(thread.swipeArrowRight(touchX, touchY)) {
					thread.setDrawArrows(false);
					Toast.makeText(getActivity(), "Right", Toast.LENGTH_SHORT).show();
				}
				
			}
			return true;  
		}

		@Override
		protected void onDraw(Canvas canvas) {

		}
	}

	/**
	 * @author Piotr
	 * This class is a new thread for SurfaceView. All drawing and canvas measurements are done here.
	 */
	public class MapThread extends Thread {

		public static final int PIXELS_PER_METER = 50;
		public static final int AVATAR_EDGE = 300;
		public static final int ARROWS_EDGE = 600;

		@SuppressWarnings("unused")
		private int canvasWidth = 200;
		private int canvasHeight = 400;
		private boolean running = false;

		private float characterX;
		private float characterY;
		private float headingY;

		SurfaceHolder surfaceHolder;
		Handler handler;
		Context context;
		Bitmap backgroundBitmap, characterBitmap, arrowsBitmap, scaledBackgroundBitmap, scaledCharacterBitmap, scaledArrowsBitmap;
		Canvas canvas = null;

		float distance = 0;
		int newWidth, newHeight;
		boolean drawArrows = false;


		public MapThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {

			this.surfaceHolder = surfaceHolder;
			this.handler = handler;
			this.context = context;
		}

		// 
		public void doStart() {

			synchronized (surfaceHolder) {

				DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
				float pxHeight = displayMetrics.heightPixels;
				float pxWidth = displayMetrics.widthPixels;

				backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.street_view);
				characterBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.character_human);
				arrowsBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrows);

				float scaleHeight = (float)backgroundBitmap.getHeight()/(float)pxHeight;
				float scaleWidth = (float)backgroundBitmap.getWidth()/(float)pxWidth;

				newHeight = Math.round(backgroundBitmap.getHeight()/scaleHeight);
				newWidth = Math.round(backgroundBitmap.getWidth()/scaleWidth);

				scaledBackgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, newWidth, newHeight, true);
				scaledCharacterBitmap = Bitmap.createScaledBitmap(characterBitmap, AVATAR_EDGE, AVATAR_EDGE, true);
				scaledArrowsBitmap = Bitmap.createScaledBitmap(arrowsBitmap, ARROWS_EDGE, ARROWS_EDGE, true);

				canvas.drawBitmap(scaledBackgroundBitmap, 0, 0, null);
				canvas.drawBitmap(scaledCharacterBitmap, characterX, characterY, null);

				characterX = pxWidth / 2;
				characterY = (float) (canvasHeight * 0.8);

				headingY = - 1;

			}
		}

		public void run() {

			while (running) {
				try {
					canvas = surfaceHolder.lockCanvas(null);

					synchronized (surfaceHolder) {
						doDraw(canvas, distanceGlobal);
					}
				} 
				catch(NullPointerException e) {
					e.printStackTrace();					
				} 
				finally {
					if (canvas != null) {
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}

		public void setRunning(boolean running) { 
			this.running = running;
		}

		public void setSurfaceSize(int width, int height) {

			synchronized (surfaceHolder) {
				canvasWidth = width;
				canvasHeight = height;
				doStart();
			}
		}

		public boolean isTouchOnAvatar(int touchX, int touchY) {

			if((characterX < touchX) &&
					(touchX < characterX + AVATAR_EDGE) &&
					(characterY < touchY) &&
					(touchY < characterY + AVATAR_EDGE))
				return true;
			else
				return false;					
		}
		
		public boolean swipeArrowUp(int touchX, int touchY) {
			
			if((characterX < touchX) &&
					(touchX < characterX + AVATAR_EDGE) &&
					(characterY > touchY)) 
				return true;
			else
				return false;	
		}
		
		public boolean swipeArrowLeft(int touchX, int touchY) {
			
			if((characterX > touchX) &&
					(characterY < touchY) &&
					(touchY < characterY + AVATAR_EDGE)) 
				return true;
			else
				return false;
		}
		
		public boolean swipeArrowRight(int touchX, int touchY) {
			
			if((characterX + AVATAR_EDGE < touchX) &&
					(characterY < touchY) &&
					(touchY < characterY + AVATAR_EDGE)) 
				return true;
			else
				return false;
		}

		public void setDrawArrows(boolean set) {
			drawArrows = set;
		}

		private void doDraw(Canvas canvas, float distance) {

			if(distanceGlobal != 0) 
				characterY = characterY + (headingY * distanceGlobal * PIXELS_PER_METER);		

			distanceGlobal = 0;

			canvas.drawBitmap(scaledBackgroundBitmap, 0,  0, null);
			canvas.drawBitmap(scaledCharacterBitmap, characterX, characterY, null);

			if(drawArrows)
				canvas.drawBitmap(scaledArrowsBitmap, characterX - 150, characterY - 200, null);
		}
	}
	
	public class GetLocationsAsyncTask extends AsyncTask<Void, Void, Void> {
		
		protected Void doInBackground(Void... params) {
			// declare parameters that are passed to PHP script i.e. the name "birthyear" and its value submitted by user   
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>(null);
			String username;

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
						username = json_data.getString("Username");
						Log.d("Username location", username);

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
}
