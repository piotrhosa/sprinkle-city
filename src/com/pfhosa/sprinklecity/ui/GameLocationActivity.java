package com.pfhosa.sprinklecity.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.virtuallocationfragments.LocationBakeryFragment;
import com.pfhosa.sprinklecity.virtuallocationfragments.LocationFarmFragment;
import com.pfhosa.sprinklecity.virtuallocationfragments.LocationParkFragment;

public class GameLocationActivity extends FragmentActivity {	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_game_location);	

		Bundle data = getIntent().getExtras();
		String fragment = data.getString("Fragment");

		if (findViewById(R.id.fragment_container_game_location) != null) {
			if (savedInstanceState != null) return;	
			fragmentSelector(fragment, data);
		}
	}

	private void fragmentSelector(String fragment, Bundle data) {
		switch(fragment) {
		case "farmer": openFarm(data); break;
		case "baker": openBakery(data); break;
		case "park": openPark(data); break;
		}
	}
	
	private void openFarm(Bundle data) {
		LocationFarmFragment farmFragment = new LocationFarmFragment();
		farmFragment.setArguments(data);		
		getSupportFragmentManager().beginTransaction()
		.add(R.id.fragment_container_game_location, farmFragment)
		.commit();
	}
	
	private void openBakery(Bundle data) {
		LocationBakeryFragment bakeryFragment = new LocationBakeryFragment();
		bakeryFragment.setArguments(data);
		getSupportFragmentManager().beginTransaction()
		.add(R.id.fragment_container_game_location, bakeryFragment)
		.commit();
	}
	
	private void openPark(Bundle data) {
		LocationParkFragment parkFragment = new LocationParkFragment();
		parkFragment.setArguments(data);
		getSupportFragmentManager().beginTransaction()
		.add(R.id.fragment_container_game_location, parkFragment)
		.commit();
	}

}
