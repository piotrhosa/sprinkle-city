package com.pfhosa.sprinklecity.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Location;
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

public class VirtualMapFragment extends Fragment {

	static float distance, distanceGlobal;
	static double currentLatitude, currentLongitude;
	static Location previousLocation, currentLocation = null, seenLocation = null;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Register BroadcastReceiver
		getActivity().registerReceiver(new LocationReceiver(), new IntentFilter("newLocationIntent"));

		return new MapSurfaceView(getActivity());
	}

	/**
	 * Method that creates Location from latitude and longitude parameters. Distance between two consecutive locations is obtained
	 * and output to display.
	 */
	public void setNewDistance(Location currentLocation) {

		if(previousLocation == null) 
			distance = 0;
		else 
			distance = previousLocation.distanceTo(currentLocation);		

		distanceGlobal = distance;
		previousLocation = currentLocation;
		
		Log.d("distance", Float.toString(distance));
		Toast.makeText(getActivity(), Float.toString(distance), Toast.LENGTH_SHORT).show();
	}

	// BroadcastReceiver class passes latitude and longitude to setNewDistance
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

	public class MapSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

		private MapThread thread;
		Context context;
		SurfaceHolder surfaceHolder;

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

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			return super.onTouchEvent(event);
		}

		@Override
		protected void onDraw(Canvas canvas) {

		}

	}

	public class MapThread extends Thread {
		
		public static final int PIXELS_PER_METER = 50;
		
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
		Bitmap backgroundBitmap, characterBitmap, scaledBackgroundBitmap, scaledCharacterBitmap;
		Canvas canvas = null;

		float distance = 0;
		int newWidth, newHeight;


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

				float scaleHeight = (float)backgroundBitmap.getHeight()/(float)pxHeight;
				float scaleWidth = (float)backgroundBitmap.getWidth()/(float)pxWidth;

				newHeight = Math.round(backgroundBitmap.getHeight()/scaleHeight);
				newWidth = Math.round(backgroundBitmap.getWidth()/scaleWidth);

				scaledBackgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, newWidth, newHeight, true);
				scaledCharacterBitmap = Bitmap.createScaledBitmap(characterBitmap, 300, 300, true);
				
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

		private void doDraw(Canvas canvas, float distance) {
			
			if(distanceGlobal != 0) 
				characterY = characterY + (headingY * distanceGlobal * PIXELS_PER_METER);		

			distanceGlobal = 0;
			
			canvas.drawBitmap(scaledBackgroundBitmap, 0,  0, null);
			canvas.drawBitmap(scaledCharacterBitmap, characterX, characterY, null);
		}
	}
}
