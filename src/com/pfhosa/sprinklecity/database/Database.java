package com.pfhosa.sprinklecity.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pfhosa.sprinklecity.model.AnimalCharacter;
import com.pfhosa.sprinklecity.model.HumanCharacter;
import com.pfhosa.sprinklecity.model.InventoryItem;
import com.pfhosa.sprinklecity.model.InventoryList;
import com.pfhosa.sprinklecity.model.User;

public class Database extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "sprinkleCity.db";

	// Table Names
	private static final String TABLE_USER = "table_user";
	private static final String TABLE_HUMAN_CHARACTER = "table_human_character";
	private static final String TABLE_ANIMAL_CHARACTER = "table_animal_character";
	private static final String TABLE_INVENTORY = "table_inventory";

	// Common column names
	private static final String KEY_ID = "id";
	//private static final String USER_ID = "user_id";

	// 	Character Table Columns
	private static final String PASSWORD = "password";

	private static final String HUMAN_AVATAR = "human_avatar";
	private static final String HUMAN_NAME = "human_name";
	private static final String HUMAN_JOB = "human_job";
	private static final String HUMAN_SOCIAL = "human_social";
	private static final String HUMAN_ANIMAL = "human_animal";
	private static final String HUMAN_BUSINESS = "human_business";

	private static final String ANIMAL_NAME = "animal_name";
	private static final String ANIMAL_AVATAR = "animal_avatar";
	private static final String ANIMAL_SLEEP = "animal_sleep";
	private static final String ANIMAL_FITNESS = "animal_fitness";

	private static final String USERNAME = "Username";
	private static final String ITEM = "Item";
	private static final String VALUE = "Value";
	private static final String TIME_COLLECTED = "TimeCollected";
	private static final String USABLE= "Usable";



	private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER +
			" ( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
			HUMAN_NAME + " STRING, " +
			PASSWORD + " STRING, " + 
			ANIMAL_NAME + " STRING " + ")";

	private static final String CREATE_TABLE_HUMAN_CHARACTER = "CREATE TABLE " + TABLE_HUMAN_CHARACTER +
			" ( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
			HUMAN_NAME + " STRING, " + 
			HUMAN_AVATAR + " INTEGER, " +
			HUMAN_JOB + " STRING, " + 
			HUMAN_SOCIAL + " INTEGER, " + 
			HUMAN_ANIMAL + " INTEGER, " + 
			HUMAN_BUSINESS + " INTEGER " + ")";

	private static final String CREATE_TABLE_ANIMAL_CHARACTER = "CREATE TABLE " + TABLE_ANIMAL_CHARACTER + 
			" ( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
			ANIMAL_NAME + " STRING, " +
			ANIMAL_AVATAR + " INTEGER, " + 
			ANIMAL_SLEEP + " INTEGER, " + 
			ANIMAL_FITNESS + " INTEGER " + ")";

	private static final String CREATE_TABLE_INVENTORY = "CREATE TABLE " + TABLE_INVENTORY + 
			" ( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
			USERNAME + " STRING, " +
			ITEM + " STRING, " + 
			VALUE + " INTEGER, " + 
			TIME_COLLECTED + " LONG, " + 
			USABLE + " TINYINT " + ")";

	private static Database mInstance = null;

	public static Database getInstance(Context context) {
		if (mInstance == null) mInstance = new Database(context.getApplicationContext());
		return mInstance;
	}

	public Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}    

	@Override
	public void onCreate(SQLiteDatabase db) { 
		// All tables initialized
		db.execSQL(CREATE_TABLE_USER);
		db.execSQL(CREATE_TABLE_HUMAN_CHARACTER);
		db.execSQL(CREATE_TABLE_ANIMAL_CHARACTER);
		db.execSQL(CREATE_TABLE_INVENTORY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop tables	
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HUMAN_CHARACTER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANIMAL_CHARACTER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);

		onCreate(db);
	}

	// Populate tables

	public void newUser(User newUser) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(HUMAN_NAME, newUser.getCharacterName());
		values.put(PASSWORD, newUser.getPassword());
		values.put(ANIMAL_NAME, newUser.getAnimalName());

		// Insert row
		db.insert(TABLE_USER, null, values);
	}

	public void newHumanCharacter(HumanCharacter newHuman) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(HUMAN_AVATAR, newHuman.getAvatar());
		values.put(HUMAN_NAME, newHuman.getName());
		values.put(HUMAN_JOB, newHuman.getJob());
		values.put(HUMAN_SOCIAL, newHuman.getSocialTrait());
		values.put(HUMAN_ANIMAL, newHuman.getAnimalTrait());
		values.put(HUMAN_BUSINESS, newHuman.getBusinessTrait());

		// Insert row
		db.insert(TABLE_HUMAN_CHARACTER, null, values);
	}

	public void newAnimalCharacter(AnimalCharacter newAnimal) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(ANIMAL_NAME, newAnimal.getName());
		values.put(ANIMAL_AVATAR, newAnimal.getAvatar());
		values.put(ANIMAL_SLEEP, newAnimal.getSleep());
		values.put(ANIMAL_FITNESS, newAnimal.getFitness());

		// Insert row
		db.insert(TABLE_ANIMAL_CHARACTER, null, values);
	}

	public void loadInventoryToLocal(InventoryList inventory) {
		SQLiteDatabase db = this.getWritableDatabase();		
		db.execSQL("DELETE FROM " + TABLE_INVENTORY);		
		ContentValues values = new ContentValues();

		for(int i = 0; i < inventory.getSize(); ++i) {
			ArrayList<InventoryItem> subList = inventory.getList(i);
			
			for(int j = 0; j < subList.size(); ++j) {
				InventoryItem ii= subList.get(j);

				if(ii.getItem() != "") {	
					Log.d("Current put in", "" + ii.getItem());
					values.put(USERNAME, ii.getCreator());
					values.put(ITEM, ii.getItem());
					values.put(VALUE, ii.getValue());
					values.put(TIME_COLLECTED, ii.getTimeCollected());
					values.put(USABLE, ii.getUsable() ? "1" : "0");
					db.insert(TABLE_INVENTORY, null, values);
				}
			}
		}		
	}

	public ArrayList<InventoryItem> getCompressedInventory(String creator) {
		SQLiteDatabase db = this.getReadableDatabase();
		String[] d = {};
		InventoryList inventory = new InventoryList(creator);

		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_INVENTORY + " WHERE " + USABLE + " = ' 1 '" + " AND " + USERNAME + " = ' " + creator + " '", d);

		if(cursor.moveToFirst()) {
			while(cursor != null) {
				InventoryItem item = new InventoryItem(cursor.getString(1),
						cursor.getString(2),
						Integer.parseInt(cursor.getString(3)),
						Long.parseLong(cursor.getString(4)),
						"1".equals(cursor.getString(5))
						);
				inventory.addItem(item.getItem(), item);
			}
		}

		return inventory.compressInventory();
	}

	public String getHumanCharacter() {

		SQLiteDatabase db = this.getReadableDatabase();
		String[] d = {};
		String retString = "";
		Cursor cursor = db.rawQuery("SELECT " + HUMAN_NAME +  " FROM " + TABLE_HUMAN_CHARACTER, d);

		if(cursor.moveToLast()) {
			retString = cursor.getString(0);
		}

		return retString;		
	}

	public boolean uniqueHumanCharacter(String name) {

		SQLiteDatabase db = this.getReadableDatabase();
		String[] d = {};

		Cursor cursor = db.rawQuery("SELECT " + HUMAN_NAME +  " FROM " + TABLE_HUMAN_CHARACTER + " WHERE " + HUMAN_NAME + " = '" + name + "'", d);

		if(cursor.moveToFirst())
			return false;
		else
			return true;
	}

	public boolean uniqueAnimalCharacter(String name) {
		SQLiteDatabase db = this.getReadableDatabase();
		String[] d = {};

		Cursor cursor = db.rawQuery("SELECT " + ANIMAL_NAME +  " FROM " + TABLE_ANIMAL_CHARACTER + " WHERE " + ANIMAL_NAME + " = '" + name + "'", d);

		if(cursor.moveToFirst())
			return false;
		else
			return true;
	}
}