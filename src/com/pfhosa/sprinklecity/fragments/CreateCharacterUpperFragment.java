package com.pfhosa.sprinklecity.fragments;

import com.pfhosa.sprinklecity.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;


public class CreateCharacterUpperFragment extends Fragment {

	View viewUpper;
	View viewLower;
	ImageButton humanButton;
	ImageButton animalButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		viewUpper = inflater.inflate(R.layout.fragment_create_character_upper, container, false);

		humanButton = (ImageButton) viewUpper.findViewById(R.id.image_character_human);
		animalButton = (ImageButton) viewUpper.findViewById(R.id.image_character_animal);

		activateHumanButtonListener();
		activateAnimalButtonListener();		

		return viewUpper;
	}

	public void activateHumanButtonListener() {
		humanButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v){

				if (v.findViewById(R.id.fragment_container_create_character) != null) {

				Toast.makeText(getActivity(), "dupa", Toast.LENGTH_SHORT).show();

				CreateCharacterHumanFragment humanFragment = new CreateCharacterHumanFragment();

				getFragmentManager().beginTransaction()
				.add(R.id.fragment_container_create_character, humanFragment).commit();
				}
			}
		});
	}

	public void activateAnimalButtonListener() {
		animalButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v){
				//if (v.findViewById(R.id.fragment_container_create_character) != null) {

				CreateCharacterHumanFragment animalFragment = new CreateCharacterHumanFragment();

				getFragmentManager().beginTransaction()
				.add(R.id.fragment_container_create_character, animalFragment).commit();
				//}
			}
		});
	}
}