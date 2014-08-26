package com.pfhosa.sprinklecity.fragments;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.database.CustomHttpClient;
import com.pfhosa.sprinklecity.database.WriteToRemoteAsyncTask;
import com.pfhosa.sprinklecity.model.DrawableObject;
import com.pfhosa.sprinklecity.model.HumanAvatar;
import com.pfhosa.sprinklecity.model.VirtualLocation;
import com.pfhosa.sprinklecity.ui.HomeActivity;

public class VirtualMapFragment extends Fragment {

	// Constants
	public static final int PIXELS_PER_METER = 10;
	public static final float DEFAULT_SCREEN_HEIGHT = 1920f;
	public static final float AVATAR_EDGE = 300f / DEFAULT_SCREEN_HEIGHT;
	public static final float AVATAR_EDGE_MARGIN = 0f / DEFAULT_SCREEN_HEIGHT;
	public static final float LOCATION_EDGE = 300f / DEFAULT_SCREEN_HEIGHT;
	public static final float ARROWS_EDGE = 600f / DEFAULT_SCREEN_HEIGHT;
	public static final float CORNER_EDGE = 300f / DEFAULT_SCREEN_HEIGHT;

	// Location
	static float mDistance;
	static Location mPreviousLocation;
	LocationReceiver mLocationReceiver;

	// View
	float mRefHeight, mRefWidth;
	private MapSurfaceView mapSurfaceView;
	HumanAvatar mHumanAvatar;
	DrawableObject mArrows, mHome, mInventory;
	ArrayList<VirtualLocation> mVirtualLocations= new ArrayList<VirtualLocation>();

	// Open new Fragment listener
	OnLocationSelectedListener mLocationSelected;

	String mUsername;
	boolean mLastTouchDown;

	public void onAttach (Activity activity) {
		super.onAttach(activity);
		mLocationSelected = (OnLocationSelectedListener) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
		mRefHeight = displayMetrics.heightPixels;
		mRefWidth = displayMetrics.widthPixels;

		if(getArguments() != null) {
			mUsername = getArguments().getString("Username");
			int avatar = getArguments().getInt("Avatar");

			makeNewAvatar(mUsername, avatar);
		}

		if(mapSurfaceView == null) mapSurfaceView = new MapSurfaceView(getActivity());

		LocationReceiver mLocationReceiver = new LocationReceiver();
		getActivity().registerReceiver(mLocationReceiver, new IntentFilter("newLocationIntent"));

		new GetAvatarAsyncTask().execute();
		new GetLocationsAsyncTask().execute();

		return mapSurfaceView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if(mapSurfaceView != null) mapSurfaceView.surfaceRestart();
	}

	@Override
	public void onPause() {
		super.onPause();
		
		if (mapSurfaceView != null)mapSurfaceView.surfaceDestroyed(mapSurfaceView.getHolder());

		if(mLocationReceiver != null) {
			getActivity().unregisterReceiver(mLocationReceiver);
			mLocationReceiver = null;
		}
		updateAvatar();
	}

	public class LocationReceiver extends BroadcastReceiver {

		double currentLatitude, currentLongitude;
		Location currentLocation;

		@Override
		public void onReceive(Context context, Intent intent) {

			try {
				currentLatitude = intent.getExtras().getDouble("latitude");
				currentLongitude = intent.getExtras().getDouble("longitude");

				currentLocation = new Location("currentLocation");

				currentLocation.setLatitude(currentLatitude);
				currentLocation.setLongitude(currentLongitude);

				setNewDistance(currentLocation);

			} catch(NullPointerException e) {e.printStackTrace();}	
		}
	}

	public void makeNewAvatar(String username, int avatar) {
		mHumanAvatar = new HumanAvatar(username, avatar, AVATAR_EDGE, 0, 0, -1, true);	
	}

	public void setNewDistance(Location currentLocation) {

		if(mPreviousLocation == null) mDistance = 0;
		else mDistance = mPreviousLocation.distanceTo(currentLocation);		

		Log.d("New mDistance", Float.toString(mDistance));
		
		mPreviousLocation = currentLocation;

		DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
		float pxHeight = displayMetrics.heightPixels;

		if(0 < mHumanAvatar.getPositionY() + (float)(PIXELS_PER_METER * mDistance * mHumanAvatar.getDirection() / DEFAULT_SCREEN_HEIGHT) &&
				(pxHeight - mHumanAvatar.getEdge() * mRefHeight) / DEFAULT_SCREEN_HEIGHT > mHumanAvatar.getPositionY() + (float)(PIXELS_PER_METER * mDistance * mHumanAvatar.getDirection() / DEFAULT_SCREEN_HEIGHT) &&
				mDistance < 20) {

			mHumanAvatar.setPositionY(mHumanAvatar.getPositionY() + (float)(PIXELS_PER_METER * mDistance * mHumanAvatar.getDirection() / DEFAULT_SCREEN_HEIGHT));
		}

	}

	private class MapSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

		private SurfaceHandler surfaceHandler;
		private SurfaceHolder surfaceHolder;

		float touchX, touchY;

		public MapSurfaceView(Context context) {
			super(context);
			setFocusable(true);

			surfaceHolder = getHolder();
			surfaceHolder.addCallback(this);

			surfaceHandler = new SurfaceHandler(this);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,	int mRefHeight) {}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {surfaceHandler.restartSurface();}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {surfaceHandler.terminateSurface();}

		public void surfaceRestart(){if(surfaceHandler != null) surfaceHandler.restartSurface();}

		@Override 
		public boolean onTouchEvent(MotionEvent event){

			touchX = (float)event.getX() / mRefHeight;
			touchY = (float)event.getY() / mRefHeight;

			if(event.getAction() == MotionEvent.ACTION_DOWN) {
				if(mHumanAvatar.isTouchOnObject(touchX, touchY)) {
					mArrows.setVisibility(true);
					mLastTouchDown = true;
				} else mLastTouchDown = false;
				if(mHome.isTouchOnObject(touchX, touchY)) {
					Intent openHome = new Intent(getActivity(), HomeActivity.class);
					startActivity(openHome);
				}
				if(mInventory.isTouchOnObject(touchX, touchY)) {
					mLocationSelected.onLocationSelected("inventory");
					//Toast.makeText(getActivity(), "Inventory", Toast.LENGTH_SHORT).show();
				}
			}

			if(event.getAction() == MotionEvent.ACTION_UP && mLastTouchDown) {

				if(mHumanAvatar.isTouchOnObject(touchX, touchY))
					mArrows.setVisibility(false);

				if(mHumanAvatar.swipeArrowUp(touchX, touchY)) {
					mArrows.setVisibility(false);
					mHumanAvatar.setDirection(-1);
					Toast.makeText(getActivity(), "You are going up.", Toast.LENGTH_SHORT).show();
				}

				if(mHumanAvatar.swipeArrowDown(touchX, touchY)) {
					mArrows.setVisibility(false);
					mHumanAvatar.setDirection(1);
					Toast.makeText(getActivity(), "You are going down.", Toast.LENGTH_SHORT).show();
				}

				if(mHumanAvatar.swipeArrowLeft(touchX, touchY)) {
					mArrows.setVisibility(false);
					//Toast.makeText(getActivity(), "Left", Toast.LENGTH_SHORT).show();
					if("noLocation" != surfaceHandler.locationToLeftExists()){
						mLocationSelected.onLocationSelected(surfaceHandler.locationToLeftExists());
					}
				}

				if(mHumanAvatar.swipeArrowRight(touchX, touchY)) {
					mArrows.setVisibility(false);
					//Toast.makeText(getActivity(), "Right", Toast.LENGTH_SHORT).show();
					if("noLocation" != surfaceHandler.locationToRightExists()){
						mLocationSelected.onLocationSelected(surfaceHandler.locationToRightExists());						
					}
				}
			}
			return true;   
		}
	}

	private class SurfaceHandler implements Runnable {
		// Class fields
		private SurfaceHolder mSurfaceHolder;
		private MapSurfaceView mMapSurfaceView;
		private Canvas mCanvas = null;
		private final Handler mHandler;			

		// View
		Bitmap backgroundBitmap, characterBitmap, mArrowsBitmap, inventoryBitmap, homeBitmap;
		Bitmap farmBitmap, bakeryBitmap, postOfficeBitmap, parkBitmap, locationBitmap;
		Bitmap scaledBackgroundBitmap, scaledCharacterBitmap, scaledArrowsBitmap, scaledInventoryBitmap, scaledHomeBitmap;
		Bitmap scaledFarmBitmap, scaledBakeryBitmap, scaledPostOfficeBitmap, scaledParkBitmap, scaledLocationBitmap;
		int newWidth, newHeight;

		public SurfaceHandler(MapSurfaceView mapSurfaceView){
			mSurfaceHolder = mapSurfaceView.getHolder();
			mMapSurfaceView = mapSurfaceView;
			mCanvas = new Canvas();

			mHandler=new Handler();
			mHandler.post(this);

			doPrepare();
		}

		public void terminateSurface(){mHandler.removeCallbacks(this);}

		public void restartSurface() {mHandler.post(this);}

		@Override
		public void run() {
			mHandler.postDelayed(this,500);	
			try {
				mCanvas = mSurfaceHolder.lockCanvas(null);
				if (mCanvas == null) mSurfaceHolder = mMapSurfaceView.getHolder();
				else doDraw(mCanvas);				
			} finally {
				if (mCanvas != null) mSurfaceHolder.unlockCanvasAndPost(mCanvas);
			}
		}

		/**
		 * Reference screen size is 1920 x 1080. A scaling factor is determined when DisplayMetrics are obtained.
		 */
		public void doPrepare() {
			synchronized (mSurfaceHolder) {			
				DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
				float pxHeight = displayMetrics.heightPixels;
				float pxWidth = displayMetrics.widthPixels;

				mArrows = new DrawableObject(0, 0, R.drawable.arrows, ARROWS_EDGE, false);
				mInventory = new DrawableObject((float)(pxWidth / mRefHeight - LOCATION_EDGE), 0, R.drawable.button_inventory, LOCATION_EDGE, true);
				mHome = new DrawableObject(0, 0, R.drawable.button_home, LOCATION_EDGE, true);

				backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.street_view);
				characterBitmap = BitmapFactory.decodeResource(getResources(), mHumanAvatar.getImage());
				mArrowsBitmap = BitmapFactory.decodeResource(getResources(), mArrows.getImage());
				inventoryBitmap = BitmapFactory.decodeResource(getResources(), mInventory.getImage());
				homeBitmap = BitmapFactory.decodeResource(getResources(), mHome.getImage());

				farmBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location_farm);
				bakeryBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location_shop);
				postOfficeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location_storage);
				parkBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location_park);
				locationBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location_temple);

				float scaleHeight = (float)backgroundBitmap.getHeight()/(float)pxHeight;
				float scaleWidth = (float)backgroundBitmap.getWidth()/(float)pxWidth;

				newHeight = Math.round(backgroundBitmap.getHeight()/scaleHeight);
				newWidth = Math.round(backgroundBitmap.getWidth()/scaleWidth);

				scaledBackgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, newWidth, newHeight, true);
				scaledCharacterBitmap = Bitmap.createScaledBitmap(characterBitmap, (int)(mHumanAvatar.getEdge() * mRefHeight), (int)(mHumanAvatar.getEdge()* mRefHeight), true);
				scaledArrowsBitmap = Bitmap.createScaledBitmap(mArrowsBitmap, (int)(ARROWS_EDGE * mRefHeight), (int)(ARROWS_EDGE * mRefHeight), true);
				scaledInventoryBitmap = Bitmap.createScaledBitmap(inventoryBitmap, (int)(mInventory.getEdge() * mRefHeight), (int)(mInventory.getEdge() * mRefHeight), true);
				scaledHomeBitmap = Bitmap.createScaledBitmap(homeBitmap, (int)(mHome.getEdge() * mRefHeight), (int)(mHome.getEdge() * mRefHeight), true);

				int adjustedEdge = (int)(LOCATION_EDGE * mRefHeight);
				scaledFarmBitmap = Bitmap.createScaledBitmap(farmBitmap, adjustedEdge, adjustedEdge, true);
				scaledBakeryBitmap = Bitmap.createScaledBitmap(bakeryBitmap, adjustedEdge, adjustedEdge, true);
				scaledPostOfficeBitmap = Bitmap.createScaledBitmap(postOfficeBitmap, adjustedEdge, adjustedEdge, true);
				scaledParkBitmap = Bitmap.createScaledBitmap(parkBitmap, adjustedEdge, adjustedEdge, true);
				scaledLocationBitmap = Bitmap.createScaledBitmap(locationBitmap, adjustedEdge, adjustedEdge, true);

				if(mHumanAvatar.getPositionX() == 0 && mHumanAvatar.getPositionY() == 0)
					mHumanAvatar.setPosition(0.2f - AVATAR_EDGE_MARGIN, 0.8f);		
			}
		}

		public Bitmap getLocationBitmap(String locationType) {			
			switch(locationType) {
			case "farmer": return scaledFarmBitmap;
			case "postman": return scaledPostOfficeBitmap;
			case "baker": return scaledBakeryBitmap;
			case "park": return scaledParkBitmap;
			}

			return scaledLocationBitmap;
		}

		public void doDraw(Canvas canvas){

			// Order of Bitmaps is important (they are drawn in this order).

			canvas.drawBitmap(scaledBackgroundBitmap, 0,  0, null);

			for(VirtualLocation vl : mVirtualLocations) 
				canvas.drawBitmap(getLocationBitmap(vl.getLocationType()), vl.getPositionX() * mRefHeight, vl.getPositionY() * mRefHeight, null);			

			canvas.drawBitmap(scaledHomeBitmap, mHome.getPositionX() * mRefHeight, mHome.getPositionY() * mRefHeight, null);
			canvas.drawBitmap(scaledInventoryBitmap, mInventory.getPositionX() * mRefHeight, mInventory.getPositionY() * mRefHeight, null);
			canvas.drawBitmap(scaledCharacterBitmap, mHumanAvatar.getPositionX() * mRefHeight, mHumanAvatar.getPositionY() * mRefHeight, null);

			if(mArrows.getVisibility())
				canvas.drawBitmap(scaledArrowsBitmap, (mHumanAvatar.getPositionX() - 150 / DEFAULT_SCREEN_HEIGHT) * mRefHeight, (mHumanAvatar.getPositionY() - 200 / DEFAULT_SCREEN_HEIGHT) * mRefHeight, null);

		}

		public String locationToLeftExists() {

			for(VirtualLocation vl : mVirtualLocations) { 
				if(vl.isObjectOnLeftFrom(mHumanAvatar.getPositionX(),
						mHumanAvatar.getPositionY(), 
						mHumanAvatar.getEdge(), 
						AVATAR_EDGE_MARGIN))
					return vl.getLocationType();
			}
			return "noLocation";
		}

		public String locationToRightExists() {

			for(VirtualLocation vl : mVirtualLocations) { 
				if(vl.isObjectOnRightFrom(mHumanAvatar.getPositionX(),
						mHumanAvatar.getPositionY(), 
						mHumanAvatar.getEdge(), 
						AVATAR_EDGE_MARGIN))
					return vl.getLocationType();
			}
			return "noLocation";
		}
	}


	public void updateAvatar() {
		String url = "http://www2.macs.hw.ac.uk/~ph109/DBConnect/updateHumanAvatar.php";
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		
		postParameters.add(new BasicNameValuePair("Username", mHumanAvatar.getUsername()));
		postParameters.add(new BasicNameValuePair("PositionX", Float.toString(mHumanAvatar.getPositionX())));
		postParameters.add(new BasicNameValuePair("PositionY", Float.toString(mHumanAvatar.getPositionY())));
		postParameters.add(new BasicNameValuePair("Direction", Integer.toString(mHumanAvatar.getDirection())));

		WriteToRemoteAsyncTask updateHumanAvatar = new WriteToRemoteAsyncTask(url, postParameters, getActivity());

		updateHumanAvatar.execute();
	}

	public class GetLocationsAsyncTask extends AsyncTask<Void, Void, Void> {

		String username, locationType;
		float locationX, locationY;

		protected Void doInBackground(Void... params) {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

			String response = null;

			try {
				response = CustomHttpClient.executeHttpPost("http://www2.macs.hw.ac.uk/~ph109/DBConnect/getLocations.php", postParameters);

				String result = response.toString();  

				try{
					JSONArray jArray = new JSONArray(result);		

					for(int i = 0; i < jArray.length(); ++i) {
						JSONObject json_data = jArray.getJSONObject(i);
						//TODO get image from remote
						int image = 0;

						username = json_data.getString("Username");
						locationType = json_data.getString("Job");
						locationX = Float.parseFloat(json_data.getString("LocationX"));
						locationY = Float.parseFloat(json_data.getString("LocationY"));

						mVirtualLocations.add(new VirtualLocation(username, locationType, locationX, locationY, image, LOCATION_EDGE, true));
					}
				} 
				catch(JSONException e){Log.e("log_tag", "Error parsing data "+ e.toString());}   
			} catch (Exception e) {Log.e("log_tag","Error in http connection!!" + e.toString());}  

			return null;
		}

		public void setLocationsArray(ArrayList<VirtualLocation> vl) {mVirtualLocations = vl;}

		public ArrayList<VirtualLocation> getLocationsArray() {return mVirtualLocations;}
	}

	public class GetAvatarAsyncTask extends AsyncTask<Void, Void, Void> {

		String username, owner;
		float positionX, positionY;
		int direction;

		protected Void doInBackground(Void... params) {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("Username", mHumanAvatar.getUsername()));

			String response = null;

			try {
				response = CustomHttpClient.executeHttpPost("http://www2.macs.hw.ac.uk/~ph109/DBConnect/getHumanAvatar.php", postParameters);

				String result = response.toString();  

				try{
					JSONArray jArray = new JSONArray(result);		

					for(int i = 0; i < jArray.length(); ++i){
						JSONObject json_data = jArray.getJSONObject(i);

						positionX = Float.parseFloat(json_data.getString("PositionX"));
						positionY = Float.parseFloat(json_data.getString("PositionY"));
						direction = Integer.parseInt(json_data.getString("Direction"));

						if(positionX != 0 && positionY != 0) mHumanAvatar.setPosition(positionX, positionY);
						else mHumanAvatar.setPosition(0.2f, 0.7f);
						mHumanAvatar.setDirection(direction);
					}
				} catch(JSONException e){Log.e("log_tag", "Error parsing data "+ e.toString());}   
			} catch (Exception e) {Log.e("log_tag","Error in http connection!!" + e.toString());}  

			return null;
		}
		protected void onPostExecute(Void result) {}
	}

	public interface OnLocationSelectedListener {public void onLocationSelected(String location);}
}