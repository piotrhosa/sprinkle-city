package com.pfhosa.sprinklecity.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.database.Database;
import com.pfhosa.sprinklecity.model.InventoryItem;

public class InventoryListFragment extends ListFragment {

	String mUsername;
	OnInventoryItemSelectedListener mInventoryItem;
	ArrayAdapter<InventoryItem> mAdapter;
	ArrayList<InventoryItem> mLoadedList;
	
	public void onAttach(Activity activity) {
		super.onAttach(getActivity());
		mInventoryItem = (OnInventoryItemSelectedListener)activity;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Database db = Database.getInstance(getActivity());		
		mLoadedList = new ArrayList<InventoryItem>();
		
		if(getArguments() != null) mUsername = getArguments().getString("Username");
		
		mLoadedList = db.getCompressedInventory(mUsername);
		
		mAdapter = new ArrayAdapter<InventoryItem>(getActivity(),
				R.layout.array_adapter_inventory_row, 
				R.id.text_item_name, 
				mLoadedList);
		
		setListAdapter(mAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if(mLoadedList.get(position).getValue() != 0) mInventoryItem.onItemSelected(position);
		else Toast.makeText(getActivity(), "You have no items of this kind.", Toast.LENGTH_SHORT).show();
	}
	
	public interface OnInventoryItemSelectedListener {public void onItemSelected(int position);}
}
