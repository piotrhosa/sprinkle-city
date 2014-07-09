package com.pfhosa.sprinklecity.model;

public class VirtualLocation {
	
	String owner;
	int locationX, locationY;	
	
	public VirtualLocation(String owner, int locatoinX, int locationY) {
		this.owner = owner;
		this.locationX = locatoinX;
		this.locationY = locationY;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public int getLocationX() {
		return locationX;
	}
	
	public int getLocationY() {
		return locationY;
	}

}
