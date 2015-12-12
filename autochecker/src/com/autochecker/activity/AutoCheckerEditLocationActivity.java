package com.autochecker.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;

import com.autochecker.R;
import com.autochecker.data.AutoCheckerDataSource;
import com.autochecker.data.exception.NoWatchedLocationFoundException;
import com.autochecker.data.model.WatchedLocation;
import com.autochecker.util.LocationUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class AutoCheckerEditLocationActivity extends Activity implements
		OnMapReadyCallback {

	private final String TAG = getClass().getSimpleName();

	private AutoCheckerDataSource dataSource;

	private WatchedLocation locationToEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {

			setContentView(R.layout.activity_auto_checker_edit_location);

			if (dataSource == null)
				dataSource = new AutoCheckerDataSource(this);

			locationToEdit = null;
			
			Intent intent = getIntent();

			if (intent.getExtras() != null) {
				
				dataSource.open();

				int locationId = getIntent().getExtras().getInt(
						AutoCheckerLocationsActivity.LOCATION_ID);
				
				locationToEdit = dataSource.getWatchedLocation(locationId);

				dataSource.close();
			}

			MapFragment mapFragment = (MapFragment) getFragmentManager()
					.findFragmentById(R.id.map);
			mapFragment.getMapAsync(this);

		} catch (NoWatchedLocationFoundException e) {
			Log.e(TAG, "No watched location found ", e);
		} catch (SQLException e) {
			Log.e(TAG, "DataSource open exception", e);
		}
	}

	@Override
	public void onMapReady(GoogleMap map) {
		
		LatLng location = null;
		
		map.setMyLocationEnabled(true);
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map.setBuildingsEnabled(true);
		map.setOnMarkerDragListener(new OnMarkerDragListener() {
			
			@Override
			public void onMarkerDragStart(Marker marker) {
			}
			
			@Override
			public void onMarkerDragEnd(Marker marker) {
			}
			
			@Override
			public void onMarkerDrag(Marker marker) {
			}
		});
		
		if (locationToEdit != null) {
			location = new LatLng(locationToEdit.getLatitude(), locationToEdit.getLongitude());
			map.moveCamera(CameraUpdateFactory.newLatLngBounds(LocationUtils.getLocationBounds(locationToEdit), 200));
			map.addMarker(new MarkerOptions().title(locationToEdit.getName()).position(location).draggable(true));
			map.addCircle(new CircleOptions().center(location).radius(locationToEdit.getRadius()));
		}
	}
}
