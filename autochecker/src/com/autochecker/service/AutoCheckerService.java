package com.autochecker.service;

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
import com.autochecker.data.model.WatchedLocation;

public class AutoCheckerService extends Service {
	
	public static final String LOCATION_ALERT = "ALERT_LOCATION";
	public static final String PROX_ALERT_INTENT = "ACTION_PROXIMITY_ALERT";
	
	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;
	
	public static final int MSG_PROX_ALERT_DONE = 3;

	private final String TAG = getClass().getSimpleName();

	private LocationManager locationManager;
	private AutoCheckerDataSource dataSource;

	private static final long LOCATION_INTERVAL = -1;
	
	private class IncomingHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			
            switch (msg.what) {
            case MSG_REGISTER_CLIENT:
            	replyMessenger = msg.replyTo;
            	break;
            case MSG_UNREGISTER_CLIENT:
            	replyMessenger = null;
            	break;
            case MSG_PROX_ALERT_DONE:
            	if (replyMessenger != null) {
            		try {
						replyMessenger.send(msg);
					} catch (RemoteException e) {
						Log.e(TAG, "Can't send proximity alert done message to service", e);
					}
            	}
            default:
            	super.handleMessage(msg);
            }
		}
	}
	
	private final Messenger messenger = new Messenger(new IncomingHandler());
	
	private Messenger replyMessenger;

	@Override
	public IBinder onBind(Intent intent) {
		return messenger.getBinder();
	}

	@Override
	public void onCreate() {
		
		Log.i(TAG, "onCreate");

		initialize();

		List<WatchedLocation> list = dataSource.getAllWatchedLocations();

		for (WatchedLocation location : list) {

			addProximityAlert(location);
		}
	}
	
	@Override
	public void onDestroy() {
		
		dataSource.close();
		
		super.onDestroy();
	}

	private void addProximityAlert(WatchedLocation location) {

		Log.d(TAG, "Adding proximity alert " + location.toString());
		
		locationManager.addProximityAlert(location.getLatitude(),
				location.getLongitude(), location.getAccuracy(),
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

	private void initialize() {

		Log.d(TAG, "initialize");

		if (dataSource == null) {
			dataSource = new AutoCheckerDataSource(getApplicationContext());
			try {
				dataSource.open();
			} catch (SQLException e) {
				Log.e(TAG, "DataSource open exception", e);
			}
		}

		if (locationManager == null) {
			locationManager = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);
		}
	}
}
