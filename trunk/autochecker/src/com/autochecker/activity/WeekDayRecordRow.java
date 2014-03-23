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

		long dayWork = 0;

		for (WatchedLocationRecord record : records) {
			if (record.isActive()) {
				dayWork += (System.currentTimeMillis() - record.getCheckIn()
						.getTime());
			} else {
				dayWork += (record.getCheckOut().getTime() - record
						.getCheckIn().getTime());
			}
		}

		this.weekDay = weekDay;
		this.firstCheckIn = records.get(0).getCheckIn();
		this.lastCheckOut = records.get(records.size() - 1).getCheckOut();
		this.duration = new Duration(dayWork);
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
		return AtuoCheckerActivity.timeFormat.format(firstCheckIn)
				+ " - "
				+ (lastCheckOut != null ? AtuoCheckerActivity.timeFormat
						.format(lastCheckOut) : "");
	}

	public String getDurationString() {
		return duration.toString();
	}
}
