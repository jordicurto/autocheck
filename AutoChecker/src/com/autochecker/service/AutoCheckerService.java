package com.autochecker.service;

import java.util.List;

import com.autochecker.data.model.WatchedLocation;
import com.autochecker.geofence.AutoCheckerGeofenceErrorMessage;
import com.autochecker.geofence.AutoCheckerGeofencingRegisterer;
import com.autochecker.managers.AutoCheckerNotificationManager;
import com.autochecker.managers.AutoCheckerTransitionManager;
import com.autochecker.managers.AutoCheckerTransitionManager.ETransitionType;
import com.autochecker.util.DateUtils;
import com.autochecker.util.Duration;
import com.autochecker.util.LocationUtils;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

public class AutoCheckerService extends Service {

	private static final long INVALID_DELAY = -1L;
	private static final long DELAY_FOR_SUSPECT_TRANSITION = 3 * Duration.MINS_PER_MILLISECOND;
	private static final long DELAY_FOR_STANDARD_TRANSITION = 1 * Duration.SECS_PER_MILLISECOND;

	private final String TAG = getClass().getSimpleName();

	private AutoCheckerGeofencingRegisterer geofencingRegisterer;
	private AutoCheckerTransitionManager transitionManager;
	private AutoCheckerNotificationManager notificationManager;

	@Override
	public void onCreate() {
		super.onCreate();

		Log.i(TAG, "onCreate");

		geofencingRegisterer = new AutoCheckerGeofencingRegisterer(this);
		transitionManager = new AutoCheckerTransitionManager(this);
		notificationManager = new AutoCheckerNotificationManager(this);
		
		try {

			Log.d(TAG, "Creating and registering geofences ");

			List<WatchedLocation> list = transitionManager.getAllWatchedLocations();

			geofencingRegisterer.registerGeofences(list);

			AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

			Log.d(TAG, "Setting alarm to launch notifications ");

			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					(System.currentTimeMillis() + (2 * Duration.MINS_PER_MILLISECOND)),
					AlarmManager.INTERVAL_FIFTEEN_MINUTES,
					PendingIntent.getService(this, 0,
							new AutoCheckerServiceIntent(this, AutoCheckerServiceIntent.ALARM_NOTIFICATION_DURATION),
							PendingIntent.FLAG_CANCEL_CURRENT));

		} catch (SQLException e) {
			Log.e(TAG, "DataSource open exception", e);
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.i(TAG, "onStartCommand");

		if (intent.getAction().equals(AutoCheckerServiceIntent.BOOT_EVENT_RECEIVED)) {
			
			Log.d(TAG, "Boot event recieved from broadcast receiver");
			
		} else if (intent.getAction().equals(AutoCheckerServiceIntent.LOCATIONS_ACTIVITY_STARTED)) {
			
			Log.d(TAG, "Activity started");

		} else if (intent.getAction().equals(AutoCheckerServiceIntent.REGISTER_LOCATION)) {

			// TODO: Implementar el regisre d'una localitzacio

		} else if (intent.getAction().equals(AutoCheckerServiceIntent.UNREGISTER_LOCATION)) {

			// TODO: Implementar el desregisre d'una localitzacio

		} else if (intent.getAction().equals(AutoCheckerServiceIntent.ALARM_NOTIFICATION_DURATION)) {

			Log.d(TAG, "Alarm notification event received");

			notificationManager.notifyUser();

		} else if (intent.getAction().equals(AutoCheckerServiceIntent.GEOFENCE_TRANSITION_RECEIVED)) {

			Log.d(TAG, "Proximity alert received by geofencing ");

			GeofencingEvent event = GeofencingEvent.fromIntent(intent);

			if (event != null) {

				if (event.hasError()) {

					Log.e(TAG, "GeoFence Error: " + AutoCheckerGeofenceErrorMessage.getErrorString(event.getErrorCode()));

				} else {

					long time = DateUtils.currentTimeMillis();

					Location triggerLocation = event.getTriggeringLocation();

					for (Geofence fence : event.getTriggeringGeofences()) {

						try {

							int locationId = geofencingRegisterer.getLocationId(fence);

							WatchedLocation location = transitionManager.getWatchedLocation(locationId);

							if (location != null && triggerLocation != null) {

								ETransitionType tType = transitionManager.getScheduledTransitionType();

								if (tType != null) {

									switch (tType) {
									case ENTER_TRANSITION:
										switch (event.getGeofenceTransition()) {
										case Geofence.GEOFENCE_TRANSITION_ENTER:
											Log.w(TAG, "Enter transition already scheduled. Event ignored");
											break;
										case Geofence.GEOFENCE_TRANSITION_EXIT:
											transitionManager.cancelScheduledRegisterTransition();
											Log.i(TAG,
													"Received leave transition while enter transition is scheduled. Scheduled transition cancelled");
											break;
										default:
											break;
										}
									case LEAVE_TRANSITION:
										switch (event.getGeofenceTransition()) {
										case Geofence.GEOFENCE_TRANSITION_ENTER:
											transitionManager.cancelScheduledRegisterTransition();
											Log.i(TAG,
													"Received enter transition while leave transition is scheduled. Scheduled transition cancelled");
											break;
										case Geofence.GEOFENCE_TRANSITION_EXIT:
											Log.w(TAG, "Leave transition already scheduled. Event ignored");
											break;
										default:
											break;
										}
									}

								} else {

									long delay = calculateDelayForRegisterTransition(triggerLocation, location,
											event.getGeofenceTransition());

									switch (event.getGeofenceTransition()) {
									case Geofence.GEOFENCE_TRANSITION_ENTER:
										transitionManager.scheduleRegisterTransition(location, time,
												ETransitionType.ENTER_TRANSITION, delay);
										break;
									case Geofence.GEOFENCE_TRANSITION_EXIT:
										transitionManager.scheduleRegisterTransition(location, time,
												ETransitionType.LEAVE_TRANSITION, delay);
										break;
									default:
										break;
									}
								}
							}

						} catch (NumberFormatException e) {
							Log.e(TAG, "Can't get location id from : " + fence.getRequestId());
						}
					}
				}
			}
		}
		
		return START_NOT_STICKY;
	}

	private long calculateDelayForRegisterTransition(Location triggerLocation, WatchedLocation wLocation,
			int transition) {

		Location location = LocationUtils.getLocationFromWatchedLocation(wLocation);

		float distance = location.distanceTo(triggerLocation);

		boolean intersect = distance < (wLocation.getRadius() + triggerLocation.getAccuracy());

		boolean locInsideTriggerLoc = (distance + wLocation.getRadius()) < triggerLocation.getAccuracy();

		switch (transition) {
		case Geofence.GEOFENCE_TRANSITION_ENTER:

			return DELAY_FOR_STANDARD_TRANSITION;
			
		case Geofence.GEOFENCE_TRANSITION_EXIT:

			if (intersect && locInsideTriggerLoc) {

				Log.d(TAG,
						"Location is inside trigger location. Maybe accuracy has changed because location source has changed");

				return DELAY_FOR_SUSPECT_TRANSITION;

			} else if (intersect) {

				Log.d(TAG,
						"Location and trigger location intersects. Maybe accuracy has changed because location source has changed");

				return DELAY_FOR_SUSPECT_TRANSITION;

			} else {

				return DELAY_FOR_STANDARD_TRANSITION;
			}

		default:
			return INVALID_DELAY;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}