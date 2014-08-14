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

	int mAvatar;
	String mCharacterName;
	
	HorizontalScrollView mHorizontalScrollView;
	OnAnimalAvatarSelectedListener mAdvanceListener;

	public void onAttach (Activity activity) {
		super.onAttach(activity);

		mAdvanceListener = (OnAnimalAvatarSelectedListener) activity;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		if(getArguments() != null)
			mCharacterName = getArguments().getString("mCharacterName", mCharacterName);

		mHorizontalScrollView = (HorizontalScrollView) inflater.inflate(R.layout.fragment_create_character_animal_avatar, container, false);

		activateAnimalCharacterGallery();

		return mHorizontalScrollView ;
	}

	public void activateAnimalCharacterGallery() {
		ImageButton character0Button = (ImageButton) mHorizontalScrollView.findViewById(R.id.image_character_animal_0);
		ImageButton character1Button = (ImageButton) mHorizontalScrollView.findViewById(R.id.image_character_animal_1);
		ImageButton character2Button = (ImageButton) mHorizontalScrollView.findViewById(R.id.image_character_animal_2);
		ImageButton character3Button = (ImageButton) mHorizontalScrollView.findViewById(R.id.image_character_animal_3);

		character0Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAdvanceListener.onAnimalAvatarSelected(mCharacterName, R.drawable.character_animal_0);
			}
		});

		character1Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAdvanceListener.onAnimalAvatarSelected(mCharacterName, R.drawable.character_animal_1);
			}
		});

		character2Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAdvanceListener.onAnimalAvatarSelected(mCharacterName, R.drawable.character_animal_2);
			}
		});

		character3Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAdvanceListener.onAnimalAvatarSelected(mCharacterName, R.drawable.character_animal_3);
			}
		});
	}	
	
	public interface OnAnimalAvatarSelectedListener {public void onAnimalAvatarSelected(String mCharacterName, int mAvatar);}
}