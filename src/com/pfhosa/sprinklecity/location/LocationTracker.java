package com.pfhosa.sprinklecity.location;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationTracker extends Service implements LocationListener {

	private static final long LOCATION_UPDATE_PERIOD = 60000; //ms
	private static final long LOCATION_UPDATE_DISTANCE = 2; //m

	boolean isLocationEnabled, isNetworkEnabled, canLocalize;

	protected LocationManager locationManager;

	Location currentLocation, previousLocation;
	double latitude, longitude;

	Context mContext;

	public LocationTracker(Context context) {
		this.mContext = context;
		getInitialLocation();
	}

	public Location getInitialLocation() {
		try {
			locationManager = (LocationManager)mContext.getSystemService(LOCATION_SERVICE);
			isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if(isLocationEnabled || isNetworkEnabled) {
				/**
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							LOCATION_UPDATE_PERIOD,
							LOCATION_UPDATE_DISTANCE, this);

					Log.d("Network", "Network");

					if (locationManager != null) {
						currentLocation = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (currentLocation != null) {
							latitude = currentLocation.getLatitude();
							longitude = currentLocation.getLongitude();
						}
					}
				}*/
				// if GPS Enabled get lat/long using GPS Services
				if (isLocationEnabled) {
					if (currentLocation == null) {
						Log.e("loc_updates_requested", "yes");
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								LOCATION_UPDATE_PERIOD,
								LOCATION_UPDATE_DISTANCE, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							currentLocation = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (currentLocation != null) {
								latitude = currentLocation.getLatitude();
								longitude = currentLocation.getLongitude();
							}
						}
					}
				}
			}


		} catch(Exception e) {
			e.printStackTrace();
		}

		return currentLocation;
	}

	public Location getUpdatedLocation() {
		
		if (locationManager != null) 
			currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);		

		return currentLocation;
	}

	@Override
	public void onLocationChanged(Location location) {
		previousLocation = currentLocation;
		currentLocation = getUpdatedLocation();
		
		//float distance = previousLocation.distanceTo(currentLocation);
		
		//onLocationUpdatedListener.onLocationUpdated(distance);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
