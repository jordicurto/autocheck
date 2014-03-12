package com.autochecker.listener;

public interface IProximityListener {

	public void onEnter(int locationId, long time);
	
	public void onLeave(int locationId, long time);
	
}
