package com.pfhosa.sprinklecity.model;

public class VirtualLocation extends DrawableObject{

	String mOwner, mLocationType;

	public VirtualLocation(String owner, String locationType, int locationX, int locationY, int image, int locationEdge, boolean visibility) {
		super(locationX, locationY, image, locationEdge, visibility);
		mOwner = owner;
		mLocationType = locationType;
	}

	// Accessors

	public String getOwner() {return mOwner;}

	public String getLocationType() {return mLocationType;}
}
