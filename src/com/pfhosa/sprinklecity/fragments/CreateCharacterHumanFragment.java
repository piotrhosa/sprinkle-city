package com.pfhosa.sprinklecity.fragments;

import java.util.ArrayList;

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

public class CreateCharacterHumanFragment extends Fragment {

	LinearLayout linearLayout;
	TextView overallTextView;

	String job, name;
	int avatar, socialTrait, animalTrait, businessTrait; 
	int overallStarsLimit = 10, maxRating;

	ArrayList<Integer> characterList = new ArrayList<Integer>() {

		private static final long serialVersionUID = 1L;

		{
			add(R.id.image_character_human);
			add(R.id.image_character_human_1);
			add(R.id.image_character_human_2);			
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_create_character_bottom, container, false);		

		activateHumanCharacterGallery();
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



	public void activateHumanCharacterGallery() {
		ImageButton character0Button = (ImageButton) linearLayout.findViewById(R.id.image_character_human);
		ImageButton character1Button = (ImageButton) linearLayout.findViewById(R.id.image_character_human_1);
		ImageButton character2Button = (ImageButton) linearLayout.findViewById(R.id.image_character_human_2);

		character0Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setAvatar(0);
			}
		});

		character1Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setAvatar(1);
			}
		});

		character2Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setAvatar(2);
			}
		});
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

				updateOverallTextView();
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

				updateOverallTextView();
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

				updateOverallTextView();
			}
		});
	}

	public void activateOverallStarsUpdate() {
		overallTextView = (TextView) linearLayout.findViewById(R.id.text_rating_signle_star);

		updateOverallTextView();		
	}

	public void activateNextButtonListener() {
		Button nextButton = (Button) linearLayout.findViewById(R.id.button_create_character_next);

		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HumanCharacter newHuman = new HumanCharacter(
						getAvatar(),
						getCharacterName(),
						getJob(),
						getSocialTrait(),
						getAnimalTrait(),
						getBusinessTrait());

				Database db = new Database(getActivity());

				db.newHumanCharacter(newHuman);	

				Toast.makeText(getActivity(), db.getHumanCharacter(), Toast.LENGTH_SHORT).show();


			}

		});
	}

	// Other methods

	public void updateOverallTextView() {
		int overall = getOverallStars();

		String overallStars = overall + "/" + overallStarsLimit;

		overallTextView.setText(overallStars);	

	}


	// Accessors

	public void setAvatar(int avatar) {
		this.avatar = avatar;
	}

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
}