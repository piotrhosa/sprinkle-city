package com.pfhosa.sprinklecity.model;

public class DrawableObject {
	
	int mPositionX, mPositionY, mImage;
	boolean mVisible;
	
	public DrawableObject(int positionX, int positionY, int image, boolean visible) {
		mPositionX = positionX;
		mPositionY = positionY;
		mImage = image;
		mVisible = visible;
	}
	
	public int getPositionX() {return mPositionX;}
	
	public int getPositionY() {return mPositionY;}
	
	public int getImage() {return mImage;}
	
	public boolean getVisibility() {return mVisible;}
	
	public void setPositionX(int positionX) {mPositionX = positionX;}
	
	public void setPositionY(int positionY) {mPositionY = positionY;}
	
	public void setImage(int image) {mImage = image;}
	
	public void setVisibility(boolean visible) {mVisible = visible;}

}
