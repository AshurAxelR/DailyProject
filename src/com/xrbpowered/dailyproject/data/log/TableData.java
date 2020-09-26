package com.xrbpowered.dailyproject.data.log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xrbpowered.dailyproject.data.InvalidFormatException;
import com.xrbpowered.dailyproject.ui.dialogs.OptionPane;

public class TableData {
	
	public static final String DATA_PATH = "log";

	public static final int FORMAT_XML = 0;
	public static final int FORMAT_DATA = 1;
	public static final int DEFAULT_FORMAT = FORMAT_DATA;
	
	public static int saveFormat = DEFAULT_FORMAT;
	
	private static final SimpleDateFormat PARSE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd"); 

	public static Calendar theEnd = null;

	private HashMap<Integer, YearData> years = new HashMap<>();
	
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
	
	public MonthData getMonthData(int year, int month) {
		YearData y = years.get(year);
		if(y==null) {
			y = new YearData(year);
			years.put(year, y);
		}
		return y.getMonthData(month);
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
		if(saveFormat==FORMAT_XML || saveFormat==FORMAT_DATA) {
			File dir = new File(DATA_PATH);
			if(!dir.isDirectory())
				dir.mkdir();
		}
		
		int failed = 0;
		for(YearData y : years.values())
			failed += y.save();
		
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
	
	public static void migrateXml() {
		File dir = new File(DATA_PATH);
		File[] list = dir.listFiles();
		Pattern regex = MonthData.getPathRegex();
		for(File f : list) {
			if(f.isDirectory())
				continue;
			Matcher m = regex.matcher(f.getName());
			if(m.matches()) {
				try {
					int year = Integer.parseInt(m.group(1));
					int month = Integer.parseInt(m.group(2)) - 1;
					getInstance().getMonthData(year, month);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		getInstance().save(true);
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
