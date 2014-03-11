package com.autochecker.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.autochecker.R;
import com.autochecker.data.AutoCheckerDataSource;
import com.autochecker.data.model.Check;
import com.autochecker.data.model.FavLocation;

public class AtuoCheckerActivity extends Activity {

	private static final String TAG = "";
	private AutoCheckerDataSource dataSource;

	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd/MM/yy hh:mm:ss", Locale.getDefault());

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atuo_checker);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		if (dataSource == null) {
			dataSource = new AutoCheckerDataSource(getApplicationContext());
			try {
				dataSource.open();
			} catch (SQLException e) {
				Log.e(TAG, "DataSource open exception", e);
			}
		}

		List<FavLocation> locations = dataSource.getAllFavLocations();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		for (FavLocation location : locations) {
			
			List<Check> checks = dataSource.getAllChecks(location);

			for (Check check : checks) {
				
				Map<String, String> map = new HashMap<String, String>();
				
				if (check.getCheckOut() != null) {
					map.put(location.getName(),
							dateFormat.format(check.getCheckIn()) + " - "
									+ dateFormat.format(check.getCheckOut()));
				} else {
					map.put(location.getName(),
							dateFormat.format(check.getCheckIn())
									+ " - ****** ");
				}

				list.add(map);
			}
		}

		SimpleAdapter adapter = new SimpleAdapter(this, list,
				android.R.layout.simple_list_item_1, new String[] {"GTD"}, new int[] {android.R.id.text1});
		
		ListView lv = (ListView) findViewById(R.id.listView);

		lv.setAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		dataSource.close();
		dataSource = null;
		super.onDestroy();
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
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_atuo_checker,
					container, false);
			return rootView;
		}
	}

}
