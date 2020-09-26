package com.xrbpowered.dailyproject.data.log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.xrbpowered.dailyproject.data.InvalidFormatException;
import com.xrbpowered.dailyproject.ui.dialogs.OptionPane;

public class TableData {
	
	public static final String DATA_PATH = "log";
	public static final String DATA_PACKED_PATH = "dailylog.dat";

	public static final int FORMAT_XML = 0;
	public static final int FORMAT_DATA = 1;
	public static final int FORMAT_DATA_PACKED = 2;
	public static final int DEFAULT_FORMAT = FORMAT_DATA_PACKED;
	
	public static int saveFormat = DEFAULT_FORMAT;
	
	private static final SimpleDateFormat PARSE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd"); 

	public static Calendar theEnd = null;

	private TreeMap<Integer, YearData> years = new TreeMap<>();
	
	private static TableData inst = null;
	private static NoteData notes = null;
	
	public static TableData getInstance() {
		if(inst==null)
			inst = new TableData();
		return inst;
	}
	
	private boolean saved = true;

	private TableData() {
		if(new File(DATA_PACKED_PATH).exists()) {
			try {
				System.out.println("Loading "+DATA_PACKED_PATH);
				ZipInputStream zip = new ZipInputStream(new FileInputStream(DATA_PACKED_PATH));
				if(!zip.getNextEntry().getName().equals("dailylogs")) {
					zip.close();
					throw new InvalidFormatException();
				}
				DataInputStream in = new DataInputStream(zip);
				loadData(in);
				zip.close();
				
				if(TableData.saveFormat!=TableData.FORMAT_DATA_PACKED)
					markUnsaved();
			}
			catch(Exception e) {
				e.printStackTrace();
				// unsave corrupted file? quarantine?
			}
		}
	}
	
	public void markUnsaved() {
		saved = false;
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
	
	private void loadData(DataInputStream in) throws IOException, InvalidFormatException {
		if(in.readByte()!='P')
			throw new InvalidFormatException();
		years.clear();
		for(;;) {
			int year = in.readShort();
			if(year==0)
				break;
			years.put(year, new YearData(year, in));
		}
	}
	
	private void saveData(DataOutputStream out) throws IOException {
		out.writeByte('P');
		for(YearData y : years.values()) {
			if(!y.isNull()) {
				out.writeShort(y.getYear());
				y.saveData(out, false);
			}
		}
		out.writeShort(0);
	}
	
	public boolean save(boolean onQuit) {
		if(saved)
			return true;
		
		if(saveFormat==FORMAT_XML || saveFormat==FORMAT_DATA) {
			File dir = new File(DATA_PATH);
			if(!dir.isDirectory())
				dir.mkdir();
		}
		
		int failed = 0;
		
		if(saveFormat==FORMAT_XML || saveFormat==FORMAT_DATA) {
			for(YearData y : years.values())
				failed += y.save();
		}
		else if(saveFormat==FORMAT_DATA_PACKED) {
			try {
				ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(DATA_PACKED_PATH));
				zip.putNextEntry(new ZipEntry("dailylogs"));
				DataOutputStream out = new DataOutputStream(zip);
				saveData(out);
				zip.closeEntry();
				zip.close();
				System.out.println("Saved "+DATA_PACKED_PATH);
				saved = true;
			}
			catch(IOException e) {
				e.printStackTrace();
				failed++;
			}
		}
		else {
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
		else {
			saved = true;
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
