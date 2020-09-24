package com.xrbpowered.dailyproject.data.log;

import java.io.File;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.xrbpowered.dailyproject.data.InvalidFormatException;
import com.xrbpowered.dailyproject.data.XmlUtils;

public class MonthData {
	private HashMap<Integer, DayData> days = new HashMap<Integer, DayData>();
	
	protected boolean saved = true;

	private int year;
	private int month;
	
	public String getPath(int year, int month) {
		return TableData.DATA_PATH+"/log"+TableData.getMonthId(year, month)+".xml";
	}
	
	public MonthData(int year, int month) {
		this.year = year;
		this.month = month;
		String path = getPath(year, month);
		try {
			Element root = XmlUtils.loadXml(path);
			if(!root.getNodeName().equals("dailylog")) {
				throw new InvalidFormatException();
			}
			if(Integer.parseInt(root.getAttribute("year"))!=year ||
					Integer.parseInt(root.getAttribute("month"))!=month) {
				throw new InvalidFormatException();
			}
			
			NodeList dayNodes = root.getElementsByTagName("day");
			for(int i=0; i<dayNodes.getLength(); i++) {
				DayData day = new DayData(this, (Element) dayNodes.item(i));
				days.put(day.getDate(), day);
			}
		} catch(Exception e) {
			// unsave corrupted file? quarantine?
		}
	}
	
	public boolean save() {
		if(saved)
			return true;
		
		if(isNull(true)) {
			new File(getPath(year, month)).delete();
			System.out.println("Deleted "+TableData.getMonthId(year, month));
			saved = true;
			return true;
		}
		
		try {
			Document doc = XmlUtils.createDocument();
			Element root = doc.createElement("dailylog");
			root.setAttribute("year", Integer.toString(year));
			root.setAttribute("month", Integer.toString(month));
			
			for(DayData day : days.values()) {
				root.appendChild(day.toXml(doc));
			}
			
			doc.appendChild(root);
			XmlUtils.saveDocument(doc, getPath(year, month));
			
			System.out.println("Saved "+TableData.getMonthId(year, month));
			saved = true;
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
	
	public int getYear() {
		return year;
	}
	
	public int getMonth() {
		return month;
	}
	
	public DayData getDayData(int date, boolean create) {
		DayData d = days.get(date);
		if(d==null && create) {
			d = new DayData(this, date);
			days.put(date, d);
		}
		return d;
	}
	
	public boolean isNull(boolean purge) {
		if(purge)
			purgeNulls();
		return days.size()==0;
	}
	
	public void purgeNulls() {
		HashMap<Integer, DayData> newDays = new HashMap<Integer, DayData>();
		for(DayData day : days.values()) {
			if(!day.isNull())
				newDays.put(day.getDate(), day);
		}
		days = newDays;
	}

}
