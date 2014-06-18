package com.pfhosa.sprinklecity.ui;


import com.pfhosa.sprinklecity.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);
		
		addCreateCharacterButtonListener();

	}

	public void addCreateCharacterButtonListener() {
		Button buttonCreateCharacter = (Button)findViewById(R.id.button_create_character);

		buttonCreateCharacter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("been at button", "yes");
				Intent intentCreateCharacter = new Intent(getBaseContext(), CreateCharacterActivity.class);
				startActivity(intentCreateCharacter);

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
