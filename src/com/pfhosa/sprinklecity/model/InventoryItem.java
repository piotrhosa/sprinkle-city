package com.pfhosa.sprinklecity.model;

public class InventoryItem {
	
	String mItem;
	long mTimeCollected;
	boolean mUsable;

	public InventoryItem(String item) {
		mItem = item;
		mTimeCollected = System.currentTimeMillis() / 1000L;
		mUsable = true;
	}
	
	// Acessors
	
	public String getItem() {return mItem;}
	
	public long getTimeCollected() {return mTimeCollected;}
	
	public void setUnusable() {mUsable = false;}
	
}
