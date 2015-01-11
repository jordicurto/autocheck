package com.autochecker.activity;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.autochecker.R;
import com.autochecker.data.model.WatchedLocationRecord;

public class WeekDayRecordRowsAdapter extends BaseExpandableListAdapter {
	
	private Activity context;
	private List<WeekDayRecordRow> objects;
	
	public WeekDayRecordRowsAdapter(Activity context,
			List<WeekDayRecordRow> objects) {
		this.context = context;
		this.objects = objects;
	}

	@Override
	public int getGroupCount() {
		return objects.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return objects.get(groupPosition).getRecords().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return objects.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return objects.get(groupPosition).getRecords().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		WeekDayRecordRow row = (WeekDayRecordRow) getGroup(groupPosition);
		
		LayoutInflater inflater = context.getLayoutInflater();
		
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.week_records_row, null);
        }
        
        TextView textWeekDay = (TextView) convertView.findViewById(R.id.week_day);
        textWeekDay.setText(row.getWeekDayString());
        
        TextView textDuration = (TextView) convertView.findViewById(R.id.day_record_duration);
        textDuration.setText(row.getDurationString());

		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		WatchedLocationRecord record = (WatchedLocationRecord) getChild(groupPosition, childPosition);
		
		LayoutInflater inflater = context.getLayoutInflater();
		
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.day_record_row, null);
        }
        
        TextView textCheck = (TextView) convertView.findViewById(R.id.record_check);
        textCheck.setText(record.getCheckString());
		
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}
