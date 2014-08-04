package com.pfhosa.sprinklecity.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.fragments.VirtualMapFragment;
import com.pfhosa.sprinklecity.fragments.VirtualMapFragment.OnLocationSelectedListener;

public class GameMapActivity extends FragmentActivity implements       
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, 
LocationListener,
OnLocationSelectedListener {

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	private static final int MILLISECONDS_PER_SECOND = 1000;
	public static final int UPDATE_INTERVAL_IN_SECONDS = 1;
	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	private static final int MIN_DISPLACEMENT_IN_METERS = 1;

	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

	LocationRequest mLocationRequest;
	LocationClient mLocationClient;
	boolean mUpdatesRequested;

	SharedPreferences mPrefs;
	Editor mEditor;

	Bundle characterData;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_map);

		// Get Extras
		characterData = getIntent().getExtras();

		// Record location
		mPrefs = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);		
		mEditor = mPrefs.edit();

		// Initialize location updates
		if(servicesConnected()) {
			mLocationClient = new LocationClient(this, this, this);

			mUpdatesRequested = false;

			mLocationRequest = LocationRequest.create();
			mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			mLocationRequest.setInterval(UPDATE_INTERVAL);
			mLocationRequest.setFastestInterval(FASTEST_INTERVAL);	
			mLocationRequest.setSmallestDisplacement(MIN_DISPLACEMENT_IN_METERS);
		}

		if (findViewById(R.id.fragment_container_game_map) != null) {

			if (savedInstanceState != null)
				return;

			VirtualMapFragment virtualMapFragment = new VirtualMapFragment();

			virtualMapFragment.setArguments(characterData);

			getSupportFragmentManager().beginTransaction()
			.add(R.id.fragment_container_game_map, virtualMapFragment)
			.addToBackStack("virtualMapFragment")
			.commit();
		}

	}

	@Override
	protected void onStart() {
		super.onStart();

		mLocationClient.connect();
	}

	@Override
	protected void onResume() {
		super.onResume();

		/*
		 * Get any previous setting for location updates
		 * Gets "false" if an error occurs
		 */
		if (mPrefs.contains("KEY_UPDATES_ON")) {
			mUpdatesRequested = mPrefs.getBoolean("KEY_UPDATES_ON", false);
		} 
		else {
			mEditor.putBoolean("KEY_UPDATES_ON", false);
			mEditor.commit();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		mEditor.putBoolean("KEY_UPDATES_ON", mUpdatesRequested);
		mEditor.commit();
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (mLocationClient.isConnected()) {
			mLocationClient.removeLocationUpdates(this);
		}

		mLocationClient.disconnect();
	}

	public static class ErrorDialogFragment extends DialogFragment {
		// Global field to contain the error dialog
		private Dialog mDialog;
		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}
		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}
		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	public void showErrorDialog(int errorCode) {
		//TODO
	}

	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates", "Google Play services is available.");
			// Continue
			return true;
			// Google Play services was not available for some reason
		} 
		else {
			// Get the error code
			int errorCode = 99;
			//connectionResult.getErrorCode();
			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
					errorCode,
					this,
					CONNECTION_FAILURE_RESOLUTION_REQUEST);
			// If Google Play services can provide an error dialog
			if (errorDialog != null) {
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				// Set the dialog in the DialogFragment
				errorFragment.setDialog(errorDialog);
				// Show the error dialog in the DialogFragment
				errorFragment.show(getSupportFragmentManager(), "Location Updates");
			}

			return false;
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onLocationChanged(Location location) {

		Intent newLocationIntent = new Intent("newLocationIntent");
		newLocationIntent.putExtra("latitude", location.getLatitude());
		newLocationIntent.putExtra("longitude", location.getLongitude());

		this.sendBroadcast(newLocationIntent);		
	}

	public void onProviderDisabled(String arg0) {}

	public void onProviderEnabled(String arg0) {}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this,	CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} 
			catch (IntentSender.SendIntentException e) {
				e.printStackTrace();
			}
		} 
		else {
			/*
			 * If no resolution is available, display a dialog to the
			 * user with the error.
			 */
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	public void virtualLocationSelector(String location) {
		Intent openLocation = new Intent(this, GameLocationActivity.class);
		openLocation.putExtra("Fragment", location);
		openLocation.putExtra("Username", characterData.getString("Username"));
		startActivity(openLocation);	
	}

	@Override
	public void onLocationSelected(String location) {
		if(location.equals("inventory")) {
			Intent openInventory = new Intent(this, InventoryActivity.class);
			openInventory.putExtra("Username", characterData.getString("Username"));
			startActivity(openInventory);
		}
		else
		virtualLocationSelector(location);
	}

}
