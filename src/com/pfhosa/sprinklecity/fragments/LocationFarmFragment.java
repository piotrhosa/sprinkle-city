package com.pfhosa.sprinklecity.fragments;

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

import com.pfhosa.sprinklecity.R;

public class LocationFarmFragment extends Fragment implements SensorEventListener {

	LinearLayout linearLayout;
	ProgressBar pickingProgressBar;
	TextView progressTextView;

	Thread thread;
	Handler handler = new Handler();

	private float mLastY;
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

		progressTextView = (TextView)linearLayout.findViewById(R.id.text_virtual_location_accelerometer);
		pickingProgressBar = (ProgressBar)linearLayout.findViewById(R.id.progress_virtual_location);

		Log.d("Fragment initialized", "yes");

		return linearLayout;
	}

	public void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {	
		float y = event.values[1];

		if (!mInitialized) {
			mLastY = y;

			mInitialized = true;
		} 
		else {

			float deltaY = Math.abs(mLastY - y);
			if (deltaY < NOISE) deltaY = (float)0.0;
			mLastY = y;

			if(progressStatus < 100)
				progressStatus = (progressStatus + (int)(deltaY*0.2));
			else
				progressStatus = 100;
			
			pickingProgressBar.setProgress(progressStatus);
			progressTextView.setText(progressStatus + "/" + pickingProgressBar.getMax());
			
			if(progressStatus < 100) {
	            //TODO Pop back stack
	            Log.d("Pop back", "jasjdjd");
			}
				

			Log.d("Progress status", Integer.toString(progressStatus));
		}
	}

	/**
	public void runProgressThread(int progressStat) {
		
		final int progress = progressStat;
		thread = new Thread(new Runnable() {
			public void run() {

				//while(progressStatus < 100) {
					try {
						// Sleep for 200 milliseconds just to display the progress slowly.
						Thread.sleep(200);
					} 
					catch (InterruptedException e) {
						e.printStackTrace();
					}

					handler.post(new Runnable() {
						public void run() {
							pickingProgressBar.setProgress(progressStatus);
							progressTextView.setText(progress + "/" + pickingProgressBar.getMax());
						}
					});
				//}
			}
		});

		thread.run();

	}
	*/
}
