package com.pfhosa.sprinklecity.database;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.pfhosa.sprinklecity.model.InventoryItem;
import com.pfhosa.sprinklecity.model.InventoryList;

public class InventoryLoaderAsyncTask {

	String mUsername;
	boolean mDoneLoading;
	InventoryList mReturnList = new InventoryList("");
	Database mDb;

	public InventoryLoaderAsyncTask(String username, Context context) {
		mUsername = username;
		mDb = Database.getInstance(context);
	}

	public boolean getListAvailability() {return mDoneLoading;}

	public InventoryList getInventory() {return mReturnList;}

	protected Void doInBackground(Void... params) {
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

		postParameters.add(new BasicNameValuePair("Username", mUsername));
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

					InventoryItem mInventoryItem = new InventoryItem(creator, item, value, timeCreated, usable);
					mReturnList.addItem(item, mInventoryItem);
				}
			} 
			catch (JSONException e){Log.e("log_tag", "Error parsing data "+ e.toString());} 
		} catch (Exception e) {Log.e("log_tag","Error in http connection!!" + e.toString());}  

		return null;
	}
	protected void onPostExecute(Void result) {
		mDb.loadInventoryToLocal(mReturnList);
		mDoneLoading = true;
	}
}
