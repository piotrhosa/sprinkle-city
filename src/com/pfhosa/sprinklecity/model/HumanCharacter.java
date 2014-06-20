package com.pfhosa.sprinklecity.model;

public class HumanCharacter {
	int avatar;
	String name;
	String job;
	int social;
	int animal;
	int business;
	
	public HumanCharacter(int avatar, String name, String job, int social, int animal, int business) {
		super();
		this.avatar = avatar;
		this.name = name;
		this.job = job;
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
