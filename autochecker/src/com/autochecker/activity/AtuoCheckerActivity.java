package com.autochecker.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.autochecker.R;
import com.autochecker.data.AutoCheckerDataSource;
import com.autochecker.data.exception.NoWatchedLocationFoundException;
import com.autochecker.data.model.Duration;
import com.autochecker.data.model.WatchedLocation;
import com.autochecker.data.model.WatchedLocationRecord;
import com.autochecker.service.AutoCheckerService;
import com.autochecker.util.DateUtils;

public class AtuoCheckerActivity extends Activity {

	private final String TAG = getClass().getSimpleName();
 
	public static final SimpleDateFormat timeFormat = new SimpleDateFormat(
			"HH:mm", Locale.getDefault());
	public static final SimpleDateFormat dayFormat = new SimpleDateFormat(
			"cccc, d", Locale.getDefault());
	public static final SimpleDateFormat weekFormat = new SimpleDateFormat(
			"d MMMM", Locale.getDefault());

	private static AutoCheckerDataSource dataSource = null;
	private boolean serviceBound = false;

	private Messenger messengerActivity = new Messenger(
			new AutoCheckServiceHandler());
	private Messenger messengerService = null;

	private ViewPager viewPager;
	private AutoCheckerLocationRecordPageAdapter pageAdapter;

	private static WatchedLocation location;

	private class AutoCheckerLocationRecordPageAdapter extends
			FragmentStatePagerAdapter {

		private List<Date> dates;

		public AutoCheckerLocationRecordPageAdapter(FragmentManager fm,
				List<Date> dates) {
			super(fm);
			this.dates = dates;
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = new AutoCheckerWeekRecordFragment();
			Bundle args = new Bundle();
			args.putLong(AutoCheckerWeekRecordFragment.ARG_START_DATE, dates
					.get(i).getTime());
			args.putLong(AutoCheckerWeekRecordFragment.ARG_END_DATE,
					dates.get(i + 1).getTime());
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return dates.size() - 1;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
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
			long weekWork = 0;

			for (int i = 0; i < dates.size() - 1; i++) {

				List<WatchedLocationRecord> records = dataSource
						.getIntervalWatchedLocationRecord(location,
								dates.get(i), dates.get(i + 1));

				if (!records.isEmpty()) {
					WeekDayRecordRow row = new WeekDayRecordRow(dates.get(i),
							records);
					weekWork += row.getDuration().getMilliseconds();
					rows.add(row);
				}
			}

			WeekDayRecordRowsAdapter adapter = new WeekDayRecordRowsAdapter(
					getActivity(), rows);

			ListView listView = (ListView) rootView
					.findViewById(R.id.weekDayList);
			listView.setAdapter(adapter);

			TextView weekWorkText = (TextView) rootView
					.findViewById(R.id.week_duration);
			weekWorkText.setText(new Duration(weekWork).toString());

			return rootView;
		}
	}

	private class AutoCheckServiceHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case AutoCheckerService.MSG_PROX_ALERT_DONE:
				Log.i(TAG, "Aqui hauria de referescar les dades");
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className,
				IBinder serviceBinder) {

			messengerService = new Messenger(serviceBinder);

			try {

				Message msg = Message.obtain(null,
						AutoCheckerService.MSG_REGISTER_CLIENT);
				msg.replyTo = messengerActivity;
				messengerService.send(msg);

			} catch (RemoteException e) {
				Log.e(TAG, "Can't send register message to service ", e);
			}

			serviceBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			serviceBound = false;
			messengerActivity = null;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atuo_checker);

		if (dataSource == null) {
			dataSource = new AutoCheckerDataSource(getApplicationContext());
			try {
				dataSource.open();
			} catch (SQLException e) {
				Log.e(TAG, "DataSource open exception", e);
			}
		}

		try {
			
			location = dataSource.getWatchedLocation("GTD");

			Pair<Date, Date> limits = dataSource.getLimitDates(location);
			List<Date> dates = DateUtils.getDateIntervals(limits.first,
					limits.second != null ? limits.second : new Date(),
					DateUtils.WEEK_INTERVAL_TYPE);

			pageAdapter = new AutoCheckerLocationRecordPageAdapter(
					getFragmentManager(), dates);
			viewPager = (ViewPager) findViewById(R.id.pager);
			viewPager.setAdapter(pageAdapter);
			
		    viewPager.setOnPageChangeListener(
		            new ViewPager.SimpleOnPageChangeListener() {
		                @Override
		                public void onPageSelected(int position) {
		                    getActionBar().setSelectedNavigationItem(position);
		                }
		            });

			final ActionBar actionBar = getActionBar();

			// Specify that tabs should be displayed in the action bar.
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			
		    ActionBar.TabListener tabListener = new ActionBar.TabListener() {

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
		    };

			for (int i = 0; i < dates.size() - 1; i++) {
				actionBar.addTab(actionBar.newTab().setText(
						weekFormat.format(dates.get(i)) + " - "
								+ weekFormat.format(dates.get(i + 1))).setTabListener(tabListener));
			}

		} catch (NoWatchedLocationFoundException e) {
			Log.e(TAG, "No watched location found ", e);
		}
	}

	@Override
	protected void onStart() {

		super.onStart();

		Intent intent = new Intent(this, AutoCheckerService.class);
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {

		super.onStop();

		if (serviceBound) {

			if (messengerService != null) {
				try {
					Message msg = Message.obtain(null,
							AutoCheckerService.MSG_UNREGISTER_CLIENT);
					messengerService.send(msg);
				} catch (RemoteException e) {
					Log.e(TAG, "Can't send unregister message to service", e);
				}
			}

			unbindService(serviceConnection);
			serviceBound = false;
		}
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
