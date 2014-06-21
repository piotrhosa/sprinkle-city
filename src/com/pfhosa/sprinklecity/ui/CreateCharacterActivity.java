package com.pfhosa.sprinklecity.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.fragments.CreateCharacterHumanAvatarFragment;
import com.pfhosa.sprinklecity.fragments.CreateCharacterHumanAvatarFragment.OnAvatarSelectedListener;
import com.pfhosa.sprinklecity.fragments.CreateCharacterHumanDetailsFragment;

public class CreateCharacterActivity extends FragmentActivity implements OnAvatarSelectedListener {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_character);

		if (findViewById(R.id.fragment_container_create_character) != null) {

			if (savedInstanceState != null) {
				return;
			}

			CreateCharacterHumanAvatarFragment humanAvatarFragment = new CreateCharacterHumanAvatarFragment();

			getSupportFragmentManager().beginTransaction()
			.add(R.id.fragment_container_create_character, humanAvatarFragment).commit();
		}

	}

	public void loadNewFragment() {

		CreateCharacterHumanDetailsFragment bottomFragment = new CreateCharacterHumanDetailsFragment();

		getSupportFragmentManager().beginTransaction()
		.add(R.id.fragment_container_create_character, bottomFragment).commit();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_screen, menu);
		return false; //disable menu
	}

	@Override
	public void onAvatarSelected(int avatar) {
		
		Bundle avatarBundle = new Bundle();
		avatarBundle.putInt("avatar", avatar);
		
		CreateCharacterHumanDetailsFragment humanFragment = new CreateCharacterHumanDetailsFragment();

		humanFragment.setArguments(avatarBundle);
		
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.fragment_container_create_character, humanFragment)
		.addToBackStack(null).commit();

	}

}
