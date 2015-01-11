package com.autochecker.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.autochecker.listener.AutoCheckerProximityListener;
import com.autochecker.listener.IProximityListener;
import com.autochecker.notification.AutoCheckerNotificationManager;
import com.autochecker.notification.INotificationManager;

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

			// Process a proximity alert intent
		} else if (intent.getAction().equals(
				AutoCheckerService.PROX_ALERT_INTENT)) {

			Log.d(TAG, "Proximity alert received");

			long time = System.currentTimeMillis();

			boolean entered = intent.getBooleanExtra(
					LocationManager.KEY_PROXIMITY_ENTERING, false);

			int locationId = intent.getBundleExtra(
					AutoCheckerService.PROX_ALERT_INTENT).getInt(
					AutoCheckerService.LOCATION_ALERT);

			if (entered) {
				proximityListener.onEnter(locationId, time, context);
			} else {
				proximityListener.onLeave(locationId, time, context);
			}

			notifyService(context, locationId);

		} else if (intent.getAction().equals(
				AutoCheckerService.ALARM_NOTIFICATION_DURATION)) {
			
			Log.d(TAG, "Alarm notification event received");

			notificationManager.notifyUser(context);
			
		} else if (intent.getAction().equals(
				AutoCheckerProximityListener.ALARM_ENTERING_LOCATION)) {
			
			Log.d(TAG, "Alarm entering location received");
			
			int locationId = intent.getBundleExtra(
					AutoCheckerProximityListener.ALARM_ENTERING_LOCATION).getInt(
					AutoCheckerProximityListener.ALARM_LOCATION);
			
			proximityListener.onConfirmEnter(locationId, context);
			
		} else if (intent.getAction().equals(
				AutoCheckerProximityListener.ALARM_LEAVING_LOCATION)) {
			
			Log.d(TAG, "Alarm leaving location received");
			
			int locationId = intent.getBundleExtra(
					AutoCheckerProximityListener.ALARM_LEAVING_LOCATION).getInt(
					AutoCheckerProximityListener.ALARM_LOCATION);
			
			proximityListener.onConfirmLeave(locationId, context);

		} else {

			Log.w(TAG, "Unrecongnized intent " + intent.toString());
		}

	}

	private void notifyService(Context context, int locationId) {

		Message msg = Message.obtain(null,
				AutoCheckerService.MSG_PROX_ALERT_DONE, locationId, -1);

		IBinder binder = peekService(context, new Intent(context,
				AutoCheckerService.class));

		if (binder != null) {

			Messenger messenger = new Messenger(binder);

			try {
				messenger.send(msg);
			} catch (RemoteException e) {
				Log.e(TAG,
						"Can't send proximity alert done message to service", e);
			}

		} else {
			Log.i(TAG, "Can't peek service. Service is not running...");
		}

	}
}
