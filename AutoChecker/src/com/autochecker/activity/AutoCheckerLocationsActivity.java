package com.autochecker.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.autochecker.R;
import com.autochecker.data.AutoCheckerDataSource;
import com.autochecker.data.model.WatchedLocation;
import com.autochecker.util.AutoCheckerConstants;

public class AutoCheckerLocationsActivity extends AutoCheckerAbstractActivity {

	public static final String LOCATION_ID = "LOCATION_ID";

	private static final int NEW_LOCATION = 0;
	private static final int EDIT_LOCATION = 1;

	private final String TAG = getClass().getSimpleName();

	private AutoCheckerDataSource dataSource;

	private WatchedLocationListAdapter adapter = new WatchedLocationListAdapter();

	private List<WatchedLocation> locations = new ArrayList<WatchedLocation>();

	private class WatchedLocationListAdapter extends BaseAdapter {

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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = AutoCheckerLocationsActivity.this
					.getLayoutInflater();

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.location_row, null);
			}

			TextView textView = (TextView) convertView
					.findViewById(R.id.location_name);

			textView.setText(locations.get(position).getName());

			Switch statusSwitch = (Switch) convertView
					.findViewById(R.id.location_status);

			statusSwitch.setChecked(locations.get(position).isInside());

			return convertView;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {

			setContentView(R.layout.activity_auto_checker_locations);

			dataSource = AutoCheckerDataSource.getInstance(this);
			
			dataSource.open();

			if (AutoCheckerConstants.testGTD
					&& dataSource.getAllWatchedLocations().isEmpty()) {
				dataSource.insertWatchedLocation(AutoCheckerConstants.wlGTD);
			}

			locations = dataSource.getAllWatchedLocations();

			dataSource.close();

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
			locationList.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					
					WatchedLocation locationClicked = locations.get(position);

					Log.d(TAG, "Starting edit activty for location "
							+ locationClicked.getName());

					openEditActivity(locationClicked.getId());					
					
					return true;
				}
			});

		} catch (SQLException e) {
			Log.e(TAG, "DataSource open exception", e);
		}
	}

	private void openRecordsActivity(int locationId) {

		Bundle extra = new Bundle();
		extra.putInt(LOCATION_ID, locationId);

		Intent intent = new Intent(this, AutoCheckerRecordsActivity.class);
		intent.putExtras(extra);
		startActivity(intent);
	}
	

	private void openEditActivity(int locationId) {

		Bundle extra = new Bundle();
		extra.putInt(LOCATION_ID, locationId);

		Intent intent = new Intent(this, AutoCheckerEditLocationActivity.class);
		intent.putExtras(extra);
		
		startActivityForResult(intent, EDIT_LOCATION);
	}

	@Override
	protected void onReceiveTransition(int locationId) {
		try {
			
			dataSource.open();
			locations = dataSource.getAllWatchedLocations();
			dataSource.close();
			adapter.notifyDataSetChanged();
			
		} catch (SQLException e) {
			Log.e(TAG, "DataSource open exception", e);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.atuo_checker, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_add_location) {
			startActivityForResult(new Intent(this, AutoCheckerEditLocationActivity.class), NEW_LOCATION);
		}
		return super.onOptionsItemSelected(item);
	}
}
