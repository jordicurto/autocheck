package com.autochecker.activity;

import java.util.List;

import com.autochecker.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class WeekDayRecordRowsAdapter extends ArrayAdapter<WeekDayRecordRow> {
	
	public WeekDayRecordRowsAdapter(Context context,
			List<WeekDayRecordRow> objects) {
		super(context, R.layout.record_row, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rootView = inflater.inflate(R.layout.record_row, parent, false);
		
		WeekDayRecordRow row = getItem(position);
		
		TextView weekDay = (TextView) rootView.findViewById(R.id.week_day);
		weekDay.setText(row.getWeekDayString());
		
		TextView firsLast = (TextView) rootView.findViewById(R.id.first_last_record);
		firsLast.setText(row.getFirstLastCheckString());
		
		TextView duration = (TextView) rootView.findViewById(R.id.record_duration);
		duration.setText(row.getDurationString());
		
		return rootView;
	}
}
