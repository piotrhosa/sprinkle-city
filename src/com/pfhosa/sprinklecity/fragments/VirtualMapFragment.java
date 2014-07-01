package com.pfhosa.sprinklecity.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pfhosa.sprinklecity.R;

public class VirtualMapFragment extends Fragment {
	
	LinearLayout linearLayout;
	TextView distanceTextView;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		linearLayout = (LinearLayout)inflater.inflate(R.layout.fragment_virtual_map, container, false);
		
		distanceTextView = (TextView)getActivity().findViewById(R.id.text_virtual_map_distance);		

		return linearLayout;
	}

}
