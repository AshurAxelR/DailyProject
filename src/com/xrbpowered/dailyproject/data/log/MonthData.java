package com.xrbpowered.dailyproject.data.log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.xrbpowered.dailyproject.data.InvalidFormatException;
import com.xrbpowered.dailyproject.data.XmlUtils;

public class MonthData {
	private DayData[] days = new DayData[31];
	
	private boolean saved = true;

	private YearData year;
	private int month;
	
	public static Pattern getPathRegex() {
		return Pattern.compile("log([0-9]{4})([0-9]{2}).xml", Pattern.CASE_INSENSITIVE);
	}
	
	public static String getMonthId(int year, int month) {
		return String.format("%04d-%02d", year, month+1);
	}

	public static String getPath(int year, int month) {
		return String.format("%s/log%04d%02d.xml", TableData.DATA_PATH, year, month+1);
	}
	
	private String getPath() {
		return getPath(year.getYear(), month);
	}
	
	public void markUnsaved() {
		year.markUnsaved();
		saved = false;
	}
	
	public MonthData(YearData year, int month) {
		this.year = year;
		this.month = month;
		String path = getPath();
		if(new File(path).exists()) {
			try {
				System.out.println("Loading "+getMonthId(year.getYear(), month));
				Element root = XmlUtils.loadXml(path);
				if(!root.getNodeName().equals("dailylog")) {
					throw new InvalidFormatException();
				}
				if(Integer.parseInt(root.getAttribute("year"))!=year.getYear() ||
						Integer.parseInt(root.getAttribute("month"))!=month) {
					throw new InvalidFormatException();
				}
				
				NodeList dayNodes = root.getElementsByTagName("day");
				for(int i=0; i<dayNodes.getLength(); i++) {
					DayData day = new DayData(this, (Element) dayNodes.item(i));
					days[day.getDate()-1] = day;
				}
				
				if(TableData.saveFormat!=TableData.FORMAT_XML)
					markUnsaved();
			} catch(Exception e) {
				e.printStackTrace();
				// unsave corrupted file? quarantine?
			}
		}
	}

	public MonthData(YearData year, int month, DataInputStream in) throws IOException, InvalidFormatException {
		this.year = year;
		this.month = in.readByte();
		if(month!=this.month)
			throw new InvalidFormatException();
		int mask = in.readInt();
		for(int d=0; d<days.length; d++) {
			if((mask&1)!=0)
				days[d] = new DayData(this, d+1, in);
			else
				days[d] = null;
			mask >>= 1;
		}
	}

	public void saveData(DataOutputStream out) throws IOException {
		out.writeByte(month);
		int mask = 0;
		for(int d=days.length-1; d>=0; d--) {
			mask = (mask<<1) | (days[d]==null ? 0 : 1);
		}
		out.writeInt(mask);
		for(int d=0; d<days.length; d++) {
			if(days[d]!=null)
				days[d].saveData(out);
		}
	}
	
	public boolean saveXml() {
		if(saved)
			return true;
		
		if(isNull()) {
			if(new File(getPath()).delete())
				System.out.println("Deleted "+getMonthId(year.getYear(), month));
			saved = true;
			return true;
		}
		
		try {
			Document doc = XmlUtils.createDocument();
			Element root = doc.createElement("dailylog");
			root.setAttribute("year", Integer.toString(year.getYear()));
			root.setAttribute("month", Integer.toString(month));
			
			for(DayData day : days) {
				if(day!=null)
					root.appendChild(day.toXml(doc));
			}
			
			doc.appendChild(root);
			XmlUtils.saveDocument(doc, getPath());
			
			System.out.println("Saved "+getMonthId(year.getYear(), month));
			saved = true;
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public YearData getYearData() {
		return year;
	}

	public int getYear() {
		return year.getYear();
	}

	public int getMonth() {
		return month;
	}
	
	public DayData getDayData(int date, boolean create) {
		if(create && days[date-1]==null)
			days[date-1] = new DayData(this, date);
		return days[date-1];
	}
	
	public boolean isNull() {
		for(int d=0; d<days.length; d++) {
			if(days[d]!=null)
				return false;
		}
		return true;
	}
	
}
