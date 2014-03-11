package com.autochecker.service;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.SQLException;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.autochecker.datamodel.AutoCheckerDataSource;
import com.autochecker.datamodel.Check;
import com.autochecker.datamodel.FavLocation;

public class AutoCheckerProximityReceiver extends BroadcastReceiver {

	private final String TAG = getClass().getSimpleName();

	private AutoCheckerDataSource dataSource = null;
	
	Handler handlerCheckingIn = new Handler();
	Handler handlerCheckingOut = new Handler();

	@Override
	public void onReceive(Context context, Intent intent) {

		long time = System.currentTimeMillis();

		if (dataSource == null) {
			dataSource = new AutoCheckerDataSource(context);
			try {
				dataSource.open();
			} catch (SQLException e) {
				Log.e(TAG, "DataSource open exception", e);
			}
		}

		boolean entered = intent.getBooleanExtra(
				LocationManager.KEY_PROXIMITY_ENTERING, false);

		Bundle b = intent.getBundleExtra(AutoCheckerService.PROX_ALERT_INTENT);
		FavLocation location = (FavLocation) b
				.getSerializable(AutoCheckerService.LOCATION_ALERT);

		try {
			
			boolean isUserInLocation = dataSource.userIsInLocation(location);

			if (entered) {
				Log.d(TAG, "User has entered to " + location.getName());

				if (isUserInLocation) {
					Log.w(TAG, "User has entered to " + location.getName()
							+ " and it was there yet");
				} else {
					Check check = new Check();
					check.setCheckIn(new Date(time));
					check.setCheckOut(null);
					check.setLocation(location);
					location.setOverThere(true);
					dataSource.createCheck(check);
					dataSource.updateFavLocation(location);
				}

			} else {
				Log.d(TAG, "User has leaving " + location.getName());

				if (isUserInLocation) {
					Check check = dataSource.getCheck(location);
					check.setCheckOut(new Date(time));
					location.setOverThere(false);
					dataSource.updateCheck(check);
					dataSource.updateFavLocation(location);
				} else {
					Log.w(TAG, "User has leaving " + location.getName()
							+ " and it wasn't there yet");
				}
			}
			
		} catch (NotFoundException e) {
			Log.e(TAG, "Something hasn't been found ");
		}
	}
}
