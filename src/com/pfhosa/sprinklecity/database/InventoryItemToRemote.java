package com.pfhosa.sprinklecity.database;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;

import com.pfhosa.sprinklecity.model.InventoryItem;

public class InventoryItemToRemote extends AsyncTask<Void, Void, Void> {

	private String mUrl = "http://www2.macs.hw.ac.uk/~ph109/DBConnect/insertInventoryItem.php";
	private InventoryItem mItem;
	private ArrayList<NameValuePair> mPostParameters = new ArrayList<NameValuePair>();

	public InventoryItemToRemote(InventoryItem item) {
		mItem = item;
		preparePostParameters();
	}
	
	public void preparePostParameters() {
		mPostParameters.add(new BasicNameValuePair("Username", mItem.getCreator()));
		mPostParameters.add(new BasicNameValuePair("Item", mItem.getItem()));
		mPostParameters.add(new BasicNameValuePair("Value", Integer.toString(mItem.getValue())));
		mPostParameters.add(new BasicNameValuePair("TimeCreated", Long.toString(mItem.getTimeCollected())));
		mPostParameters.add(new BasicNameValuePair("Usable", mItem.getUsable() ? "1" : "0"));	
	}

	protected Void doInBackground(Void... params) {
		try {CustomHttpClient.executeHttpPostInsert(mUrl, mPostParameters);} 
		catch (Exception e) {Log.e("log_tag","Error in http connection. " + e.toString());}  
		return null;
	}

	protected void onPostExecute(Void result) {}

}