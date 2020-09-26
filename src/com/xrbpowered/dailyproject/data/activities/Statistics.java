package com.xrbpowered.dailyproject.data.activities;

import com.xrbpowered.dailyproject.data.log.DayData;

public class Statistics {
	private Activity[] log = new Activity[DayData.LOG_LEN];
	private int[] statistics;
	private boolean noStats;

	private ActivityList activityList;
	
	public Statistics(ActivityList activityList) {
		this.activityList = activityList;
		statistics = new int[activityList.activities.length];
	}
	
	public void setDayData(DayData data, int[] activityOrder) {
		noStats = true;
		for(int i=0; i<activityList.activities.length; i++) {
			statistics[i] = 0;
		}
		for(int col=0; col<DayData.LOG_LEN; col++) {
			Activity activity = data.getActivity(col);
			if(activity!=null && activity.index>=0) {
				noStats = false;
				statistics[activity.index]++;
			}
		}
		if(activityOrder!=null) {
			int col = 0;
			for(int i=0; i<activityOrder.length; i++) {
				for(int j=0; j<statistics[activityOrder[i]]; j++)
					log[col++] = activityList.activities[activityOrder[i]];
			}
			for(; col<DayData.LOG_LEN; col++)
				log[col] = null;
		}
	}
	
	public Activity getActivity(int col) {
		if(col<0 || col>=DayData.LOG_LEN)
			return null;
		return log[col];
	}
	
	public ActivityList getActivityList() {
		return activityList;
	}
	
	public int getStatValue(int activityIndex) {
		return statistics[activityIndex];
	}

	public boolean noStats() {
		return noStats;
	}
}
