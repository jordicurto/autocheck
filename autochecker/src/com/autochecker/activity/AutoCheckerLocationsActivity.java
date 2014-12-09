package com.autochecker.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.autochecker.R;
import com.autochecker.data.AutoCheckerDataSource;
import com.autochecker.data.model.WatchedLocation;

public class AutoCheckerLocationsActivity extends AutoCheckerAbstractActivity {

	public static final String LOCATION_ID = "LOCATION_ID";

	private final String TAG = getClass().getSimpleName();

	private AutoCheckerDataSource dataSource;

	private WatchedLocationListAdapter adapter;
	
	// Test GTD
	private static final boolean testGTD = true;
	private static final WatchedLocation wlGTD = new WatchedLocation("GTD",
			2.2095447778701782, 41.40069010694943, 150);

	//

	private class WatchedLocationListAdapter extends BaseAdapter {

		private List<WatchedLocation> locations;
		private Context context;

		public WatchedLocationListAdapter(Context context,
				List<WatchedLocation> locations) {
			super();
			this.context = context;
			this.locations = locations;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.location_row, parent,
					false);
			TextView textView = (TextView) rowView
					.findViewById(R.id.location_name);
			textView.setText(locations.get(position).getName());
			Switch statusSwitch = (Switch) rowView
					.findViewById(R.id.location_status);
			statusSwitch.setChecked(locations.get(position).isInside());
			return rowView;
		}

		@Override
		public int getCount() {
			return locations.size();
		}

		@Override
		public Object getItem(int position) {
			return locations.get(position);
		}

		@Override
		public long getItemId(int position) {
			return locations.get(position).getId();
		}
		
		public void setLocations(List<WatchedLocation> locations) {
			this.locations = locations;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {

			setContentView(R.layout.activity_auto_checker_locations);

		    dataSource = new AutoCheckerDataSource(this);
			dataSource.open();

			if (testGTD && dataSource.getAllWatchedLocations().isEmpty()) {
				dataSource.insertWatchedLocation(wlGTD);
			}

			final List<WatchedLocation> locations = dataSource
					.getAllWatchedLocations();
			
			adapter = new WatchedLocationListAdapter(this, locations);

			ListView locationList = (ListView) findViewById(R.id.locationsList);
			locationList.setEmptyView(findViewById(R.id.locationsEmpty));
			locationList.setAdapter(adapter);
			locationList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					WatchedLocation locationClicked = locations.get(position);

					Log.d(TAG, "Starting records activty for location "
							+ locationClicked.getName());

					openRecordsActivity(locationClicked.getId());
				}
			});

		} catch (SQLException e) {
			Log.e(TAG, "DataSource open exception", e);
		}
	}

	private void openRecordsActivity(int locationId) {

		Bundle extra = new Bundle();
		extra.putInt(LOCATION_ID, locationId);

		Intent intent = new Intent(this, AtuoCheckerRecordsActivity.class);
		intent.putExtras(extra);
		startActivity(intent);
	}

	@Override
	protected void onReceiveProximityAlert(int locationId) {
		final List<WatchedLocation> locations = dataSource
				.getAllWatchedLocations();
		adapter.setLocations(locations);
		adapter.notifyDataSetChanged();
	}
}
