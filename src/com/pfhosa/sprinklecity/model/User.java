package com.pfhosa.sprinklecity.model;

public class User {
	
	String characterName;
	String password;
	String animalName;
	
	public User(String characterName, String password, String animalName) {
		this.characterName = characterName;
		this.password = password;
		this.animalName = animalName;
	}
	
	public String getCharacterName() {return characterName;}
	
	public String getPassword() {return password;}
	
	public String getAnimalName() {return animalName;}
}