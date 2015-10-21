package com.autochecker.geofence;

import java.util.ArrayList;
import java.util.List;

import com.autochecker.data.model.WatchedLocation;
import com.autochecker.service.AutoCheckerServiceIntent;
import com.autochecker.util.ContextKeeper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class AutoCheckerGeofencingRegisterer extends ContextKeeper
		implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	private static final String AUTOCHECKER_GEOFENCE_REQ_ID = "AUTOCHECKER_GEOFENCE_";

	private GoogleApiClient mGoogleApiClient;
	private PendingIntent mGeofencePendingIntent;
	private List<Geofence> geofencesToAdd  = new ArrayList<Geofence>();

	public final String TAG = getClass().getSimpleName();
		
	public AutoCheckerGeofencingRegisterer(Context context) {
		super(context);
	}

	private Geofence createGeofence(WatchedLocation location) {
		return new Geofence.Builder()
				.setCircularRegion(location.getLatitude(), location.getLongitude(), location.getRadius())
				.setExpirationDuration(Geofence.NEVER_EXPIRE)
				.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
				.setRequestId(AUTOCHECKER_GEOFENCE_REQ_ID + location.getId()).build();
	}

	private void updateGeofences(List<WatchedLocation> list) {
		
		geofencesToAdd.clear();
		for (WatchedLocation location : list)
			geofencesToAdd.add(createGeofence(location));
	}

	public void registerGeofences(List<WatchedLocation> watchedLocationList) {

		updateGeofences(watchedLocationList);
		mGoogleApiClient = new GoogleApiClient.Builder(mContext).addApi(LocationServices.API)
				.addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnected(Bundle bundle) {

		mGeofencePendingIntent = createRequestPendingIntent();
		GeofencingRequest request = new GeofencingRequest.Builder().addGeofences(geofencesToAdd).build();
		PendingResult<Status> result = LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, request,
				mGeofencePendingIntent);
		result.setResultCallback(new ResultCallback<Status>() {
			@Override
			public void onResult(Status status) {
				if (status.isSuccess()) {
					Log.i(TAG, "Registering successful");
				} else {
					Log.e(TAG, "Registering failed: " + 
							AutoCheckerGeofenceErrorMessage.getErrorString(status.getStatusCode()));
				}
			}
		});
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.e(TAG, "onConnectionSuspended: " + i);
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.e(TAG, "onConnectionFailed: " + connectionResult.getErrorCode());
	}

	/**
	 * Get a PendingIntent to send with the request to add Geofences. Location
	 * Services issues the Intent inside this PendingIntent whenever a geofence
	 * transition occurs for the current list of geofences.
	 * 
	 * @return A PendingIntent for the IntentService that handles geofence
	 *         transitions.
	 */
	private PendingIntent createRequestPendingIntent() {
		if (mGeofencePendingIntent != null) {
			return mGeofencePendingIntent;
		} else {
			return PendingIntent.getService(mContext, 0,
					new AutoCheckerServiceIntent(mContext, AutoCheckerServiceIntent.GEOFENCE_TRANSITION_RECEIVED),
					PendingIntent.FLAG_CANCEL_CURRENT);
		}
	}

	public int getLocationId(Geofence fence) throws NumberFormatException {
		return new Integer(fence.getRequestId().substring(AUTOCHECKER_GEOFENCE_REQ_ID.length())).intValue();
	}
}