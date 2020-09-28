package com.xrbpowered.dailyproject.data.log;

import java.util.Calendar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.xrbpowered.dailyproject.data.InvalidFormatException;
import com.xrbpowered.dailyproject.ui.RenderUtils;

public class Note implements Comparable<Note> {
	private int year;
	private int month;
	private int day;
	private int col;
	private String key;
	
	public String text = "";
	
	public int infoy = 0;
	
	private Note() {
	}
	
	public Note(DayData day, int col) {
		this.col = col;
		setDay(day);
	}

	public Note(Calendar calendar, int col) {
		setDayCol(calendar, col);
	}

	public Note(Element el) throws InvalidFormatException {
		this.col = Integer.parseInt(el.getAttribute("at"));
		this.text = el.getTextContent();
		setDay(TableData.parseDate(el.getAttribute("day")));
	}
	
	public Element toXml(Document doc) {
		Element el = doc.createElement("note");
		el.setAttribute("day", TableData.formatDate(year, month, day));
		el.setAttribute("at", Integer.toString(col));
		el.setTextContent(text);
		return el;
	}
	
	public void setDayCol(Calendar calendar, int col) {
		this.col = col;
		setDay(calendar);
	}
	
	public void setDay(Calendar calendar) {
		this.year = calendar.get(Calendar.YEAR);
		this.month = calendar.get(Calendar.MONTH);
		this.day = calendar.get(Calendar.DAY_OF_MONTH);
		makeKey();
	}
	
	public void setDay(DayData day) {
		this.year = day.getMonthData().getYear();
		this.month = day.getMonthData().getMonth();
		this.day = day.getDate();
		makeKey();
	}
	
	public boolean dayEquals(DayData day) {
		return this.day==day.getDate()
				&& this.month==day.getMonthData().getMonth()
				&& this.year==day.getMonthData().getYear();
	}

	public boolean dayEquals(Calendar calendar) {
		return this.day==calendar.get(Calendar.DAY_OF_MONTH)
				&& this.month==calendar.get(Calendar.MONTH)
				&& this.year==calendar.get(Calendar.YEAR);
	}

	public int getDay() {
		return day;
	}
	
	public int getMonth() {
		return month;
	}
	
	public int getYear() {
		return year;
	}
	
	public int getCol() {
		return col;
	}
	
	public String formatTimeStamp() {
		return RenderUtils.formatTimeStamp(getCalendar(), col);
	}
	
	public Calendar getCalendar() {
		Calendar c = Calendar.getInstance();
		c.set(year, month, day);
		c.set(Calendar.HOUR_OF_DAY, col/DayData.HOUR_COLS);
		c.set(Calendar.MINUTE, col%DayData.HOUR_COLS * (60/DayData.HOUR_COLS));
		return c;
	}
	
	private void makeKey() {
		key = makeKey(year, month, day, col);
	}
	
	public String getKey() {
		return key;
	}
	
	public Note copyNote() {
		Note n = new Note();
		n.year = year;
		n.month = month;
		n.day = day;
		n.col = col;
		n.key = key;
		n.text = text;
		return n;
	}
	
	@Override
	public int compareTo(Note o) {
		int res = Integer.compare(year, o.year);
		if(res==0) {
			res = Integer.compare(month, o.month);
			if(res==0) {
				res = Integer.compare(day, o.day);
				if(res==0) {
					res = Integer.compare(col, o.col);
				}
			}
		}
		return res;
	}
	
	public static String makeKey(int year, int month, int day, int col) {
		return String.format("%04d-%02d-%02d:%02d", year, month+1, day, col);
	}

	public static String makeKey(Calendar c, int col) {
		return makeKey(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), col);
	}

}
