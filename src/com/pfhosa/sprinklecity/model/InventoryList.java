package com.pfhosa.sprinklecity.model;

import java.io.Serializable;
import java.util.ArrayList;

public class InventoryList extends ArrayList<ArrayList<InventoryItem>> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3203110440170398902L;

	String mUsername;

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
	}

	// Acessors

	public ArrayList<ArrayList<InventoryItem>> geWholetInventory() {return mInventory;}

	public ArrayList<InventoryItem> getItemList(String listName) {return listFinder(listName);}

	public void addItem(String listName, InventoryItem item){getItemList(listName).add(item);}

	// Methods

	public int size() {return 4;}

	public ArrayList<InventoryItem> listFinder(String listName) {
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
				compressedItem.setItem(ii.getItem());
				compressedItem.setValue(compressedItem.getValue() + ii.getValue());
				compressedItem.setTimeCollected();
				compressedItem.setUsable();
			}

			compressedInventory.add(compressedItem);
		}

		return compressedInventory;
	}

	public ArrayList<InventoryItem> compressInventory(InventoryList inventory) {
		ArrayList<InventoryItem> compressedInventory = new ArrayList<InventoryItem>();

		for(ArrayList<InventoryItem> al: inventory) {
			InventoryItem compressedItem = new InventoryItem();
			for(InventoryItem ii: al) {
				compressedItem.setItem(ii.getItem());
				compressedItem.setValue(compressedItem.getValue() + ii.getValue());
				compressedItem.setTimeCollected();
				compressedItem.setUsable();
			}			
			compressedInventory.add(compressedItem);
		}

		return compressedInventory;
	}

}
