package com.pfhosa.sprinklecity.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.fragments.VirtualMapFragment;

public class GameMapActivity extends FragmentActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_map);

		if (findViewById(R.id.fragment_container_game_map) != null) {

			if (savedInstanceState != null) {
				return;
			}

			VirtualMapFragment virtualMapFragment = new VirtualMapFragment();

			getSupportFragmentManager().beginTransaction()
			.add(R.id.fragment_container_game_map, virtualMapFragment).commit();
		}

	}

}
