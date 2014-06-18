package com.pfhosa.sprinklecity.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;

import com.pfhosa.sprinklecity.R;

public class CreateCharacterHumanFragment extends Fragment {

	View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		inflater.inflate(R.layout.fragment_create_character_bottom, container, false);

		activateHumanCharacterGallery();
		activateBakerButtonListener();
		activatePostmanButtonListener();
		activateFarmerButtonListener();
		activateSocialRatingListener();
		activateAnimalRatingListener();
		activateBusinessRatingListener();

		return view;
	}

	public void activateHumanCharacterGallery() {
//TODO activate horizontal scroll view
	}

	public void activateBakerButtonListener() {
		ImageButton bakerButton = (ImageButton) view.findViewById(R.id.image_job_baker);

		bakerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


			}

		});

	}

	public void activatePostmanButtonListener() {
		ImageButton postmanButton = (ImageButton) view.findViewById(R.id.image_job_postman);		

		postmanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


			}

		});
	}

	public void activateFarmerButtonListener() {
		ImageButton farmerButton = (ImageButton) view.findViewById(R.id.image_job_farmer);

		farmerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


			}

		});
	}

	public void activateSocialRatingListener() {
		RatingBar socialRatingBar = (RatingBar) view.findViewById(R.id.rating_social);

		socialRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {


			}
		});

	}

	public void activateAnimalRatingListener() {
		RatingBar animalRatingBar = (RatingBar) view.findViewById(R.id.rating_animal_friendly);

		animalRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {


			}
		});
	}

	public void activateBusinessRatingListener() {
		RatingBar businessRatingBar = (RatingBar) view.findViewById(R.id.rating_business);

		businessRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {


			}
		});
	}
}