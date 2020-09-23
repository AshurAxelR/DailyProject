package com.xrbpowered.dailyproject.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Calendar;
import java.util.LinkedList;

import javax.swing.JPanel;

import com.xrbpowered.dailyproject.data.activities.ActivityList;
import com.xrbpowered.dailyproject.data.activities.Statistics;
import com.xrbpowered.dailyproject.data.log.DayData;
import com.xrbpowered.dailyproject.data.log.Note;
import com.xrbpowered.dailyproject.data.log.TableData;
import com.xrbpowered.dailyproject.ui.table.DailyTableGrid;

public class RichInfoPane extends JPanel {

	public static final String[] MONTH_NAMES = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

	private DayData day = null;
	private Statistics stats;
	private Calendar calendar = null;
	
	public RichInfoPane(ActivityList activityList) {
		stats = activityList.getStatistics();
	}
	
	@Override
	public void paint(Graphics g) {
		updateInfo();
		
		// paint background
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		g2.setColor(RenderUtils.GRAY240);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setColor(RenderUtils.GRAY248);
		g2.fillRect(8, 8, getWidth()-16, getHeight()-16);
		g2.setColor(RenderUtils.GRAY224);
		g2.drawRect(8, 8, getWidth()-16, getHeight()-16);
		
		g2.setClip(8, 8, getWidth()-16, getHeight()-16);
		
		// paint title
		g2.setFont(RenderUtils.FONT11BOLD);
		g2.setColor(Color.BLACK);
		g2.drawString(RenderUtils.formatTimeStamp(calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.MONTH), calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE)), 24, 42);
		g2.setColor(RenderUtils.GRAY192);
		g2.drawLine(24, 46, getWidth()-24, 46);
		
		int y = 56;

		g2.setFont(RenderUtils.FONT11);
		g2.setColor(Color.GRAY);

		// recent notes
		Calendar c = (Calendar) calendar.clone();
		LinkedList<Note> notes = new LinkedList<Note>();
		int col = calendar.get(Calendar.HOUR_OF_DAY)*4+calendar.get(Calendar.MINUTE)/15;
		findRecentNotes: for(int i=0; i<7; i++, c.add(Calendar.DAY_OF_MONTH, -1)) {
			DayData data = TableData.getInstance().getDayData(c, false);
			if(data==null || data.notes==null)
				continue;
			for(Note n : data.notes.values()) {
				if(i==0 && n.col>col)
					continue;
				int j = 0;
				for(; j<notes.size(); j++)
					if(notes.get(j).col>n.col || notes.get(j).day!=n.day)
						break;
				notes.add(j, n);
				if(notes.size()>=5)
					break findRecentNotes;
			}
		}
		if(notes.size()>0) {
			g2.setColor(Color.GRAY);
			g2.drawString("Recently passed notes:", 24, y+22);
			y+=30;
			for(Note n : notes) {
				y += paintNote(g2, n, y);
			}
		}
		
		// future notes
		c = (Calendar) calendar.clone();
		notes.clear();
		findFutureNotes: for(int i=0; i<7; i++, c.add(Calendar.DAY_OF_MONTH, 1)) {
			DayData data = TableData.getInstance().getDayData(c, false);
			if(data==null || data.notes==null)
				continue;
			for(Note n : data.notes.values()) {
				if(i==0 && n.col<=col)
					continue;
				int j = notes.size();
				for(; j>0; j--)
					if(notes.get(j-1).col<n.col || notes.get(j-1).day!=n.day)
						break;
				notes.add(j, n);
				if(notes.size()>=5)
					break findFutureNotes;
			}
		}
		if(notes.size()>0) {
			g2.setColor(Color.GRAY);
			g2.drawString("Forthcoming notes:", 24, y+22);
			y+=30;
			for(Note n : notes) {
				y += paintNote(g2, n, y);
			}
		}
	}
	
	private int paintNote(Graphics2D g2, Note n, int y) {
		g2.setColor(Color.BLACK);
		g2.drawString("\u25cf", 24, y+12);
		String timeStamp = RenderUtils.formatTimeStamp(n.day.getDate(),
				n.day.getMonthData().getMonth(), n.col/DailyTableGrid.HOUR_COLS,
				n.col%DailyTableGrid.HOUR_COLS * (60/DailyTableGrid.HOUR_COLS));
		g2.setColor(RenderUtils.GRAY160);
		g2.drawString(timeStamp, 40, y+12);
		g2.setColor(Color.BLACK);
		g2.drawString(n.text, 128, y+12);
		return 16;
	}
	
	public void updateInfo() {
		calendar = Calendar.getInstance();
		day = TableData.getInstance().getDayData(calendar, false);
		if(day!=null)
			stats.setDayData(day, null);
	}
}
