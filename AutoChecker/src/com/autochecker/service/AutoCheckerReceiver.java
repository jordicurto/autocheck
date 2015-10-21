package com.autochecker.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoCheckerReceiver extends BroadcastReceiver {

	private final String TAG = getClass().getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {

		/* Check action of intent */

		// Process a boot completed intent
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

			Log.d(TAG, "Boot completed received. Starting service");

			context.startService(
					new AutoCheckerServiceIntent(context, AutoCheckerServiceIntent.BOOT_EVENT_RECEIVED));

		}
	}
}
