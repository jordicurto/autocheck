package com.autochecker.listener;

import com.autochecker.data.model.WatchedLocation;

import android.content.Context;

public interface IProximityListener {

	public WatchedLocation onEnter(int locationId, long time, Context context);
	
	public WatchedLocation onLeave(int locationId, long time, Context context);
	
}
