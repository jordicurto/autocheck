package com.autochecker.data.model;

import java.io.Serializable;

import android.location.Location;

public class FavLocation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private double longitude;
	private double latitude;
	private float accuracy;
	private boolean overThere;

	public FavLocation() {

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

	public boolean isOverThere() {
		return overThere;
	}

	public void setOverThere(boolean overThere) {
		this.overThere = overThere;
	}

	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	public float distanceTo(Location location) {
		float[] results = new float[1];
		Location.distanceBetween(latitude, longitude, location.getLatitude(), location.getLongitude(), results);
		return results[0];
	}
	
	public String toString() {
		return "(" + name + ") Lat: " + latitude + ", Long: " + longitude + ", Accuracy: " + accuracy;
	}
}
