package com.pfhosa.sprinklecity.model;

public class VirtualLocation {
	
	String owner;
	int locationX, locationY, locationEdge;	
	
	public VirtualLocation(String owner, int locatoinX, int locationY, int locationEdge) {
		this.owner = owner;
		this.locationX = locatoinX;
		this.locationY = locationY;
		this.locationEdge = locationEdge;
	}
	
	// Accessors
	
	public String getOwner() {return owner;}
	
	public int getLocationX() {return locationX;}
	
	public int getLocationY() {return locationY;}
	
	public int getLocationEdge() {return locationEdge;}
	
	// Methods
	
	public boolean isLocationOnLeft(int avatarX, int avatarY, int avatarEdge, int avatarEdgeMargin) {
		return	(locationX < avatarX + avatarEdge) &&
				(locationY + locationEdge / 2 > avatarY + avatarEdgeMargin) &&
				(locationY + locationEdge / 2 < avatarY + avatarEdge - avatarEdgeMargin);
	}
	
	public boolean isLocationOnRight(int avatarX, int avatarY, int avatarEdge, int avatarEdgeMargin) {
		return	(locationX > avatarX) &&
				(locationY + locationEdge / 2 > avatarY + avatarEdgeMargin) &&
				(locationY + locationEdge / 2 < avatarY + avatarEdge - avatarEdgeMargin);
	}

}
