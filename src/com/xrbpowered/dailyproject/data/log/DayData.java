package com.xrbpowered.dailyproject.data.log;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.xrbpowered.dailyproject.data.InvalidFormatException;
import com.xrbpowered.dailyproject.data.activities.Activity;
import com.xrbpowered.dailyproject.data.activities.ActivityList;

public class DayData {
	private Activity[] log = new Activity[24*4];
	
	private MonthData month;
	private int date;
	
	public DayData(MonthData month, int date) {
		this.month = month;
		this.date = date;
		for(int i=0; i<24*4; i++)
			log[i] = Activity.nullActivity();
	}

	public DayData(MonthData month, Element el) throws IOException, InvalidFormatException {
		this.month = month;
		try {
			this.date = Integer.parseInt(el.getAttribute("date"));
		}
		catch (NumberFormatException e) {
			throw new InvalidFormatException();
		}
		NodeList marks = el.getElementsByTagName("mark");
		if(marks.getLength()==0) {
			for(int i=0; i<24*4; i++)
				log[i] = Activity.nullActivity();
		}
		else {
			for(int i=0; i<marks.getLength(); i++) {
				Element mark = (Element) marks.item(i);
				Activity activity = ActivityList.getInstance().activitiesById.get(mark.getAttribute("id"));
				if(activity==null)
					activity = Activity.nullActivity();
				int from = Integer.parseInt(mark.getAttribute("from"));
				int to = Integer.parseInt(mark.getAttribute("to"));
				for(int col=from; col<=to; col++) {
					log[col] = activity;
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
	
	public Element toXml(Document doc) {
		Element el = doc.createElement("day");
		el.setAttribute("date", Integer.toString(date));
		Activity prevActivity = null;
		int prevStart = -1;
		for(int i=0; ; i++) {
			if(i==24*4 || prevActivity!=log[i]) {
				if(prevActivity!=null) {
					Element mark = doc.createElement("mark");
					mark.setAttribute("id", prevActivity.id);
					mark.setAttribute("from", Integer.toString(prevStart));
					mark.setAttribute("to", Integer.toString(i-1));
					el.appendChild(mark);
					prevStart = i;
				}
				else
					prevStart = 0;
				if(i==24*4)
					break;
				else
					prevActivity = log[i];
			}
		}
		return el;
	}
	
	public int getDate() {
		return date;
	}
	
	public MonthData getMonthData() {
		return month;
	}
	
	public boolean isNull() {
		for(int i=0; i<24*4; i++) {
			if(!log[i].isNull())
				return false;
		}
		return true;
	}
	
	public Activity getActivity(int col) {
		if(col<0 || col>=24*4)
			return null;
		return log[col];
	}
	
	public void setActivity(int col, Activity activity) {
		if(activity==null)
			return;
		log[col] = activity;
		month.saved = false;
	}
	
	public void setActivity(int from, int to, Activity activity) {
		for(int i=from; i<=to; i++) {
			setActivity(i, activity);
		}
	}
	
}
