package com.pfhosa.sprinklecity.model;

public class InventoryItem {
	
	String mItem;
	int mValue;
	long mTimeCollected;
	boolean mUsable;

	public InventoryItem(String item, int value) {
		mItem = item;
		mValue = value;
		mTimeCollected = System.currentTimeMillis() / 1000L;
		mUsable = true;
	}
	
	// Acessors
	
	public String getItem() {return mItem;}
	
	public int getValue() {return mValue;}
	
	public long getTimeCollected() {return mTimeCollected;}
	
	public void setUnusable() {mUsable = false;}
	
}
