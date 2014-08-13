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
import com.pfhosa.sprinklecity.fragments.InventoryListFragment;
import com.pfhosa.sprinklecity.model.InventoryItem;
import com.pfhosa.sprinklecity.model.InventoryList;

public class InventoryActivity extends FragmentActivity {

	Bundle characterData;
	InventoryList inventory;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory);	

		characterData = getIntent().getExtras();

		inventory = new InventoryList(characterData.getString("Username"));
		new InventoryLoaderAsyncTask().execute();
	}

	public void openInventoryFragment(Bundle characterData) {
		if (findViewById(R.id.fragment_container_inventory) != null) {

			InventoryListFragment inventoryListFragment = new InventoryListFragment();

			inventoryListFragment.setArguments(characterData);

			getSupportFragmentManager().beginTransaction()
			.add(R.id.fragment_container_inventory,inventoryListFragment)
			.addToBackStack("inventoryListFragment")
			.commit();
		}
	}

	public void inventoryLoader() {
		Database db = Database.getInstance(this);
		db.loadInventoryToLocal(inventory);		
		openInventoryFragment(characterData);	
	}

	public class InventoryLoaderAsyncTask extends AsyncTask<Void, Void, Void> {

		protected Void doInBackground(Void... params) {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

			postParameters.add(new BasicNameValuePair("Username", characterData.getString("Username")));
			String response = null, item = null, creator = null;
			int value = 0;
			long timeCreated = 0;
			boolean usable;

			try {
				response = CustomHttpClient.executeHttpPost("http://www2.macs.hw.ac.uk/~ph109/DBConnect/getInventory.php", postParameters);

				// store the result returned by PHP script that runs MySQL query
				String result = response.toString();  

				//parse json data
				try{
					JSONArray jArray = new JSONArray(result);		

					for(int i = 0; i < jArray.length(); ++i) {
						JSONObject json_data = jArray.getJSONObject(i);
						creator = json_data.getString("Username");
						item = json_data.getString("Item");
						value = Integer.parseInt(json_data.getString("Value"));
						timeCreated = Long.parseLong(json_data.getString("TimeCreated"));
						usable = "1".equals(json_data.getString("Usable").toString());				

						InventoryItem inventoryItem = new InventoryItem(creator, item, value, timeCreated, usable);
						inventory.addItem(item, inventoryItem);
					}
					Log.d("Loaded inventory to Object", "true");

				} 
				catch (JSONException e){Log.e("log_tag", "Error parsing data "+ e.toString());} 
			} catch (Exception e) {Log.e("log_tag","Error in http connection!!" + e.toString());}  
			
			return null;
		}
		protected void onPostExecute(Void result) {inventoryLoader();}
	}
	
	@Override
	public void onBackPressed() {finish();}
}
