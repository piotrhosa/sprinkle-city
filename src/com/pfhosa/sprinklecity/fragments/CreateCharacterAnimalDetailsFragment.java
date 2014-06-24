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

	LinearLayout linearLayout;
	TextView overallTextView;
	OnAnimalCharacterCreatedListener progressListener;
	Database db;

	String characterName, name, owner;
	int avatar, sleep, fitness, maxRating;
	int overallStarsLimit = 7;

	public void onAttach(Activity activity) {
		super.onAttach(activity);

		progressListener = (OnAnimalCharacterCreatedListener) activity;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if(getArguments() != null) {
			characterName = getArguments().getString("characterName", characterName);
			avatar = getArguments().getInt("avatar", avatar);
			
			setOwner(characterName);
		}

		db = new Database(getActivity());

		linearLayout = (LinearLayout) inflater.inflate(R.layout.fragmnet_create_character_animal_details, container, false);		

		activateCharacterName();
		activateSleepRatingListener();
		activateFitnessRatingListener();
		activateOverallStarsUpdate();
		activateNextButtonListener();
		
		return linearLayout;
	}

	public void activateCharacterName() {
		final EditText characterName = (EditText) linearLayout.findViewById(R.id.edit_animal_character_name);

		characterName.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, 
					int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, 
					int before, int count) {
				setName(characterName.getText().toString());
			}
		});
	}

	public void activateSleepRatingListener() {
		final RatingBar sleepRatingBar = (RatingBar) linearLayout.findViewById(R.id.rating_sleep);

		sleepRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

				maxRating = overallStarsLimit - getFitness();

				if((int)sleepRatingBar.getRating() <= maxRating)
					setSleep((int)sleepRatingBar.getRating());
				if((int)sleepRatingBar.getRating() > maxRating) {
					sleepRatingBar.setRating(maxRating);
					setSleep(maxRating);
				}

				updateOverallStarTextView();
			}
		});

	}

	public void activateFitnessRatingListener() {
		final RatingBar fitnessRatingBar = (RatingBar) linearLayout.findViewById(R.id.rating_fitness);

		fitnessRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

				maxRating = overallStarsLimit - getSleep();

				if((int)fitnessRatingBar.getRating() <= maxRating)
					setFitness((int)fitnessRatingBar.getRating());
				if((int)fitnessRatingBar.getRating() > maxRating) {
					fitnessRatingBar.setRating(maxRating);
					setFitness(maxRating);
				}

				updateOverallStarTextView();
			}
		});

	}

	public void activateOverallStarsUpdate() {
		overallTextView = (TextView) linearLayout.findViewById(R.id.text_rating_signle_star_animal);

		updateOverallStarTextView();		
	}

	public void activateNextButtonListener() {
		Button nextButton = (Button) linearLayout.findViewById(R.id.button_create_animal_character_next);

		nextButton.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View v) {

				if(getOverallStars() < overallStarsLimit)
					Toast.makeText(getActivity(), "There are still stars left!", Toast.LENGTH_SHORT).show();

				if(db.uniqueAnimalCharacter(getName()) 
						&& getName() != "" 
						&& getOverallStars() == overallStarsLimit) {
					createNewAnimalCharacter();
					progressListener.onAnimalCharacterCreated();
				}
			}

		});
	}

	// Other methods

	public void updateOverallStarTextView() {
		int overall = getOverallStars();

		String overallStars = overall + "/" + overallStarsLimit;

		overallTextView.setText(overallStars);	

	}
	
	public void createNewAnimalCharacter() {
		AnimalCharacter newAnimal = new AnimalCharacter(
				getName(),
				getOwner(),
				getAvatar(),
				getSleep(),
				getFitness()
				);			


		db.newAnimalCharacter(newAnimal);
	}
	
	// Acessors
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public void setAvatar(int avatar) {
		this.avatar = avatar;
	}
	
	public void setSleep(int sleep) {
		this.sleep = sleep;
	}
	
	public void setFitness(int fitness) {
		this.fitness = fitness;
	}
	
	public String getName() {
		return name;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public int getAvatar() {
		return avatar;
	}
	
	public int getSleep() {
		return sleep;
	}
	
	public int getFitness() {
		return fitness;
	}
	
	public int getOverallStars() {
		return getSleep() + getFitness();
	}

	public interface OnAnimalCharacterCreatedListener {
		public void onAnimalCharacterCreated();
	}
}