package com.pfhosa.sprinklecity.fragments;

import com.pfhosa.sprinklecity.R;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class VirtualLocationFragment extends Fragment implements SensorEventListener {

	LinearLayout linearLayout;
	ProgressBar pickingProgressBar;
	TextView tvX;

	Thread thread;
	Handler handler = new Handler();

	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;
	int progressStatus;

	private SensorManager mSensorManager; 
	private Sensor mAccelerometer; 

	private final float NOISE = (float)1.5;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		linearLayout = (LinearLayout)inflater.inflate(R.layout.fragment_virtual_location, container, false);

		progressStatus = 0;

		mInitialized = false;
		mSensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);		

		tvX = (TextView)linearLayout.findViewById(R.id.text_virtual_location_accelerometer);
		pickingProgressBar = (ProgressBar)linearLayout.findViewById(R.id.progress_virtual_location);

		Log.d("Fragment initialized", "yes");

		runProgressThread();

		return linearLayout;
	}

	public void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
		//thread.interrupt();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {		
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];

		if (!mInitialized) {
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			//tvX.setText("0.0");
			mInitialized = true;
		} 
		else {
			float deltaX = Math.abs(mLastX - x);
			float deltaY = Math.abs(mLastY - y);
			float deltaZ = Math.abs(mLastZ - z);
			if (deltaX < NOISE) deltaX = (float)0.0;
			if (deltaY < NOISE) deltaY = (float)0.0;
			if (deltaZ < NOISE) deltaZ = (float)0.0;
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			//tvX.setText(Float.toString(deltaY));

			if(progressStatus < 100)
				progressStatus = (progressStatus + (int)(deltaY*0.2));
			else
				progressStatus = 100;
			
			Log.d("Progress status", Float.toString(progressStatus));
		}
	}

	public void runProgressThread() {
		thread = new Thread(new Runnable() {
			public void run() {
				while (progressStatus <= 100) {
					//progressStatus += 1;
					// Update the progress bar and display the 
					//current value in the text view
					handler.post(new Runnable() {
						public void run() {
							pickingProgressBar.setProgress(progressStatus);
							tvX.setText(progressStatus + "/" + pickingProgressBar.getMax());
							Log.d("bar set to", Integer.toString(progressStatus));
						}
					});
					try {
						// Sleep for 200 milliseconds. 
						//Just to display the progress slowly
						Thread.sleep(200);
					} 
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		thread.run();
		
	}
}
