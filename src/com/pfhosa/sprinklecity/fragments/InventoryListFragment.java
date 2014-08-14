package com.pfhosa.sprinklecity.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.database.Database;
import com.pfhosa.sprinklecity.model.InventoryItem;

public class InventoryListFragment extends ListFragment {

	String mUsername;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Database db = Database.getInstance(getActivity());		
		ArrayList<InventoryItem> loadedList = new ArrayList<InventoryItem>();
		
		if(getArguments() != null) mUsername = getArguments().getString("Username");
		
		loadedList = db.getCompressedInventory(mUsername);
		
		ArrayAdapter<InventoryItem> adapter = new ArrayAdapter<InventoryItem>(getActivity(),
				R.layout.array_adapter_inventory_row, 
				R.id.text_item_name, 
				loadedList);
		
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {}
}
