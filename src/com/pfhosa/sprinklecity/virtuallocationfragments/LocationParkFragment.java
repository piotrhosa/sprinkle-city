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

public class LocationParkFragment extends Fragment implements SensorEventListener {
	
	private final float NOISE = (float)1.5;	
	
	LinearLayout mLinearLayout;
	ProgressBar mPickingProgressBar;
	TextView mProgressTextView;

	private String mUsername;
	private float mLastX;
	private boolean mInitialized, mItemCreated;
	private int mProgressStatus, mCounter;

	private SensorManager mSensorManager; 
	private Sensor mAccelerometer; 

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(getArguments() != null) mUsername = getArguments().getString("Username");

		mLinearLayout = (LinearLayout)inflater.inflate(R.layout.fragment_location_park, container, false);

		mProgressStatus = 0;

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
		float x = event.values[0];

		if (!mInitialized) {
			mLastX = x;

			mInitialized = true;
		} 
		else {

			float deltaX = Math.abs(mLastX - x);
			if (deltaX < NOISE) deltaX = (float)0.0;
			else {
				++mCounter;
				mLastX = x;

				if(mProgressStatus < 100)
					mProgressStatus = (mProgressStatus + (int)(deltaX*0.2));
				else
					mProgressStatus = 100;

				mPickingProgressBar.setProgress(mProgressStatus);
				mProgressTextView.setText(mProgressStatus + "/" + mPickingProgressBar.getMax());

				if(mProgressStatus >= 100 && !mItemCreated) {
					getActivity().onBackPressed();
					InventoryItem fetch = new InventoryItem(mUsername, "fetch" + Integer.toString(mCounter), 1);
					insertItemInRemote(fetch);
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
		Log.d("Username", mUsername);
		Log.d("Item", item.getItem());
		Log.d("Time", Long.toString(item.getTimeCollected()));

		WriteToRemoteAsyncTask insertInventoryAsyncTask = new WriteToRemoteAsyncTask(url, postParameters, getActivity());
		@SuppressWarnings("unused")
		WeakReference<WriteToRemoteAsyncTask>insertInventoryWeakReference = new WeakReference<WriteToRemoteAsyncTask>(insertInventoryAsyncTask);

		insertInventoryAsyncTask.execute();	
	}

}
