package com.pfhosa.sprinklecity.database;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.pfhosa.sprinklecity.model.InventoryItem;

import android.os.AsyncTask;
import android.util.Log;

public class DeleteInventoryItemFromRemote extends AsyncTask<Void, Void, Void> {
	
	private String mUrl = "http://www2.macs.hw.ac.uk/~ph109/DBConnect/deleteInventoryItem.php";
	private InventoryItem mItem;
	private ArrayList<NameValuePair> mPostParameters = new ArrayList<NameValuePair>();

	public DeleteInventoryItemFromRemote(InventoryItem item) {
		mItem = item;
		preparePostParameters();
	}
	
	public void preparePostParameters() {
		mPostParameters.add(new BasicNameValuePair("ID", Integer.toString(mItem.getId())));	
	}

	protected Void doInBackground(Void... params) {
		try {CustomHttpClient.executeHttpPostInsert(mUrl, mPostParameters);} 
		catch (Exception e) {Log.e("log_tag","Error in http connection. " + e.toString());}  
		return null;
	}

	protected void onPostExecute(Void result) {}

}
