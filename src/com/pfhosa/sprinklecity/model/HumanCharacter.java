package com.pfhosa.sprinklecity.model;

import android.os.Parcel;
import android.os.Parcelable;

public class HumanCharacter implements Parcelable {

	String name;
	int avatar;
	String job;
	int social;
	int animal;
	int business;
	
	public HumanCharacter(String name, int avatar, String job, int social, int animal, int business) {
		this.name = name;
		this.avatar = avatar;
		this.job = job;
		this.social = social;
		this.animal = animal;
		this.business = business;		
	}
	
	// Accessors
	
	public String getName() {return name;}
	
	public int getAvatar() {return avatar;}
	
	public String getJob(){return job;}
	
	public int getSocialTrait() {return social;}
	
	public int getAnimalTrait() {return animal;}
	
	public int getBusinessTrait() {return business;}
	
	// Parcelable implementation
	
	public int describeContents() {return 0;}
	
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getName());
        out.writeInt(getAvatar());
        out.writeString(getJob());
        out.writeInt(getSocialTrait());
        out.writeInt(getAnimalTrait());
        out.writeInt(getBusinessTrait());
    }

    public static final Parcelable.Creator<HumanCharacter> CREATOR = new Parcelable.Creator<HumanCharacter>() {
        public HumanCharacter createFromParcel(Parcel in) {
            return new HumanCharacter(in);
        }

        public HumanCharacter[] newArray(int size) {
            return new HumanCharacter[size];
        }
    };

    private HumanCharacter(Parcel in) {
        this.name = in.readString();
        this.avatar = in.readInt();
        this.job = in.readString();
        this.social = in.readInt();
        this.animal = in.readInt();
        this.business = in.readInt();
    }
}
