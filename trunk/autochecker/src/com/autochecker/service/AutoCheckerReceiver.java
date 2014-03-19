package com.autochecker.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.autochecker.listener.AutoCheckerProximityListener;
import com.autochecker.listener.IProximityListener;

public class AutoCheckerReceiver extends BroadcastReceiver {
	
	private IProximityListener listener = null;
	
	private final String TAG = getClass().getSimpleName();

	private Messenger messengerService;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		/* Check action of intent */
		
		// Process a boot completed intent 
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			
			Log.d(TAG, "Boot completed received. Starting service");

			context.startService(new Intent(context, AutoCheckerService.class));
		
		// Process a proximity alert intent
		} else if (intent.getAction().equals(AutoCheckerService.PROX_ALERT_INTENT)) {
			
			Log.d(TAG, "Proximity alert received");
				
			if (listener == null) {
				listener = new AutoCheckerProximityListener(context);
			}
			
			long time = System.currentTimeMillis();

			boolean entered = intent.getBooleanExtra(
					LocationManager.KEY_PROXIMITY_ENTERING, false);

			Bundle b = intent.getBundleExtra(AutoCheckerService.PROX_ALERT_INTENT);
			int locationId = b.getInt(AutoCheckerService.LOCATION_ALERT);
			
			if (entered) {
				listener.onEnter(locationId, time);
			} else {
				listener.onLeave(locationId, time);
			}
			
			if (messengerService == null) {
				IBinder binder = peekService(context, new Intent(context, AutoCheckerService.class));
				messengerService = new Messenger(binder);
			}
			
			Message msg = Message.obtain(null, AutoCheckerService.MSG_PROX_ALERT_DONE);
			try {
				messengerService.send(msg);
			} catch (RemoteException e) {
				Log.e(TAG, "Can't send proximity alert done message to service", e);
			}
			
		} else {
			
			Log.w(TAG, "Unrecongnized intent " + intent.toString());
		}

	}

}
