package com.autochecker.listener;

import java.util.Date;

import android.content.Context;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.autochecker.data.AutoCheckerDataSource;
import com.autochecker.data.exception.NoWatchedLocationFoundException;
import com.autochecker.data.model.WatchedLocation;
import com.autochecker.data.model.WatchedLocationRecord;
import com.autochecker.service.AutoCheckerService;

public class AutoCheckerProximityListener implements IProximityListener {

	private static final long DELAY_MILLIS = 1000;

	private static final String LOCATION_KEY = "location";
	private static final String TIME_KEY = "time";

	private String TAG = getClass().getSimpleName();

	private static AutoCheckerDataSource dataSource;
	private static Messenger messenger;

	private static Handler chgLocHandler = new Handler() {

		private String TAG = getClass().getSimpleName();

		public void handleMessage(Message msg) {

			switch (msg.what) {

			case WatchedLocation.INSIDE_LOCATION:

				try {

					Bundle bundle = msg.getData();

					WatchedLocation location = dataSource
							.getWatchedLocation(bundle.getInt(LOCATION_KEY));
					location.setStatus(WatchedLocation.INSIDE_LOCATION);
					dataSource.updateWatchedLocation(location);

					WatchedLocationRecord record = new WatchedLocationRecord();
					record.setCheckIn(new Date(bundle.getLong(TIME_KEY)));
					record.setCheckOut(null);
					record.setLocation(location);
					dataSource.insertRecord(record);
					
					notifyService();

					Log.i(TAG, "User has entered to " + location.getName());

				} catch (NoWatchedLocationFoundException e) {
					Log.e(TAG, "Watched Location doesn't exist ", e);
				}
				
				break;

			case WatchedLocation.OUTSIDE_LOCATION:

				try {

					Bundle bundle = msg.getData();

					WatchedLocation location = dataSource
							.getWatchedLocation(bundle.getInt(LOCATION_KEY));
					location.setStatus(WatchedLocation.OUTSIDE_LOCATION);
					dataSource.updateWatchedLocation(location);

					WatchedLocationRecord record = dataSource
							.getUnCheckedWatchedLocationRecord(location);
					record.setCheckOut(new Date(bundle.getLong(TIME_KEY)));
					dataSource.updateRecord(record);
					
					notifyService();

					Log.i(TAG, "User has left " + location.getName());

				} catch (NoWatchedLocationFoundException e) {
					Log.e(TAG, "Watched Location doesn't exist ", e);
				}
				
				break;

			default:
				
				Log.w(TAG, "Unrecognized message received " + msg.what);
				
				break;
			}
		}

		private void notifyService() {
			
			Message msg = Message.obtain(null, AutoCheckerService.MSG_PROX_ALERT_DONE);
			try {
				messenger.send(msg);
			} catch (RemoteException e) {
				Log.e(TAG, "Can't send proximity alert done message to service", e);
			}
			
		};
	};

	public AutoCheckerProximityListener(Context context, Messenger mService) {

		dataSource = new AutoCheckerDataSource(context);
		try {
			dataSource.open();
		} catch (SQLException e) {
			Log.e(TAG, "DataSource open exception", e);
		}
		messenger = mService;
	}

	@Override
	public void onEnter(int locationId, long time) {

		Log.d(TAG, "Processing enter event");

		try {

			WatchedLocation location = dataSource
					.getWatchedLocation(locationId);

			switch (location.getStatus()) {

			case WatchedLocation.OUTSIDE_LOCATION:

				location.setStatus(WatchedLocation.ENTERING_LOCATION);
				dataSource.updateWatchedLocation(location);

				Message msg = new Message();
				msg.what = WatchedLocation.INSIDE_LOCATION;

				Bundle bundle = new Bundle();
				bundle.putInt(LOCATION_KEY, locationId);
				bundle.putLong(TIME_KEY, time);

				msg.setData(bundle);

				chgLocHandler.sendMessageDelayed(msg, DELAY_MILLIS);

				Log.d(TAG, "Queued an enter event");

				break;

			case WatchedLocation.LEAVING_LOCATION:

				chgLocHandler.removeMessages(WatchedLocation.OUTSIDE_LOCATION);

				location.setStatus(WatchedLocation.INSIDE_LOCATION);
				dataSource.updateWatchedLocation(location);

				Log.d(TAG,
						"Detected too early leave -> enter events. This event isn't taken in account");

				break;
				
			case WatchedLocation.INSIDE_LOCATION:
			case WatchedLocation.ENTERING_LOCATION:

				Log.w(TAG, "User has entered to " + location.getName()
						+ " and it was there yet");
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

				location.setStatus(WatchedLocation.LEAVING_LOCATION);
				dataSource.updateWatchedLocation(location);

				Message msg = new Message();
				msg.what = WatchedLocation.OUTSIDE_LOCATION;

				Bundle bundle = new Bundle();
				bundle.putInt(LOCATION_KEY, locationId);
				bundle.putLong(TIME_KEY, time);

				msg.setData(bundle);

				chgLocHandler.sendMessageDelayed(msg, DELAY_MILLIS);

				Log.d(TAG, "Queued a leave event");

				break;

			case WatchedLocation.ENTERING_LOCATION:

				chgLocHandler.removeMessages(WatchedLocation.INSIDE_LOCATION);

				location.setStatus(WatchedLocation.OUTSIDE_LOCATION);
				dataSource.updateWatchedLocation(location);

				Log.d(TAG,
						"Detected too early enter -> leave events. This event isn't taken in account");

				break;
				
			case WatchedLocation.OUTSIDE_LOCATION:
			case WatchedLocation.LEAVING_LOCATION:

				Log.w(TAG, "User has leaving " + location.getName()
						+ " and it wasn't there yet");
				break;
			}

		} catch (NoWatchedLocationFoundException e) {
			Log.e(TAG, "Watched Location doesn't exist ", e);
		}
	}
}
