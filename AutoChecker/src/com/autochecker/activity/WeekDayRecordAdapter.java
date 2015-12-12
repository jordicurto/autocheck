package com.autochecker.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.autochecker.R;
import com.autochecker.data.model.WatchedLocationRecord;
import com.autochecker.util.Duration;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WeekDayRecordAdapter extends RecyclerView.Adapter<WeekDayRecordAdapter.ViewHolder> {
	
	private List<WeekDayRecordRow> records;
	
	class ViewHolder extends RecyclerView.ViewHolder {
		
		private static final int CHART_MAXIMUM = 12 * Duration.HOURS_PER_MILLISECOND;
		
		PieChart chart;

		public ViewHolder(View itemView) {
			super(itemView);
			chart = (PieChart) itemView.findViewById(R.id.chart);
		}
		
		public void udpdate(WeekDayRecordRow row) {
			List<Entry> entryList = new ArrayList<Entry>();
			int xIndex = 0;
			for (int i = 0; i < row.getRecords().size(); i++) {
				entryList.add(new Entry(row.getRecords().get(i).calculateDuration().getMilliseconds() / CHART_MAXIMUM, xIndex++));
				if ( (i + 1) <  row.getRecords().size()) {
					WatchedLocationRecord recordBetween = new WatchedLocationRecord();
					recordBetween.setCheckIn(row.getRecords().get(i).getCheckOut());
					recordBetween.setCheckOut(row.getRecords().get(i + 1).getCheckIn());
					entryList.add(new Entry(recordBetween.calculateDuration().getMilliseconds() / CHART_MAXIMUM, xIndex++));
				}
			}
			
			PieDataSet dataSet = new PieDataSet(entryList, "");
			dataSet.setSliceSpace(0f);
			dataSet.setSelectionShift(0f);
			PieData data = new PieData(Arrays.asList(new String[xIndex + 1]), dataSet);
		}

	}
	
	public WeekDayRecordAdapter(List<WeekDayRecordRow> records) {
		this.records = records;
	}

	@Override
	public int getItemCount() {
		return records.size();
	}

	@Override
	public void onBindViewHolder(WeekDayRecordAdapter.ViewHolder viewHolder, int position) {

	}

	@Override
	public WeekDayRecordAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.day_records_chart, viewGroup, false);
		WeekDayRecordAdapter.ViewHolder viewHolder = new WeekDayRecordAdapter.ViewHolder(v);
		return viewHolder;
	}

}
