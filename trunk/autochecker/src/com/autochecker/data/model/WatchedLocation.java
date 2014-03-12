package com.autochecker.data.model;

import java.io.Serializable;

public class WatchedLocation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int OUTSIDE_LOCATION = 0;
	public static final int INSIDE_LOCATION = 1;
	public static final int ENTERING_LOCATION = 2;	
	public static final int LEAVING_LOCATION = 3;
	
	private int id;
	private String name;
	private double longitude;
	private double latitude;
	private float accuracy;
	private int status;

	public WatchedLocation() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public String toString() {
		return "(" + name + ") Lat: " + latitude + ", Long: " + longitude + ", Accuracy: " + accuracy;
	}

}
