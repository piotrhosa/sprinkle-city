package com.pfhosa.sprinklecity.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.fragments.VirtualLocationFragment;

public class GameLocationActivity extends FragmentActivity {	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_game_location);		
		
		if (findViewById(R.id.fragment_container_game_location) != null) {

			if (savedInstanceState != null)
				return;			

			VirtualLocationFragment virtualLocationFragment = new VirtualLocationFragment();

			getSupportFragmentManager().beginTransaction()
			.add(R.id.fragment_container_game_location, virtualLocationFragment).commit();
		}


	}

}
