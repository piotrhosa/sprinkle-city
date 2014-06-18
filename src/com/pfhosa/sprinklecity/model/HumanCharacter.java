package com.pfhosa.sprinklecity.model;

public class HumanCharacter {
	String name;
	String job;
	short social;
	short animal;
	short business;
	
	public HumanCharacter(String name, String job, short social, short animal, short business) {
		super();
		this.name = name;
		this.job = job;
		this.social = social;
		this.animal = animal;
		this.business = business;		
	}
	
	public String getName() {
		return name;
	}
	
	public String getJob(){
		return job;
	}
	
	public short getSocialTrait() {
		return social;
	}
	
	public short getAnimalTrait() {
		return animal;
	}
	
	public short getBusinessTrait() {
		return business;
	}
}
