package com.pfhosa.sprinklecity.fragments;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.database.WriteToRemoteAsyncTask;
import com.pfhosa.sprinklecity.model.AnimalCharacter;
import com.pfhosa.sprinklecity.model.HumanCharacter;
import com.pfhosa.sprinklecity.model.User;
import com.pfhosa.sprinklecity.ui.HomeActivity;

public class CreateCharacterPasswordFragment extends Fragment {

	LinearLayout mLinearLayout;
	EditText mCharacterNameEditText, mPasswordEditText, mAnimalNameEditText;
	Button mConfirmPasswordButton;	

	HumanCharacter mHumanCharacter;
	AnimalCharacter mAnimalCharacter;
	User mUser;

	// Use WeakReference so that AsyncTasks can be garbage collected
	private WeakReference<WriteToRemoteAsyncTask> newUserWeakReference, newAvatarWeakReference, mHumanCharacterWeakReference, mAnimalCharacterWeakReference;
	WriteToRemoteAsyncTask newUserAsyncTask, newAvatarAsyncTask, mHumanCharacterAsyncTask, mAnimalCharacterAsyncTask;

	boolean usernameIsAvailable;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mLinearLayout = (LinearLayout)inflater.inflate(R.layout.fragment_create_character_password, container, false);

		if(getArguments() != null) {
			mHumanCharacter = (HumanCharacter)getArguments().getParcelable("humanCharacter");
			mAnimalCharacter = (AnimalCharacter)getArguments().getParcelable("animalCharacter");
		}

		setRetainInstance(true);
		activateListeners();

		return mLinearLayout;
	}

	public void activateListeners() {
		mCharacterNameEditText = (EditText)mLinearLayout.findViewById(R.id.edit_character_name_pass);
		mAnimalNameEditText = (EditText)mLinearLayout.findViewById(R.id.edit_animal_name_pass);
		mPasswordEditText = (EditText)mLinearLayout.findViewById(R.id.edit_character_password);
		mConfirmPasswordButton = (Button)mLinearLayout.findViewById(R.id.button_create_character_confirm_password);

		mCharacterNameEditText.setText(mHumanCharacter.getName());
		mAnimalNameEditText.setText(mAnimalCharacter.getName());

		mConfirmPasswordButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(mPasswordEditText.getText().toString().length() > 3) {
					User newUser = new User(
							mCharacterNameEditText.getText().toString(),
							mPasswordEditText.getText().toString(),
							mAnimalNameEditText.getText().toString());

					mUser = newUser;

					// Insert into remote 
					if(!isNewUserAsyncTaskRunning()) startNewUserAsyncTask();	

					if(!isNewAvatarAsyncTaskRunning())startNewAvatarAsyncTask();

					if(!isHumanCharacterAsyncTaskRunning()) startNewHumanCharacterAsyncTask();

					if(!isAnimalCharacterAsyncTaskRunning()) startNewAnimalCharacterAsyncTask();

					startActivity(new Intent(getActivity(), HomeActivity.class));

				} else {Toast.makeText(getActivity(), "Your password must be loner.", Toast.LENGTH_SHORT).show();}
			}
		});
	}

	public void startNewUserAsyncTask() {
		String urlUser = "http://www2.macs.hw.ac.uk/~ph109/DBConnect/insertUser.php";

		ArrayList<NameValuePair> postParametersUser = new ArrayList<NameValuePair>();	
		postParametersUser.add(new BasicNameValuePair("Username", mUser.getCharacterName()));
		postParametersUser.add(new BasicNameValuePair("Password", mUser.getPassword()));
		postParametersUser.add(new BasicNameValuePair("Animal", mUser.getAnimalName()));

		newUserAsyncTask = new WriteToRemoteAsyncTask(urlUser, postParametersUser, getActivity());
		newUserWeakReference = new WeakReference<WriteToRemoteAsyncTask>(newUserAsyncTask);

		newUserAsyncTask.execute();	
	}

	public void startNewHumanCharacterAsyncTask() {
		String url = "http://www2.macs.hw.ac.uk/~ph109/DBConnect/insertHumanCharacter.php";

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();	
		postParameters.add(new BasicNameValuePair("Username", mHumanCharacter.getName()));
		postParameters.add(new BasicNameValuePair("Avatar", Integer.toString(mHumanCharacter.getAvatar())));
		postParameters.add(new BasicNameValuePair("Job", mHumanCharacter.getJob()));
		postParameters.add(new BasicNameValuePair("Social", Integer.toString(mHumanCharacter.getSocialTrait())));
		postParameters.add(new BasicNameValuePair("Animal", Integer.toString(mHumanCharacter.getAnimalTrait())));
		postParameters.add(new BasicNameValuePair("Business", Integer.toString(mHumanCharacter.getBusinessTrait())));

		mHumanCharacterAsyncTask = new WriteToRemoteAsyncTask(url, postParameters, getActivity());

		mHumanCharacterWeakReference = new WeakReference<WriteToRemoteAsyncTask>(mHumanCharacterAsyncTask);
		mHumanCharacterAsyncTask.execute();	
	}

	public void startNewAnimalCharacterAsyncTask() {

		String url = "http://www2.macs.hw.ac.uk/~ph109/DBConnect/insertAnimalCharacter.php";

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();	
		postParameters.add(new BasicNameValuePair("Animal", mAnimalCharacter.getName()));
		postParameters.add(new BasicNameValuePair("AnimalAvatar", Integer.toString(mAnimalCharacter.getAvatar())));
		postParameters.add(new BasicNameValuePair("Sleep", Integer.toString(mAnimalCharacter.getSleep())));
		postParameters.add(new BasicNameValuePair("Fitness", Integer.toString(mAnimalCharacter.getFitness())));

		mAnimalCharacterAsyncTask = new WriteToRemoteAsyncTask(url, postParameters, getActivity());

		@SuppressWarnings("unused")
		WeakReference<WriteToRemoteAsyncTask> mAnimalCharacterWeakReference = new WeakReference<WriteToRemoteAsyncTask>(mAnimalCharacterAsyncTask);
		mAnimalCharacterAsyncTask.execute();	

	}

	public void startNewAvatarAsyncTask() {
		String urlAvatar = "http://www2.macs.hw.ac.uk/~ph109/DBConnect/insertHumanAvatar.php";

		ArrayList<NameValuePair> postParametersAvatar = new ArrayList<NameValuePair>();	
		postParametersAvatar.add(new BasicNameValuePair("Username", mUser.getCharacterName()));
		postParametersAvatar.add(new BasicNameValuePair("PositionX", Integer.toString(0)));
		postParametersAvatar.add(new BasicNameValuePair("PositionY", Integer.toString(0)));	
		postParametersAvatar.add(new BasicNameValuePair("Direction", Integer.toString(-1)));

		newAvatarAsyncTask = new WriteToRemoteAsyncTask(urlAvatar, postParametersAvatar, getActivity());
		newAvatarWeakReference = new WeakReference<WriteToRemoteAsyncTask>(newAvatarAsyncTask);

		newAvatarAsyncTask.execute();		
	}

	private boolean isNewUserAsyncTaskRunning() {
		return 	this.newUserWeakReference != null &&
				this.newUserWeakReference.get() != null && 
				!this.newUserWeakReference.get().getStatus().equals(Status.FINISHED);
	}

	private boolean isHumanCharacterAsyncTaskRunning() {
		return 	this.mHumanCharacterWeakReference != null &&
				this.mHumanCharacterWeakReference.get() != null && 
				!this.mHumanCharacterWeakReference.get().getStatus().equals(Status.FINISHED);
	}

	private boolean isAnimalCharacterAsyncTaskRunning() {
		return 	this.mAnimalCharacterWeakReference != null &&
				this.mAnimalCharacterWeakReference.get() != null && 
				!this.mAnimalCharacterWeakReference.get().getStatus().equals(Status.FINISHED);
	}

	private boolean isNewAvatarAsyncTaskRunning() {
		return 	this.newAvatarWeakReference != null &&
				this.newAvatarWeakReference.get() != null && 
				!this.newAvatarWeakReference.get().getStatus().equals(Status.FINISHED);
	}

}
