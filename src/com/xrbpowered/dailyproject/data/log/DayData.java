package com.xrbpowered.dailyproject.data.log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.xrbpowered.dailyproject.data.InvalidFormatException;
import com.xrbpowered.dailyproject.data.activities.Activity;
import com.xrbpowered.dailyproject.data.activities.ActivityList;

public class DayData {
	public static final int HOUR_COLS = 4;
	public static final int LOG_LEN = 24*HOUR_COLS;
	
	private byte[] log = new byte[LOG_LEN];

	private ActivityList activities;
	private MonthData month;
	private int date;
	
	public DayData(MonthData month, int date) {
		try {
			this.activities = ActivityList.getInstance();
		}
		catch(IOException | InvalidFormatException e) {
			e.printStackTrace();
		}
		this.month = month;
		this.date = date;
		for(int i=0; i<LOG_LEN; i++)
			log[i] = Activity.NULL_ID;
	}

	public DayData(MonthData month, Element el) throws IOException, InvalidFormatException {
		this.activities = ActivityList.getInstance();
		this.month = month;
		try {
			this.date = Integer.parseInt(el.getAttribute("date"));
		}
		catch (NumberFormatException e) {
			throw new InvalidFormatException();
		}
		NodeList marks = el.getElementsByTagName("mark");
		if(marks.getLength()==0) {
			for(int i=0; i<LOG_LEN; i++)
				log[i] = Activity.NULL_ID;
		}
		else {
			for(int i=0; i<marks.getLength(); i++) {
				Element mark = (Element) marks.item(i);
				byte activityId = Activity.NULL_ID;
				try {
					activityId = checkActivityId(Integer.parseInt(mark.getAttribute("id")));
				}
				catch(NumberFormatException e) {
				}
				int from = Integer.parseInt(mark.getAttribute("from"));
				int to = Integer.parseInt(mark.getAttribute("to"));
				for(int col=from; col<=to; col++) {
					log[col] = activityId;
				}
			}
		}
		NodeList noteList = el.getElementsByTagName("note");
		if(noteList.getLength()>0) {
			NoteData notes = TableData.getNotes();
			for(int i=0; i<noteList.getLength(); i++) {
				Element note = (Element) noteList.item(i);
				int col = Integer.parseInt(note.getAttribute("at"));
				Note n = new Note(this, col);
				n.text = note.getTextContent();
				notes.add(n);
			}
		}
	}
	
	public DayData(MonthData month, int date, DataInputStream in) throws IOException, InvalidFormatException {
		this.activities = ActivityList.getInstance();
		this.month = month;
		this.date = date;
		for(int i=0; i<LOG_LEN; i++)
			log[i] = checkActivityId(in.readByte());
	}
	
	private byte checkActivityId(int id) {
		if(id>=0 && id<=Activity.MAX_ID && activities.activitiesById[id]!=null)
			return (byte)id;
		else
			return Activity.NULL_ID;
	}
	
	public Element toXml(Document doc) {
		Element el = doc.createElement("day");
		el.setAttribute("date", Integer.toString(date));
		byte prevId = log[0];
		int prevStart = 0;
		for(int i=1; i<=LOG_LEN; i++) {
			if(i==LOG_LEN || prevId!=log[i]) {
				Element mark = doc.createElement("mark");
				mark.setAttribute("id", Integer.toString(prevId));
				mark.setAttribute("from", Integer.toString(prevStart));
				mark.setAttribute("to", Integer.toString(i-1));
				el.appendChild(mark);
				prevStart = i;
				if(i<LOG_LEN)
					prevId = log[i];
			}
		}
		return el;
	}
	
	public void saveData(DataOutputStream out) throws IOException {
		for(int i=0; i<LOG_LEN; i++)
			out.writeByte(log[i]);
	}
	
	public int getDate() {
		return date;
	}
	
	public MonthData getMonthData() {
		return month;
	}
	
	public boolean isNull() {
		for(int i=0; i<LOG_LEN; i++) {
			if(log[i]!=Activity.NULL_ID)
				return false;
		}
		return true;
	}
	
	public Activity getActivity(int col) {
		if(col<0 || col>=LOG_LEN)
			return null;
		byte activityId = log[col];
		return activityId==Activity.NULL_ID ? null : activities.activitiesById[activityId];
	}
	
	public void setActivity(int col, byte activityId) {
		log[col] = activityId;
		month.markUnsaved();
	}
	
	public void setActivity(int from, int to, byte activityId) {
		for(int i=from; i<=to; i++) {
			setActivity(i, activityId);
		}
	}
	
}
