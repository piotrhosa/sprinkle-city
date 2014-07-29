package com.pfhosa.sprinklecity.model;

import android.util.Log;

public class DrawableObject {
	
	int mPositionX, mPositionY, mImage, mEdge;
	boolean mVisible;
	
	/**
	 * 
	 * @param positionX
	 * @param positionY
	 * @param image
	 * @param edge
	 * @param visible
	 */
	public DrawableObject(int positionX, int positionY, int image, int edge, boolean visible) {
		mPositionX = positionX;
		mPositionY = positionY;
		mImage = image;
		mEdge = edge;
		mVisible = visible;
	}
	
	public int getPositionX() {return mPositionX;}
	
	public int getPositionY() {return mPositionY;}
	
	public int getImage() {return mImage;}
	
	public int getEdge() {return mEdge;}
	
	public boolean getVisibility() {return mVisible;}
	
	public void setPositionX(int positionX) {mPositionX = positionX;}
	
	public void setPositionY(int positionY) {mPositionY = positionY;}
	
	public void setPosition(int positionX, int positionY) {
		mPositionX = positionX;
		mPositionY = positionY;
		Log.d("Position set", Integer.toString(mPositionY));
	}
	
	public void setImage(int image) {mImage = image;}
	
	public void setEdge(int edge) {mEdge = edge;}
	
	public void setVisibility(boolean visible) {mVisible = visible;}

	// Methods
	
	public boolean isTouchOnObject(int touchX, int touchY) {
		
		return 	(touchX > mPositionX) &&
				(touchX < mPositionX + mEdge) &&
				(touchY > mPositionY) &&
				(touchY < mPositionY + mEdge);	
	}
	
	public boolean swipeArrowUp(int touchX, int touchY) {

		return 	(touchX > mPositionX) &&
				(touchX < mPositionX + mEdge) &&
				(touchY < mPositionY);
	}

	public boolean swipeArrowDown(int touchX, int touchY) {

		return 	(touchX > mPositionX) &&
				(touchX < mPositionX + mEdge) &&
				(touchY > mPositionY + mEdge);
	}

	public boolean swipeArrowLeft(int touchX, int touchY) {

		return 	(touchX < mPositionX) &&
				(touchY > mPositionY) &&
				(touchY < mPositionY + mEdge); 
	}

	public boolean swipeArrowRight(int touchX, int touchY) {

		return	(touchX > mPositionX + mEdge) &&
				(touchY > mPositionY) &&
				(touchY < mPositionY + mEdge);
	}
	
	public boolean isObjectOnLeftFrom(int avatarX, int avatarY, int avatarEdge, int avatarEdgeMargin) {
		return	(mPositionX < avatarX + avatarEdge) &&
				(mPositionY + mEdge / 2 > avatarY + avatarEdgeMargin) &&
				(mPositionY + mEdge / 2 < avatarY + avatarEdge - avatarEdgeMargin);
	}
	
	public boolean isObjectOnRightFrom(int avatarX, int avatarY, int avatarEdge, int avatarEdgeMargin) {
		return	(mPositionX > avatarX) &&
				(mPositionY + mEdge / 2 > avatarY + avatarEdgeMargin) &&
				(mPositionY + mEdge / 2 < avatarY + avatarEdge - avatarEdgeMargin);
	}
}
