package com.autochecker.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocChangedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Bundle b = intent.getExtras();
	    Location loc = (Location)b.get(LocationManager.KEY_LOCATION_CHANGED);

		Log.d(getClass().getSimpleName(), "location changed " + loc.toString());

	}

}
