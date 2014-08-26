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

public class InventoryDetailFragment extends ListFragment {
	
	Database mDb = Database.getInstance(getActivity());	
	ArrayList<InventoryItem> mLoadedList = new ArrayList<InventoryItem>();
	OnInventoryExchangeListener mExchangeListener;
	ArrayAdapter<InventoryItem> mAdapter;
	String mUsername, mItem;

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mExchangeListener = (OnInventoryExchangeListener)activity;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if(getArguments() != null) {
			mUsername = getArguments().getString("Username");
			mItem = getArguments().getString("Item");
		}

		Database db = Database.getInstance(getActivity());		
		
		mLoadedList = db.getAllSameItems(mUsername, mItem);       
		
		mAdapter = new ArrayAdapter<InventoryItem>(getActivity(),
				R.layout.array_adapter_inventory_concrete_row, 
				R.id.text2_item_name, 
				mLoadedList);
		
		setListAdapter(mAdapter);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if(mLoadedList.get(position).getValue() != 0) mExchangeListener.exchangeSelected(mLoadedList.get(position));
		else Toast.makeText(getActivity(), "You have no items of this kind.", Toast.LENGTH_SHORT).show();

	}
	
	public interface OnInventoryExchangeListener {
		public void exchangeSelected(InventoryItem item);
	}

}
