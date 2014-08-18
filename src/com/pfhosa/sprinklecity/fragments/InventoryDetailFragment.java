package com.pfhosa.sprinklecity.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.database.Database;
import com.pfhosa.sprinklecity.model.InventoryItem;

public class InventoryDetailFragment extends ListFragment {
	
	Database mDb = Database.getInstance(getActivity());		
	ArrayList<InventoryItem> mLoadedList = new ArrayList<InventoryItem>();
	String[] list = {"dog", "cat"};
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		//if(getArguments() != null) mUsername = getArguments().getString("Username");
		
		//loadedList = db.getCompressedInventory(mUsername);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.array_adapter_inventory_row, 
				R.id.text_item_name, 
				list);
		
		setListAdapter(adapter);
	}

}
