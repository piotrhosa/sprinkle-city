package com.pfhosa.sprinklecity.fragments;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.pfhosa.sprinklecity.database.CustomHttpClient;
import com.pfhosa.sprinklecity.database.Database;
import com.pfhosa.sprinklecity.model.HumanCharacter;

public class CreateCharacterHumanDetailsFragment extends Fragment {

	LinearLayout mLinearLayout;
	TextView mOverallTextView;
	OnHumanCharacterCreatedListener mAdvanceListener;
	Database mDb;

	String mJob, mName;
	int mAvatar, mSocialTrait, mAnimalTrait, mBusinessTrait, mMaxRating;	
	static final int OVERALL_STARS_LIMIT  = 10;

	HumanCharacter newHuman;

	@SuppressWarnings("unused")
	private WeakReference<CheckUsernameAvailabilityAsyncTask> usernameAvailabilityWeakReference;

	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mAdvanceListener = (OnHumanCharacterCreatedListener) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if(getArguments() != null)
			mAvatar = getArguments().getInt("avatar", mAvatar);

		mLinearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_create_character_human_details, container, false);		

		mDb = new Database(getActivity());

		activateCharacterName();
		activateBakerButtonListener();
		activatePostmanButtonListener();
		activateFarmerButtonListener();
		activateSocialRatingListener();
		activateAnimalRatingListener();
		activateBusinessRatingListener();
		activateOverallStarsUpdate();
		activateNextButtonListener();

		return mLinearLayout;
	}

	public void activateCharacterName() {
		final EditText characterName = (EditText) mLinearLayout.findViewById(R.id.edit_character_name);

		characterName.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mName = characterName.getText().toString();
			}
		});
	}
	public void activateBakerButtonListener() {
		final ImageButton bakerButton = (ImageButton) mLinearLayout.findViewById(R.id.image_job_baker);

		bakerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {mJob = "baker";}

		});

	}

	public void activatePostmanButtonListener() {
		ImageButton postmanButton = (ImageButton) mLinearLayout.findViewById(R.id.image_job_postman);		

		postmanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {mJob = "postman";}

		});
	}

	public void activateFarmerButtonListener() {
		ImageButton farmerButton = (ImageButton) mLinearLayout.findViewById(R.id.image_job_farmer);

		farmerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {mJob = "farmer";}

		});
	}

	public void activateSocialRatingListener() {
		final RatingBar socialRatingBar = (RatingBar) mLinearLayout.findViewById(R.id.rating_social);

		socialRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

				mMaxRating = OVERALL_STARS_LIMIT - mAnimalTrait - mBusinessTrait;

				if((int)socialRatingBar.getRating() <= mMaxRating) mSocialTrait = (int)socialRatingBar.getRating();
				if((int)socialRatingBar.getRating() > mMaxRating) {
					socialRatingBar.setRating(mMaxRating);
					mSocialTrait = mMaxRating;
				}

				updateOverallStarTextView();
			}
		});

	}

	public void activateAnimalRatingListener() {
		final RatingBar animalRatingBar = (RatingBar) mLinearLayout.findViewById(R.id.rating_animal_friendly);

		animalRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

				mMaxRating = OVERALL_STARS_LIMIT - mSocialTrait - mBusinessTrait;

				if((int)animalRatingBar.getRating() <= mMaxRating) mAnimalTrait = (int)animalRatingBar.getRating();
				if((int)animalRatingBar.getRating() > mMaxRating) {
					animalRatingBar.setRating(mMaxRating);
					mAnimalTrait = mMaxRating;
				}

				updateOverallStarTextView();
			}
		});
	}

	public void activateBusinessRatingListener() {
		final RatingBar businessRatingBar = (RatingBar) mLinearLayout.findViewById(R.id.rating_business);

		businessRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

				mMaxRating = OVERALL_STARS_LIMIT - mAnimalTrait - mSocialTrait;

				if((int)businessRatingBar.getRating() <= mMaxRating) mBusinessTrait = (int)businessRatingBar.getRating();
				if((int)businessRatingBar.getRating() > mMaxRating) {
					businessRatingBar.setRating(mMaxRating);
					mBusinessTrait = mMaxRating;
				}

				updateOverallStarTextView();
			}
		});
	}

	public void activateOverallStarsUpdate() {
		mOverallTextView = (TextView) mLinearLayout.findViewById(R.id.text_rating_signle_star);

		updateOverallStarTextView();		
	}

	public void activateNextButtonListener() {
		Button nextButton = (Button) mLinearLayout.findViewById(R.id.button_create_character_next);

		nextButton.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View v) {

				if(getOverallStars() < OVERALL_STARS_LIMIT)
					Toast.makeText(getActivity(), "There are still stars left!", Toast.LENGTH_SHORT).show();

				if(mDb.uniqueHumanCharacter(mName) 
						&& mJob != "" 
						&& getOverallStars() == OVERALL_STARS_LIMIT) {

					newHuman = new HumanCharacter(				
							mName,
							mAvatar,
							mJob,
							mSocialTrait,
							mAnimalTrait,
							mBusinessTrait);

					//mDb.newHumanCharacter(newHuman);	

					startNewUserAsyncTask(newHuman);

				}
			}

		});
	}

	public void startNewUserAsyncTask(HumanCharacter newHuman) {		
		CheckUsernameAvailabilityAsyncTask checkUsernameAT = new CheckUsernameAvailabilityAsyncTask(newHuman.getName());
		usernameAvailabilityWeakReference = new WeakReference<CheckUsernameAvailabilityAsyncTask>(checkUsernameAT);
		checkUsernameAT.execute();		
	}

	public void usernameIsAvailable() {mAdvanceListener.onHumanCharacterCreated(newHuman);}

	public void usernameIsNOTAvailable() {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getActivity(), "This username is taken. Please change it.", Toast.LENGTH_SHORT).show();

			}
		});
	}

	public class CheckUsernameAvailabilityAsyncTask extends AsyncTask<Void, Void, Void> {
		String name;

		public CheckUsernameAvailabilityAsyncTask(String name) {this.name = name;}
		
		@SuppressWarnings("unused")
		protected Void doInBackground(Void... params) {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

			postParameters.add(new BasicNameValuePair("Username", name));
			String response = null;

			try {
				response = CustomHttpClient.executeHttpPost("http://www2.macs.hw.ac.uk/~ph109/DBConnect/checkUsernameAvailability.php", postParameters);

				String result = response.toString();  

				try{
					JSONArray jArray = new JSONArray(result);		

					for(int i = 0; i < jArray.length(); ++i){
						JSONObject json_data = jArray.getJSONObject(i);
					}
					usernameIsNOTAvailable();
					Log.d("Username toast called", "yes");

				} catch(JSONException e){
					Log.e("log_tag", "Error parsing data "+ e.toString());
					usernameIsAvailable();
				}   
			} catch (Exception e) {Log.e("log_tag","Error in http connection!!" + e.toString());}  
			return null;
		}

		protected void onPostExecute(Void result) {this.cancel(true);}
	}

	// Other methods

	public void updateOverallStarTextView() {
		int overall = getOverallStars();

		String overallStars = overall + "/" + OVERALL_STARS_LIMIT;

		mOverallTextView.setText(overallStars);	

	}

	// Accessors

	public int getOverallStars() {return mSocialTrait + mAnimalTrait + mBusinessTrait;}

	// Interface that initializes replace in Transaction in CreateCharacterActivity
	public interface OnHumanCharacterCreatedListener {
		public void onHumanCharacterCreated(HumanCharacter humanCharacter);
	}
}