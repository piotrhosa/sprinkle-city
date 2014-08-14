package com.pfhosa.sprinklecity.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RatingBar.OnRatingBarChangeListener;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.database.Database;
import com.pfhosa.sprinklecity.model.AnimalCharacter;

public class CreateCharacterAnimalDetailsFragment extends Fragment {

	LinearLayout mLinearLayout;
	TextView mOverallTextView;
	OnAnimalCharacterCreatedListener mAdvanceListener;
	Database mDb;

	String mCharacterName, mName, mOwner;
	int mAvatar, mSleep, mFitness, mMaxRating;
	static final int OVERALL_STARS_LIMIT = 7;

	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mAdvanceListener = (OnAnimalCharacterCreatedListener) activity;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if(getArguments() != null) {
			mCharacterName = getArguments().getString("mCharacterName", mCharacterName);
			mAvatar = getArguments().getInt("avatar", mAvatar);
			
			mOwner = mCharacterName;
		}

		mDb = new Database(getActivity());

		mLinearLayout = (LinearLayout) inflater.inflate(R.layout.fragmnet_create_character_animal_details, container, false);		

		activateCharacterName();
		activateSleepRatingListener();
		activateFitnessRatingListener();
		activateOverallStarsUpdate();
		activateNextButtonListener();
		
		return mLinearLayout;
	}

	public void activateCharacterName() {
		final EditText characterName = (EditText) mLinearLayout.findViewById(R.id.edit_animal_character_name);

		characterName.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mName = characterName.getText().toString();
			}
		});
	}

	public void activateSleepRatingListener() {
		final RatingBar sleepRatingBar = (RatingBar) mLinearLayout.findViewById(R.id.rating_sleep);

		sleepRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

				mMaxRating = OVERALL_STARS_LIMIT - mFitness;

				if((int)sleepRatingBar.getRating() <= mMaxRating) mSleep = (int)sleepRatingBar.getRating();
				if((int)sleepRatingBar.getRating() > mMaxRating) {
					sleepRatingBar.setRating(mMaxRating);
					mSleep = mMaxRating;
				}

				updateOverallStarTextView();
			}
		});

	}

	public void activateFitnessRatingListener() {
		final RatingBar fitnessRatingBar = (RatingBar) mLinearLayout.findViewById(R.id.rating_fitness);

		fitnessRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

				mMaxRating = OVERALL_STARS_LIMIT - mSleep;

				if((int)fitnessRatingBar.getRating() <= mMaxRating) mFitness = (int)fitnessRatingBar.getRating();
				if((int)fitnessRatingBar.getRating() > mMaxRating) {
					fitnessRatingBar.setRating(mMaxRating);
					mFitness = mMaxRating;
				}

				updateOverallStarTextView();
			}
		});

	}

	public void activateOverallStarsUpdate() {
		mOverallTextView = (TextView) mLinearLayout.findViewById(R.id.text_rating_signle_star_animal);

		updateOverallStarTextView();		
	}

	public void activateNextButtonListener() {
		Button nextButton = (Button) mLinearLayout.findViewById(R.id.button_create_animal_character_next);

		nextButton.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View v) {

				if(getOverallStars() < OVERALL_STARS_LIMIT)
					Toast.makeText(getActivity(), "There are still stars left!", Toast.LENGTH_SHORT).show();

				if(mDb.uniqueAnimalCharacter(mName) 
						&& mName != "" 
						&& getOverallStars() == OVERALL_STARS_LIMIT) {

					AnimalCharacter newAnimal = new AnimalCharacter(
							mName,
							mAvatar,
							mSleep,
							mFitness
							);			
					
					//mDb.newAnimalCharacter(newAnimal);
					mAdvanceListener.onAnimalCharacterCreated(newAnimal);
				}
			}

		});
	}

	// Other methods

	public void updateOverallStarTextView() {
		int overall = getOverallStars();

		String overallStars = overall + "/" + OVERALL_STARS_LIMIT;

		mOverallTextView.setText(overallStars);	

	}
	
	// Acessors
	
	public int getOverallStars() {
		return mSleep + mFitness;
	}

	public interface OnAnimalCharacterCreatedListener {
		public void onAnimalCharacterCreated(AnimalCharacter animalCharacter);
	}
}