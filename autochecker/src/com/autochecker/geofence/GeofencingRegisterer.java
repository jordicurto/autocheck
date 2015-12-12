package com.autochecker.geofence;

import java.util.List;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

public class GeofencingRegisterer implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	public static final String GEOFENCE_TRANSITION_RECEIVED = "GEOFENCE_TRANSITION_RECEIVED";

	private Context mContext;
	private GoogleApiClient mGoogleApiClient;
	private List<Geofence> geofencesToAdd;
	private PendingIntent mGeofencePendingIntent;

	public final String TAG = getClass().getSimpleName();

	public GeofencingRegisterer(Context context) {
		mContext = context;
	}

	public void registerGeofences(List<Geofence> geofences) {
		geofencesToAdd = geofences;

		mGoogleApiClient = new GoogleApiClient.Builder(mContext)
				.addApi(LocationServices.API).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).build();
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnected(Bundle bundle) {

		mGeofencePendingIntent = createRequestPendingIntent();
		GeofencingRequest request = new GeofencingRequest.Builder()
				.addGeofences(geofencesToAdd).build();
		PendingResult<Status> result = LocationServices.GeofencingApi
				.addGeofences(mGoogleApiClient, request, mGeofencePendingIntent);
		result.setResultCallback(new ResultCallback<Status>() {
			@Override
			public void onResult(Status status) {
				if (status.isSuccess()) {
					Log.i(TAG, "Registering successful ");
				} else {
					// No recovery. Weep softly or inform the user.
					Log.e(TAG, "Registering failed: " + status.getStatusCode());
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
	 * Returns the current PendingIntent to the caller.
	 * 
	 * @return The PendingIntent used to create the current set of geofences
	 */
	public PendingIntent getRequestPendingIntent() {
		return createRequestPendingIntent();
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
			return PendingIntent
					.getBroadcast(mContext, 0, new Intent(
							GEOFENCE_TRANSITION_RECEIVED),
							PendingIntent.FLAG_CANCEL_CURRENT);
		}
	}
}