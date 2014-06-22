package com.pfhosa.sprinklecity.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.fragments.CreateCharacterAnimalAvatarFragment;
import com.pfhosa.sprinklecity.fragments.CreateCharacterHumanAvatarFragment;
import com.pfhosa.sprinklecity.fragments.CreateCharacterHumanAvatarFragment.OnHumanAvatarSelectedListener;
import com.pfhosa.sprinklecity.fragments.CreateCharacterHumanDetailsFragment;
import com.pfhosa.sprinklecity.fragments.CreateCharacterHumanDetailsFragment.OnHumanCharacterCreated;

public class CreateCharacterActivity 
			extends FragmentActivity 
			implements OnHumanAvatarSelectedListener, OnHumanCharacterCreated {

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
	public void onHumanAvatarSelected(int avatar) {

		Bundle avatarBundle = new Bundle();
		avatarBundle.putInt("avatar", avatar);

		CreateCharacterHumanDetailsFragment humanDetailsFragment = new CreateCharacterHumanDetailsFragment();

		humanDetailsFragment.setArguments(avatarBundle);

		getSupportFragmentManager().beginTransaction()
		.setCustomAnimations(R.anim.anim_slide_in, R.anim.anim_slie_out)
		.replace(R.id.fragment_container_create_character, humanDetailsFragment)
		.addToBackStack(null)
		.commit();

	}

	@Override
	public void onHumanCharacterCreated() {

		CreateCharacterAnimalAvatarFragment animalAvatarFragment = new CreateCharacterAnimalAvatarFragment();

		animalAvatarFragment.setArguments(null);

		getSupportFragmentManager().beginTransaction()
		.setCustomAnimations(R.anim.anim_slide_in, R.anim.anim_slie_out)
		.replace(R.id.fragment_container_create_character, animalAvatarFragment)
		.addToBackStack(null)
		.commit();

	}

}
