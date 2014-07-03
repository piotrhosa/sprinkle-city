package com.pfhosa.sprinklecity.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AnimalCharacter implements Parcelable {

	String name;
	int avatar;
	int sleep;
	int fitness;

	public AnimalCharacter(String name, int avatar, int sleep, int fitness) {
		this.name = name;
		this.avatar = avatar;
		this.sleep = sleep;
		this.fitness = fitness;		
	}

	// Accessors

	public String getName() {
		return name;
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

	// Parcelable implementation

	public int describeContents() {
		return 0;
	}

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
		this.name = in.readString();
		this.avatar = in.readInt();
		this.sleep = in.readInt();
		this.fitness = in.readInt();
	}
}
