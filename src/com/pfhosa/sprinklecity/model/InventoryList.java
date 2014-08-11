package com.pfhosa.sprinklecity.model;

import java.io.Serializable;
import java.util.ArrayList;

import android.util.Log;

public class InventoryList extends ArrayList<ArrayList<InventoryItem>> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3203110440170398902L;

	String mUsername;
	
	String[] mNameArray = {"apple", "coin", "fetch", "cupcake"};

	ArrayList<ArrayList<InventoryItem>> mInventory = new ArrayList<ArrayList<InventoryItem>>();

	ArrayList<InventoryItem> mApple = new ArrayList<InventoryItem>();
	ArrayList<InventoryItem> mCoin = new ArrayList<InventoryItem>();
	ArrayList<InventoryItem> mFetch = new ArrayList<InventoryItem>();
	ArrayList<InventoryItem> mCupcake = new ArrayList<InventoryItem>();

	/**
	 * 
	 * @param username
	 */
	public InventoryList(String username) {
		mUsername = username;
		mInventory.add(mApple);
		mInventory.add(mCoin);
		mInventory.add(mFetch);
		mInventory.add(mCupcake);

		mApple.add(new InventoryItem());
		mCoin.add(new InventoryItem());
		mFetch.add(new InventoryItem());
		mCupcake.add(new InventoryItem());
	}

	// Acessors

	public ArrayList<ArrayList<InventoryItem>> geWholetInventory() {return mInventory;}

	public ArrayList<InventoryItem> getItemList(String listName) {return listFinder(listName);}

	public void addItem(String listName, InventoryItem item){getItemList(listName).add(item);}

	public ArrayList<InventoryItem> getList(int index) {return mInventory.get(index);}

	public int getSize() {return mInventory.size();}

	// Methods

	public int size() {return 4;}

	private ArrayList<InventoryItem> listFinder(String listName) {
		switch(listName) {
		case "apple": return mApple;
		case "coin": return mCoin;
		case "fetch": return mFetch;
		case "cupcake": return mCupcake;
		default: return null;
		}
	}

	public ArrayList<InventoryItem> compressInventory() {
		ArrayList<InventoryItem> compressedInventory = new ArrayList<InventoryItem>();

		for(ArrayList<InventoryItem> al: mInventory) {
			InventoryItem compressedItem = new InventoryItem();
			
			for(InventoryItem ii: al) {
				
				Log.d("Value only", Integer.toString(ii.getValue()));
				compressedItem.setItem(mNameArray[mInventory.indexOf(al)]);
				compressedItem.setValue(compressedItem.getValue() + ii.getValue());
				compressedItem.setTimeCollected();
				compressedItem.setUsable();
			}
			compressedInventory.add(compressedItem);
			Log.d("Item and value", Integer.toString(compressedItem.getValue()));
		}
		return compressedInventory;
	}
}
