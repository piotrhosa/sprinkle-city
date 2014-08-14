package com.pfhosa.sprinklecity.virtuallocationfragments;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.database.WriteToRemoteAsyncTask;
import com.pfhosa.sprinklecity.model.InventoryItem;

public class LocationFarmFragment extends Fragment implements SensorEventListener {

	LinearLayout mLinearLayout;
	ProgressBar mPickingProgressBar;
	TextView mProgressTextView;

	String mUsername;
	boolean mItemCreated = false;

	private float mLastY;
	private boolean mInitialized;
	int progressStatus;

	private SensorManager mSensorManager; 
	private Sensor mAccelerometer; 

	private final float NOISE = (float)1.5;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(getArguments() != null) mUsername = getArguments().getString("Username");

		mLinearLayout = (LinearLayout)inflater.inflate(R.layout.fragment_location_farm, container, false);

		progressStatus = 0;

		mInitialized = false;
		mSensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);		

		mProgressTextView = (TextView)mLinearLayout.findViewById(R.id.text_virtual_location_accelerometer);
		mPickingProgressBar = (ProgressBar)mLinearLayout.findViewById(R.id.progress_virtual_location);

		Log.d("Fragment initialized", "yes");

		return mLinearLayout;
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
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {	
		float y = event.values[1];

		if (!mInitialized) {
			mLastY = y;

			mInitialized = true;
		} else {

			float deltaY = Math.abs(mLastY - y);
			if (deltaY < NOISE) deltaY = 0.0f;
			else {
				mLastY = y;

				if(progressStatus < 100) progressStatus = (progressStatus + (int)(deltaY*0.2));
				else progressStatus = 100;

				mPickingProgressBar.setProgress(progressStatus);
				mProgressTextView.setText(progressStatus + "/" + mPickingProgressBar.getMax());

				if(progressStatus >= 100 && !mItemCreated) {
					getActivity().onBackPressed();
					InventoryItem apple = new InventoryItem(mUsername, "apple", 1);
					insertItemInRemote(apple);
					mItemCreated = true;
				}
			}
		}
	}

	private void insertItemInRemote(InventoryItem item) {
		String url = "http://www2.macs.hw.ac.uk/~ph109/DBConnect/insertInventoryItem.php";

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();	
		postParameters.add(new BasicNameValuePair("Username", mUsername));
		postParameters.add(new BasicNameValuePair("Item", item.getItem()));
		postParameters.add(new BasicNameValuePair("Value", Integer.toString(item.getValue())));
		postParameters.add(new BasicNameValuePair("TimeCreated", Long.toString(item.getTimeCollected())));
		postParameters.add(new BasicNameValuePair("Usable", item.getUsable() ? "1" : "0"));
		
		Log.d("Username", mUsername);
		Log.d("Item", item.getItem());
		Log.d("Time", Long.toString(item.getTimeCollected()));
		Log.d("Usable", Boolean.toString(item.getUsable()));

		WriteToRemoteAsyncTask insertInventoryAsyncTask = new WriteToRemoteAsyncTask(url, postParameters, getActivity());
		@SuppressWarnings("unused")
		WeakReference<WriteToRemoteAsyncTask>insertInventoryWeakReference = new WeakReference<WriteToRemoteAsyncTask>(insertInventoryAsyncTask);

		insertInventoryAsyncTask.execute();	
	}
}
