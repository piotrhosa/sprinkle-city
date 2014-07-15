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
import android.os.Looper;
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

	EditText nameLogin, passwordLogin;
	TextView loginReturn, loginDetails;
	String nameLoginString, passwordLoginString;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		nameLogin = (EditText)findViewById(R.id.edit_character_name);
		passwordLogin = (EditText)findViewById(R.id.edit_password);
		loginReturn = (TextView)findViewById(R.id.text_login_return);
		loginDetails = (TextView)findViewById(R.id.text_login_details);

		activateLoginButtonListener();
	}	

	public void activateLoginButtonListener() {
		Button loginButton = (Button)findViewById(R.id.button_login_final);

		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				nameLoginString = nameLogin.getText().toString();
				passwordLoginString = passwordLogin.getText().toString();

				loginDetails.setText(nameLoginString + " " + passwordLoginString);

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
		@SuppressWarnings("unused")
		protected Void doInBackground(Void... params) {
			// declare parameters that are passed to PHP script i.e. the name "birthyear" and its value submitted by user   
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			ArrayList<NameValuePair> postParametersData = new ArrayList<NameValuePair>();

			Looper.prepare();

			// define the parameter
			postParameters.add(new BasicNameValuePair("Username", nameLoginString));
			postParameters.add(new BasicNameValuePair("Password", passwordLoginString));
			postParametersData.add(new BasicNameValuePair("Username", nameLoginString));
			String response = null;
			String responseData = null;
			String username = null, avatar = null;

			// call executeHttpPost method passing necessary parameters 
			try {
				response = CustomHttpClient.executeHttpPost("http://www2.macs.hw.ac.uk/~ph109/DBConnect/connect.php", postParameters);
				responseData = CustomHttpClient.executeHttpPost("http://www2.macs.hw.ac.uk/~ph109/DBConnect/getCharacter.php", postParametersData);

				// store the result returned by PHP script that runs MySQL query
				String result = response.toString();  

				//parse json data
				try{
					JSONArray jArray = new JSONArray(result);		

					for(int i = 0; i < jArray.length(); ++i) {
						JSONObject json_data = jArray.getJSONObject(i);
						username = json_data.getString("Username");
						avatar = json_data.getString("Avatar");

					}

					openMapFragment(username, Integer.parseInt(avatar));

				} 
				catch (JSONException e){
					Log.e("log_tag", "Error parsing data "+ e.toString());
					makeToastWrongData();
				}   

			} 
			catch (Exception e) {
				Log.e("log_tag","Error in http connection!!" + e.toString());

			}  
			return null;
		}

		protected void onPostExecute(Void result) {

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.home_screen, menu);
		return false; //disable menu
	}
}