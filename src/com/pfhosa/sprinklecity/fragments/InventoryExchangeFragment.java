package com.pfhosa.sprinklecity.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.pfhosa.sprinklecity.R;

public class InventoryExchangeFragment extends Fragment {

	OnNfcNeededListener mNfcListener;
	LinearLayout mLinearLayout;
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		mNfcListener = (OnNfcNeededListener)activity;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		mLinearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_inventory_exchange, container, false);

		mNfcListener.nfcNeeded();
		
		return mLinearLayout ;
	}
	
	public interface OnNfcNeededListener {
		public void nfcNeeded();
	}
}
