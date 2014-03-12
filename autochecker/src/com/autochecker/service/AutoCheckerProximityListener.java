package com.autochecker.service;

import java.util.Date;

import android.content.Context;
import android.database.SQLException;
import android.os.Handler;
import android.util.Log;

import com.autochecker.data.AutoCheckerDataSource;
import com.autochecker.data.exception.NoWatchedLocationFoundException;
import com.autochecker.data.model.WatchedLocation;
import com.autochecker.data.model.WatchedLocationRecord;
import com.autochecker.listener.IProximityListener;

public class AutoCheckerProximityListener implements IProximityListener {

	private final String TAG = getClass().getSimpleName();

	private AutoCheckerDataSource dataSource = null;

	Handler handlerEnetering = new Handler();
	Handler handlerLeaving = new Handler();

	public AutoCheckerProximityListener(Context context) {

		dataSource = new AutoCheckerDataSource(context);
		try {
			dataSource.open();
		} catch (SQLException e) {
			Log.e(TAG, "DataSource open exception", e);
		}
	}

	@Override
	public void onEnter(int locationId, long time) {

		Log.d(TAG, "Processing enter event");

		try {

			WatchedLocation location = dataSource
					.getWatchedLocation(locationId);

			switch (location.getStatus()) {
			
			case WatchedLocation.INSIDE_LOCATION:
				
				Log.w(TAG, "User has entered to " + location.getName()
						+ " and it was there yet");
				break;
				
			case WatchedLocation.OUTSIDE_LOCATION:
				
				Log.i(TAG, "User has entered to " + location.getName());
				
				WatchedLocationRecord record = new WatchedLocationRecord();
				record.setCheckIn(new Date(time));
				record.setCheckOut(null);
				record.setLocation(location);
				location.setStatus(WatchedLocation.INSIDE_LOCATION);
				dataSource.insertRecord(record);
				dataSource.updateWatchedLocation(location);
				break;
			}

		} catch (NoWatchedLocationFoundException e) {
			Log.e(TAG, "Watched Location doesn't exist ", e);
		}
	}

	@Override
	public void onLeave(int locationId, long time) {

		Log.d(TAG, "Processing leave event");

		try {

			WatchedLocation location = dataSource
					.getWatchedLocation(locationId);

			switch (location.getStatus()) {
			
			case WatchedLocation.INSIDE_LOCATION:
				
				Log.i(TAG, "User has left " + location.getName());
				
				WatchedLocationRecord record = dataSource
						.getUnCheckedWatchedLocationRecord(location);
				record.setCheckOut(new Date(time));
				location.setStatus(WatchedLocation.OUTSIDE_LOCATION);
				dataSource.updateRecord(record);
				dataSource.updateWatchedLocation(location);
				break;
				
			case WatchedLocation.OUTSIDE_LOCATION:
				
				Log.w(TAG, "User has leaving " + location.getName()
						+ " and it wasn't there yet");
				break;
			}

		} catch (NoWatchedLocationFoundException e) {
			Log.e(TAG, "Watched Location doesn't exist ", e);
		}
	}
}
