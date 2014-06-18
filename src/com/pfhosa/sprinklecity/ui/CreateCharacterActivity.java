package com.pfhosa.sprinklecity.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.fragments.CreateCharacterHumanFragment;

public class CreateCharacterActivity extends FragmentActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_character);
			/**	
        if (findViewById(R.id.fragment_container_human_and_animal) != null) {
        	
            if (savedInstanceState != null) {
                return;
            }

            CreateCharacterUpperFragment upperFragment = new CreateCharacterUpperFragment();
            
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_human_and_animal, upperFragment).commit();
        }
        */
        if (findViewById(R.id.fragment_container_create_character) != null) {
        	
            if (savedInstanceState != null) {
                return;
            }

            CreateCharacterHumanFragment bottomFragment = new CreateCharacterHumanFragment();
            
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_create_character, bottomFragment).commit();
        }
		
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.home_screen, menu);
		return false; //disable menu
	}
}
