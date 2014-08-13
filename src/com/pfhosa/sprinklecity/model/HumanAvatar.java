package com.pfhosa.sprinklecity.model;

public class HumanAvatar extends DrawableObject {
	
	private String mUsername;
	private int mDirection;
	
	public HumanAvatar(String username, int avatarImage, float avatarEdge, int positionX, int positionY, int direction, boolean visibility) {
		super(positionX, positionY, avatarImage, avatarEdge, visibility);
		mUsername = username;
		mDirection = direction;
	}
	
	// Accessors
	
	public String getUsername() {return mUsername;}
	
	public int getDirection() {return mDirection;}

	public void setDirection(int direction) {mDirection = direction;}
}
