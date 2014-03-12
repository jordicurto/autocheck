package com.autochecker.service;

import java.util.List;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.autochecker.data.AutoCheckerDataSource;
import com.autochecker.data.model.WatchedLocation;

public class AutoCheckerService extends Service {

	private final String TAG = getClass().getSimpleName();

	private LocationManager locationManager = null;
	private AutoCheckerDataSource dataSource = null;

	private static final long LOCATION_INTERVAL = -1;

	public static final String LOCATION_ALERT = "ALERT_LOCATION";
	public static final String PROX_ALERT_INTENT = "ACTION_PROXIMITY_ALERT";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
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
