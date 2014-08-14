package com.pfhosa.sprinklecity.ui;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.database.CustomHttpClient;

public class LoginActivity extends Activity {	

	EditText mNameLogin, mPasswordLogin;
	TextView mLoginReturn, mLoginDetails;
	String mNameLoginString, mPasswordLoginString;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mNameLogin = (EditText)findViewById(R.id.edit_character_name);
		mPasswordLogin = (EditText)findViewById(R.id.edit_password);
		
		activateLoginButtonListener();
	}	

	public void activateLoginButtonListener() {
		Button loginButton = (Button)findViewById(R.id.button_login_final);

		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				mNameLoginString = mNameLogin.getText().toString();
				mPasswordLoginString = mPasswordLogin.getText().toString();

				new ConnectAsyncTask().execute();
			}

		});
	}

	public void openMapFragment(String username, int avatar) {
		Intent intent = new Intent(this, GameMapActivity.class);
		intent.putExtra("Username", username);
		intent.putExtra("Avatar", avatar);
		startActivity(intent);
	}

	public void makeToastWrongData() {	
		LoginActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(LoginActivity.this, "Your username or password is wrong.", Toast.LENGTH_SHORT).show();

			}
		});
	}

	public class ConnectAsyncTask extends AsyncTask<Void, Void, Void> {
		String username = null;
		int avatar = 0;
		
		@SuppressWarnings("unused")
		protected Void doInBackground(Void... params) {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			ArrayList<NameValuePair> postParametersData = new ArrayList<NameValuePair>();

			postParameters.add(new BasicNameValuePair("Username", mNameLoginString));
			postParameters.add(new BasicNameValuePair("Password", mPasswordLoginString));
			postParametersData.add(new BasicNameValuePair("Username", mNameLoginString));
			String response = null;
			String responseData = null;

			try {
				response = CustomHttpClient.executeHttpPost("http://www2.macs.hw.ac.uk/~ph109/DBConnect/connect.php", postParameters);
				responseData = CustomHttpClient.executeHttpPost("http://www2.macs.hw.ac.uk/~ph109/DBConnect/getHumanCharacter.php", postParametersData);

				String result = response.toString();  
				String resultData = responseData.toString();

				try{
					JSONArray jArray = new JSONArray(result);		

					for(int i = 0; i < jArray.length(); ++i) {
						JSONObject json_data = jArray.getJSONObject(i);
					}
					
					JSONArray jArrayData = new JSONArray(resultData);		

					for(int i = 0; i < jArrayData.length(); ++i) {
						JSONObject json_data = jArrayData.getJSONObject(i);
						username = json_data.getString("Username");
						avatar = Integer.parseInt(json_data.getString("Avatar"));
					}

					openMapFragment(username, avatar);

				} catch (JSONException e){
					Log.e("log_tag", "Error parsing data "+ e.toString());
					makeToastWrongData();
				}   
			} catch (Exception e) {Log.e("log_tag","Error in http connection!!" + e.toString());}  
			return null;
		}

		protected void onPostExecute(Void result) {}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.home_screen, menu);
		return false; //disable menu
	}
}