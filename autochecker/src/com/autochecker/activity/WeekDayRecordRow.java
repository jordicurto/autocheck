package com.autochecker.activity;

import java.util.Date;
import java.util.List;

import com.autochecker.data.model.Duration;
import com.autochecker.data.model.WatchedLocationRecord;

public class WeekDayRecordRow {

	private Date weekDay;
	private Date firstCheckIn;
	private Date lastCheckOut;
	private Duration duration;

	public WeekDayRecordRow(Date weekDay, List<WatchedLocationRecord> records) {

		this.weekDay = weekDay;
		
		firstCheckIn = records.get(0).getCheckIn();
		lastCheckOut = records.get(records.size() - 1).getCheckOut();
		duration = new Duration(0);

		for (WatchedLocationRecord record : records) {
			duration.add(record.calculateDuration());
		}
	}

	public Date getWeekDay() {
		return weekDay;
	}

	public Date getFirstCheckIn() {
		return firstCheckIn;
	}

	public Date getLastCheckOut() {
		return lastCheckOut;
	}

	public Duration getDuration() {
		return duration;
	}

	public String getWeekDayString() {
		return AtuoCheckerActivity.dayFormat.format(weekDay);
	}

	public String getFirstLastCheckString() {
		return "(" + AtuoCheckerActivity.timeFormat.format(firstCheckIn)
				+ " - "
				+ (lastCheckOut != null ? AtuoCheckerActivity.timeFormat
						.format(lastCheckOut) : "") + ")";
	}

	public String getDurationString() {
		return duration.toString();
	}
}
