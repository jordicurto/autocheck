package com.autochecker.service;

import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.autochecker.data.AutoCheckerDataSource;
import com.autochecker.data.model.Duration;
import com.autochecker.data.model.WatchedLocation;
import com.autochecker.geofence.GeofencingRegisterer;
import com.google.android.gms.location.Geofence;

public class AutoCheckerService extends Service {

	public static final String ALARM_NOTIFICATION_DURATION = "ALARM_NOTIFICATION_DURATION";

	public static final int ALARM_NOTIFICATION_DURATION_CODE = 500;

	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;
	public static final int MSG_GEOFENCE_TRANSITION_DONE = 3;

	private final String TAG = getClass().getSimpleName();

	private AutoCheckerDataSource dataSource;
	private GeofencingRegisterer geofencingRegisterer;

	private static final int TWO_MINUTES = 2 * Duration.MINS_PER_MILLISECOND;

	private static List<Messenger> replyMessenger = new ArrayList<Messenger>();

	private static class IncomingHandler extends Handler {

		private final String TAG = getClass().getSimpleName();

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

			case MSG_GEOFENCE_TRANSITION_DONE:

				Log.d(TAG, "Notifying geofence transition to clients ");

				Message msgAct = Message.obtain(null,
						MSG_GEOFENCE_TRANSITION_DONE, msg.arg1, msg.arg2);

				for (Messenger messenger : replyMessenger) {

					try {
						messenger.send(msgAct);
					} catch (RemoteException e) {
						Log.e(TAG,
								"Can't send proximity alert done message to activity",
								e);
					}
				}

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
	public int onStartCommand(Intent intent, int flags, int startId) {

		try {

			Log.i(TAG, "onStartCommand");

			if (dataSource == null) {
				dataSource = new AutoCheckerDataSource(getApplicationContext());
			}

			if (geofencingRegisterer == null) {
				geofencingRegisterer = new GeofencingRegisterer(
						getApplicationContext());
			}

			if (intent != null) {

				dataSource.open();

				List<WatchedLocation> list = dataSource
						.getAllWatchedLocations();

				dataSource.close();

				Log.d(TAG, "Creating and registering geofences ");

				List<Geofence> geofenceList = createGeofences(list);

				geofencingRegisterer.registerGeofences(geofenceList);

				AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

				Log.d(TAG, "Setting alarm to launch notifications ");
				
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, (System
						.currentTimeMillis() + TWO_MINUTES),
						AlarmManager.INTERVAL_FIFTEEN_MINUTES,
						PendingIntent.getBroadcast(getApplicationContext(),
								ALARM_NOTIFICATION_DURATION_CODE, new Intent(
										ALARM_NOTIFICATION_DURATION),
								PendingIntent.FLAG_CANCEL_CURRENT));
			}

		} catch (SQLException e) {
			Log.e(TAG, "DataSource open exception", e);
		}

		return START_STICKY;
	}

	private Geofence createGeofence(WatchedLocation location) {
		return new Geofence.Builder()
				.setCircularRegion(location.getLatitude(),
						location.getLongitude(), location.getRadius())
				.setExpirationDuration(Geofence.NEVER_EXPIRE)
				.setTransitionTypes(
						Geofence.GEOFENCE_TRANSITION_ENTER
								| Geofence.GEOFENCE_TRANSITION_EXIT)
				.setRequestId(new Integer(location.getId()).toString()).build();
	}

	private List<Geofence> createGeofences(List<WatchedLocation> list) {

		List<Geofence> geofences = new ArrayList<Geofence>();

		for (WatchedLocation location : list) {
			geofences.add(createGeofence(location));
		}

		return geofences;
	}
}