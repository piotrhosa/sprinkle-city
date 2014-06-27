package com.pfhosa.sprinklecity.ui;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

				new connectAsyncTask().execute();
			}

		});
	}

	public class connectAsyncTask extends AsyncTask<Void, Void, Void>
	{
		@SuppressWarnings("unused")
		protected Void doInBackground(Void... params) {
			// declare parameters that are passed to PHP script i.e. the name "birthyear" and its value submitted by user   
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

			String returnString = "";
			String username;
			boolean error;

			// define the parameter
			postParameters.add(new BasicNameValuePair("Username", nameLoginString));
			postParameters.add(new BasicNameValuePair("Password", passwordLoginString));
			String response = null;

			// call executeHttpPost method passing necessary parameters 
			try {
				//response = CustomHttpClient.executeHttpPost("http://www2.macs.hw.ac.uk/~sg244/DBConnect/connect.php", postParameters);
				response = CustomHttpClient.executeHttpPost("http://www2.macs.hw.ac.uk/~ph109/DBConnect/connect.php", postParameters);

				// store the result returned by PHP script that runs MySQL query
				String result = response.toString();  

				//parse json data
				try{
					returnString = "";
					JSONArray jArray = new JSONArray(result);
					
					//String s = jArray.get(0).toString();
					//JSONObject json_data = new JSONObject(s);
					

					for(int i=0;i<jArray.length();i++){
						JSONObject json_data = jArray.getJSONObject(i);

						//Get an output to the screen
						returnString = json_data.getString("Username") + " -> "+ json_data.getString("Password");
						username = json_data.getString("Username");

					}

				} catch(JSONException e){
					Log.e("log_tag", "Error parsing data "+ e.toString());
					error = true;      
				}

				try{
					//loginReturn.setText(returnString);		          

				} catch(Exception e){
					Log.e("log_tag","Error in Display!" + e.toString());; 
				}     

			} catch (Exception e) {
				Log.e("log_tag","Error in http connection!!" + e.toString());
				error = true;	

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