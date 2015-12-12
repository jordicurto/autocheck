package com.autochecker.data.model;

import java.util.List;
import java.util.Locale;

public class Duration {
	
	public static final int MINS_PER_HOUR = 60;
	public static final int MINS_PER_MILLISECOND = 60 * 1000;
	public static final int HOURS_PER_MILLISECOND = 60 * MINS_PER_MILLISECOND; 

	private long milliseconds;
	
	public Duration() {
		this.milliseconds = 0;
	}

	public Duration(long milliseconds) {
		this.milliseconds = milliseconds;
	}

	public long getMilliseconds() {
		return milliseconds;
	}

	public void setMilliseconds(long milliseconds) {
		this.milliseconds = milliseconds;
	}
	
	public Duration add(Duration duration) {
		milliseconds += duration.getMilliseconds();
		return this;
	}
	
	public String toString() {
		int hours = (int) (milliseconds / HOURS_PER_MILLISECOND);
		int minutes = (int) (milliseconds / MINS_PER_MILLISECOND) % MINS_PER_HOUR;
		return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
	}
	
	public static Duration calculateDuration(List<WatchedLocationRecord> records) {

		Duration duration = new Duration();

		for (WatchedLocationRecord watchedLocation : records) {
			duration.add(watchedLocation.calculateDuration());
		}

		return duration;
	}
}
