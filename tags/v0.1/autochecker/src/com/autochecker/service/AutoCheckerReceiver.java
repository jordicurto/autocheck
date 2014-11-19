package com.autochecker.service;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.location.LocationManager;
import android.os.Bundle;
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

public class AutoCheckerReceiver extends BroadcastReceiver implements IProximityListener {
	
	private final String TAG = getClass().getSimpleName();
	
	private AutoCheckerDataSource dataSource;
	
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
			
			long time = System.currentTimeMillis();

			boolean entered = intent.getBooleanExtra(
					LocationManager.KEY_PROXIMITY_ENTERING, false);

			Bundle b = intent.getBundleExtra(AutoCheckerService.PROX_ALERT_INTENT);
			int locationId = b.getInt(AutoCheckerService.LOCATION_ALERT);
			
			if(dataSource == null) {
				dataSource = new AutoCheckerDataSource(context);
			}
			
			if (entered) {
				onEnter(locationId, time);
			} else {
				onLeave(locationId, time);
			}
			
			notifyService(context);
			
		} else {
			
			Log.w(TAG, "Unrecongnized intent " + intent.toString());
		}

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

	private void notifyService(Context context) {

		Message msg = Message.obtain(null,
				AutoCheckerService.MSG_PROX_ALERT_DONE);
		
		IBinder binder = peekService(context, new Intent(context, AutoCheckerService.class));
		
		if (binder != null) {
		
			Messenger messenger = new Messenger(binder);
			
			try {
				messenger.send(msg);
			} catch (RemoteException e) {
				Log.e(TAG,
						"Can't send proximity alert done message to service", e);
			}
			
		} else {
			Log.w(TAG, "Can't peek service. Service not running...");
		}
	}
}
