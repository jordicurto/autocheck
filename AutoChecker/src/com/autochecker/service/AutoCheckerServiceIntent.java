package com.autochecker.service;

import android.content.Context;
import android.content.Intent;

public class AutoCheckerServiceIntent extends Intent {

    public static final String GEOFENCE_TRANSITION_RECEIVED = "GEOFENCE_TRANSITION_RECEIVED";
	public static final String ALARM_NOTIFICATION_DURATION = "ALARM_NOTIFICATION_DURATION";
	
	public final static String BOOT_EVENT_RECEIVED = "BOOT_EVENT_RECIVED";
	public final static String LOCATIONS_ACTIVITY_STARTED = "ACTIVITY_STARTED";
	public final static String REGISTER_LOCATION = "REGISTER_LOCATION";
	public final static String UNREGISTER_LOCATION = "UNREGISTER_LOCATION";
	
	public final static String TRANSITION_RECEIVED = "TRANSITION_RECEIVED";
	
	public static final String LOCATION_ID = "LOCATION_ID";
	
	public AutoCheckerServiceIntent(Context context, String action) {
		super(context, AutoCheckerService.class);
		setAction(action);
	}
}
