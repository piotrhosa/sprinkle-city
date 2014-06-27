	package com.pfhosa.sprinklecity.fragments;

import com.pfhosa.sprinklecity.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;

public class CreateCharacterAnimalAvatarFragment extends Fragment {

	HorizontalScrollView horizontalScrollView;
	OnAnimalAvatarSelectedListener advanceListener;
	
	int avatar;
	String characterName;

	public void onAttach (Activity activity) {
		super.onAttach(activity);

		advanceListener = (OnAnimalAvatarSelectedListener) activity;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		if(getArguments() != null)
			characterName = getArguments().getString("characterName", characterName);

		horizontalScrollView = (HorizontalScrollView) inflater.inflate(R.layout.fragment_create_character_animal_avatar, container, false);

		activateAnimalCharacterGallery();

		return horizontalScrollView ;
	}

	public void activateAnimalCharacterGallery() {
		ImageButton character0Button = (ImageButton) horizontalScrollView.findViewById(R.id.image_character_animal_0);
		ImageButton character1Button = (ImageButton) horizontalScrollView.findViewById(R.id.image_character_animal_1);
		ImageButton character2Button = (ImageButton) horizontalScrollView.findViewById(R.id.image_character_animal_2);
		ImageButton character3Button = (ImageButton) horizontalScrollView.findViewById(R.id.image_character_animal_3);

		character0Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				advanceListener.onAnimalAvatarSelected(characterName, 0);
			}
		});

		character1Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				advanceListener.onAnimalAvatarSelected(characterName, 1);
			}
		});

		character2Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				advanceListener.onAnimalAvatarSelected(characterName, 2);
			}
		});

		character3Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				advanceListener.onAnimalAvatarSelected(characterName, 3);
			}
		});
	}	
	
	public interface OnAnimalAvatarSelectedListener {
		public void onAnimalAvatarSelected(String characterName, int avatar);
	}
}