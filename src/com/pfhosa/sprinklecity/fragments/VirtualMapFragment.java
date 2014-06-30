package com.pfhosa.sprinklecity.fragments;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.location.LocationReceiver;
import com.pfhosa.sprinklecity.location.LocationTracker;

public class VirtualMapFragment extends Fragment {
	
	LinearLayout linearLayout;
	TextView distanceTextView;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		getActivity().registerReceiver(new LocationReceiver(), new IntentFilter("locationUpdater"));

		linearLayout = (LinearLayout)inflater.inflate(R.layout.fragment_virtual_map, container, false);
		
		LocationTracker locationTracker = new LocationTracker(getActivity());
		
		locationTracker.getInitialLocation();
		
		distanceTextView = (TextView)getActivity().findViewById(R.id.text_virtual_map_distance);		

		return linearLayout;
	}

}
