package com.autochecker.listener;

import java.util.Date;

import android.content.Context;
import android.database.SQLException;
import android.util.Log;

import com.autochecker.data.AutoCheckerDataSource;
import com.autochecker.data.exception.NoWatchedLocationFoundException;
import com.autochecker.data.model.WatchedLocation;
import com.autochecker.data.model.WatchedLocationRecord;

public class AutoCheckerProximityListener implements IProximityListener {

	private final String TAG = getClass().getSimpleName();

	private AutoCheckerDataSource dataSource;

	@Override
	public void onEnter(int locationId, long time, Context context) {

		Log.d(TAG, "Processing enter event");

		try {

			if (dataSource == null) {
				dataSource = new AutoCheckerDataSource(context);
			}

			dataSource.open();

			WatchedLocation location = dataSource
					.getWatchedLocation(locationId);

			switch (location.getStatus()) {

			case WatchedLocation.OUTSIDE_LOCATION:

				location.setStatus(WatchedLocation.INSIDE_LOCATION);
				dataSource.updateWatchedLocation(location);

				WatchedLocationRecord record = new WatchedLocationRecord();
				record.setCheckIn(new Date(time));
				record.setCheckOut(null);
				record.setLocation(location);
				dataSource.insertRecord(record);

				Log.i(TAG, "User has entered to " + location.getName());

				break;

			case WatchedLocation.INSIDE_LOCATION:

				Log.w(TAG, "User has entered to " + location.getName()
						+ " and it was there yet");
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

			WatchedLocation location = dataSource
					.getWatchedLocation(locationId);

			switch (location.getStatus()) {

			case WatchedLocation.INSIDE_LOCATION:

				location.setStatus(WatchedLocation.OUTSIDE_LOCATION);
				dataSource.updateWatchedLocation(location);

				WatchedLocationRecord record = dataSource
						.getUnCheckedWatchedLocationRecord(location);
				record.setCheckOut(new Date(time));
				dataSource.updateRecord(record);

				Log.i(TAG, "User has left " + location.getName());

				break;

			case WatchedLocation.OUTSIDE_LOCATION:

				Log.w(TAG, "User has leaving " + location.getName()
						+ " and it wasn't there yet");
				break;
			}

			dataSource.close();

		} catch (NoWatchedLocationFoundException e) {
			Log.e(TAG, "Watched Location doesn't exist ", e);
		} catch (SQLException e) {
			Log.e(TAG, "DataSource open exception", e);
		}
	}
}
