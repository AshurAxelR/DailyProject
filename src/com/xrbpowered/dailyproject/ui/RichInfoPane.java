package com.xrbpowered.dailyproject.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Calendar;

import javax.swing.JPanel;

import com.xrbpowered.dailyproject.data.log.Note;
import com.xrbpowered.dailyproject.data.log.TableData;

public class RichInfoPane extends JPanel {

	public static final String[] MONTH_NAMES = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

	public RichInfoPane() {
	}
	
	@Override
	public void paint(Graphics g) {
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
		
		Calendar calendar = Calendar.getInstance();
		
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

		// paint notes
		boolean titlePastLong = false;
		boolean titleFutureLong = false;
		boolean titlePast = false;
		boolean titleFuture = false;
		for(Note n : TableData.getNotes()) {
			String title = null;
			boolean highlighted = false;
			Calendar c = n.getCalendar();
			boolean past = c.compareTo(calendar)<0;
			int diff = calcDiffDays(c, calendar);
			if(diff<=-7) {
				if(!titlePastLong) {
					title = "Past notes:";
					titlePastLong = true;
				}
			}
			else if(diff>=7) {
				if(!titleFutureLong) {
					title = "Future notes:";
					titleFutureLong = true;
				}
			}
			else {
				if(past && !titlePast) {
					title = "Recently past notes:";
					titlePast = true;
				}
				if(!past) {
					if(!titleFuture) {
						title = "Notes coming soon:";
						titleFuture = true;
					}
					highlighted = (diff<1);
				}
			}
			if(title!=null) {
				g2.setColor(Color.GRAY);
				g2.drawString(title, 24, y+22);
				y+=30;
			}
			g2.setColor(highlighted ? Color.RED : Color.BLACK);
			g2.drawString("\u25cf", 24, y+12);
			g2.setColor(RenderUtils.GRAY160);
			g2.drawString(n.formatTimeStamp(), 40, y+12);
			g2.setColor(Color.BLACK);
			g2.drawString(n.text, 128, y+12);
			y += 16;
		}
	}

	public int calcDiffDays(Calendar then, Calendar now) {
		long t = then.getTimeInMillis();
		long tnow = now.getTimeInMillis();
		return (int)((t-tnow)/(1000L*3600L*24L));
	}
	

}
