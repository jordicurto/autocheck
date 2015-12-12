package com.autochecker.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.autochecker.geofence.GeofencingRegisterer;
import com.autochecker.listener.AutoCheckerProximityListener;
import com.autochecker.listener.IProximityListener;
import com.autochecker.notification.AutoCheckerNotificationManager;
import com.autochecker.notification.INotificationManager;
import com.autochecker.util.DateUtils;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class AutoCheckerReceiver extends BroadcastReceiver {

	private final String TAG = getClass().getSimpleName();

	private IProximityListener proximityListener = new AutoCheckerProximityListener();
	private INotificationManager notificationManager = new AutoCheckerNotificationManager();

	@Override
	public void onReceive(Context context, Intent intent) {

		/* Check action of intent */

		// Process a boot completed intent
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

			Log.d(TAG, "Boot completed received. Starting service");

			context.startService(new Intent(context, AutoCheckerService.class));

		} else if (intent.getAction().equals(
				GeofencingRegisterer.GEOFENCE_TRANSITION_RECEIVED)) {

			GeofencingEvent event = GeofencingEvent.fromIntent(intent);
			
			if (event != null) {
				
				if (event.hasError()) {
					
					Log.e(TAG, "GeoFence Error: " + event.getErrorCode());
					
				} else {

					Log.d(TAG, "Proximity alert received by geofencing ");
					
					long time = DateUtils.currentTimeMillis();
					
					//Location location = event.getTriggeringLocation();

					for (Geofence fence : event.getTriggeringGeofences()) {

						int locationId = new Integer(fence.getRequestId())
								.intValue();

						switch (event.getGeofenceTransition()) {
						case Geofence.GEOFENCE_TRANSITION_ENTER:
							proximityListener
									.onEnter(locationId, time, context);
							break;
						case Geofence.GEOFENCE_TRANSITION_EXIT:
							proximityListener
									.onLeave(locationId, time, context);
							break;
						default:
							break;
						}

						notifyGeofenceTransition(context, locationId);
					}
				}
			}

		} else if (intent.getAction().equals(
				AutoCheckerService.ALARM_NOTIFICATION_DURATION)) {

			Log.d(TAG, "Alarm notification event received");

			notificationManager.notifyUser(context);

		}
	}
	
	private void notifyGeofenceTransition(Context context, int locationId) {

		Message msg = Message.obtain(null,
				AutoCheckerService.MSG_GEOFENCE_TRANSITION_DONE, locationId, -1);

		IBinder binder = peekService(context, new Intent(context,
				AutoCheckerService.class));

		if (binder != null) {

			Messenger messenger = new Messenger(binder);

			try {
				messenger.send(msg);
			} catch (RemoteException e) {
				Log.e(TAG,
						"Can't send geofence transiton message to service", e);
			}

		} else {
			Log.i(TAG, "Can't peek service. Service is not running...");
		}

	}
}
