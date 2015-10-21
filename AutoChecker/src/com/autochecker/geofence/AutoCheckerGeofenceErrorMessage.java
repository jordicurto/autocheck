package com.autochecker.geofence;

import com.google.android.gms.location.GeofenceStatusCodes;

public class AutoCheckerGeofenceErrorMessage {
	
	/**
	 * Prevents instantiation.
	 */
	private AutoCheckerGeofenceErrorMessage() {
	}

	/**
	 * Returns the error string for a geofencing error code.
	 */
	public static String getErrorString(int errorCode) {
		switch (errorCode) {
		case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
			return "Geofence is not available";
		case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
			return "Too many geofences";
		case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
			return "Too many pending intents";
		default:
			return "Unknow error code: " + errorCode;
		}
	}
}
