package com.pfhosa.sprinklecity.model;

public class DrawableObject {

	private float mPositionX, mPositionY, mEdge;
	private int mImage;
	private boolean mVisible;
	
	/**
	 * 
	 * @param positionX
	 * @param positionY
	 * @param image
	 * @param edge
	 * @param visible
	 */
	public DrawableObject(float positionX, float positionY, int image, float edge, boolean visible) {
		mPositionX = positionX;
		mPositionY = positionY;
		mImage = image;
		mEdge = edge;
		mVisible = visible;
	}
	
	public float getPositionX() {return mPositionX;}
	
	public float getPositionY() {return mPositionY;}
	
	public int getImage() {return mImage;}
	
	public float getEdge() {return mEdge;}
	
	public boolean getVisibility() {return mVisible;}
	
	public void setPositionX(float positionX) {mPositionX = positionX;}
	
	public void setPositionY(float positionY) {mPositionY = positionY;}
	
	public void setPosition(float positionX, float positionY) {
		mPositionX = positionX;
		mPositionY = positionY;
	}
	
	public void setImage(int image) {mImage = image;}
	
	public void setEdge(int edge) {mEdge = edge;}
	
	public void setVisibility(boolean visible) {mVisible = visible;}

	// Methods
	
	public boolean isTouchOnObject(float touchX, float touchY) {
		
		return 	(touchX > mPositionX) &&
				(touchX < mPositionX + mEdge) &&
				(touchY > mPositionY) &&
				(touchY < mPositionY + mEdge);	
	}
	
	public boolean swipeArrowUp(float touchX, float touchY) {

		return 	(touchX > mPositionX) &&
				(touchX < mPositionX + mEdge) &&
				(touchY < mPositionY);
	}

	public boolean swipeArrowDown(float touchX, float touchY) {

		return 	(touchX > mPositionX) &&
				(touchX < mPositionX + mEdge) &&
				(touchY > mPositionY + mEdge);
	}

	public boolean swipeArrowLeft(float touchX, float touchY) {

		return 	(touchX < mPositionX) &&
				(touchY > mPositionY) &&
				(touchY < mPositionY + mEdge); 
	}

	public boolean swipeArrowRight(float touchX, float touchY) {

		return	(touchX > mPositionX + mEdge) &&
				(touchY > mPositionY) &&
				(touchY < mPositionY + mEdge);
	}
	
	public boolean isObjectOnLeftFrom(float avatarX, float avatarY, float avatarEdge, float avatarEdgeMargin) {
		return	(mPositionX < avatarX + avatarEdge / 2) &&
				(mPositionY + mEdge / 2 > avatarY + avatarEdgeMargin) &&
				(mPositionY + mEdge / 2 < avatarY + avatarEdge - avatarEdgeMargin);
	}
	
	public boolean isObjectOnRightFrom(float avatarX, float avatarY, float avatarEdge, float avatarEdgeMargin) {
		return	(mPositionX > avatarX + avatarEdge / 2) &&
				(mPositionY + mEdge / 2 > avatarY + avatarEdgeMargin) &&
				(mPositionY + mEdge / 2 < avatarY + avatarEdge - avatarEdgeMargin);
	}
}
