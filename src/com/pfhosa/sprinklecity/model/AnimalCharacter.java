package com.pfhosa.sprinklecity.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AnimalCharacter implements Parcelable {

	String mName;
	int mAvatar;
	int mSleep;
	int mFitness;

	public AnimalCharacter(String name, int avatar, int sleep, int fitness) {
		mName = name;
		mAvatar = avatar;
		mSleep = sleep;
		mFitness = fitness;		
	}

	// Accessors

	public String getName() {return mName;}

	public int getAvatar() {return mAvatar;}

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
		mName = in.readString();
		mAvatar = in.readInt();
		mSleep = in.readInt();
		mFitness = in.readInt();
	}
}
