package com.autochecker.service;

import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
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
import com.autochecker.data.model.Duration;
import com.autochecker.data.model.WatchedLocation;

public class AutoCheckerService extends Service {

	public static final String LOCATION_ALERT = "ALERT_LOCATION";
	public static final String PROX_ALERT_INTENT = "ACTION_PROXIMITY_ALERT";
	public static final String ALARM_NOTIFICATION_DURATION = "ALARM_NOTIFICATION_DURATION";

	public static final int ALARM_NOTIFICATION_DURATION_CODE = 500;

	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;
	public static final int MSG_PROX_ALERT_DONE = 3;

	private final String TAG = getClass().getSimpleName();

	private LocationManager locationManager;
	private AutoCheckerDataSource dataSource;

	private static final long LOCATION_INTERVAL = -1;
	private static final long TWO_MINUTES = 2 * Duration.MINS_PER_MILLISECOND;

	private static class IncomingHandler extends Handler {

		private final String TAG = getClass().getSimpleName();

		private List<Messenger> replyMessenger = new ArrayList<Messenger>();

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case MSG_REGISTER_CLIENT:

				Log.d(TAG, "Register client");

				if (!replyMessenger.contains(msg.replyTo)) {
					replyMessenger.add(msg.replyTo);
				}

				break;

			case MSG_UNREGISTER_CLIENT:

				Log.d(TAG, "Unregister client");

				if (replyMessenger.contains(msg.replyTo)) {
					replyMessenger.remove(msg.replyTo);
				}

				break;

			case MSG_PROX_ALERT_DONE:

				Log.d(TAG, "Proximity alert received from receiver...");
				
				Message msgAct = Message.obtain(null, MSG_PROX_ALERT_DONE, msg.arg1, msg.arg2);

				for (Messenger messenger : replyMessenger) {

					try {
						messenger.send(msgAct);
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

	private final Messenger messenger = new Messenger(new IncomingHandler());

	@Override
	public IBinder onBind(Intent intent) {
		return messenger.getBinder();
	}

	@Override
	public void onCreate() {

		super.onCreate();

		try {

			Log.i(TAG, "onCreate");

			if (dataSource == null) {
				dataSource = new AutoCheckerDataSource(getApplicationContext());
			}

			if (locationManager == null) {
				locationManager = (LocationManager) this
						.getSystemService(Context.LOCATION_SERVICE);
			}

			dataSource.open();

			List<WatchedLocation> list = dataSource.getAllWatchedLocations();

			for (WatchedLocation location : list) {
				addProximityAlert(location);
			}

			dataSource.close();

			AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

			Log.d(TAG, "Setting alarm to launch notifications ");

			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					(System.currentTimeMillis() + TWO_MINUTES),
					AlarmManager.INTERVAL_FIFTEEN_MINUTES, PendingIntent
							.getBroadcast(this,
									ALARM_NOTIFICATION_DURATION_CODE,
									new Intent(ALARM_NOTIFICATION_DURATION),
									PendingIntent.FLAG_CANCEL_CURRENT));

		} catch (SQLException e) {
			Log.e(TAG, "DataSource open exception", e);
		}
	}

	private void addProximityAlert(WatchedLocation location) {

		PendingIntent intent = getPendingIntent(location);

		Log.d(TAG, "Adding proximity alert " + location.toString());

		locationManager.addProximityAlert(location.getLatitude(),
				location.getLongitude(), location.getRadius(),
				LOCATION_INTERVAL, intent);

	}

	private PendingIntent getPendingIntent(WatchedLocation location) {

		Bundle extra = new Bundle();

		extra.putInt(LOCATION_ALERT, location.getId());

		Intent intent = new Intent(PROX_ALERT_INTENT);
		intent.putExtra(PROX_ALERT_INTENT, extra);

		return PendingIntent.getBroadcast(this, location.getId(), intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
	}

}