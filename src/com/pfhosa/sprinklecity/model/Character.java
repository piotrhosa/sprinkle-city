package com.pfhosa.sprinklecity.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Character implements Parcelable {
	private String mName;
	private int mAvatar;
	
	/**
	 * 
	 * @param name
	 * @param avatar
	 */
	public Character(String name, int avatar) {
		mName = name;
		mAvatar = avatar;
	}

	public String getName() {return mName;}
	
	public int getAvatar() {return mAvatar;}
	
	// Parcelable implementation

	public int describeContents() {return 0;}

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(getName());
		out.writeInt(getAvatar());
	}

	public static final Parcelable.Creator<Character> CREATOR = new Parcelable.Creator<Character>() {
		public Character createFromParcel(Parcel in) {
			return new Character(in);
		}

		public Character[] newArray(int size) {
			return new AnimalCharacter[size];
		}
	};

	protected Character(Parcel in) {
		mName = in.readString();
		mAvatar = in.readInt();
	}
}
