package com.autochecker.activity;

import java.util.Date;
import java.util.List;

import com.autochecker.data.model.WatchedLocationRecord;
import com.autochecker.util.DateUtils;
import com.autochecker.util.Duration;

public class WeekDayRecordRow {

	private Date weekDay;
	private Duration duration;
	private List<WatchedLocationRecord> records;

	public WeekDayRecordRow(Date weekDay, List<WatchedLocationRecord> records) {
		
		this.weekDay = weekDay;
		this.records = records;
		duration = Duration.calculateDuration(records);
	}

	public Date getWeekDay() {
		return weekDay;
	}

	public Duration getDuration() {
		return duration;
	}

	public String getWeekDayString() {
		return DateUtils.dayFormat.format(weekDay);
	}

	public String getDurationString() {
		return duration.toString();
	}

	public List<WatchedLocationRecord> getRecords() {
		return records;
	}
}
