package com.pfhosa.sprinklecity.database;

import java.util.ArrayList;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class WriteToRemoteAsyncTask extends AsyncTask<Void, Void, Void> {

	String mResponse = null, mUrl = null;
	ArrayList<NameValuePair> mPostParameters = new ArrayList<NameValuePair>();
	Activity mFeedbackActivity;

	public WriteToRemoteAsyncTask(String url, ArrayList<NameValuePair> postParameters, Activity feedbackActivity) {		
		mUrl = url;
		mPostParameters = postParameters;
		mFeedbackActivity = feedbackActivity;
	}

	protected Void doInBackground(Void... params) {

		try {CustomHttpClient.executeHttpPostInsert(mUrl, mPostParameters);} 
		catch (Exception e) {Log.e("log_tag","Error in http connection. " + e.toString());}  
		return null;
	}

	protected void onPostExecute(Void result) {}

}