package com.autochecker.listener;

import android.content.Context;

public interface IProximityListener {

	public void onEnter(int locationId, long time, Context context);
	
	public void onLeave(int locationId, long time, Context context);
	
}
