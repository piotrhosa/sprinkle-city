package com.pfhosa.sprinklecity.database;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.pfhosa.sprinklecity.model.InventoryItem;
import com.pfhosa.sprinklecity.model.InventoryList;

public class InventoryLoader {
	String mUsername;	
	boolean mInventoryAccessible;
	InventoryList mInventory;
	OnInventoryLoadedListener mInventoryLoaded;

	public InventoryLoader(String username) {
		mUsername = username;
		mInventoryAccessible = false;
		mInventory = new InventoryList(mUsername);
		
		new InventoryLoaderAsyncTask().execute();
	}
	
	public boolean getInvetnoryAvailable() {return mInventoryAccessible;}

	public InventoryList getInventory() {
		if(mInventoryAccessible) return mInventory;
		else return null;
	}
	
	public ArrayList<InventoryItem> getCompressedInventory() {return getInventory().compressInventory();}

	public class InventoryLoaderAsyncTask extends AsyncTask<Void, Void, Void> {

		protected Void doInBackground(Void... params) {
			// declare parameters that are passed to PHP script i.e. the name "birthyear" and its value submitted by user   
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

			//Looper.prepare();

			// define the parameter
			postParameters.add(new BasicNameValuePair("Username", mUsername));
			String response = null, item = null, creator = null;
			int value = 0;
			long timeCreated = 0;
			boolean usable;

			// call executeHttpPost method passing necessary parameters 
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
						usable = "1".equals(json_data.getString("Usable").toString()	);

						InventoryItem inventoryItem = new InventoryItem(creator, item, value, timeCreated, usable);
						mInventory.addItem(item, inventoryItem);

					}

				} 
				catch (JSONException e){
					Log.e("log_tag", "Error parsing data "+ e.toString());
					//makeToastWrongData();
				}   

			} 
			catch (Exception e) {
				Log.e("log_tag","Error in http connection!!" + e.toString());

			}  
			return null;
		}

		protected void onPostExecute(Void result) {
			mInventoryAccessible = true;
			//mInventoryLoaded.onInventoryLoaded(mInventory);
			//Database db = Database.getInstance();
			
		}
	}
	
	public interface OnInventoryLoadedListener {
		public void onInventoryLoaded(InventoryList inventory);
	}
}
