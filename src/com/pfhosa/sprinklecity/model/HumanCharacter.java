package com.pfhosa.sprinklecity.model;

public class HumanCharacter {

	String name;
	String job;
	int avatar;
	int social;
	int animal;
	int business;
	
	public HumanCharacter(String name, String job, int avatar, int social, int animal, int business) {
		this.name = name;
		this.job = job;
		this.avatar = avatar;
		this.social = social;
		this.animal = animal;
		this.business = business;		
	}
	
	public int getAvatar() {
		return avatar;
	}
	public String getName() {
		return name;
	}
	
	public String getJob(){
		return job;
	}
	
	public int getSocialTrait() {
		return social;
	}
	
	public int getAnimalTrait() {
		return animal;
	}
	
	public int getBusinessTrait() {
		return business;
	}
}
