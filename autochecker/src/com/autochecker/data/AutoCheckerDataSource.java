package com.autochecker.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.autochecker.data.model.Check;
import com.autochecker.data.model.FavLocation;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AutoCheckerDataSource {

	private final String TAG = getClass().getSimpleName();

	private SQLiteDatabase database;

	private AutoCheckerSQLiteOpenHelper dbHelper;

	public AutoCheckerDataSource(Context context) {
		dbHelper = new AutoCheckerSQLiteOpenHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void insertFavLocation(FavLocation location) {

		Cursor cursor = database.query(
				AutoCheckerSQLiteOpenHelper.TABLE_LOCATIONS_NAME,
				AutoCheckerSQLiteOpenHelper.COLUMNS_TABLE_LOCATIONS,
				AutoCheckerSQLiteOpenHelper.COLUMN_NAME + " = \""
						+ location.getName() + "\"", null, null, null, null);

		if (!cursor.moveToFirst()) {

			ContentValues values = new ContentValues();
			values.put(AutoCheckerSQLiteOpenHelper.COLUMN_NAME,
					location.getName());
			values.put(AutoCheckerSQLiteOpenHelper.COLUMN_LATITUDE,
					location.getLatitude());
			values.put(AutoCheckerSQLiteOpenHelper.COLUMN_LONGITUDE,
					location.getLongitude());
			values.put(AutoCheckerSQLiteOpenHelper.COLUMN_ACCURACY,
					location.getAccuracy());
			values.put(AutoCheckerSQLiteOpenHelper.COLUMN_OVER_THERE, false);
			database.insert(AutoCheckerSQLiteOpenHelper.TABLE_LOCATIONS_NAME,
					null, values);
		} else {
			Log.d(TAG, "Location exists " + location.toString());
		}

		cursor.close();
	}

	public List<FavLocation> getAllFavLocations() {

		Cursor cursor = database.query(
				AutoCheckerSQLiteOpenHelper.TABLE_LOCATIONS_NAME,
				AutoCheckerSQLiteOpenHelper.COLUMNS_TABLE_LOCATIONS, null,
				null, null, null, null);

		List<FavLocation> locations = new ArrayList<FavLocation>();

		while (cursor.moveToNext()) {
			FavLocation fl = new FavLocation();
			fl.setId(cursor.getInt(AutoCheckerSQLiteOpenHelper.COLUMN_ID_INDEX));
			fl.setName(cursor
					.getString(AutoCheckerSQLiteOpenHelper.COLUMN_NAME_INDEX));
			fl.setLatitude(cursor
					.getDouble(AutoCheckerSQLiteOpenHelper.COLUMN_LATITUDE_INDEX));
			fl.setLongitude(cursor
					.getDouble(AutoCheckerSQLiteOpenHelper.COLUMN_LONGITUDE_INDEX));
			fl.setAccuracy(cursor
					.getFloat(AutoCheckerSQLiteOpenHelper.COLUMN_ACCURACY_INDEX));
			fl.setOverThere(cursor
					.getInt(AutoCheckerSQLiteOpenHelper.COLUMN_OVER_THERE_INDEX) == 0 ? false
					: true);
			locations.add(fl);
		}

		cursor.close();

		return locations;
	}

	public void updateFavLocation(FavLocation location) {
		ContentValues values = new ContentValues();
		values.put(AutoCheckerSQLiteOpenHelper.COLUMN_NAME, location.getName());
		values.put(AutoCheckerSQLiteOpenHelper.COLUMN_LATITUDE,
				location.getLatitude());
		values.put(AutoCheckerSQLiteOpenHelper.COLUMN_LONGITUDE,
				location.getLongitude());
		values.put(AutoCheckerSQLiteOpenHelper.COLUMN_ACCURACY,
				location.getAccuracy());
		values.put(AutoCheckerSQLiteOpenHelper.COLUMN_OVER_THERE,
				location.isOverThere());
		database.update(AutoCheckerSQLiteOpenHelper.TABLE_LOCATIONS_NAME,
				values, AutoCheckerSQLiteOpenHelper.COLUMN_ID + " = "
						+ location.getId(), null);
	}

	public void insertCheck(Check check) {
		ContentValues values = new ContentValues();
		values.put(AutoCheckerSQLiteOpenHelper.COLUMN_LOCATION_ID, check
				.getLocation().getId());
		values.put(AutoCheckerSQLiteOpenHelper.COLUMN_CHECKIN, check
				.getCheckIn().getTime());
		values.put(AutoCheckerSQLiteOpenHelper.COLUMN_CHECKOUT, check
				.getCheckOut().getTime());
		database.insert(AutoCheckerSQLiteOpenHelper.TABLE_CHECKS_NAME, null,
				values);
	}

	public void updateCheck(Check check) {
		ContentValues values = new ContentValues();
		values.put(AutoCheckerSQLiteOpenHelper.COLUMN_CHECKIN, check
				.getCheckIn().getTime());
		values.put(AutoCheckerSQLiteOpenHelper.COLUMN_CHECKOUT, check
				.getCheckOut().getTime());
		database.update(AutoCheckerSQLiteOpenHelper.TABLE_CHECKS_NAME, values,
				"id = " + check.getId(), null);
	}

	public boolean userIsInLocation(FavLocation location)
			throws NotFoundException {

		boolean isInLocation = false;

		Cursor cursor = database.query(
				AutoCheckerSQLiteOpenHelper.TABLE_LOCATIONS_NAME,
				AutoCheckerSQLiteOpenHelper.COLUMNS_TABLE_LOCATIONS,
				AutoCheckerSQLiteOpenHelper.COLUMN_ID + " = "
						+ location.getId(), null, null, null, null);

		if (cursor.moveToFirst()) {
			isInLocation = cursor
					.getInt(AutoCheckerSQLiteOpenHelper.COLUMN_OVER_THERE_INDEX) == 0 ? false
					: true;
		} else {
			Log.w(TAG, "No location found in database " + location.toString());
			throw new NotFoundException();
		}

		cursor.close();

		return isInLocation;
	}

	public void createCheck(Check check) {

		ContentValues values = new ContentValues();
		values.put(AutoCheckerSQLiteOpenHelper.COLUMN_LOCATION_ID, check
				.getLocation().getId());
		values.put(AutoCheckerSQLiteOpenHelper.COLUMN_CHECKIN, check
				.getCheckIn().getTime());
		if (check.getCheckOut() == null) {
			values.putNull(AutoCheckerSQLiteOpenHelper.COLUMN_CHECKOUT);
		} else {
			values.put(AutoCheckerSQLiteOpenHelper.COLUMN_CHECKOUT, check
					.getCheckOut().getTime());
		}

		database.insert(AutoCheckerSQLiteOpenHelper.TABLE_CHECKS_NAME, null,
				values);
	}

	public Check getCheck(FavLocation location) throws NotFoundException {

		Cursor cursor = database.query(
				AutoCheckerSQLiteOpenHelper.TABLE_CHECKS_NAME,
				AutoCheckerSQLiteOpenHelper.COLUMNS_TABLE_CHECKS,
				AutoCheckerSQLiteOpenHelper.COLUMN_LOCATION_ID + " = "
						+ location.getId() + " and "
						+ AutoCheckerSQLiteOpenHelper.COLUMN_CHECKOUT
						+ " is null", null, null, null, null);

		Check check = new Check();
		if (cursor.moveToFirst()) {
			check.setId(cursor
					.getInt(AutoCheckerSQLiteOpenHelper.COLUMN_ID_INDEX));
			check.setCheckIn(new Date(cursor
					.getLong(AutoCheckerSQLiteOpenHelper.COLUMN_CHECKIN_INDEX)));
			if(cursor.isNull(AutoCheckerSQLiteOpenHelper.COLUMN_CHECKOUT_INDEX)) {
				check.setCheckOut(null);
			} else {
				check.setCheckOut(new Date(cursor
						.getLong(AutoCheckerSQLiteOpenHelper.COLUMN_CHECKOUT_INDEX)));
			}
			check.setLocation(location);
		} else {
			Log.w(TAG, "No opened check in found for " + location.toString());
			throw new NotFoundException();
		}

		return check;
	}

	public List<Check> getAllChecks(FavLocation location) {

		Cursor cursor = database.query(
				AutoCheckerSQLiteOpenHelper.TABLE_CHECKS_NAME,
				AutoCheckerSQLiteOpenHelper.COLUMNS_TABLE_CHECKS,
				AutoCheckerSQLiteOpenHelper.COLUMN_LOCATION_ID + " = "
						+ location.getId(), null, null, null, null);

		List<Check> checks = new ArrayList<Check>();

		while (cursor.moveToNext()) {
			Check check = new Check();
			check.setId(cursor
					.getInt(AutoCheckerSQLiteOpenHelper.COLUMN_ID_INDEX));
			check.setCheckIn(new Date(cursor
					.getLong(AutoCheckerSQLiteOpenHelper.COLUMN_CHECKIN_INDEX)));
			if(cursor.isNull(AutoCheckerSQLiteOpenHelper.COLUMN_CHECKOUT_INDEX)) {
				check.setCheckOut(null);
			} else {
				check.setCheckOut(new Date(cursor
						.getLong(AutoCheckerSQLiteOpenHelper.COLUMN_CHECKOUT_INDEX)));
			}
			check.setLocation(location);
			checks.add(check);
		}

		cursor.close();

		return checks;
	}
}
