package com.autochecker.util;

import com.autochecker.data.model.WatchedLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class LocationUtils {

	private static final double earthRadius = 6378137.0;

	public static LatLngBounds getLocationBounds(WatchedLocation location) {

		double centerLatitude = location.getLatitude();
		double centerLongitude = location.getLongitude();
		float offset = location.getRadius();

		double offsetLat = offset / earthRadius;
		double offsetLon = offset / earthRadius
				* Math.cos(Math.PI * centerLatitude / 180.0);

		double offsetLatDeg = ((offsetLat * 180.0) / Math.PI);
		double offsetLonDeg = ((offsetLon * 180.0) / Math.PI);

		LatLng southwest = new LatLng(centerLatitude + offsetLatDeg,
				centerLongitude + offsetLonDeg);
		LatLng northeast = new LatLng(centerLatitude - offsetLatDeg,
				centerLongitude - offsetLonDeg);

		return new LatLngBounds(southwest, northeast);

	}
}
