package com.autochecker.listener;

import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;

import com.autochecker.data.AutoCheckerDataSource;
import com.autochecker.data.exception.NoWatchedLocationFoundException;
import com.autochecker.data.model.WatchedLocation;
import com.autochecker.data.model.WatchedLocationRecord;

public class AutoCheckerProximityListener implements IProximityListener {

	public static final String ALARM_ENTERING_LOCATION = "ALARM_ENTERING_LOCATION";
	public static final String ALARM_LEAVING_LOCATION = "ALARM_LEAVING_LOCATION";

	public static final String ALARM_LOCATION = "ALARM_LOCATION";

	private final String TAG = getClass().getSimpleName();

	private static final long INTERVAL_ACCEPT_EVENT = 2 * 60 * 1000;

	private AutoCheckerDataSource dataSource;

	@Override
	public void onEnter(int locationId, long time, Context context) {

		Log.d(TAG, "Processing enter event");

		try {

			if (dataSource == null) {
				dataSource = new AutoCheckerDataSource(context);
			}

			dataSource.open();

			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);

			WatchedLocation location = dataSource
					.getWatchedLocation(locationId);

			switch (location.getStatus()) {

			case WatchedLocation.OUTSIDE_LOCATION:

				location.setStatus(WatchedLocation.ENTERING_LOCATION);
				dataSource.updateWatchedLocation(location);

				WatchedLocationRecord record = new WatchedLocationRecord();
				record.setCheckIn(new Date(time));
				record.setCheckOut(null);
				record.setLocation(location);
				dataSource.insertRecord(record);

				Log.i(TAG, "User has entering to " + location.getName());

				alarmManager.set(
						AlarmManager.ELAPSED_REALTIME,
						INTERVAL_ACCEPT_EVENT,
						getPendingIntent(context, location,
								ALARM_ENTERING_LOCATION));

				Log.i(TAG, "Set alarm to validate enter to location "
						+ location.getName());

				break;

			case WatchedLocation.INSIDE_LOCATION:
			case WatchedLocation.ENTERING_LOCATION:

				Log.w(TAG, "User has entered to " + location.getName()
						+ " and it was there yet");
				break;

			case WatchedLocation.LEAVING_LOCATION:

				alarmManager.cancel(getPendingIntent(context, location,
						ALARM_LEAVING_LOCATION));

				Log.i(TAG, "Canceled leave alarm " + location.getName());

				location.setStatus(WatchedLocation.ENTERING_LOCATION);
				dataSource.updateWatchedLocation(location);

				WatchedLocationRecord lastRecord = dataSource
						.getLastWatchedLocationRecord(location);

				lastRecord.setCheckOut(null);
				dataSource.updateRecord(lastRecord);

				Log.i(TAG, "Canceled leaving event to " + location.getName());

				alarmManager.set(
						AlarmManager.ELAPSED_REALTIME,
						INTERVAL_ACCEPT_EVENT,
						getPendingIntent(context, location,
								ALARM_ENTERING_LOCATION));

				Log.i(TAG, "Set alarm to validate enter to location "
						+ location.getName());

				break;
			}

			dataSource.close();

		} catch (NoWatchedLocationFoundException e) {
			Log.e(TAG, "Watched Location doesn't exist ", e);
		} catch (SQLException e) {
			Log.e(TAG, "DataSource open exception", e);
		}
	}

	@Override
	public void onLeave(int locationId, long time, Context context) {

		Log.d(TAG, "Processing leave event");

		try {

			if (dataSource == null) {
				dataSource = new AutoCheckerDataSource(context);
			}

			dataSource.open();

			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);

			WatchedLocation location = dataSource
					.getWatchedLocation(locationId);

			switch (location.getStatus()) {

			case WatchedLocation.INSIDE_LOCATION:

				location.setStatus(WatchedLocation.LEAVING_LOCATION);
				dataSource.updateWatchedLocation(location);

				WatchedLocationRecord record = dataSource
						.getUnCheckedWatchedLocationRecord(location);
				record.setCheckOut(new Date(time));
				dataSource.updateRecord(record);

				Log.i(TAG, "User has left " + location.getName());

				alarmManager.set(
						AlarmManager.ELAPSED_REALTIME,
						INTERVAL_ACCEPT_EVENT,
						getPendingIntent(context, location,
								ALARM_LEAVING_LOCATION));

				Log.i(TAG, "Set alarm to validate leave to location "
						+ location.getName());

				break;

			case WatchedLocation.OUTSIDE_LOCATION:
			case WatchedLocation.LEAVING_LOCATION:

				Log.w(TAG, "User has leaving " + location.getName()
						+ " and it wasn't there yet");
				break;

			case WatchedLocation.ENTERING_LOCATION:

				alarmManager.cancel(getPendingIntent(context, location,
						ALARM_ENTERING_LOCATION));

				Log.i(TAG, "Canceled enter alarm " + location.getName());

				location.setStatus(WatchedLocation.LEAVING_LOCATION);
				dataSource.updateWatchedLocation(location);

				dataSource.removeLastWatchedLocationRecord(location);

				Log.i(TAG, "Canceled leaving event to " + location.getName());

				alarmManager.set(
						AlarmManager.ELAPSED_REALTIME,
						INTERVAL_ACCEPT_EVENT,
						getPendingIntent(context, location,
								ALARM_ENTERING_LOCATION));

				Log.i(TAG, "Set alarm to validate enter to location "
						+ location.getName());

				break;
			}

			dataSource.close();

		} catch (NoWatchedLocationFoundException e) {
			Log.e(TAG, "Watched Location doesn't exist ", e);
		} catch (SQLException e) {
			Log.e(TAG, "DataSource open exception", e);
		}
	}

	private PendingIntent getPendingIntent(Context context,
			WatchedLocation location, String action) {

		Bundle extra = new Bundle();
		extra.putInt(ALARM_LOCATION, location.getId());

		Intent intent = new Intent(action);
		intent.putExtra(action, extra);

		return PendingIntent.getBroadcast(context, location.getId(), intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
	}

	@Override
	public void onConfirmEnter(int locationId, Context context) {

		Log.d(TAG, "Processing confirm enter event");

		try {

			if (dataSource == null) {
				dataSource = new AutoCheckerDataSource(context);
			}

			dataSource.open();

			WatchedLocation location = dataSource
					.getWatchedLocation(locationId);

			location.setStatus(WatchedLocation.INSIDE_LOCATION);
			dataSource.updateWatchedLocation(location);

			dataSource.close();

		} catch (NoWatchedLocationFoundException e) {
			Log.e(TAG, "Watched Location doesn't exist ", e);
		} catch (SQLException e) {
			Log.e(TAG, "DataSource open exception", e);
		}

	}

	@Override
	public void onConfirmLeave(int locationId, Context context) {

		Log.d(TAG, "Processing confirm leave event");

		try {

			if (dataSource == null) {
				dataSource = new AutoCheckerDataSource(context);
			}

			dataSource.open();

			WatchedLocation location = dataSource
					.getWatchedLocation(locationId);

			location.setStatus(WatchedLocation.OUTSIDE_LOCATION);
			dataSource.updateWatchedLocation(location);

			dataSource.close();

		} catch (NoWatchedLocationFoundException e) {
			Log.e(TAG, "Watched Location doesn't exist ", e);
		} catch (SQLException e) {
			Log.e(TAG, "DataSource open exception", e);
		}

	}

}
