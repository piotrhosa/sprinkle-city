package com.pfhosa.sprinklecity.model;

public class HumanAvatar {
	
	String username;
	int avatarImage, avatarEdge, positionX, positionY, direction;
	
	public HumanAvatar(String username, int avatarImage, int avatarEdge, int positionX, int positionY, int direction) {
		this.username = username;
		this.avatarImage = avatarImage;
		this.avatarEdge = avatarEdge;
		this.positionX = positionX;
		this.positionY = positionY;
		this.direction = direction;
	}
	
	// Accessors
	
	public String getUsername() {return username;}
	
	public int getAvatarImage() {return avatarImage;}
	
	public int getAvatarEdge() {return avatarEdge;}
	
	public int getPositionX() {return positionX;}
	
	public int getPositionY() {return positionY;}
	
	public int getDirection() {return direction;}
	
	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}
	
	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}
	
	public void setPosition(int positionX, int positionY) {
		this.positionX = positionX;
		this.positionY = positionY;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	// Methods
	
	public boolean isTouchOnAvatar(int touchX, int touchY) {
		
		return 	(touchX > positionX) &&
				(touchX < positionX + avatarEdge) &&
				(touchY > positionY) &&
				(touchY < positionY + avatarEdge);	
	}
	
	public boolean swipeArrowUp(int touchX, int touchY) {

		return 	(touchX > positionX) &&
				(touchX < positionX + avatarEdge) &&
				(touchY < positionY);
	}

	public boolean swipeArrowDown(int touchX, int touchY) {

		return 	(touchX > positionX) &&
				(touchX < positionX + avatarEdge) &&
				(touchY > positionY + avatarEdge);
	}

	public boolean swipeArrowLeft(int touchX, int touchY) {

		return 	(touchX < positionX) &&
				(touchY > positionY) &&
				(touchY < positionY + avatarEdge); 
	}

	public boolean swipeArrowRight(int touchX, int touchY) {

		return	(touchX > positionX + avatarEdge) &&
				(touchY > positionY) &&
				(touchY < positionY + avatarEdge);
	}

}
