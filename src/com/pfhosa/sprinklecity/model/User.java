package com.pfhosa.sprinklecity.model;

public class User {
	
	String mUsername;
	String mPassword;
	String mAnimalName;
	
	public User(String username, String password, String animalName) {
		mUsername = username;
		mPassword = password;
		mAnimalName = animalName;
	}
	
	public String getCharacterName() {return mUsername;}
	
	public String getPassword() {return mPassword;}
	
	public String getAnimalName() {return mAnimalName;}
}