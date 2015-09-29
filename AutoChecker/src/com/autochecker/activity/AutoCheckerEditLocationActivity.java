package com.autochecker.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;

import com.autochecker.R;
import com.autochecker.data.AutoCheckerDataSource;
import com.autochecker.data.exception.NoLocationFoundException;
import com.autochecker.data.model.WatchedLocation;
import com.autochecker.util.LocationUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class AutoCheckerEditLocationActivity extends Activity implements
		OnMapReadyCallback {
	
	public static final int MIN_RADIUS = 50;
	public static final int MAX_RADIUS = 1000;

	private final String TAG = getClass().getSimpleName();

	private AutoCheckerDataSource dataSource;

	private WatchedLocation locationToEdit;

	private Marker marker;

	private Circle circle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {

			setContentView(R.layout.activity_auto_checker_edit_location);

			dataSource = AutoCheckerDataSource.getInstance(this);

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
			
			SeekBar seekBar = (SeekBar) findViewById(R.id.radiusBar);
			seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
				}
			});

		} catch (NoLocationFoundException e) {
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
				circle.setCenter(marker.getPosition());
			}

			@Override
			public void onMarkerDrag(Marker marker) {
				circle.setCenter(marker.getPosition());
			}
		});

		if (locationToEdit != null) {
			location = new LatLng(locationToEdit.getLatitude(),
					locationToEdit.getLongitude());
			map.moveCamera(CameraUpdateFactory.newLatLngBounds(
					LocationUtils.getLocationBounds(locationToEdit), 200));
			marker = map.addMarker(new MarkerOptions()
					.title(locationToEdit.getName()).position(location).icon(
							BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
					.draggable(true));
			circle = map.addCircle(new CircleOptions().center(location)
					.radius(locationToEdit.getRadius())
					.fillColor(Color.parseColor("#2033b5e5"))
					.strokeColor(Color.parseColor("#ff0099cc")).strokeWidth(5));
		}
	}
}
