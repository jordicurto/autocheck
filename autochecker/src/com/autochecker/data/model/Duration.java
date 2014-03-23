package com.autochecker.data.model;

import java.util.Locale;

public class Duration {
	
	private static final int MINS_PER_HOUR = 60;
	private static final int MINS_PER_MILLISECOND = 60 * 1000;
	private static final int HOURS_PER_MILLISECOND = 60 * MINS_PER_MILLISECOND;

	private long milliseconds;
	
	public Duration() {
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
	
	public String toString() {
		int hours = (int) (milliseconds / HOURS_PER_MILLISECOND);
		int minutes = (int) (milliseconds / MINS_PER_MILLISECOND) % MINS_PER_HOUR;
		return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
	}
}
