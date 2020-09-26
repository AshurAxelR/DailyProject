package com.xrbpowered.dailyproject.data.activities;

import java.io.IOException;
import java.util.LinkedList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.xrbpowered.dailyproject.data.InvalidFormatException;
import com.xrbpowered.dailyproject.data.XmlUtils;
import com.xrbpowered.dailyproject.data.log.TableData;



public class ActivityList {
	private static final String ACTIVITY_CONFIG_PATH = "activities.xml";
	
	public ActivityGroup[] activityGroups = null;
	public Activity[] activities = null;
	
	public Activity[] activitiesById = new Activity[Activity.MAX_ID+1];
	
	private static ActivityList inst = null;
	private Statistics statistics = null;
	
	public static ActivityList getInstance() throws IOException, InvalidFormatException {
		if(inst==null)
			inst = new ActivityList();
		return inst;
	}
	
	public ActivityList() throws IOException, InvalidFormatException {
		this(ACTIVITY_CONFIG_PATH);
	}
	
	public ActivityList(String path) throws IOException, InvalidFormatException  {
		Element root = XmlUtils.loadXml(path);
		if(!root.getNodeName().equals("daily")) {
			throw new InvalidFormatException();
		}
		
		TableData.theEnd = null;
		if(root.hasAttribute("theend")) {
			TableData.theEnd = TableData.parseDate(root.getAttribute("theend"));
		}
		TableData.saveFormat = TableData.DEFAULT_FORMAT;
		if(root.hasAttribute("saveFormat")) {
			String sf = root.getAttribute("saveFormat");
			if(sf.equalsIgnoreCase("xml"))
				TableData.saveFormat = TableData.FORMAT_XML;
			else if(sf.equalsIgnoreCase("dat") || sf.equalsIgnoreCase("data"))
				TableData.saveFormat = TableData.FORMAT_DATA;
		}
		
		LinkedList<ActivityGroup> groupList = new LinkedList<ActivityGroup>();
		LinkedList<Activity> activityList = new LinkedList<Activity>();
		
		NodeList groups = root.getElementsByTagName("activitygroup");
		if(groups.getLength()==0) {
			throw new InvalidFormatException();
		}
		for(int i=0; i<groups.getLength(); i++) {
			Element group = (Element) groups.item(i);
			ActivityGroup grp = new ActivityGroup(group);
			groupList.add(grp);
			
			NodeList activities = group.getElementsByTagName("activity");
			if(activities.getLength()==0) {
				throw new InvalidFormatException();
			}
			for(int j=0; j<activities.getLength(); j++) {
				Element activityNode = (Element) activities.item(j);
				Activity activity = new Activity(activityNode);
				activity.group = grp;
				activitiesById[activity.getId()] = activity;
				if(activity.isInactive())
					continue;
				activityList.add(activity);
			}
		}
		
		activityGroups = new ActivityGroup[groupList.size()];
		for(int i=0; i<groupList.size(); i++) {
			activityGroups[i] = groupList.get(i);
		}
		activities = new Activity[activityList.size()];
		for(int i=0; i<activityList.size(); i++) {
			activities[i] = activityList.get(i);
			activities[i].index = i;
		}
		
		statistics = new Statistics(this);
	}
	
	public Statistics getStatistics() {
		return statistics;
	}
}
