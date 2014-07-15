package com.pfhosa.sprinklecity.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;

import com.pfhosa.sprinklecity.R;

public class CreateCharacterHumanAvatarFragment extends Fragment {

	HorizontalScrollView horizontalScrollView;
	OnHumanAvatarSelectedListener advanceListener;

	int avatar;

	public void onAttach(Activity activity) {
		super.onAttach(activity);

		advanceListener = (OnHumanAvatarSelectedListener) activity;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		horizontalScrollView = (HorizontalScrollView) inflater.inflate(R.layout.fragment_create_character_human_avatar, container, false);

		activateHumanCharacterGallery();

		return horizontalScrollView ;
	}

	public void activateHumanCharacterGallery() {
		ImageButton character0Button = (ImageButton) horizontalScrollView.findViewById(R.id.image_character_human_0);
		ImageButton character1Button = (ImageButton) horizontalScrollView.findViewById(R.id.image_character_human_1);
		ImageButton character2Button = (ImageButton) horizontalScrollView.findViewById(R.id.image_character_human_2);

		character0Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				advanceListener.onHumanAvatarSelected(R.drawable.character_human_0);
			}
		});

		character1Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				advanceListener.onHumanAvatarSelected(R.drawable.character_human_1);
			}
		});

		character2Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				advanceListener.onHumanAvatarSelected(R.drawable.character_human_2);
			}
		});
	}	

	// Interface to pass avatar to Activity

	public interface OnHumanAvatarSelectedListener {
		public void onHumanAvatarSelected(int avatar);
	}

}
