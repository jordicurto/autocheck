package com.autochecker.datamodel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AutoCheckerSQLiteOpenHelper extends SQLiteOpenHelper {

	public static final String TABLE_LOCATIONS_NAME = "locations";
	public static final String TABLE_CHECKS_NAME = "checks";

	public static final String COLUMN_ID = "id";
	public static final int COLUMN_ID_INDEX = 0;
	public static final String COLUMN_NAME = "name";
	public static final int COLUMN_NAME_INDEX = 1;
	public static final String COLUMN_LATITUDE = "lat";
	public static final int COLUMN_LATITUDE_INDEX = 2;
	public static final String COLUMN_LONGITUDE = "long";
	public static final int COLUMN_LONGITUDE_INDEX = 3;
	public static final String COLUMN_ACCURACY = "acc";
	public static final int COLUMN_ACCURACY_INDEX = 4;
	public static final String COLUMN_OVER_THERE = "over";
	public static final int COLUMN_OVER_THERE_INDEX = 5;

	public static final String COLUMN_LOCATION_ID = "loc";
	public static final int COLUMN_LOCATION_ID_INDEX = 1;
	public static final String COLUMN_CHECKIN = "checkin";
	public static final int COLUMN_CHECKIN_INDEX = 2;
	public static final String COLUMN_CHECKOUT = "checkout";
	public static final int COLUMN_CHECKOUT_INDEX = 3;

	public static final String[] COLUMNS_TABLE_LOCATIONS = { COLUMN_ID,
			COLUMN_NAME, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_ACCURACY,
			COLUMN_OVER_THERE };

	public static final String[] COLUMNS_TABLE_CHECKS = { COLUMN_ID,
			COLUMN_LOCATION_ID, COLUMN_CHECKIN, COLUMN_CHECKOUT };

	private static final String DATABASE_NAME = "autocheck.db";
	private static final int DATABASE_VERSION = 4;

	// Database creation sql statement
	private static final String CREATE_TABLE_LOCATIONS = "create table "
			+ TABLE_LOCATIONS_NAME + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_NAME
			+ " string, " + COLUMN_LATITUDE + " real, " + COLUMN_LONGITUDE
			+ " real , " + COLUMN_ACCURACY + " real, " + COLUMN_OVER_THERE
			+ " integer);";

	// Database creation sql statement
	private static final String CREATE_TABLE_CHECKS = "create table "
			+ TABLE_CHECKS_NAME + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_LOCATION_ID
			+ " integer references " + TABLE_LOCATIONS_NAME + " (" + COLUMN_ID
			+ "), " + COLUMN_CHECKIN + " integer , " + COLUMN_CHECKOUT
			+ " integer);";

	public AutoCheckerSQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_LOCATIONS);
		db.execSQL(CREATE_TABLE_CHECKS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(AutoCheckerSQLiteOpenHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("drop table if exists " + CREATE_TABLE_CHECKS);
		db.execSQL("drop table if exists " + CREATE_TABLE_LOCATIONS);
		onCreate(db);
	}

}
