package com.xrbpowered.dailyproject.data.activities;

import com.xrbpowered.dailyproject.data.log.DayData;

public class Statistics {
	private Activity[] log = new Activity[24*4];
	private int[] statistics;
	private int[] groupStatistics;
	private boolean noStats;

	private ActivityList activityList;
	
	public Statistics(ActivityList activityList) {
		this.activityList = activityList;
		statistics = new int[activityList.activities.length];
		groupStatistics = new int[activityList.activityGroups.length];
	}
	
	public void setDayData(DayData data, int[] activityOrder) {
		noStats = true;
		for(int i=0; i<activityList.activities.length; i++) {
			statistics[i] = 0;
		}
		for(int i=0; i<activityList.activityGroups.length; i++) {
			groupStatistics[i] = 0;
		}
		for(int col=0; col<24*4; col++) {
			Activity activity = data.getActivity(col);
			if(!activity.isNull() && activity.index>=0) {
				noStats = false;
				statistics[activity.index]++;
				groupStatistics[activity.group.index]++;
			}
		}
		if(activityOrder!=null) {
			int col = 0;
			for(int i=0; i<activityOrder.length; i++) {
				for(int j=0; j<statistics[activityOrder[i]]; j++)
					log[col++] = activityList.activities[activityOrder[i]];
			}
			for(; col<24*4; col++)
				log[col] = Activity.nullActivity();
		}
	}
	
	public Activity getActivity(int col) {
		if(col<0 || col>=24*4)
			return null;
		return log[col];
	}
	
	public ActivityList getActivityList() {
		return activityList;
	}
	
	public ActivityGroup getActivityGroup(int col) {
		if(col<0 || col>=24*4)
			return null;
		if(log[col].isNull())
			return null;
		return log[col].group;
	}
	
	public int getStatValue(int activityIndex) {
		return statistics[activityIndex];
	}

	public int getStatGroupValue(int groupIndex) {
		return groupStatistics[groupIndex];
	}
	
	public boolean noStats() {
		return noStats;
	}
}
