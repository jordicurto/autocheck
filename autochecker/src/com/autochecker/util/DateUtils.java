package com.autochecker.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.util.Pair;

public class DateUtils {

	public static final SimpleDateFormat timeFormat = new SimpleDateFormat(
			"HH:mm", Locale.getDefault());
	public static final SimpleDateFormat dayFormat = new SimpleDateFormat(
			"cccc, d", Locale.getDefault());
	public static final SimpleDateFormat weekFormat = new SimpleDateFormat(
			"d MMMM", Locale.getDefault());
	public static final SimpleDateFormat weekDayFormat = new SimpleDateFormat(
			"d", Locale.getDefault());

	public static final int DAY_INTERVAL_TYPE = 0;
	public static final int WEEK_INTERVAL_TYPE = 1;
	public static final int MONTH_INTERVAL_TYPE = 2;

	public static List<Date> getDateIntervals(Date start, Date end,
			int intervalType) {

		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(start);

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(end);

		startCalendar.set(Calendar.MINUTE,
				startCalendar.getMinimum(Calendar.MINUTE));
		startCalendar.set(Calendar.HOUR_OF_DAY,
				startCalendar.getMinimum(Calendar.MINUTE));

		endCalendar.set(Calendar.MINUTE,
				endCalendar.getMinimum(Calendar.MINUTE));

		if (intervalType == WEEK_INTERVAL_TYPE) {
			startCalendar.set(Calendar.DAY_OF_WEEK,
					startCalendar.getMinimum(Calendar.DAY_OF_WEEK)
							+ startCalendar.getFirstDayOfWeek());

		} else if (intervalType == MONTH_INTERVAL_TYPE) {
			startCalendar.set(Calendar.DAY_OF_MONTH,
					startCalendar.getMinimum(Calendar.DAY_OF_MONTH));
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

	public static String getDateIntervalString(List<Date> dates,
			int intervalPosition) {

		Pair<Date, Date> interval = getInterval(dates, intervalPosition);

		return (sameMonth(interval) ? weekDayFormat.format(interval.first)
				: weekFormat.format(interval.first))
				+ " - "
				+ weekFormat.format(interval.second);
	}

	public static Pair<Date, Date> getLimitDates(int intervalType) {

		Calendar startCalendar = Calendar.getInstance();
		Calendar endCalendar = Calendar.getInstance();

		if (intervalType == MONTH_INTERVAL_TYPE) {

			startCalendar.set(Calendar.DAY_OF_MONTH,
					startCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));

		} else if (intervalType == WEEK_INTERVAL_TYPE) {

			startCalendar.set(Calendar.DAY_OF_WEEK,
					startCalendar.getMinimum(Calendar.DAY_OF_WEEK)
							+ startCalendar.getFirstDayOfWeek());
		}

		startCalendar.set(Calendar.HOUR_OF_DAY,
				startCalendar.getMinimum(Calendar.HOUR_OF_DAY));
		startCalendar.set(Calendar.MINUTE,
				startCalendar.getMinimum(Calendar.MINUTE));
		startCalendar.set(Calendar.SECOND,
				startCalendar.getMinimum(Calendar.SECOND));
		startCalendar.set(Calendar.MILLISECOND,
				startCalendar.getMinimum(Calendar.MILLISECOND));

		if (intervalType == MONTH_INTERVAL_TYPE) {

			endCalendar.set(Calendar.DAY_OF_MONTH,
					endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));

		} else if (intervalType == WEEK_INTERVAL_TYPE) {

			endCalendar.set(Calendar.DAY_OF_WEEK,
					(endCalendar.getMaximum(Calendar.DAY_OF_WEEK)));
			endCalendar.add(
					Calendar.DAY_OF_MONTH,
					(endCalendar.getMaximum(Calendar.DAY_OF_WEEK) + endCalendar
							.getFirstDayOfWeek())
							% endCalendar.getMaximum(Calendar.DAY_OF_WEEK));
		}

		endCalendar.set(Calendar.HOUR_OF_DAY,
				endCalendar.getMaximum(Calendar.HOUR_OF_DAY));
		endCalendar.set(Calendar.MINUTE,
				endCalendar.getMaximum(Calendar.MINUTE));
		endCalendar.set(Calendar.SECOND,
				endCalendar.getMaximum(Calendar.SECOND));
		endCalendar.set(Calendar.MILLISECOND,
				endCalendar.getMaximum(Calendar.MILLISECOND));

		return new Pair<Date, Date>(startCalendar.getTime(),
				endCalendar.getTime());
	}

	public static void runTest() {

		DateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

		Calendar calS = Calendar.getInstance();
		calS.set(2014, Calendar.MARCH, 19, 11, 15, 0);
		Calendar calE = Calendar.getInstance();
		calE.set(2014, Calendar.MARCH, 22, 23, 52, 0);

		System.out.println("DAY_INTERVAL");
		for (Date d : DateUtils.getDateIntervals(calS.getTime(), calE.getTime(),
				DateUtils.DAY_INTERVAL_TYPE)) {
			System.out.println(format.format(d));
		}

		System.out.println("WEEK_INTERVAL");
		for (Date d : DateUtils.getDateIntervals(calS.getTime(), calE.getTime(),
				DateUtils.WEEK_INTERVAL_TYPE)) {
			System.out.println(format.format(d));
		}

		System.out.println("MONTH_INTERVAL");
		for (Date d : DateUtils.getDateIntervals(calS.getTime(), calE.getTime(),
				DateUtils.MONTH_INTERVAL_TYPE)) {
			System.out.println(format.format(d));
		}
		
		Pair<Date, Date> dayDates = DateUtils.getLimitDates(DateUtils.DAY_INTERVAL_TYPE);
		System.out.println("Day Limit: " + format.format(dayDates.first) + " - " + format.format(dayDates.second));
		
        Pair<Date, Date> weekDates = DateUtils.getLimitDates(DateUtils.WEEK_INTERVAL_TYPE);
		System.out.println("Week Limit: " + format.format(weekDates.first) + " - " + format.format(weekDates.second));
		
        Pair<Date, Date> monthDates = DateUtils.getLimitDates(DateUtils.MONTH_INTERVAL_TYPE);
		System.out.println("Month Limit: " + format.format(monthDates.first) + " - " + format.format(monthDates.second));
	}
}
