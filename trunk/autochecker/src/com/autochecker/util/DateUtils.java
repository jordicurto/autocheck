package com.autochecker.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.util.Pair;

public class DateUtils {

	public static final int DAY_INTERVAL_TYPE = 0;
	public static final int WEEK_INTERVAL_TYPE = 1;
	public static final int MONTH_INTERVAL_TYPE = 2;

	public static List<Date> getDateIntervals(Date start, Date end,
			int intervalType) {

		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(start);

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(end);

		startCalendar.set(Calendar.MINUTE, 0);
		startCalendar.set(Calendar.HOUR_OF_DAY, 0);

		endCalendar.set(Calendar.MINUTE, 0);

		if (intervalType == WEEK_INTERVAL_TYPE) {
			startCalendar.set(Calendar.DAY_OF_WEEK,
					startCalendar.getFirstDayOfWeek());

		} else if (intervalType == MONTH_INTERVAL_TYPE) {
			startCalendar.set(Calendar.DAY_OF_MONTH, 1);
		}

		List<Date> dates = new ArrayList<Date>();

		while (startCalendar.before(endCalendar)) {
			dates.add(startCalendar.getTime());
			switch (intervalType) {
			case DAY_INTERVAL_TYPE:
				startCalendar.add(Calendar.DAY_OF_MONTH, 1);
				break;
			case WEEK_INTERVAL_TYPE:
				startCalendar.add(Calendar.WEEK_OF_YEAR, 1);
				break;
			case MONTH_INTERVAL_TYPE:
				startCalendar.add(Calendar.MONTH, 1);
				break;
			}
		}

		dates.add(startCalendar.getTime());

		return dates;
	}

	public static Pair<Date, Date> getInterval(List<Date> dates,
			int intervalPosition) {

		if ((intervalPosition - 1) < dates.size()) {

			return new Pair<Date, Date>(dates.get(intervalPosition), new Date(
					dates.get(intervalPosition + 1).getTime() - 10000));
		}

		return null;
	}

	public static boolean sameMonth(Pair<Date, Date> interval) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(interval.first);
		int monthStart = cal.get(Calendar.MONTH);
		cal.setTime(interval.second);
		int monthEnd = cal.get(Calendar.MONTH);

		return (monthStart == monthEnd);
	}

	public static void main(String args[]) {

		DateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

		Calendar calS = Calendar.getInstance();
		calS.set(2014, Calendar.MARCH, 19, 11, 15, 0);
		Calendar calE = Calendar.getInstance();
		calE.set(2014, Calendar.MARCH, 19, 23, 52, 0);

		System.out.println("DAY_INTERVAL");
		for (Date d : getDateIntervals(calS.getTime(), calE.getTime(),
				DAY_INTERVAL_TYPE)) {
			System.out.println(format.format(d));
		}

		System.out.println("WEEK_INTERVAL");
		for (Date d : getDateIntervals(calS.getTime(), calE.getTime(),
				WEEK_INTERVAL_TYPE)) {
			System.out.println(format.format(d));
		}

		System.out.println("MONTH_INTERVAL");
		for (Date d : getDateIntervals(calS.getTime(), calE.getTime(),
				MONTH_INTERVAL_TYPE)) {
			System.out.println(format.format(d));
		}
	}
}
