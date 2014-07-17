package com.pfhosa.sprinklecity.model;

import android.os.Parcel;
import android.os.Parcelable;

public class HumanCharacter implements Parcelable {

	String mUsername, mJob;
	int mAvatar, mSocial, mAnimal, mBusiness;
	
	public HumanCharacter(String username, int avatar, String job, int social, int animal, int business) {
		mUsername = username;
		mAvatar = avatar;
		mJob = job;
		mSocial = social;
		mAnimal = animal;
		mBusiness = business;		
	}
	
	// Accessors
	
	public String getName() {return mUsername;}
	
	public int getAvatar() {return mAvatar;}
	
	public String getJob(){return mJob;}
	
	public int getSocialTrait() {return mSocial;}
	
	public int getAnimalTrait() {return mAnimal;}
	
	public int getBusinessTrait() {return mBusiness;}
	
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
        mUsername = in.readString();
        mAvatar = in.readInt();
        mJob = in.readString();
        mSocial = in.readInt();
        mAnimal = in.readInt();
        mBusiness = in.readInt();
    }
}
