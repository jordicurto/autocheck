package com.autochecker.activity;

import java.util.List;

import android.app.Activity;
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
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.autochecker.R;
import com.autochecker.data.AutoCheckerDataSource;
import com.autochecker.data.model.WatchedLocation;

public class AutoCheckerLocationsActivity extends Activity {

	public static final String LOCATION_ID = "LOCATION_ID";

	private final String TAG = getClass().getSimpleName();

	private AutoCheckerDataSource dataSource;

	// Test GTD
	private static final boolean testGTD = true;
	private static final WatchedLocation wlGTD = new WatchedLocation("GTD",
			2.2095447778701782, 41.40069010694943, 150);

	//

	private class WatchedLocationListAdapter extends
			ArrayAdapter<WatchedLocation> {

		private WatchedLocation[] locations;

		public WatchedLocationListAdapter(Context context,
				WatchedLocation[] locations) {
			super(context, R.layout.location_row, locations);
			this.locations = locations;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.location_row, parent,
					false);
			TextView textView = (TextView) rowView
					.findViewById(R.id.location_name);
			textView.setText(locations[position].getName());
			Switch statusSwitch = (Switch) rowView
					.findViewById(R.id.location_status);
			statusSwitch.setSelected(locations[position].isInside());
			return rowView;
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

			ListView locationList = (ListView) findViewById(R.id.locationsList);
			locationList.setEmptyView(findViewById(R.id.locationsEmpty));
			locationList.setAdapter(new WatchedLocationListAdapter(this,
					locations.toArray(new WatchedLocation[locations.size()])));
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

			dataSource.close();

		} catch (SQLException e) {
			Log.e(TAG, "DataSource open exception", e);
		}
	}

	private void openRecordsActivity(int locationId) {

		Bundle extra = new Bundle();
		extra.putInt(LOCATION_ID, locationId);

		Intent intent = new Intent(this, AtuoCheckerRecordsActivity.class);
		intent.putExtras(extra);
		;
		startActivity(intent);
	}
}
