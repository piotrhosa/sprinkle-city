package com.pfhosa.sprinklecity.model;

import java.util.Scanner;

import android.os.Parcel;
import android.os.Parcelable;

public class InventoryItem implements Parcelable {

	private String mCreator, mItem;
	private int mValue, mId;
	private long mTimeCollected;
	private boolean mUsable;

	/**
	 * 
	 * @param item
	 * @param value
	 */
	public InventoryItem(String creator, String item, int value) {
		mId = 0;
		mCreator = creator;
		mItem = item;
		mValue = value;
		mTimeCollected = System.currentTimeMillis() / 1000L;
		mUsable = true;
	}

	/**
	 * 
	 * @param item
	 * @param value
	 * @param timeCollected
	 * @param usable
	 */
	public InventoryItem(int id, String creator, String item, int value, long timeCollected, boolean usable) {
		mId = id;
		mCreator = creator;
		mItem = item;
		mValue = value;
		mTimeCollected = timeCollected;
		mUsable = usable;
	}
	
	public InventoryItem() {
		mId = 0;
		mCreator = "void";
		mItem = "void";
		mValue = 0;
		mTimeCollected = 0;
		mUsable = false;
	}
	
	public InventoryItem(String beamMessage) {
		Scanner cursor = new Scanner(beamMessage);
		
		mId = cursor.nextInt();
		mCreator = cursor.next();//username;
		mItem = cursor.next();
		mValue = cursor.nextInt();
		mTimeCollected = cursor.nextLong();
		mUsable = cursor.nextBoolean();
		
		cursor.close();
	}

	// Acessors
	
	public int getId() {return mId;}

	public String getCreator() {return mCreator;}

	public String getItem() {return mItem;}

	public int getValue() {return mValue;}

	public long getTimeCollected() {return mTimeCollected;}

	public boolean getUsable() {return mUsable;}
	
	public void setId(int id) {mId = id;}
	
	public void setCreator(String creator) {mCreator = creator;}

	public void setItem(String item ) {mItem = item;}

	public void setValue(int value) {mValue = value;}

	public void setTimeCollected(Boolean now) {mTimeCollected = now ? System.currentTimeMillis() / 1000L : 0;}

	public void setUnusable() {mUsable = false;}

	public void setUsable() {mUsable = true;}

	// Parcelable implementation

	public int describeContents() {return 0;}

	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(mId);
		out.writeString(mCreator);
		out.writeString(mItem);
		out.writeInt(mValue);
		out.writeLong(mTimeCollected);
		out.writeByte(mUsable ? (byte)1 : (byte)0);
	}

	protected InventoryItem(Parcel in) {
		mId = in.readInt();
		mCreator = in.readString();
		mItem = in.readString();
		mValue = in.readInt();
		mTimeCollected = in.readLong();
		mUsable = in.readByte() == 1;
	}

	public static final Parcelable.Creator<InventoryItem> CREATOR = new Parcelable.Creator<InventoryItem>() {
		public InventoryItem createFromParcel(Parcel in) {
			return new InventoryItem(in);
		}

		public InventoryItem[] newArray(int size) {
			return new InventoryItem[size];
		}
	};

	public String toBeamString() {
		return mId + " " + mCreator + " " + mItem + " " + Integer.toString(mValue) + " " + Long.toString(mTimeCollected) + 
				" " + Boolean.toString(mUsable);
	}
	public String toString() {
			return mItem.toString() + " " + Integer.toString(mValue);
	}

}
