package com.pfhosa.sprinklecity.model;

public class VirtualLocation {
	
	String mOwner;
	int mLocationX, mLocationY, mLocationEdge;	
	
	public VirtualLocation(String owner, int locationX, int locationY, int locationEdge) {
		mOwner = owner;
		mLocationX = locationX;
		mLocationY = locationY;
		mLocationEdge = locationEdge;
	}
	
	// Accessors
	
	public String getOwner() {return mOwner;}
	
	public int getLocationX() {return mLocationX;}
	
	public int getLocationY() {return mLocationY;}
	
	public int getLocationEdge() {return mLocationEdge;}
	
	// Methods
	
	public boolean isLocationOnLeft(int avatarX, int avatarY, int avatarEdge, int avatarEdgeMargin) {
		return	(mLocationX < avatarX + avatarEdge) &&
				(mLocationY + mLocationEdge / 2 > avatarY + avatarEdgeMargin) &&
				(mLocationY + mLocationEdge / 2 < avatarY + avatarEdge - avatarEdgeMargin);
	}
	
	public boolean isLocationOnRight(int avatarX, int avatarY, int avatarEdge, int avatarEdgeMargin) {
		return	(mLocationX > avatarX) &&
				(mLocationY + mLocationEdge / 2 > avatarY + avatarEdgeMargin) &&
				(mLocationY + mLocationEdge / 2 < avatarY + avatarEdge - avatarEdgeMargin);
	}

}
