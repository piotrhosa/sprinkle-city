package com.pfhosa.sprinklecity.ui;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.database.CustomHttpClient;
import com.pfhosa.sprinklecity.database.Database;
import com.pfhosa.sprinklecity.fragments.InventoryDetailFragment;
import com.pfhosa.sprinklecity.fragments.InventoryListFragment;
import com.pfhosa.sprinklecity.fragments.InventoryListFragment.OnInventoryItemSelectedListener;
import com.pfhosa.sprinklecity.model.InventoryItem;
import com.pfhosa.sprinklecity.model.InventoryList;

public class InventoryActivity extends FragmentActivity implements OnInventoryItemSelectedListener {

	Bundle mCharacterData;
	InventoryList mInventory;
	InventoryListFragment mInventoryListFragment;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory);	

		mCharacterData = getIntent().getExtras();

		mInventory = new InventoryList(mCharacterData.getString("Username"));
		new InventoryLoaderAsyncTask().execute();
	}

	public void openInventoryFragment() {
		if (findViewById(R.id.fragment_container_inventory) != null) {

			mInventoryListFragment = new InventoryListFragment();

			mInventoryListFragment.setArguments(mCharacterData);

			getSupportFragmentManager().beginTransaction()
			.add(R.id.fragment_container_inventory, mInventoryListFragment)
			.addToBackStack("mInventoryListFragment")
			.commit();
		}
	}

	public void mInventoryLoader() {
		Database db = Database.getInstance(this);
		db.loadInventoryToLocal(mInventory);		
		openInventoryFragment();	
	}

	public class InventoryLoaderAsyncTask extends AsyncTask<Void, Void, Void> {

		protected Void doInBackground(Void... params) {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

			postParameters.add(new BasicNameValuePair("Username", mCharacterData.getString("Username")));
			String response = null, item = null, creator = null;
			int value = 0;
			long timeCreated = 0;
			boolean usable;

			try {
				response = CustomHttpClient.executeHttpPost("http://www2.macs.hw.ac.uk/~ph109/DBConnect/getInventory.php", postParameters);

				String result = response.toString();  

				try{
					JSONArray jArray = new JSONArray(result);		

					for(int i = 0; i < jArray.length(); ++i) {
						JSONObject json_data = jArray.getJSONObject(i);
						creator = json_data.getString("Username");
						item = json_data.getString("Item");
						value = Integer.parseInt(json_data.getString("Value"));
						timeCreated = Long.parseLong(json_data.getString("TimeCreated"));
						usable = "1".equals(json_data.getString("Usable").toString());	

						InventoryItem mInventoryItem = new InventoryItem(creator, item, value, timeCreated, usable);
						mInventory.addItem(item, mInventoryItem);
					}
				} 
				catch (JSONException e){Log.e("log_tag", "Error parsing data "+ e.toString());} 
			} catch (Exception e) {Log.e("log_tag","Error in http connection!!" + e.toString());}  
			
			return null;
		}
		protected void onPostExecute(Void result) {mInventoryLoader();}
	}
	
	@Override
	public void onBackPressed() {
		if (mInventoryListFragment.isVisible()) {Log.e("First frag", "true");finish();return;}
		openInventoryFragment();
	}

	@Override
	public void onItemSelected() {
		if (findViewById(R.id.fragment_container_inventory) != null) {

			InventoryDetailFragment inventoryDetailFragment = new InventoryDetailFragment();

			inventoryDetailFragment.setArguments(mCharacterData);

			getSupportFragmentManager().beginTransaction()
			.replace(R.id.fragment_container_inventory, inventoryDetailFragment)
			.addToBackStack("inventoryDetailFragment")
			.commit();
		}
	}
}
