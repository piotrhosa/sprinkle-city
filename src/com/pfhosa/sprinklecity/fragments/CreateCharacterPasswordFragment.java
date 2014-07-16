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
import android.widget.TextView;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.database.Database;
import com.pfhosa.sprinklecity.database.WriteToRemoteAsyncTask;
import com.pfhosa.sprinklecity.model.AnimalCharacter;
import com.pfhosa.sprinklecity.model.HumanCharacter;
import com.pfhosa.sprinklecity.model.User;
import com.pfhosa.sprinklecity.ui.HomeActivity;

public class CreateCharacterPasswordFragment extends Fragment {

	Database db;

	// Use WeakReference so that AsyncTasks can be garbage collected
	private WeakReference<WriteToRemoteAsyncTask> newUserWeakReference, newAvatarWeakReference;

	LinearLayout linearLayout;
	TextView descriptionTextView;
	EditText characterNameEditText, passwordEditText, animalNameEditText;
	Button confirmPasswordButton;	

	HumanCharacter humanCharacter;
	AnimalCharacter animalCharacter;
	User globalUser;

	WriteToRemoteAsyncTask newUserAsyncTask, newAvatarAsyncTask;

	boolean usernameIsAvailable;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		linearLayout = (LinearLayout)inflater.inflate(R.layout.fragment_create_character_password, container, false);

		if(getArguments() != null) {
			humanCharacter = (HumanCharacter)getArguments().getParcelable("humanCharacter");
			animalCharacter = (AnimalCharacter)getArguments().getParcelable("animalCharacter");
		}

		db = new Database(getActivity());

		setRetainInstance(true);
		activateListeners();

		return linearLayout;
	}

	public void activateListeners() {
		characterNameEditText = (EditText)linearLayout.findViewById(R.id.edit_character_name_pass);
		animalNameEditText = (EditText)linearLayout.findViewById(R.id.edit_animal_name_pass);
		passwordEditText = (EditText)linearLayout.findViewById(R.id.edit_character_password);
		confirmPasswordButton = (Button)linearLayout.findViewById(R.id.button_create_character_confirm_password);

		characterNameEditText.setText(humanCharacter.getName());
		animalNameEditText.setText(animalCharacter.getName());

		confirmPasswordButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				User newUser = new User(
						characterNameEditText.getText().toString(),
						passwordEditText.getText().toString(),
						animalNameEditText.getText().toString());

				globalUser = newUser;

				if(!isNewUserAsyncTaskRunning())
					startNewUserAsyncTask();	
				
				if(!isNewAvatarAsyncTaskRunning())
					startNewAvatarAsyncTask();
				
				startActivity(new Intent(getActivity(), HomeActivity.class));
				
			}
		});
	}

	public void startNewUserAsyncTask() {
		String urlUser = "http://www2.macs.hw.ac.uk/~ph109/DBConnect/insertUser.php";

		ArrayList<NameValuePair> postParametersUser = new ArrayList<NameValuePair>();	
		postParametersUser.add(new BasicNameValuePair("Username", globalUser.getCharacterName()));
		postParametersUser.add(new BasicNameValuePair("Password", globalUser.getPassword()));
		postParametersUser.add(new BasicNameValuePair("Animal", globalUser.getAnimalName()));
		
		newUserAsyncTask = new WriteToRemoteAsyncTask(urlUser, postParametersUser, getActivity());
		newUserWeakReference = new WeakReference<WriteToRemoteAsyncTask>(newUserAsyncTask);
		
		newUserAsyncTask.execute();	
	}
	
	public void startNewAvatarAsyncTask() {
		String urlAvatar = "http://www2.macs.hw.ac.uk/~ph109/DBConnect/insertHumanAvatar.php";
		
		ArrayList<NameValuePair> postParametersAvatar = new ArrayList<NameValuePair>();	
		postParametersAvatar.add(new BasicNameValuePair("Username", globalUser.getCharacterName()));
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
	
	private boolean isNewAvatarAsyncTaskRunning() {
		return 	this.newAvatarWeakReference != null &&
				this.newAvatarWeakReference.get() != null && 
				!this.newAvatarWeakReference.get().getStatus().equals(Status.FINISHED);
	}
}
