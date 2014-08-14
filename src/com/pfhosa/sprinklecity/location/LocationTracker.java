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

	private static final long LOCATION_UPDATE_PERIOD = 5000; //ms
	private static final long LOCATION_UPDATE_DISTANCE = 2; //m

	boolean mIsLocationEnabled, mIsNetworkEnabled, mCanLocalize;
	protected LocationManager mLocationManager;
	Location mCurrentLocation, mPreviousLocation;
	double mLatitude, mLongitude;

	Context mContext;

	public LocationTracker(Context context) {
		this.mContext = context;
		getInitialLocation();
	}

	public Location getInitialLocation() {
		try {
			mLocationManager = (LocationManager)mContext.getSystemService(LOCATION_SERVICE);
			mIsLocationEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			mIsNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if(mIsLocationEnabled || mIsNetworkEnabled) {
				/**
				if (mIsNetworkEnabled) {
					mLocationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							LOCATION_UPDATE_PERIOD,
							LOCATION_UPDATE_DISTANCE, this);

					Log.d("Network", "Network");

					if (mLocationManager != null) {
						mCurrentLocation = mLocationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (mCurrentLocation != null) {
							mLatitude = mCurrentLocation.getLatitude();
							mLongitude = mCurrentLocation.getLongitude();
						}
					}
				}*/
				// if GPS Enabled get lat/long using GPS Services
				if (mIsLocationEnabled) {
					if (mCurrentLocation == null) {
						mLocationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								LOCATION_UPDATE_PERIOD,
								LOCATION_UPDATE_DISTANCE, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (mLocationManager != null) {
							mCurrentLocation = mLocationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (mCurrentLocation != null) {
								mLatitude = mCurrentLocation.getLatitude();
								mLongitude = mCurrentLocation.getLongitude();
							}
						}
					}
				}
			}
		} catch(Exception e) {e.printStackTrace();}

		return mCurrentLocation;
	}

	public Location getUpdatedLocation() {

		if (mLocationManager != null) mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);		

		return mCurrentLocation;
	}

	@Override
	public void onLocationChanged(Location location) {
		mCurrentLocation = getUpdatedLocation();

		Intent locationUpdate = new Intent("locationUpdater");
		locationUpdate.putExtra("distance", mCurrentLocation);
		sendBroadcast(locationUpdate);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public IBinder onBind(Intent intent) {return null;}

}
