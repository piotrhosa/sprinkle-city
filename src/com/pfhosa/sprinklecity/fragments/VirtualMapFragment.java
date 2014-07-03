package com.pfhosa.sprinklecity.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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

		linearLayout = (LinearLayout)inflater.inflate(R.layout.fragment_virtual_map, container, false);

		distanceTextView = (TextView)linearLayout.findViewById(R.id.text_virtual_map_distance);		

		// Register BroadcastReceiver
		getActivity().registerReceiver(new LocationReceiver(), new IntentFilter("newLocation"));

		return linearLayout;
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
			distanceTextView.setText(Float.toString(distance));
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


}
