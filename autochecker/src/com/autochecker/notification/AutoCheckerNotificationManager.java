package com.autochecker.notification;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.database.SQLException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Pair;

import com.autochecker.R;
import com.autochecker.data.AutoCheckerDataSource;
import com.autochecker.data.exception.NoWatchedLocationFoundException;
import com.autochecker.data.model.Duration;
import com.autochecker.data.model.WatchedLocation;
import com.autochecker.data.model.WatchedLocationRecord;
import com.autochecker.util.DateUtils;

public class AutoCheckerNotificationManager implements INotificationManager {

	public static final int ALARM_ID = 500;

	private static final long HOURS_PER_DAY = 8 * Duration.HOURS_PER_MILLISECOND;
	private static final long HOURS_PER_WEEK = 40 * Duration.HOURS_PER_MILLISECOND;

	private static final long HOURS_PER_DAY_LIMIT = 12 * Duration.HOURS_PER_MILLISECOND;

	private final String TAG = getClass().getSimpleName();

	private Map<Integer, Pair<Long, Long>> limits;

	private AutoCheckerDataSource dataSource;

	public AutoCheckerNotificationManager() {
		limits = new HashMap<Integer, Pair<Long, Long>>();
		limits.put(DateUtils.DAY_INTERVAL_TYPE, new Pair<Long, Long>(
				HOURS_PER_DAY, HOURS_PER_DAY_LIMIT));
		limits.put(DateUtils.WEEK_INTERVAL_TYPE, new Pair<Long, Long>(
				HOURS_PER_WEEK, null));
	}

	@Override
	public void notifyUser(Context context) {

		Log.d(TAG, "Notifying to user if needed");

		try {

			if (dataSource == null) {
				dataSource = new AutoCheckerDataSource(context);
			}

			NotificationManager nManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);

			dataSource.open();

			List<WatchedLocation> list = dataSource.getAllWatchedLocations();

			for (WatchedLocation location : list) {

				if (dataSource.isUserInWatchedLocation(location)) {

					for (Entry<Integer, Pair<Long, Long>> entry : limits
							.entrySet()) {

						Pair<Date, Date> limits = DateUtils.getLimitDates(entry
								.getKey());

						List<WatchedLocationRecord> records = dataSource
								.getIntervalWatchedLocationRecord(location,
										limits.first, limits.second);

						Duration duration = Duration.calculateDuration(records);

						Log.d(TAG, "User is in " + location.getName()
								+ ", duration " + duration.toString());

						if (entry.getValue().first != null) {

							if (duration.getMilliseconds() > entry.getValue().first) {

								nManager.notify(
										ALARM_ID,
										buildNotification(
												context,
												location.getName(),
												(int) (entry.getValue().first / Duration.HOURS_PER_MILLISECOND)));
							}

						}

						if (entry.getValue().second != null) {

							if (duration.getMilliseconds() > entry.getValue().second) {
								
								forceLeaveLocation(location);
							}
						}
					}
				}
			}

			dataSource.close();

		} catch (SQLException e) {
			Log.e(TAG, "DataSource open exception", e);
		}
	}

	private Notification buildNotification(Context context, String locName,
			int limit) {

		return new NotificationCompat.Builder(context)
				.setContentTitle(
						context.getString(R.string.not_title) + locName)
				.setContentText(
						context.getString(R.string.not_content_1) + limit
								+ context.getString(R.string.not_content_2)
								+ locName).setAutoCancel(true).build();
	}
	
	private void forceLeaveLocation(WatchedLocation location) {
		
		try {

			Log.i(TAG, "Forced to leave location "
					+ location.getName());

			location.setStatus(WatchedLocation.OUTSIDE_LOCATION);
			dataSource.updateWatchedLocation(location);

			WatchedLocationRecord record = dataSource
					.getUnCheckedWatchedLocationRecord(location);
			record.setCheckOut(new Date(System
					.currentTimeMillis()));
			dataSource.updateRecord(record);

		} catch (NoWatchedLocationFoundException e) {
			Log.e(TAG,
					"Watched Location doesn't exist ",
					e);
		}
	}
}
