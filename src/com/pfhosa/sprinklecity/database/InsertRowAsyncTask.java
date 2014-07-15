package com.pfhosa.sprinklecity.database;

import java.util.ArrayList;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class InsertRowAsyncTask extends AsyncTask<Void, Void, Void> {

	String response = null;

	String url = null;
	ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
	Activity feedbackActivity;

	public InsertRowAsyncTask(String url, ArrayList<NameValuePair> postParameters, Activity feedbackActivity) {		
		this.url = url;
		this.postParameters = postParameters;
		this.feedbackActivity = feedbackActivity;
	}
	
	protected Void doInBackground(Void... params) {

		//Looper.prepare();

		try {
			CustomHttpClient.executeHttpPostInsert(url, postParameters);

		} 
		catch (Exception e) {
			Log.e("log_tag","Error in http connection. " + e.toString());
		}  
		return null;
	}

	protected void onPostExecute(Void result) {}
}