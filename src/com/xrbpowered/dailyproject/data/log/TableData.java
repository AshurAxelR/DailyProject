package com.xrbpowered.dailyproject.data.log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import com.xrbpowered.dailyproject.data.InvalidFormatException;
import com.xrbpowered.dailyproject.ui.dialogs.OptionPane;

public class TableData {
	
	public static final String DATA_PATH = "log";
	
	private static final SimpleDateFormat PARSE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd"); 

	public static Calendar theEnd = null;

	private HashMap<String, MonthData> months = new HashMap<String, MonthData>();
	
	private static TableData inst = new TableData();
	private static NoteData notes = null;
	
	public static TableData getInstance() {
		return inst;
	}
	
	public static NoteData getNotes() {
		if(notes==null)
			notes = new NoteData();
		return notes;
	}
	
	public static String getMonthId(int year, int month) {
		return String.format("%04d%02d", year, month+1);
	}
	
	public MonthData getMonthData(int year, int month) {
		String id = getMonthId(year, month);
		MonthData m = months.get(id);
		if(m==null) {
			m = new MonthData(year, month);
			months.put(id, m);
		}
		return m;
	}
	
	public DayData getDayData(int year, int month, int date, boolean create) {
		MonthData m = getMonthData(year, month);
		if(m!=null) {
			return m.getDayData(date, create);
		}
		else
			return null;
	}
	
	public DayData getDayData(Calendar calendar, boolean create) {
		return getDayData(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), create);
	}
	
	public boolean save(boolean onQuit) {
		File dir = new File(DATA_PATH);
		if(!dir.isDirectory())
			dir.mkdir();
		
		int failed = 0;
		for(MonthData month : months.values()) {
			if(!month.save())
				failed++;
		}
		if(!getNotes().save())
			failed++;
		if(failed>0) {
			if(onQuit)
				return (OptionPane.showMessageDialog("Failed to save "+failed+" file(s). Quit anyways?", "Save error",
					OptionPane.ERROR_ICON, new String[] {"Quit", "Cancel"})==0);
			else
				OptionPane.showMessageDialog("Failed to save "+failed+" file(s).", "Save error",
					OptionPane.ERROR_ICON, new String[] {"Ok"});
		}
		return true;
	}
	
	public static Calendar parseDate(String s) throws InvalidFormatException {
		if(s==null || s.isEmpty())
			throw new InvalidFormatException();
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(PARSE_DATE_FORMAT.parse(s));
			return calendar;
		} catch(ParseException e) {
			throw new InvalidFormatException();
		}
	}
	
	public static String formatDate(int year, int month, int day) {
		return String.format("%04d-%02d-%02d", year, month+1, day);
	}
	
}
