package com.pfhosa.sprinklecity.virtuallocationfragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.pfhosa.sprinklecity.R;

public class LocationBakeryFragment extends Fragment {

	LinearLayout mLinearLayout;
	String mUsername;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(getArguments() != null) mUsername = getArguments().getString("Username");

		mLinearLayout = (LinearLayout)inflater.inflate(R.layout.fragment_location_bakery, container, false);

		initializeButtons();
		
		return mLinearLayout;
	}
	
	private void initializeButtons() {
		ImageButton cupcake0 = (ImageButton) mLinearLayout.findViewById(R.id.image_cupcake_0);
		ImageButton cupcake1 = (ImageButton) mLinearLayout.findViewById(R.id.image_cupcake_1);
		ImageButton cupcake2 = (ImageButton) mLinearLayout.findViewById(R.id.image_cupcake_2);
		
		cupcake0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		
		cupcake1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		
		cupcake2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
	}

}
