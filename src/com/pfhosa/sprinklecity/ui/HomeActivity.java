package com.pfhosa.sprinklecity.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.pfhosa.sprinklecity.R;

public class HomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);
		
		activateButtonMenu();

	}

	public void activateButtonMenu() {
		Button buttonCreateCharacter = (Button)findViewById(R.id.button_create_character);
		Button buttonLogin = (Button)findViewById(R.id.button_login);

		buttonCreateCharacter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intentCreateCharacter = new Intent(getBaseContext(), CreateCharacterActivity.class);
				startActivity(intentCreateCharacter);

			}

		});
		
		buttonLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intentLogin = new Intent(getBaseContext(), LoginActivity.class);
				startActivity(intentLogin);

			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.home_screen, menu);
		return false; //disable menu
	}

}
