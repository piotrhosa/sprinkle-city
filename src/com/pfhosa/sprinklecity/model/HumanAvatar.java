package com.pfhosa.sprinklecity.model;

public class HumanAvatar {
	
	String mUsername;
	int mAvatarImage, mAvatarEdge, mPositionX, mPositionY, mDirection;
	
	public HumanAvatar(String username, int avatarImage, int avatarEdge, int positionX, int positionY, int direction) {
		mUsername = username;
		mAvatarImage = avatarImage;
		mAvatarEdge = avatarEdge;
		mPositionX = positionX;
		mPositionY = positionY;
		mDirection = direction;
	}
	
	// Accessors
	
	public String getUsername() {return mUsername;}
	
	public int getAvatarImage() {return mAvatarImage;}
	
	public int getAvatarEdge() {return mAvatarEdge;}
	
	public int getPositionX() {return mPositionX;}
	
	public int getPositionY() {return mPositionY;}
	
	public int getDirection() {return mDirection;}
	
	public void setPositionX(int positionX) {
		mPositionX = positionX;
	}
	
	public void setPositionY(int positionY) {
		mPositionY = positionY;
	}
	
	public void setPosition(int positionX, int positionY) {
		mPositionX = positionX;
		mPositionY = positionY;
	}
	
	public void setDirection(int direction) {
		mDirection = direction;
	}
	
	// Methods
	
	public boolean isTouchOnAvatar(int touchX, int touchY) {
		
		return 	(touchX > mPositionX) &&
				(touchX < mPositionX + mAvatarEdge) &&
				(touchY > mPositionY) &&
				(touchY < mPositionY + mAvatarEdge);	
	}
	
	public boolean swipeArrowUp(int touchX, int touchY) {

		return 	(touchX > mPositionX) &&
				(touchX < mPositionX + mAvatarEdge) &&
				(touchY < mPositionY);
	}

	public boolean swipeArrowDown(int touchX, int touchY) {

		return 	(touchX > mPositionX) &&
				(touchX < mPositionX + mAvatarEdge) &&
				(touchY > mPositionY + mAvatarEdge);
	}

	public boolean swipeArrowLeft(int touchX, int touchY) {

		return 	(touchX < mPositionX) &&
				(touchY > mPositionY) &&
				(touchY < mPositionY + mAvatarEdge); 
	}

	public boolean swipeArrowRight(int touchX, int touchY) {

		return	(touchX > mPositionX + mAvatarEdge) &&
				(touchY > mPositionY) &&
				(touchY < mPositionY + mAvatarEdge);
	}

}
