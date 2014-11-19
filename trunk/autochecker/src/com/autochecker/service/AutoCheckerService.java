package com.autochecker.service;

import java.util.Date;
import java.util.List;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.autochecker.data.AutoCheckerDataSource;
import com.autochecker.data.exception.NoWatchedLocationFoundException;
import com.autochecker.data.model.WatchedLocation;
import com.autochecker.data.model.WatchedLocationRecord;
import com.autochecker.listener.IProximityListener;
import com.autochecker.service.model.Check;

public class AutoCheckerService extends Service implements IProximityListener {

	public static final String LOCATION_ALERT = "ALERT_LOCATION";
	public static final String PROX_ALERT_INTENT = "ACTION_PROXIMITY_ALERT";

	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;
	public static final int MSG_PROX_ALERT_RECEIVED = 3;

	private final String TAG = getClass().getSimpleName();

	private LocationManager locationManager;
	private AutoCheckerDataSource dataSource;

	private static final long LOCATION_INTERVAL = -1;

	private static class IncomingHandler extends Handler {

		private final String TAG = getClass().getSimpleName();
		private IProximityListener listener;

		public IncomingHandler(IProximityListener listener) {
			this.listener = listener;
		}

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			
			case MSG_REGISTER_CLIENT:
				
				replyMessenger = msg.replyTo;
				break;
				
			case MSG_UNREGISTER_CLIENT:

				replyMessenger = null;
				break;
				
			case MSG_PROX_ALERT_RECEIVED:
				
				Log.d(TAG, "Proximity alert received from receiver...");

				Check check = (Check) msg.obj;

				if (check.isEnter()) {
					listener.onEnter(check.getLocationId(), check.getTime());
				} else {
					listener.onLeave(check.getLocationId(), check.getTime());
				}

				if (replyMessenger != null) {
					try {
						replyMessenger.send(msg);
					} catch (RemoteException e) {
						Log.e(TAG,
								"Can't send proximity alert done message to activity",
								e);
					}
				}
				break;
				
			default:
				super.handleMessage(msg);
			}
		}
	}

	private final Messenger messenger = new Messenger(new IncomingHandler(this));

	private static Messenger replyMessenger;

	@Override
	public IBinder onBind(Intent intent) {
		return messenger.getBinder();
	}
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		
		Log.i(TAG, "onCreate");
		
		if (dataSource == null) {
			dataSource = new AutoCheckerDataSource(getApplicationContext());
		}

		if (locationManager == null) {
			locationManager = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		try {

			Log.i(TAG, "onStartCommand");
			
			dataSource.open();
			
			List<WatchedLocation> list = dataSource.getAllWatchedLocations();

			for (WatchedLocation location : list) {
				addProximityAlert(location);
			}

			dataSource.close();

		} catch (SQLException e) {
			Log.e(TAG, "DataSource open exception", e);
		}

		return START_STICKY;
	}

	private void addProximityAlert(WatchedLocation location) {

		Log.d(TAG, "Adding proximity alert " + location.toString());

		locationManager.addProximityAlert(location.getLatitude(),
				location.getLongitude(), location.getRadius(),
				LOCATION_INTERVAL, getPendingIntent(location));

	}

	private PendingIntent getPendingIntent(WatchedLocation location) {

		Bundle extra = new Bundle();

		extra.putInt(LOCATION_ALERT, location.getId());

		Intent intent = new Intent(PROX_ALERT_INTENT);
		intent.putExtra(PROX_ALERT_INTENT, extra);

		return PendingIntent.getBroadcast(this, location.getId(), intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
	}

	@Override
	public void onEnter(int locationId, long time) {

		Log.d(TAG, "Processing enter event");

		try {

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
	public void onLeave(int locationId, long time) {

		Log.d(TAG, "Processing leave event");

		try {

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
