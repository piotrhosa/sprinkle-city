package com.pfhosa.sprinklecity.model;

public class AnimalCharacter {
	
	String name;
	String owner;
	int avatar;
	int sleep;
	int fitness;
	
	public AnimalCharacter(String name, String owner, int avatar, int sleep, int fitness) {
		this.name = name;
		this.owner = owner;
		this.avatar = avatar;
		this.sleep = sleep;
		this.fitness = fitness;		
	}
	
	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}
	
	public int getAvatar() {
		return avatar;
	}
	
	public int getSleep() {
		return sleep;
	}
	
	public int getFitness() {
		return fitness;
	}
}
