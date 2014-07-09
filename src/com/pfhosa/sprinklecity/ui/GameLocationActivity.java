package com.pfhosa.sprinklecity.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;

import com.pfhosa.sprinklecity.R;

public class GameLocationActivity extends FragmentActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_game_location);
		
		
		
		if (findViewById(R.id.fragment_container_create_character) != null) {

			if (savedInstanceState != null) {
				return;
			}

			//CreateCharacterHumanAvatarFragment humanAvatarFragment = new CreateCharacterHumanAvatarFragment();

			//getSupportFragmentManager().beginTransaction()
			//.add(R.id.fragment_container_create_character, humanAvatarFragment).commit();
		}


	}

}
