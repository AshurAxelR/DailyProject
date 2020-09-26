package com.xrbpowered.dailyproject.data.log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.xrbpowered.dailyproject.data.InvalidFormatException;

public class YearData {
	private MonthData[] months = new MonthData[12]; 
	
	private boolean saved = true;

	private int year;

	public static String getPath(int year) {
		return String.format("%s/log%04d.dat", TableData.DATA_PATH, year);
	}
	
	private String getPath() {
		return getPath(year);
	}
	
	public YearData(int year) {
		this.year = year;
		String path = getPath();
		if(new File(path).exists()) {
			try {
				System.out.println("Loading "+year);
				ZipInputStream zip = new ZipInputStream(new FileInputStream(path));
				if(!zip.getNextEntry().getName().equals("dailylog")) {
					zip.close();
					throw new InvalidFormatException();
				}
				DataInputStream in = new DataInputStream(zip);
				loadData(in);
				zip.close();
				
				if(TableData.saveFormat!=TableData.FORMAT_DATA)
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

	public int getYear() {
		return year;
	}
	
	public MonthData getMonthData(int m) {
		if(months[m]==null)
			months[m] = new MonthData(this, m);
		return months[m];
	}
	
	private void loadData(DataInputStream in) throws IOException, InvalidFormatException {
		if(in.readByte()!='Y')
			throw new InvalidFormatException();
		int year = in.readShort();
		if(year!=this.year)
			throw new InvalidFormatException();
		int mask = in.readShort();
		for(int m=0; m<months.length; m++) {
			if((mask&1)!=0)
				months[m] = new MonthData(this, m, in);
			else
				months[m] = null;
			mask >>= 1;
		}
	}
	
	private void saveData(DataOutputStream out) throws IOException {
		out.writeByte('Y');
		out.writeShort(year);
		int mask = 0;
		for(int m=months.length-1; m>=0; m--) {
			mask = (mask<<1) | (months[m]==null ? 0 : 1);
		}
		out.writeShort(mask);
		for(int m=0; m<months.length; m++) {
			if(months[m]!=null)
				months[m].saveData(out);
		}
	}
	
	public int save() {
		if(saved)
			return 0;
		
		if(TableData.saveFormat==TableData.FORMAT_XML) {
			int failed = 0;
			for(int m=0; m<months.length; m++) {
				if(months[m]!=null) {
					if(!months[m].saveXml())
						failed++;
				}
			}
			saved = (failed==0);
			return failed;
		}
		else if(TableData.saveFormat==TableData.FORMAT_DATA) {
			if(isNull()) {
				if(new File(getPath()).delete())
					System.out.println("Deleted "+year);
				saved = true;
				return 0;
			}
			
			try {
				ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(getPath()));
				zip.putNextEntry(new ZipEntry("dailylog"));
				DataOutputStream out = new DataOutputStream(zip);
				
				saveData(out);
				
				zip.closeEntry();
				zip.close();
				
				System.out.println("Saved "+year);
				saved = true;
				return 0;
			}
			catch(IOException e) {
				e.printStackTrace();
				return 1;
			}
		}
		else {
			return 1;
		}
	}
	
	public boolean isNull() {
		for(int m=0; m<months.length; m++) {
			if(months[m]!=null)
				return false;
		}
		return true;
	}
	
}
