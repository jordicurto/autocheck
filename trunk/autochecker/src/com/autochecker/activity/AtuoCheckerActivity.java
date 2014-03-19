package com.autochecker.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
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
import com.autochecker.data.model.WatchedLocation;
import com.autochecker.data.model.WatchedLocationRecord;
import com.autochecker.service.AutoCheckerService;

public class AtuoCheckerActivity extends Activity {

	private final String TAG = getClass().getSimpleName();

	private AutoCheckerDataSource dataSource = null;
	private boolean serviceBound = false;

	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd/MM/yy hh:mm:ss", Locale.getDefault());

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

	private Messenger messengerActivity = new Messenger(
			new AutoCheckServiceHandler());
	private Messenger messengerService = null;

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

		List<WatchedLocation> locations = dataSource.getAllWatchedLocations();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		for (WatchedLocation location : locations) {

			List<WatchedLocationRecord> checks = dataSource
					.getAllWatchedLocationRecord(location);

			for (WatchedLocationRecord check : checks) {

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
				android.R.layout.simple_list_item_1, new String[] { "GTD" },
				new int[] { android.R.id.text1 });

		ListView lv = (ListView) findViewById(R.id.listView);

		lv.setAdapter(adapter);
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
