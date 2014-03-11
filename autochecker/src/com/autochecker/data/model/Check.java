package com.autochecker.data.model;

import java.io.Serializable;
import java.util.Date;

public class Check implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private FavLocation location;
	private Date checkIn;
	private Date checkOut;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public FavLocation getLocation() {
		return location;
	}

	public void setLocation(FavLocation location) {
		this.location = location;
	}

	public Date getCheckIn() {
		return checkIn;
	}

	public void setCheckIn(Date checkIn) {
		this.checkIn = checkIn;
	}

	public Date getCheckOut() {
		return checkOut;
	}

	public void setCheckOut(Date checkOut) {
		this.checkOut = checkOut;
	}

}
