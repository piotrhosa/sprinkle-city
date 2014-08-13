package com.pfhosa.sprinklecity.model;

public class VirtualLocation extends DrawableObject{

	private String mOwner, mLocationType;

	public VirtualLocation(String owner, String locationType, float locationX, float locationY, int image, float locationEdge, boolean visibility) {
		super(locationX, locationY, image, locationEdge, visibility);
		mOwner = owner;
		mLocationType = locationType;
	}

	// Accessors

	public String getOwner() {return mOwner;}

	public String getLocationType() {return mLocationType;}
}
