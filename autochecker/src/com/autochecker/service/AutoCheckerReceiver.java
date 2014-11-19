package com.autochecker.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.autochecker.service.model.Check;

public class AutoCheckerReceiver extends BroadcastReceiver implements
		ServiceConnection {

	private final String TAG = getClass().getSimpleName();

	private Messenger messengerService = null;

	private Check lastCheck;

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

			Bundle b = intent
					.getBundleExtra(AutoCheckerService.PROX_ALERT_INTENT);
			int locationId = b.getInt(AutoCheckerService.LOCATION_ALERT);

			lastCheck = new Check(locationId, entered, time);

			context.bindService(new Intent(context, AutoCheckerService.class),
					this, Context.BIND_AUTO_CREATE);

			notifyService();

		} else {

			Log.w(TAG, "Unrecongnized intent " + intent.toString());
		}

	}

	private void notifyService() {

		if (messengerService != null) {

			Log.d(TAG, "Sending message to service...");
			
			Message msg = Message.obtain(null,
					AutoCheckerService.MSG_PROX_ALERT_RECEIVED, lastCheck);

			try {
				
				messengerService.send(msg);
				
			} catch (RemoteException e) {
				Log.e(TAG,
						"Can't send proximity alert message to service", e);
			}
			
			lastCheck = null;
			
		} else {
			Log.w(TAG, "Service is not bound yet!");
		}
	}

	@Override
	public void onServiceConnected(ComponentName className,
			IBinder serviceBinder) {

		messengerService = new Messenger(serviceBinder);

		if (lastCheck != null) {
			notifyService();
		}

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		lastCheck = null;
		messengerService = null;
	}
}
