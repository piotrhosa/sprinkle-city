package com.pfhosa.sprinklecity.model;

import java.util.ArrayList;

public class InventoryList extends ArrayList<ArrayList<InventoryItem>> {

	private static final long serialVersionUID = 1L;
	private String mUsername;
	private ArrayList<ArrayList<InventoryItem>> mInventory = new ArrayList<ArrayList<InventoryItem>>();
	private ArrayList<InventoryItem> mApple = new ArrayList<InventoryItem>();
	private ArrayList<InventoryItem> mCoin = new ArrayList<InventoryItem>();
	private ArrayList<InventoryItem> mFetch = new ArrayList<InventoryItem>();
	private ArrayList<InventoryItem> mCupcake = new ArrayList<InventoryItem>();
	
	private String[] mNameArray = {"apple", "coin", "fetch", "cupcake"};

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
	
	public String getUsername() {return mUsername;}

	public ArrayList<ArrayList<InventoryItem>> geWholeInventory() {return mInventory;}

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

				compressedItem.setItem(mNameArray[mInventory.indexOf(al)]);
				//Log.d("Item", ii.getItem() + " " + Integer.toString(ii.getValue()));
				compressedItem.setValue(compressedItem.getValue() + ii.getValue());
				compressedItem.setTimeCollected();
				compressedItem.setUsable();
			}
			compressedInventory.add(compressedItem);
		}
		return compressedInventory;
	}
}
