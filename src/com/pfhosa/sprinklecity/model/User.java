package com.pfhosa.sprinklecity.model;

public class User {
	
	private String mUsername, mPassword, mAnimalName;
	
	public User(String username, String password, String animalName) {
		mUsername = username;
		mPassword = password;
		mAnimalName = animalName;
	}
	
	public String getCharacterName() {return mUsername;}
	
	public String getPassword() {return mPassword;}
	
	public String getAnimalName() {return mAnimalName;}
}