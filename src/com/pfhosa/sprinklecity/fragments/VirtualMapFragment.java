package com.pfhosa.sprinklecity.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pfhosa.sprinklecity.R;

public class VirtualMapFragment extends Fragment {

	LinearLayout linearLayout;
	static TextView distanceTextView;

	static float distance;
	static double currentLatitude, currentLongitude;
	static Location previousLocation, currentLocation;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Register BroadcastReceiver
		getActivity().registerReceiver(new LocationReceiver(), new IntentFilter("newLocation"));

		//return linearLayout;
		return new MapSurfaceView(getActivity());
	}

	/**
	 * Method that creates Location from latitude and longitude parameters. Distance between two consecutive locations is obtained
	 * and output to display.
	 */
	public static void makeNewLocation(double latitude, double longitude) {
		if(currentLocation != null)
			previousLocation = currentLocation;

		currentLocation = new Location("currentLocation");

		currentLocation.setLatitude(latitude);
		currentLocation.setLongitude(longitude);		

		if(previousLocation == null) {
			distance = 0;
		}
		else {
			distance = previousLocation.distanceTo(currentLocation);
		}

		if(distance != 0.0) {
			Log.d("distance", Float.toString(distance));
		}
	}


	// BroadcastReceiver class passes latitude and longitude to makeNewLocation
	public static class LocationReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			currentLatitude = intent.getExtras().getDouble("latitude");
			currentLongitude = intent.getExtras().getDouble("longitude");

			makeNewLocation(currentLatitude, currentLongitude);

		}

	}

	// 
	public class MapSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

		// Variables go here
		private MapThread thread;
		Context context;
		SurfaceHolder surfaceHolder;
		Paint paint;

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
		private int canvasWidth = 200;
		private int canvasHeight = 400;
		private static final int SPEED = 2;
		private boolean running = false;

		private float bubbleX;
		private float bubbleY;
		private float headingX;
		private float headingY;

		SurfaceHolder surfaceHolder;
		Handler handler;
		Context context;
		Bitmap background, scaled, character;
		Canvas canvas = null;

		float distance = 0;
		int newWidth, newHeight;


		public MapThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
			this.surfaceHolder = surfaceHolder;
			this.handler = handler;
			this.context = context;
		}

		public void doStart() {
			synchronized (surfaceHolder) {
				// Start bubble in centre and create some random motion

				DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

				float pxHeight = displayMetrics.heightPixels;
				float pxWidth = displayMetrics.widthPixels;

				background = BitmapFactory.decodeResource(getResources(), R.drawable.street_view);
				
				float scaleHeight = (float)background.getHeight()/(float)pxHeight;
				float scaleWidth = (float)background.getWidth()/(float)pxWidth;
				
				newHeight = Math.round(background.getHeight()/scaleHeight);
				newWidth = Math.round(background.getWidth()/scaleWidth);

				scaled = Bitmap.createScaledBitmap(background, newWidth, newHeight, true);

				character = BitmapFactory.decodeResource(getResources(), R.drawable.character_human);

				bubbleX = pxWidth / 3;
				bubbleY = (float) (canvasHeight * 0.8);
				
				headingY = - 1;

			}
		}

		public void run() {
			while (running) {
				try {
					canvas = surfaceHolder.lockCanvas(null);
					synchronized (surfaceHolder) {
						doDraw(canvas);
					}
				} catch(NullPointerException e) {
					e.printStackTrace();
				} finally {
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

		private void doDraw(Canvas canvas) {

			bubbleX = bubbleX;
			bubbleY = bubbleY + (headingY * SPEED);
			//canvas.restore();
			
			//character = Bitmap.createScaledBitmap(character, (int)(character.getWidth() * 0.8), (int)(character.getHeight() *0.8), false);

			canvas.drawBitmap(scaled, 0, 0, null);
			canvas.drawBitmap(character, bubbleX, bubbleY, null);
		}
	}
}
