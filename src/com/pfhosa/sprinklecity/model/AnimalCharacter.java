package com.pfhosa.sprinklecity.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AnimalCharacter extends Character implements Parcelable {
	
	private int mSleep, mFitness;

	public AnimalCharacter(String name, int avatar, int sleep, int fitness) {
		super(name, avatar);
		mSleep = sleep;
		mFitness = fitness;		
	}

	// Accessors

	public int getSleep() {return mSleep;}

	public int getFitness() {return mFitness;}

	// Parcelable implementation

	public int describeContents() {return 0;}

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(getName());
		out.writeInt(getAvatar());
		out.writeInt(getSleep());
		out.writeInt(getFitness());
	}

	public static final Parcelable.Creator<AnimalCharacter> CREATOR = new Parcelable.Creator<AnimalCharacter>() {
		public AnimalCharacter createFromParcel(Parcel in) {
			return new AnimalCharacter(in);
		}

		public AnimalCharacter[] newArray(int size) {
			return new AnimalCharacter[size];
		}
	};

	private AnimalCharacter(Parcel in) {
		super(in);
		mSleep = in.readInt();
		mFitness = in.readInt();
	}
}
