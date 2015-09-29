package com.autochecker.managers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.autochecker.data.AutoCheckerDataSource;
import com.autochecker.data.exception.NoLocationFoundException;
import com.autochecker.data.exception.NoRecordFoundException;
import com.autochecker.data.model.WatchedLocation;
import com.autochecker.data.model.WatchedLocationRecord;
import com.autochecker.service.AutoCheckerServiceIntent;
import com.autochecker.util.ContextKeeper;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.util.Log;

public class AutoCheckerTransitionManager extends ContextKeeper {

	private final String TAG = getClass().getSimpleName();
	
	public enum ETransitionType {
		ENTER_TRANSITION,
		LEAVE_TRANSITION
	}
	
	private AutoCheckerDataSource dataSource;

	private ScheduledFuture<?> scheduledTask;
	
	private TransitionScheduledRegister transitionScheduled;	

	private class TransitionScheduledRegister implements Runnable {

		private WatchedLocation location;
		private long time;
		private ETransitionType tType;

		public TransitionScheduledRegister(WatchedLocation location, long time, ETransitionType tType) {
			this.location = location;
			this.time = time;
			this.tType = tType;
		}

		@Override
		public void run() {
			
			switch (tType) {
			case ENTER_TRANSITION:
				registerEnterTransition(location, time);
				break;
			case LEAVE_TRANSITION:
				registerLeaveTransition(location, time);
				break;
			}
			
			Intent intentAct = new Intent(AutoCheckerServiceIntent.TRANSITION_RECEIVED);
			intentAct.putExtra(AutoCheckerServiceIntent.LOCATION_ID, location.getId());							
			mContext.sendBroadcast(intentAct);
		}

		public ETransitionType gettType() {
			return tType;
		}
		
		private void registerEnterTransition(WatchedLocation location, long time) {

			Log.d(TAG, "Processing enter event");

			try {

				dataSource.open();

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

					Log.w(TAG, "User has entered to " + location.getName() + " and it was there yet");
					break;
				}

				dataSource.close();

			} catch (SQLException e) {
				Log.e(TAG, "DataSource exception", e);
			} finally {
				dataSource.close();
			}
		}

		private void registerLeaveTransition(WatchedLocation location, long time) {

			Log.d(TAG, "Processing leave event");

			try {

				dataSource.open();

				switch (location.getStatus()) {

				case WatchedLocation.INSIDE_LOCATION:

					location.setStatus(WatchedLocation.OUTSIDE_LOCATION);
					dataSource.updateWatchedLocation(location);

					WatchedLocationRecord record = dataSource.getUnCheckedWatchedLocationRecord(location);
					record.setCheckOut(new Date(time));
					dataSource.updateRecord(record);

					Log.i(TAG, "User has left " + location.getName());

					break;

				case WatchedLocation.OUTSIDE_LOCATION:

					Log.w(TAG, "User has leaving " + location.getName() + " and it wasn't there yet");
					break;
				}

				dataSource.close();

			} catch (NoRecordFoundException e) {
				Log.e(TAG, "Record not found execption", e);
			} catch (SQLException e) {
				Log.e(TAG, "DataSource exception", e);
			} finally {
				dataSource.close();
			}
		}
	}

	public AutoCheckerTransitionManager(Context context) {
		super(context);
		dataSource = AutoCheckerDataSource.getInstance(context);
	}
	
	public void scheduleRegisterTransition(WatchedLocation location, long time, ETransitionType tType, long delay) {
		cancelScheduledRegisterTransition();
		transitionScheduled = new TransitionScheduledRegister(location, time, tType);
		scheduledTask = Executors.newSingleThreadScheduledExecutor()
				.schedule(transitionScheduled, delay, TimeUnit.MILLISECONDS);
	}
	
	public ETransitionType getScheduledTransitionType() {
		if (isRegisterTransitionScheduled()) {
			return transitionScheduled.gettType();
		} else {
			return null;
		}
	}
	
	public boolean isRegisterTransitionScheduled() {
		return (scheduledTask != null && scheduledTask.getDelay(TimeUnit.MILLISECONDS) > 0);
	}
	
	public void cancelScheduledRegisterTransition() {

		if (isRegisterTransitionScheduled()) {
			scheduledTask.cancel(false);
		}
	}
	
	public List<WatchedLocation> getAllWatchedLocations() {

		List<WatchedLocation> list = new ArrayList<WatchedLocation>();

		try {

			dataSource.open();
			list = dataSource.getAllWatchedLocations();
			dataSource.close();

		} catch (SQLException e) {
			Log.e(TAG, "DataSource exception", e);
		} finally {
			dataSource.close();
		}

		return list;
	}

	public WatchedLocation getWatchedLocation(int locationId) {

		WatchedLocation location = null;
		try {

			dataSource.open();
			location = dataSource.getWatchedLocation(locationId);
			dataSource.close();

		} catch (NoLocationFoundException e) {
			Log.e(TAG, "Location not found execption", e);
		} catch (SQLException e) {
			Log.e(TAG, "DataSource exception", e);
		} finally {
			dataSource.close();
		}

		return location;
	}
}
