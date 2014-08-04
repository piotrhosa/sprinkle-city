package com.pfhosa.sprinklecity.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.database.Database;
import com.pfhosa.sprinklecity.database.InventoryLoader;
import com.pfhosa.sprinklecity.database.InventoryLoader.OnInventoryLoadedListener;
import com.pfhosa.sprinklecity.fragments.InventoryListFragment;
import com.pfhosa.sprinklecity.model.InventoryList;

public class InventoryActivity extends FragmentActivity {

	Bundle characterData;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory);	

		characterData = getIntent().getExtras();

		InventoryLoader loader = new InventoryLoader(characterData.getString("Username"));

		openInventoryFragment(characterData);		
	}
	
	public void openInventoryFragment(Bundle characterData) {
		if (findViewById(R.id.fragment_container_inventory) != null) {

			//if (savedInstanceState != null)
				//return;

			InventoryListFragment inventoryListFragment = new InventoryListFragment();

			inventoryListFragment.setArguments(characterData);

			getSupportFragmentManager().beginTransaction()
			.add(R.id.fragment_container_inventory,inventoryListFragment)
			.addToBackStack("inventoryListFragment")
			.commit();
		}
	}
}
