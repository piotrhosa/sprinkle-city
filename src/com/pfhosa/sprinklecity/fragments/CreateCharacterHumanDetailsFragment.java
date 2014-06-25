package com.pfhosa.sprinklecity.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.database.Database;
import com.pfhosa.sprinklecity.model.HumanCharacter;

public class CreateCharacterHumanDetailsFragment extends Fragment {

	LinearLayout linearLayout;
	TextView overallTextView;
	OnHumanCharacterCreatedListener advanceListener;
	Database db;

	String job, name;
	int avatar, socialTrait, animalTrait, businessTrait; 
	int overallStarsLimit = 10, maxRating;
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		advanceListener = (OnHumanCharacterCreatedListener) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if(getArguments() != null)
			avatar = getArguments().getInt("avatar", avatar);

		linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_create_character_human_details, container, false);		
		
		db = new Database(getActivity());
		
		activateCharacterName();
		activateBakerButtonListener();
		activatePostmanButtonListener();
		activateFarmerButtonListener();
		activateSocialRatingListener();
		activateAnimalRatingListener();
		activateBusinessRatingListener();
		activateOverallStarsUpdate();
		activateNextButtonListener();

		return linearLayout;
	}

	public void activateCharacterName() {
		final EditText characterName = (EditText) linearLayout.findViewById(R.id.edit_character_name);

		characterName.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, 
					int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, 
					int before, int count) {
				setCharacterName(characterName.getText().toString());
			}
		});
	}
	public void activateBakerButtonListener() {
		ImageButton bakerButton = (ImageButton) linearLayout.findViewById(R.id.image_job_baker);

		bakerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				setJob("baker");
			}

		});

	}

	public void activatePostmanButtonListener() {
		ImageButton postmanButton = (ImageButton) linearLayout.findViewById(R.id.image_job_postman);		

		postmanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				setJob("postman");
			}

		});
	}

	public void activateFarmerButtonListener() {
		ImageButton farmerButton = (ImageButton) linearLayout.findViewById(R.id.image_job_farmer);

		farmerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				setJob("farmer");
			}

		});
	}

	public void activateSocialRatingListener() {
		final RatingBar socialRatingBar = (RatingBar) linearLayout.findViewById(R.id.rating_social);

		socialRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

				maxRating = overallStarsLimit - getAnimalTrait() - getBusinessTrait();

				if((int)socialRatingBar.getRating() <= maxRating)
					setSocialTrait((int)socialRatingBar.getRating());
				if((int)socialRatingBar.getRating() > maxRating) {
					socialRatingBar.setRating(maxRating);
					setSocialTrait(maxRating);
				}

				updateOverallStarTextView();
			}
		});

	}

	public void activateAnimalRatingListener() {
		final RatingBar animalRatingBar = (RatingBar) linearLayout.findViewById(R.id.rating_animal_friendly);

		animalRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

				maxRating = overallStarsLimit - getSocialTrait() - getBusinessTrait();

				if((int)animalRatingBar.getRating() <= maxRating)
					setAnimalTrait((int)animalRatingBar.getRating());
				if((int)animalRatingBar.getRating() > maxRating) {
					animalRatingBar.setRating(maxRating);
					setAnimalTrait(maxRating);
				}

				updateOverallStarTextView();
			}
		});
	}

	public void activateBusinessRatingListener() {
		final RatingBar businessRatingBar = (RatingBar) linearLayout.findViewById(R.id.rating_business);

		businessRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

				maxRating = overallStarsLimit - getAnimalTrait() - getSocialTrait();

				if((int)businessRatingBar.getRating() <= maxRating)
					setBusinessTrait((int)businessRatingBar.getRating());
				if((int)businessRatingBar.getRating() > maxRating) {
					businessRatingBar.setRating(maxRating);
					setBusinessTrait(maxRating);
				}

				updateOverallStarTextView();
			}
		});
	}

	public void activateOverallStarsUpdate() {
		overallTextView = (TextView) linearLayout.findViewById(R.id.text_rating_signle_star);

		updateOverallStarTextView();		
	}

	public void activateNextButtonListener() {
		Button nextButton = (Button) linearLayout.findViewById(R.id.button_create_character_next);

		nextButton.setOnClickListener(new OnClickListener() {
			

			@Override
			public void onClick(View v) {
				
				if(getOverallStars() < overallStarsLimit)
					Toast.makeText(getActivity(), "There are still stars left!", Toast.LENGTH_SHORT).show();
			
				if(db.uniqueHumanCharacter(getCharacterName()) 
						&& getJob() != "" 
						&& getOverallStars() == overallStarsLimit)
					createNewHumanCharacter();
				
				advanceListener.onHumanCharacterCreated(getCharacterName());
			}

		});
	}

	// Other methods

	public void updateOverallStarTextView() {
		int overall = getOverallStars();

		String overallStars = overall + "/" + overallStarsLimit;

		overallTextView.setText(overallStars);	

	}
	
	public void createNewHumanCharacter() {
		HumanCharacter newHuman = new HumanCharacter(				
				getCharacterName(),
				getJob(),
				getAvatar(),
				getSocialTrait(),
				getAnimalTrait(),
				getBusinessTrait());
		
		db.newHumanCharacter(newHuman);	

		Toast.makeText(getActivity(), db.getHumanCharacter(), Toast.LENGTH_SHORT).show();
	}
	

	// Accessors
	public void setCharacterName(String name) {
		this.name = name;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public void setSocialTrait(int socialTrait) {
		this.socialTrait = socialTrait;
	}

	public void setAnimalTrait(int animalTrait) {
		this.animalTrait = animalTrait;
	}

	public void setBusinessTrait(int businessTrait) {
		this.businessTrait = businessTrait;
	}

	public int getAvatar() {
		return avatar;
	}
	
	public String getCharacterName() {
		return name;
	}

	public String getJob() {
		return job;
	}

	public int getSocialTrait() {
		return socialTrait;
	}

	public int getAnimalTrait() {
		return animalTrait;
	}

	public int getBusinessTrait() {
		return businessTrait;
	}

	public int getOverallStars() {
		return getSocialTrait() + getAnimalTrait() + getBusinessTrait();
	}
	
	// Interface that initializes replace in Transaction in CreateCharacterActivity
	public interface OnHumanCharacterCreatedListener {
		public void onHumanCharacterCreated(String characterName);
	}
}