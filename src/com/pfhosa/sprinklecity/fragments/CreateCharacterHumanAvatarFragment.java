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
	OnAvatarSelectedListener mListener;

	int avatar;

	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mListener = (OnAvatarSelectedListener) activity;
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
				setAvatar(0);
				mListener.onAvatarSelected(getAvatar());
			}
		});

		character1Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setAvatar(1);
				mListener.onAvatarSelected(getAvatar());
			}
		});

		character2Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setAvatar(2);
				mListener.onAvatarSelected(getAvatar());
			}
		});
	}	

	// Accessors

	public void setAvatar(int avatar) {
		this.avatar = avatar;
	}

	public int getAvatar() {
		return avatar;
	}

	public interface OnAvatarSelectedListener {
		public void onAvatarSelected(int avatar);
	}

}
