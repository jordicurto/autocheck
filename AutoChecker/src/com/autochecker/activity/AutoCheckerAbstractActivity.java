package com.autochecker.activity;

import com.autochecker.service.AutoCheckerServiceIntent;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public abstract class AutoCheckerAbstractActivity extends Activity {

	private final String TAG = getClass().getSimpleName();

	//
	protected class AutoCheckerActvityBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			onReceiveTransition(intent.getIntExtra(AutoCheckerServiceIntent.LOCATION_ID, -1));
		}

	}

	protected AutoCheckerActvityBroadcastReceiver serviceReciever = new AutoCheckerActvityBroadcastReceiver();

	@Override
	protected void onStart() {
		super.onStart();

		registerReceiver(serviceReciever, new IntentFilter(AutoCheckerServiceIntent.TRANSITION_RECEIVED));

		Log.i(TAG, "Activity started, Starting service...");
		startService(new AutoCheckerServiceIntent(this, AutoCheckerServiceIntent.REGISTER_ALL_LOCATIONS));
	}

	@Override
	protected void onStop() {

		unregisterReceiver(serviceReciever);
		super.onStop();
	}

	protected abstract void onReceiveTransition(int locationId);
}
