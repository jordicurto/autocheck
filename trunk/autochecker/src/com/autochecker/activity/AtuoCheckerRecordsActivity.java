package com.autochecker.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.autochecker.R;
import com.autochecker.data.AutoCheckerDataSource;
import com.autochecker.data.exception.NoWatchedLocationFoundException;
import com.autochecker.data.model.Duration;
import com.autochecker.data.model.WatchedLocation;
import com.autochecker.data.model.WatchedLocationRecord;
import com.autochecker.service.AutoCheckerService;
import com.autochecker.util.DateUtils;

public class AtuoCheckerRecordsActivity extends AutoCheckerAbstractActivity
		implements ActionBar.TabListener {

	private final String TAG = getClass().getSimpleName();

	private static AutoCheckerDataSource dataSource = null;

	private ViewPager viewPager;
	private AutoCheckerLocationRecordPageAdapter pageAdapter;

	private static WatchedLocation location;
	private List<Date> intervalTabDates;

	private class AutoCheckerLocationRecordPageAdapter extends
			FragmentStatePagerAdapter {

		public AutoCheckerLocationRecordPageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = new AutoCheckerWeekRecordFragment();
			Bundle args = new Bundle();
			args.putLong(AutoCheckerWeekRecordFragment.ARG_START_DATE,
					intervalTabDates.get(i).getTime());
			args.putLong(AutoCheckerWeekRecordFragment.ARG_END_DATE,
					intervalTabDates.get(i + 1).getTime());
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return intervalTabDates.size() - 1;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}

	public static class AutoCheckerWeekRecordFragment extends Fragment {

		public static final String ARG_START_DATE = "start_date";
		public static final String ARG_END_DATE = "end_date";

		public AutoCheckerWeekRecordFragment() {

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_atuo_checker,
					container, false);

			Bundle args = getArguments();
			Date startDate = new Date(args.getLong(ARG_START_DATE));
			Date endDate = new Date(args.getLong(ARG_END_DATE));

			List<Date> dates = DateUtils.getDateIntervals(startDate, endDate,
					DateUtils.DAY_INTERVAL_TYPE);

			List<WeekDayRecordRow> rows = new ArrayList<WeekDayRecordRow>();
			Duration weekWork = new Duration();

			for (int i = 0; i < dates.size() - 1; i++) {

				List<WatchedLocationRecord> records = dataSource
						.getIntervalWatchedLocationRecord(location,
								dates.get(i), dates.get(i + 1));

				if (!records.isEmpty()) {
					WeekDayRecordRow row = new WeekDayRecordRow(dates.get(i),
							records);
					weekWork.add(row.getDuration());
					rows.add(row);
				}
			}

			WeekDayRecordRowsAdapter adapter = new WeekDayRecordRowsAdapter(
					getActivity(), rows);

			ExpandableListView listView = (ExpandableListView) rootView
					.findViewById(R.id.weekDayList);
			listView.setAdapter(adapter);

			TextView weekWorkText = (TextView) rootView
					.findViewById(R.id.week_duration);
			weekWorkText.setText(weekWork.toString());

			return rootView;
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {

			setContentView(R.layout.activity_atuo_checker_records);

			dataSource = new AutoCheckerDataSource(this);

			dataSource.open();

			int locationId = getIntent().getExtras().getInt(
					AutoCheckerLocationsActivity.LOCATION_ID);

			try {

				location = dataSource.getWatchedLocation(locationId);
				intervalTabDates = dataSource.getDateIntervals(location,
						DateUtils.WEEK_INTERVAL_TYPE);
				
				if (intervalTabDates.isEmpty()) {
					Pair<Date, Date> weekLim = DateUtils.getLimitDates(DateUtils.WEEK_INTERVAL_TYPE);
					intervalTabDates.add(weekLim.first);
					intervalTabDates.add(weekLim.second);
				}

			} catch (NoWatchedLocationFoundException e) {
				Log.e(TAG, "No watched location found ", e);
			}

			pageAdapter = new AutoCheckerLocationRecordPageAdapter(
					getFragmentManager());

			viewPager = (ViewPager) findViewById(R.id.pager);
			viewPager.setAdapter(pageAdapter);

			viewPager
					.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
						@Override
						public void onPageSelected(int position) {
							getActionBar().setSelectedNavigationItem(position);
						}
					});

			final ActionBar actionBar = getActionBar();

			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

			refreshTabs();

			setCurrentTab();
			
			setTitle(location.getName());

		} catch (SQLException e) {
			Log.e(TAG, "DataSource open exception", e);
		}
	}

	@Override
	protected void onReceiveProximityAlert(int locationId) {

		if (location.getId() == locationId) {

			intervalTabDates.clear();
			intervalTabDates = dataSource.getDateIntervals(location,
					DateUtils.WEEK_INTERVAL_TYPE);
			refreshTabs();
			pageAdapter.notifyDataSetChanged();

		}

	}

	private void refreshTabs() {

		final ActionBar actionBar = getActionBar();

		int currentPos = actionBar.getSelectedNavigationIndex();

		actionBar.removeAllTabs();

		for (int i = 0; i < intervalTabDates.size() - 1; i++) {

			String tabText = DateUtils.getDateIntervalString(intervalTabDates,
					i);

			Tab tab = actionBar.newTab();
			tab.setText(tabText);
			tab.setTabListener(this);

			actionBar.addTab(tab);
		}

		if (currentPos >= 0) {
			actionBar.setSelectedNavigationItem(currentPos);
		}
	}

	private void setCurrentTab() {
		Date current = new Date();
		int pos = 0;
		for (int i = 0; i < intervalTabDates.size() - 1; i++) {
			if (intervalTabDates.get(i).before(current)) {
				pos = i;
			}
		}
		if (getActionBar().getTabCount() > 0)
			getActionBar().setSelectedNavigationItem(pos);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dataSource.close();
		dataSource = null;
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
}
