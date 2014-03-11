package com.autochecker.service;

import com.autochecker.data.AutoCheckerDataSource;
import com.autochecker.data.model.FavLocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.util.Log;

public class AutoCheckerReceiver extends BroadcastReceiver {
	
	private AutoCheckerDataSource dataSource = null;
	
	private final String TAG = getClass().getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (dataSource == null) {
			dataSource = new AutoCheckerDataSource(context);
			try {
				dataSource.open();
			} catch (SQLException e) {
				Log.e(TAG, "DataSource open exception", e);
			}
		}
			
		FavLocation location = new FavLocation();
		
		location.setName("GTD");
		location.setLatitude(41.40069010694943);
		location.setLongitude(2.2095447778701782);
		location.setAccuracy(120.0f);

		dataSource.updateFavLocation(location);
//		
//		location.setName("GTD ext");
//		location.setLatitude(41.400657);
//		location.setLongitude(2.209513);
//		location.setAccuracy(500.0f);
//		
//		dataSource.insertFavLocation(location);
        
        context.startService(new Intent(context, AutoCheckerService.class));

	}

}
