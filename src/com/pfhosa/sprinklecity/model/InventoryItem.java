package com.pfhosa.sprinklecity.model;

public class InventoryItem {
	
	String mItem;
	int mValue;
	long mTimeCollected;
	boolean mUsable;

	/**
	 * 
	 * @param item
	 * @param value
	 */
	public InventoryItem(String item, int value) {
		mItem = item;
		mValue = value;
		mTimeCollected = System.currentTimeMillis() / 1000L;
		mUsable = true;
	}
	
	/**
	 * 
	 * @param item
	 * @param value
	 * @param timeCollected
	 * @param usable
	 */
	public InventoryItem(String item, int value, long timeCollected, boolean usable) {
		mItem = item;
		mValue = value;
		mTimeCollected = timeCollected;
		mUsable = usable;
	}
	
	// Acessors
	
	public String getItem() {return mItem;}
	
	public int getValue() {return mValue;}
	
	public long getTimeCollected() {return mTimeCollected;}
	
	public boolean getUsable() {return mUsable;}
	
	public void setUnusable() {mUsable = false;}
	
}
