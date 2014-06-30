package com.pfhosa.sprinklecity.ui;

import android.app.Dialog;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.fragments.VirtualMapFragment;

public class GameMapActivity extends FragmentActivity implements       
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		LocationListener {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_map);

		if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this))

			if (findViewById(R.id.fragment_container_game_map) != null) {

				if (savedInstanceState != null) {
					return;
				}

				VirtualMapFragment virtualMapFragment = new VirtualMapFragment();

				getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container_game_map, virtualMapFragment).commit();
			}

	}

	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates",
					"Google Play services is available.");
			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Get the error code
			int errorCode = connectionResult.getErrorCode();
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
		}
	}

	public static void locationChanged(float distance) {		
	}

}
