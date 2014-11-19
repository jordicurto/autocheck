package com.autochecker.service.model;

public class Check {

	private int locationId;
	private boolean enter;
	private long time;

	public Check(int locationId, boolean enter, long time) {
		this.locationId = locationId;
		this.enter = enter;
		this.time = time;
	}

	public int getLocationId() {
		return locationId;
	}

	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}

	public boolean isEnter() {
		return enter;
	}

	public void setEnter(boolean enter) {
		this.enter = enter;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
