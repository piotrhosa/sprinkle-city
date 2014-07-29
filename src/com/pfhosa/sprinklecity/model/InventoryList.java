package com.pfhosa.sprinklecity.model;

import java.util.ArrayList;

public class InventoryList {
	
	String mUsername;
	
	ArrayList<ArrayList<InventoryItem>> mInventory = new ArrayList<ArrayList<InventoryItem>>();
	
	ArrayList<InventoryItem> mApples = new ArrayList<InventoryItem>();
	
	public InventoryList(String username) {
		mUsername = username;
		mInventory.add(mApples);
	}
	
	// Acessors
	
	public ArrayList<ArrayList<InventoryItem>> geWholetInventory() {return mInventory;}
	
	public ArrayList<InventoryItem> getItemList(String listName) {return listFinder(listName);}

	public void addItem(String listName, InventoryItem item){getItemList(listName).add(item);}
	
	// Methods
	
	public ArrayList<InventoryItem> listFinder(String listName) {
		switch(listName) {
		case "apples": return mApples;
		default: return null;
		}
	}
}
