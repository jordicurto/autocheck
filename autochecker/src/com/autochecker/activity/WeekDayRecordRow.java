package com.autochecker.activity;

import java.util.Date;
import java.util.List;

import com.autochecker.data.model.Duration;
import com.autochecker.data.model.WatchedLocationRecord;
import com.autochecker.util.DateUtils;

public class WeekDayRecordRow {

	//private static final long MINIMUN_DURATION = 100000;

	private Date weekDay;
	private Duration duration;
	private List<WatchedLocationRecord> records;

	public WeekDayRecordRow(Date weekDay, List<WatchedLocationRecord> records) {
		
		this.weekDay = weekDay;
		this.records = records;
		duration = Duration.calculateDuration(records);
		//filterRecords(records);
		//init();
	}

//	private void filterRecords(List<WatchedLocationRecord> rawRecords) {
//
//		records = new ArrayList<WatchedLocationRecord>();
//
//		Date lastCheckOut = null;
//
//		for (WatchedLocationRecord record : rawRecords) {
//			if (lastCheckOut != null
//					&& record.getCheckIn().getTime() - lastCheckOut.getTime() <= MINIMUN_DURATION) {
//				if(!records.isEmpty()) {
//					records.get(records.size() - 1).setCheckOut(
//							record.getCheckOut());
//				}
//			} else {
//				if (record.isActive() || record.calculateDuration().getMilliseconds() >= MINIMUN_DURATION) {
//					records.add(record);
//				}
//			}
//			lastCheckOut = record.getCheckOut();
//		}
//	}
//
//	private void init() {
//		duration = Duration.calculateDuration(records);
//	}

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
