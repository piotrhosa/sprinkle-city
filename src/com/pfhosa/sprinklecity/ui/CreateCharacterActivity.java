package com.pfhosa.sprinklecity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.fragments.CreateCharacterAnimalAvatarFragment;
import com.pfhosa.sprinklecity.fragments.CreateCharacterAnimalAvatarFragment.OnAnimalAvatarSelectedListener;
import com.pfhosa.sprinklecity.fragments.CreateCharacterAnimalDetailsFragment;
import com.pfhosa.sprinklecity.fragments.CreateCharacterAnimalDetailsFragment.OnAnimalCharacterCreatedListener;
import com.pfhosa.sprinklecity.fragments.CreateCharacterHumanAvatarFragment;
import com.pfhosa.sprinklecity.fragments.CreateCharacterHumanAvatarFragment.OnHumanAvatarSelectedListener;
import com.pfhosa.sprinklecity.fragments.CreateCharacterHumanDetailsFragment;
import com.pfhosa.sprinklecity.fragments.CreateCharacterHumanDetailsFragment.OnHumanCharacterCreatedListener;
import com.pfhosa.sprinklecity.fragments.CreateCharacterPasswordFragment;
import com.pfhosa.sprinklecity.model.AnimalCharacter;
import com.pfhosa.sprinklecity.model.HumanCharacter;

public class CreateCharacterActivity extends FragmentActivity implements 
OnHumanAvatarSelectedListener, OnHumanCharacterCreatedListener,
OnAnimalAvatarSelectedListener, OnAnimalCharacterCreatedListener {
	
	HumanCharacter mHumanCharacter;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_character);

		if (findViewById(R.id.fragment_container_create_character) != null) {

			if (savedInstanceState != null) return;

			CreateCharacterHumanAvatarFragment humanAvatarFragment = new CreateCharacterHumanAvatarFragment();

			getSupportFragmentManager().beginTransaction()
			.add(R.id.fragment_container_create_character, humanAvatarFragment).commit();
		}

	}
	
	public void onPause() {
		this.finish();
		super.onPause();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_screen, menu);
		return false; //disable menu
	}

	public void onHumanAvatarSelected(int avatar) {
		CreateCharacterHumanDetailsFragment humanDetailsFragment = new CreateCharacterHumanDetailsFragment();

		Bundle avatarBundle = new Bundle();
		avatarBundle.putInt("avatar", avatar);
		humanDetailsFragment.setArguments(avatarBundle);

		getSupportFragmentManager().beginTransaction()
		.setCustomAnimations(R.anim.anim_slide_in, R.anim.anim_slie_out)
		.replace(R.id.fragment_container_create_character, humanDetailsFragment)
		.addToBackStack(null)
		.commit();

	}

	@Override
	public void onHumanCharacterCreated(HumanCharacter mHumanCharacter) {
		CreateCharacterAnimalAvatarFragment animalAvatarFragment = new CreateCharacterAnimalAvatarFragment();
		
		this.mHumanCharacter = mHumanCharacter;

		Intent mHumanCharacterIntent = new Intent();
		mHumanCharacterIntent.putExtra("mHumanCharacter", mHumanCharacter);
		
		Bundle characterNameBundle = new Bundle();
		characterNameBundle = mHumanCharacterIntent.getExtras();
		animalAvatarFragment.setArguments(characterNameBundle);

		getSupportFragmentManager().beginTransaction()
		.setCustomAnimations(R.anim.anim_slide_in, R.anim.anim_slie_out)
		.replace(R.id.fragment_container_create_character, animalAvatarFragment)
		.addToBackStack(null)
		.commit();
	}

	@Override
	public void onAnimalAvatarSelected(String characterName, int avatar) {
		CreateCharacterAnimalDetailsFragment animalDetailsFragment = new CreateCharacterAnimalDetailsFragment();

		Bundle avatarBundle = new Bundle();
		avatarBundle.putInt("avatar", avatar);
		avatarBundle.putString("characterName", characterName);
		animalDetailsFragment.setArguments(avatarBundle);

		getSupportFragmentManager().beginTransaction()
		.setCustomAnimations(R.anim.anim_slide_in, R.anim.anim_slie_out)
		.replace(R.id.fragment_container_create_character, animalDetailsFragment)
		.addToBackStack(null)
		.commit();
	}

	@Override
	public void onAnimalCharacterCreated(AnimalCharacter animalCharacter) {
		CreateCharacterPasswordFragment passwordFragment = new CreateCharacterPasswordFragment();

		/**
		 * Bundle does not like Parcelable and that is why Parcelable is put into Intent
		 * and only then Bundle gets extras from Intent.
		 */
		Intent passwordIntent = new Intent();
		passwordIntent.putExtra("animalCharacter", animalCharacter);
		passwordIntent.putExtra("humanCharacter", mHumanCharacter);

		Bundle passwordBundle = new Bundle();
		passwordBundle = passwordIntent.getExtras();
		passwordFragment.setArguments(passwordBundle);

		getSupportFragmentManager().beginTransaction()
		.setCustomAnimations(R.anim.anim_slide_in, R.anim.anim_slie_out)
		.replace(R.id.fragment_container_create_character, passwordFragment)
		.addToBackStack(null)
		.commit();
	}
}
