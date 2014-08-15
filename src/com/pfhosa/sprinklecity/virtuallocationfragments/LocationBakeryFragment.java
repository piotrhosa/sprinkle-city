package com.pfhosa.sprinklecity.virtuallocationfragments;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.database.Database;
import com.pfhosa.sprinklecity.database.InventoryLoaderAsyncTask;
import com.pfhosa.sprinklecity.database.WriteToRemoteAsyncTask;
import com.pfhosa.sprinklecity.model.InventoryItem;

public class LocationBakeryFragment extends Fragment {

	LinearLayout mLinearLayout;
	String mUsername;
	Database mDb;
	InventoryLoaderAsyncTask mInventoryLoader;
	
	WriteToRemoteAsyncTask addCupcakeAsyncTask;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(getArguments() != null) mUsername = getArguments().getString("Username");

		mLinearLayout = (LinearLayout)inflater.inflate(R.layout.fragment_location_bakery, container, false);

		initializeButtons();
		
		return mLinearLayout;
	}
	
	private void initializeButtons() {
		ImageButton cupcake0Button = (ImageButton) mLinearLayout.findViewById(R.id.image_cupcake_0);
		ImageButton cupcake1Button = (ImageButton) mLinearLayout.findViewById(R.id.image_cupcake_1);
		ImageButton cupcake2Button = (ImageButton) mLinearLayout.findViewById(R.id.image_cupcake_2);
		
		cupcake0Button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InventoryItem cupcake0 = new InventoryItem(mUsername, "choc cupcake", 2);
				addCupcakeAsyncTask(cupcake0);
			}
		});
		
		cupcake1Button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		
		cupcake2Button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
	}
	
	public void addCupcakeAsyncTask(InventoryItem item) {
		String urlAvatar = "http://www2.macs.hw.ac.uk/~ph109/DBConnect/insertInventoryItem.php";

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();	
		postParameters.add(new BasicNameValuePair("Username", item.getCreator()));
		postParameters.add(new BasicNameValuePair("Item", item.getItem()));
		postParameters.add(new BasicNameValuePair("Value", Integer.toString(item.getValue())));	
		postParameters.add(new BasicNameValuePair("TimeCreated", Long.toString(item.getTimeCollected())));
		postParameters.add(new BasicNameValuePair("Usable", item.getUsable() ? "1" : "0"));

		addCupcakeAsyncTask = new WriteToRemoteAsyncTask(urlAvatar, postParameters, getActivity());
		//newAvatarWeakReference = new WeakReference<WriteToRemoteAsyncTask>(newAvatarAsyncTask);

		addCupcakeAsyncTask.execute();		
	}

}
