package com.pfhosa.sprinklecity.virtuallocationfragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.pfhosa.sprinklecity.R;

public class LocationBakeryFragment extends Fragment {

	LinearLayout linearLayout;
	String username;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(getArguments() != null) 
			username = getArguments().getString("Username");

		linearLayout = (LinearLayout)inflater.inflate(R.layout.fragment_location_bakery, container, false);

		return linearLayout;
	}

}
