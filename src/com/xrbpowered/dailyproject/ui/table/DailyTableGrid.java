package com.xrbpowered.dailyproject.ui.table;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Calendar;

import javax.swing.JPanel;

import com.xrbpowered.dailyproject.data.activities.Activity;
import com.xrbpowered.dailyproject.data.activities.ActivityList;
import com.xrbpowered.dailyproject.data.activities.Statistics;
import com.xrbpowered.dailyproject.data.log.DayData;
import com.xrbpowered.dailyproject.data.log.Note;
import com.xrbpowered.dailyproject.data.log.NoteData;
import com.xrbpowered.dailyproject.data.log.TableData;
import com.xrbpowered.dailyproject.ui.RenderUtils;
import com.xrbpowered.dailyproject.ui.dialogs.EditNoteDialog;
import com.xrbpowered.dailyproject.ui.dialogs.OptionPane;
import com.xrbpowered.dailyproject.ui.dialogs.SelectActivityDialog;
import com.xrbpowered.dailyproject.ui.images.ActivityImageHolder;
import com.xrbpowered.dailyproject.ui.images.BackgroundImageHolder;

public class DailyTableGrid extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

	private static final String[] DAY_OF_WEEK_LETTERS = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
	private static final String BG_IMAGE_NAME = "bg.bmp";
	private static final String FG_IMAGE_NAME = "fg.bmp";
	
	public static final int ACTIVITY_IMAGE_WIDTH = 80;
	public static final int ACTIVITY_IMAGE_HEIGHT = 16;
	public static final int HOUR_COLS = 4;
	
	private static final int GRID_STARTX = 50;
	private static final int GRID_STARTY = 16;
	
	private static final int COL_WIDTH = 10;
	private static final int HOUR_COL_WIDTH = COL_WIDTH*HOUR_COLS;
	private static final int ROW_HEIGHT = ACTIVITY_IMAGE_HEIGHT+3;
	
	private static final int CALENDAR_MONTHBAR_WIDTH = 16;
	private static final int CALENDAR_DAYBAR_WIDTH = 34;
	private static final int CALENDAR_DAYOFWEEK_WIDTH = 16;
	private static final int TEXT_HEIGHT = 12;
	private static final int NODE_MARK_RADIUS = 3;

	private static boolean beforeEnd(Calendar calendar) {
		return TableData.theEnd==null || calendar.compareTo(TableData.theEnd)<0;
	}

	private static boolean isEnd(Calendar calendar) {
		return TableData.theEnd!=null
				&& TableData.theEnd.get(Calendar.YEAR)==calendar.get(Calendar.YEAR)
				&& TableData.theEnd.get(Calendar.MONTH)==calendar.get(Calendar.MONTH)
				&& TableData.theEnd.get(Calendar.DATE)==calendar.get(Calendar.DATE);
	}

	private static BufferedImage bgImage = null;
	private static BufferedImage fgImage = null;
	private static ActivityImageHolder bgImageHolder = null;
	
	private ActivityList activityList;
	private DailyTable parent;

	private Calendar startDate;
	private int startCol = 0;

	private boolean selecting = false;
	private Point selectionStart = null;
	private Point selectionSize = null;

	private Note showingNote = null;
	private Point showingNotePoint = null;

	private Note dragNote = null;
	private Point dragNotePosition = null;
	
	private int[] activityOrder;

	public DailyTableGrid(DailyTable parent, ActivityList activityList) throws IOException {
		this.parent = parent;
		this.activityList = activityList;
		scrollToToday();
		loadResources();
		resetActivityOrder();
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}

	private static void loadResources() throws IOException {
		if(bgImage == null) {
			bgImage = RenderUtils.loadImage(BG_IMAGE_NAME);
			bgImageHolder = new BackgroundImageHolder(bgImage);
		}
		if(fgImage == null) {
			fgImage = RenderUtils.loadImage(FG_IMAGE_NAME);
		}
	}

	private void resetActivityOrder() {
		activityOrder = new int[activityList.activities.length];
		for(int i=0; i<activityOrder.length; i++)
			activityOrder[i] = i;
	}
	
	public int getMaxGridWidth() {
		return GRID_STARTX + getMaxGridCols()*HOUR_COL_WIDTH;
	}
	
	public int getMaxGridCols() {
		return Math.max(24, activityList.activities.length);
	}
	
	public int getMaxGridRows() {
		return (getHeight()-GRID_STARTY) / ROW_HEIGHT;
	}
	
	public int getModeGridCols() {
		if(parent.getMode() == DailyTable.MODE_STATS  && parent.statsSummary) {
			return activityList.activities.length;
		}
		else
			return 24;
	}
	
	private int getColX(int col) {
		 return GRID_STARTX + col*COL_WIDTH;
	}
	
	private int getRowY(int row) {
		return GRID_STARTY + row*ROW_HEIGHT;
	}

	private void drawActivityItem(Graphics2D g2, int col, int colx, int rowy, ActivityImageHolder activity,
			ActivityImageHolder prevActivity, ActivityImageHolder nextActivity) {
		ActivityImageHolder imgHolder = activity;
		if(activity == null || activity.isNull())
			imgHolder = bgImageHolder;
		
		BufferedImage img;
		int x = colx + 1;
		int y = rowy + 2;
		
		if(activity != prevActivity)
			img = imgHolder.getImage(ActivityImageHolder.START);
		else if(col%HOUR_COLS==0)
			img = imgHolder.getImage(ActivityImageHolder.START_HOUR);
		else
			img = imgHolder.getImage(ActivityImageHolder.LEFT);
		g2.drawImage(img, x, y, null);
		if(activity != nextActivity)
			img = imgHolder.getImage(ActivityImageHolder.END);
		else if(col%HOUR_COLS == 3)
			img = imgHolder.getImage(ActivityImageHolder.END_HOUR);
		else
			img = imgHolder.getImage(ActivityImageHolder.RIGHT);
		g2.drawImage(img, x+COL_WIDTH/2, y, null);
	}
	
	private void paintBackground(Graphics2D g2, int gridw) {
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, gridw, getHeight());
		if(getWidth() > gridw) {
			g2.setColor(RenderUtils.GRAY248);
			g2.fillRect(gridw, 0, getWidth()-gridw, getHeight());
			g2.setColor(Color.GRAY);
			g2.drawLine(gridw, 0, gridw, getHeight());
		}
		g2.setColor(RenderUtils.GRAY224);
		g2.fillRect(0, GRID_STARTY, CALENDAR_MONTHBAR_WIDTH, getHeight());
	}
	
	private void paintCalendarRow(Graphics2D g2, Calendar calendar, int rowy) {
		boolean isWeekend = calendar.get(Calendar.DAY_OF_WEEK) < 2
				|| calendar.get(Calendar.DAY_OF_WEEK) > 6;
		g2.setFont(RenderUtils.FONT10);
		
		g2.setColor((isWeekend)?RenderUtils.LIGHT_RED240:RenderUtils.GRAY240);
		g2.fillRect(CALENDAR_MONTHBAR_WIDTH, rowy,
				CALENDAR_DAYBAR_WIDTH, ROW_HEIGHT);
		
		g2.setColor((isWeekend)?Color.RED:Color.BLACK);
		g2.drawString(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)),
				CALENDAR_MONTHBAR_WIDTH+2, rowy+TEXT_HEIGHT);
		
		g2.setColor((isWeekend)?Color.RED:Color.GRAY);
		g2.drawString(DAY_OF_WEEK_LETTERS[calendar.get(Calendar.DAY_OF_WEEK)-1],
				CALENDAR_MONTHBAR_WIDTH+CALENDAR_DAYBAR_WIDTH-CALENDAR_DAYOFWEEK_WIDTH,
				rowy+TEXT_HEIGHT);
		
		if(rowy==GRID_STARTY || calendar.get(Calendar.DAY_OF_MONTH) == 1) {
			g2.setColor(Color.GRAY);
			g2.drawString(Integer.toString(calendar.get(Calendar.MONTH)+1), 2, rowy+TEXT_HEIGHT);
		}
	}
	
	private void paintTitleBar(Graphics2D g2, int col, int colx) {
		if(parent.getMode() == DailyTable.MODE_STATS && parent.statsSummary) {
			if(col%HOUR_COLS == 0) {
				col /= HOUR_COLS;
				ActivityImageHolder actImage = null;
				if(col < activityList.activities.length) {
					actImage = activityList.activities[col];
				}
				if(actImage != null) {
					RenderUtils.renderLongActivityThumb(g2, actImage, colx+1, 0);
				}
			}
		} else {
			if(col%HOUR_COLS == 0) {
				g2.setColor(RenderUtils.GRAY224);
				g2.drawLine(colx, 0, colx, GRID_STARTY);
				g2.setColor(Color.BLACK);
				g2.drawString(String.format("%02d", col/HOUR_COLS), colx+3, TEXT_HEIGHT+1);
			} else if(col%HOUR_COLS == 2) {
				g2.setColor(RenderUtils.GRAY248);
				g2.drawLine(colx, 0, colx, GRID_STARTY);
			}
		}
	}
	
	private int getStatsValue(DayData data, Statistics stats, int col) {
		if(data != null && !stats.noStats()) {
			if(col < activityList.activities.length) {
				return stats.getStatValue(col);
			}
		}
		return -1;
	}
	
	private void paintActivityRow(Graphics2D g2, DayData data, Statistics stats, int col, int colx, int rowy) {
		if(parent.getMode() == DailyTable.MODE_STATS) {
			if(parent.statsSummary) {
				if(col%HOUR_COLS == 0) {
					col /= HOUR_COLS;
					int value = getStatsValue(data, stats, col);
					if(value >= 0) {
						g2.setFont(RenderUtils.FONT11);
						g2.setColor((value == 0)?RenderUtils.GRAY208:Color.GRAY);
						String duration = RenderUtils.formatDuration(value);
						g2.drawString(duration,
								colx+HOUR_COL_WIDTH-5-g2.getFontMetrics().stringWidth(duration),
								rowy+TEXT_HEIGHT+2);
						g2.setFont(RenderUtils.FONT10);
						g2.setColor(RenderUtils.GRAY224);
						g2.drawLine(colx+HOUR_COL_WIDTH, rowy+1, colx+HOUR_COL_WIDTH,
								rowy+ROW_HEIGHT-1);
					}
				}
			} else {
				if(data == null)
					drawActivityItem(g2, col, colx, rowy, null, null, null);
				else
					drawActivityItem(g2, col, colx, rowy, stats.getActivity(col),
							stats	.getActivity(col-1), stats.getActivity(col+1));
			}
		} else if(data == null)
			drawActivityItem(g2, col, colx, rowy, null, null, null);
		else
			drawActivityItem(g2, col, colx, rowy, data.getActivity(col),
					data.getActivity(col-1), data.getActivity(col+1));
	}
	
	private void paintNoteMark(Graphics2D g2, Color color, int colx, int rowy) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(new Color(255, 255, 255, 224));
		g2.fillOval(colx+1 + COL_WIDTH/2 - NODE_MARK_RADIUS-2, rowy+9 - NODE_MARK_RADIUS-2,
				NODE_MARK_RADIUS*2+4, NODE_MARK_RADIUS*2+4);
		g2.setColor(color);
		g2.fillOval(colx+1 + COL_WIDTH/2 - NODE_MARK_RADIUS, rowy+9 - NODE_MARK_RADIUS,
				NODE_MARK_RADIUS*2, NODE_MARK_RADIUS*2);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
	}
	
	private void paintRowBottomLine(Graphics2D g2, Calendar calendar, int gridw, int rowy) {
		if(isEnd(calendar)) {
			g2.setColor(Color.RED);
			g2.drawLine(CALENDAR_MONTHBAR_WIDTH, rowy, gridw-1, rowy);
			g2.drawLine(CALENDAR_MONTHBAR_WIDTH, rowy+1, gridw-1, rowy+1);
			return;
		}
		if(!beforeEnd(calendar))
			return;
		if(calendar.get(Calendar.DAY_OF_WEEK) == 2 || calendar.get(Calendar.DAY_OF_MONTH) == 1)
			g2.setColor(Color.BLACK);
		else if(calendar.get(Calendar.DAY_OF_WEEK) == 1
				|| calendar.get(Calendar.DAY_OF_WEEK) == 7)
			g2.setColor(RenderUtils.LIGHT_RED192);
		else
			g2.setColor(RenderUtils.GRAY224);
		g2.drawLine(CALENDAR_MONTHBAR_WIDTH, rowy, gridw-1, rowy);
		if(calendar.get(Calendar.DAY_OF_MONTH) == 1) {
			g2.setColor(Color.BLACK);
			g2.drawLine(CALENDAR_MONTHBAR_WIDTH, rowy+1, gridw-1, rowy+1);
		}
	}
	
	private void paintForeground(Graphics2D g2) {
		g2.setColor(RenderUtils.GRAY224);
		g2.drawLine(CALENDAR_MONTHBAR_WIDTH+CALENDAR_DAYBAR_WIDTH-CALENDAR_DAYOFWEEK_WIDTH-2, 
				GRID_STARTY,
				CALENDAR_MONTHBAR_WIDTH+CALENDAR_DAYBAR_WIDTH-CALENDAR_DAYOFWEEK_WIDTH-2,
				getHeight());
		g2.setColor(Color.GRAY);
		g2.drawLine(GRID_STARTX, GRID_STARTY, GRID_STARTX, getHeight());
	}
	
	private void paintSelection(Graphics2D g2, Rectangle sel, int sum, int num) {
		if(parent.getMode()==DailyTable.MODE_EDIT_ACTIVITIES
				|| parent.getMode()==DailyTable.MODE_STATS && parent.statsSummary) {
			sel = snapToScreen(sel);
			if(sel.width>0 && sel.height>0) {
				int x = getColX(sel.x-startCol);
				int w = sel.width*COL_WIDTH;
				int y = getRowY(sel.y);
				int h = sel.height*ROW_HEIGHT;
				g2.setColor(new Color(128, 128, 128, 64));
				g2.fillRect(x, 0, w, 16);
				g2.fillRect(16, y, 34, h);
				g2.setColor(new Color(128, 128, 128, 128));
				g2.fillRect(x, y, w, h);
				g2.setColor(Color.GRAY);
				g2.drawRect(x, y, w, h);
				if(parent.getMode()==DailyTable.MODE_STATS && parent.statsSummary && num>0) {
					g2.setFont(RenderUtils.FONT11);
					String info = String.format("%dd  \u03a3: %s  \u0100: %s",
							num, RenderUtils.formatDuration(sum),
							RenderUtils.formatDuration(Math.round(sum/(float)num)));
					int tw = g2.getFontMetrics().stringWidth(info) + 16;
					int tx = x+w-tw;
					if(tx<GRID_STARTX)
						tx = GRID_STARTX;
					int ty = y+h+18;
					if(ty>getHeight())
						ty = getHeight();
					
					g2.setPaint(new GradientPaint(0, ty-16, new Color(248, 248, 248), 0,
							ty, new Color(224, 224, 224)));
					g2.fillRect(tx, ty-17, tw, 17);
					g2.setColor(RenderUtils.GRAY208);
					g2.drawRect(tx, ty-17, tw, 17);
					g2.setColor(Color.BLACK);
					g2.drawString(info, tx+8, ty-3);
				}
			}
		}
	}
	
	private void paintTodayMarker(Graphics2D g2, int gridw, int curRow) {
		int rowy = getRowY(curRow);
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.BLACK);
		g2.fillPolygon(new int[] { 
				CALENDAR_MONTHBAR_WIDTH-6,
				CALENDAR_MONTHBAR_WIDTH-1,
				CALENDAR_MONTHBAR_WIDTH-6
			}, new int[] {rowy+3, rowy+8, rowy+13}, 3);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		
		g2.setPaint(new GradientPaint(0, rowy+2, new Color(255, 0, 0, 16), 0,
				rowy+ROW_HEIGHT-1, new Color(255, 255, 255, 32)));
		g2.fillRect(GRID_STARTX+2, rowy+2, gridw-GRID_STARTX-5, ROW_HEIGHT-5);
		g2.setColor(new Color(255, 255, 255, 128));
		g2.drawRect(GRID_STARTX+2, rowy+2, gridw-GRID_STARTX-5, ROW_HEIGHT-5);
		g2.setColor(Color.RED);
		g2.drawRect(GRID_STARTX+1, rowy+1, gridw-GRID_STARTX-3, ROW_HEIGHT-3);
		g2.setColor(new Color(255, 0, 0, 128));
		g2.drawRect(GRID_STARTX, rowy, gridw-GRID_STARTX-1, ROW_HEIGHT-1);
	}
	
	private void paintNowMarker(Graphics2D g2, int startCol) {
		int cur = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)*HOUR_COLS
				+ (Calendar.getInstance().get(Calendar.MINUTE)+7)/15;
		int colx = getColX(cur-startCol);
		g2.setColor(Color.RED);
		g2.drawLine(colx, GRID_STARTY, colx, getHeight());
		g2.setColor(Color.WHITE);
		g2.drawLine(colx-1, GRID_STARTY, colx-1, getHeight());
		g2.drawLine(colx+1, GRID_STARTY, colx+1, getHeight());
		g2.setColor(new Color(255, 255, 255, 128));
		g2.drawLine(colx-2, GRID_STARTY, colx-2, getHeight());
		g2.drawLine(colx+2, GRID_STARTY, colx+2, getHeight());
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.RED);
		g2.fillPolygon(new int[] {colx-3, colx, colx+3},
				new int[] {GRID_STARTY-4, GRID_STARTY-1, GRID_STARTY-4}, 3);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
	}
	
	private void paintNoteCaption(Graphics2D g2, Note note, int col, int row) {
		int x = getColX(col)+6;
		int y = getRowY(row)+4;
		
		String text = note.text;
		if(text == null || text.isEmpty())
			text = "...";
		
		int w = g2.getFontMetrics(RenderUtils.FONT11).stringWidth(text) + 20;
		int h = 36;
		
		String timeStamp = note.formatTimeStamp();
		
		w = Math.max(w, g2.getFontMetrics(RenderUtils.FONT10).stringWidth(timeStamp) + 20);
		g2.setPaint(new GradientPaint(0, y-h-5, new Color(248, 248, 248),
				0, y-h+13, new Color(224, 224, 224)));
		g2.fillRect(x-w/2, y-h-5, w, 18);
		g2.setColor(Color.WHITE);
		g2.fillRect(x-w/2, y-h-5+18, w, h-18);
		g2.setColor(new Color(255, 255, 255, 192));
		g2.drawRect(x-w/2, y-h-5, w, h);
		g2.drawLine(x, y-5, x, y);
		
		g2.setColor(Color.RED);
		g2.drawRect(x-w/2+1, y-h-5+1, w-2, h-2);
		g2.setFont(RenderUtils.FONT10);
		g2.setColor(Color.GRAY);
		g2.drawString(timeStamp, x-w/2+8, y-27);
		g2.setFont(RenderUtils.FONT11);
		g2.setColor(Color.BLACK);
		g2.drawString(text, x-w/2+8, y-10);
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		int gridw = GRID_STARTX + getModeGridCols()*HOUR_COL_WIDTH - startCol*COL_WIDTH + 1;
		paintBackground(g2, gridw);

		Rectangle sel = null;
		if(selectionStart!=null && selectionSize!=null) {
			Point selStart = (Point) selectionStart.clone();
			Point selSize = (Point) selectionSize.clone();
			sortSelection(selStart, selSize);
			sel = new Rectangle();
			if(parent.getMode() == DailyTable.MODE_STATS && parent.statsSummary) { 
				sel.x = (selStart.x/4)*4;
				sel.width = ((selSize.x+selStart.x%4)/4 + 1)*4;
			}
			else {
				sel.x = selStart.x;
				sel.width = selSize.x + 1;
			}
			sel.y = selStart.y;
			sel.height = selSize.y + 1;
		}
		
		int summarySum = 0;
		
		// paint main grid
		Calendar calendar = (Calendar) startDate.clone();
		int nRows = getMaxGridRows();
		int curRow = -1;
		NoteData notes = TableData.getNotes();
		Note nextNote = notes.findNoteAfter(new Note(calendar, -1));
		for(int row = 0; row <= nRows; row++) {
			int rowy = getRowY(row);
			if(beforeEnd(calendar)) {
				paintCalendarRow(g2, calendar, rowy);
	
				DayData data = TableData.getInstance().getDayData(calendar, false);
				Statistics stats = activityList.getStatistics();
				if(parent.getMode()==DailyTable.MODE_STATS && data != null) {
					stats.setDayData(data, activityOrder);
				}
				
				int nCols = getModeGridCols()*HOUR_COLS - startCol;
				int curCol = startCol;
				boolean hasNotes = nextNote!=null && nextNote.dayEquals(calendar);
				for(int col = 0; col < nCols; col++, curCol++) {
					int colx = getColX(col);
					if(row == 0) {
						paintTitleBar(g2, curCol, colx);
					}
	
					paintActivityRow(g2, data, stats, curCol, colx, rowy);
	
					if(parent.getMode()==DailyTable.MODE_OBSERVE || parent.getMode()==DailyTable.MODE_EDIT_NOTES) {
						if(hasNotes && nextNote.getCol()==col) {
							paintNoteMark(g2, Color.RED, colx, rowy);
							nextNote = notes.findNoteAfter(nextNote);
							hasNotes = nextNote!=null && nextNote.dayEquals(calendar);
						}
					}
					
					if(sel!=null && parent.getMode()==DailyTable.MODE_STATS && parent.statsSummary
							&& curCol%HOUR_COLS==0 && row>=sel.y && row<sel.y+sel.height &&
							curCol>=sel.x && curCol<sel.x+sel.width) {
						int value = getStatsValue(data, stats, curCol/HOUR_COLS);
						if(value<0)
							value = 0;
						summarySum += value;
					}
	
				}
			}
			paintRowBottomLine(g2, calendar, gridw, rowy);
			
			// find current row
			if(calendar.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
					&& calendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)
					&& calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
				curRow = row;
			}
			
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		g2.setColor(Color.BLACK);
		g2.drawString(String.format("%04d", startDate.get(Calendar.YEAR)), 2, TEXT_HEIGHT+1);

		paintForeground(g2);

		if(curRow >= 0) {
			paintTodayMarker(g2, gridw, curRow);
		}
		if(parent.getMode() != DailyTable.MODE_STATS) {
			paintNowMarker(g2, startCol);
		}

		if(showingNote != null) {
			paintNoteCaption(g2, showingNote, showingNotePoint.x-startCol, showingNotePoint.y);
		}
		if(sel!=null) {
			paintSelection(g2, sel, summarySum, sel.height);
		}

		if(dragNote != null) {
			paintNoteMark(g2, Color.GRAY, getColX(dragNotePosition.x-startCol), getRowY(dragNotePosition.y));
		}
	}

	public static BufferedImage createColorisedActivityImage(Color color) throws IOException {
		loadResources();
		BufferedImage img = new BufferedImage(fgImage.getWidth(), fgImage.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		for(int x = 0; x < fgImage.getWidth(); x++)
			for(int y = 0; y < fgImage.getHeight(); y++) {
				Color src = new Color(fgImage.getRGB(x, y));
				Color dst = new Color((src.getRed() - src.getGreen()) * color.getRed() / 255
						+ src.getGreen(), (src.getRed() - src.getGreen()) * color.getGreen() / 255
						+ src.getGreen(), (src.getRed() - src.getGreen()) * color.getBlue() / 255
						+ src.getGreen(), 255);
				img.setRGB(x, y, dst.getRGB());
			}
		return img;
	}
	
	public static void splitActivityImage(BufferedImage[] images) {
		BufferedImage full = images[ActivityImageHolder.FULL];
		images[ActivityImageHolder.START] = RenderUtils.cutImage(full, 0, 0, COL_WIDTH/2, ACTIVITY_IMAGE_HEIGHT);
		images[ActivityImageHolder.START_HOUR] = RenderUtils.cutImage(full, HOUR_COL_WIDTH, 0, COL_WIDTH/2, ACTIVITY_IMAGE_HEIGHT);
		images[ActivityImageHolder.LEFT] = RenderUtils.cutImage(full, COL_WIDTH, 0, COL_WIDTH/2, ACTIVITY_IMAGE_HEIGHT);
		images[ActivityImageHolder.END] = RenderUtils.cutImage(full, ACTIVITY_IMAGE_WIDTH-COL_WIDTH/2, 0, COL_WIDTH/2, ACTIVITY_IMAGE_HEIGHT);
		images[ActivityImageHolder.END_HOUR] = RenderUtils.cutImage(full, HOUR_COL_WIDTH-COL_WIDTH/2, 0, COL_WIDTH/2, ACTIVITY_IMAGE_HEIGHT);
		images[ActivityImageHolder.RIGHT] = RenderUtils.cutImage(full, COL_WIDTH+COL_WIDTH/2, 0, COL_WIDTH/2, ACTIVITY_IMAGE_HEIGHT);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			if(parent.getMode() == DailyTable.MODE_EDIT_NOTES) {
				Point loc = pointToCell(e.getPoint());
				if(isOnScreen(loc)) {
					NoteData notes = TableData.getNotes();
					Calendar c = getRowCalendar(loc.y);
					Note oldNote = notes.getNoteAt(c, loc.x);
					Note note = EditNoteDialog.show(e.getLocationOnScreen(),
							oldNote==null ? new Note(c, loc.x) : oldNote, oldNote!=null);
					if(oldNote==null && note!=null)
						notes.add(note);
					else if(oldNote!=null && note==null)
						notes.remove(oldNote);
					else if(oldNote!=null)
						notes.update(note);
					getRootPane().repaint();
				}
			} else if(parent.getMode() == DailyTable.MODE_EDIT_ACTIVITIES) {
				if(selectionStart != null) {
					Activity sel = SelectActivityDialog.show(e.getLocationOnScreen(), activityList);
					if(sel != null) {
						Calendar date = (Calendar) startDate.clone();
						date.add(Calendar.DAY_OF_MONTH, selectionStart.y);
						DayData data = TableData.getInstance().getDayData(date, true);
						data.setActivity(selectionStart.x, selectionStart.x + selectionSize.x, sel);
						repaint();
					}
				}
			} else if(parent.getMode() == DailyTable.MODE_STATS
					&& !parent.statsSummary) {
				resetActivityOrder();
				repaint();
			}
		}
	}

	public void scrollToToday() {
		startDate = Calendar.getInstance();
		startDate.add(Calendar.DAY_OF_MONTH, -7);
		deselect();
		repaint();
	}

	public void deselect() {
		selectionStart = null;
		selectionSize = null;
		showingNote = null;
	}

	private Calendar getRowCalendar(int row) {
		Calendar c = (Calendar) startDate.clone();
		c.add(Calendar.DAY_OF_MONTH, row);
		return c;
	}

	private DayData getDayAtRow(int row, boolean create) {
		return TableData.getInstance().getDayData(getRowCalendar(row), create);
	}

	private Point pointToCell(Point point) {
		return new Point((point.x-GRID_STARTX)/COL_WIDTH + startCol, (point.y-GRID_STARTY)/ROW_HEIGHT);
	}

	private boolean isOnScreen(Point loc) {
		return loc.x>=startCol && loc.x<getMaxGridCols()*4 && loc.y>0;
	}

	private void sortSelection(Point start, Point size) {
		if(size.x<0) {
			start.x = start.x+size.x;
			size.x = -size.x;
		}
		if(size.y<0) {
			start.y = start.y+size.y;
			size.y = -size.y;
		}
	}
	
	private Rectangle snapToScreen(Rectangle rect) {
		Point start = snapToScreen(new Point(rect.x, rect.y));
		Point end = snapToScreen(new Point(rect.x+rect.width, rect.y+rect.height));
		return new Rectangle(start.x, start.y, end.x-start.x, end.y-start.y);
	}
	
	private Point snapToScreen(Point loc) {
		Point pt = (Point) loc.clone();
		if(pt.x<startCol)
			pt.x = startCol;
		if(pt.x > getMaxGridCols()*4)
			pt.x = getMaxGridCols()*4;
		if(pt.y<0)
			pt.y = 0;
		if(pt.y > getMaxGridRows()+1)
			pt.y = getMaxGridRows()+1;
		return pt;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(parent.getMode() == DailyTable.MODE_EDIT_NOTES) {
				Point loc = pointToCell(e.getPoint());
				if(isOnScreen(loc)) {
					NoteData notes = TableData.getNotes();
					dragNote = notes.getNoteAt(startDate, loc.y, loc.x);
					if(dragNote!=null)
						notes.remove(dragNote);
					
					dragNotePosition = loc;
					showingNote = null;
					repaint();
				}
			}
			else if(parent.getMode() == DailyTable.MODE_EDIT_ACTIVITIES
					|| parent.getMode() == DailyTable.MODE_STATS && parent.statsSummary) {
				Point loc = pointToCell(e.getPoint());
				if(isOnScreen(loc)) {
					selecting = true;
					selectionStart = new Point(loc.x, loc.y);
					selectionSize = new Point(0, 0);
					repaint();
				}
			}
			else if(parent.getMode() == DailyTable.MODE_STATS && !parent.statsSummary) {
				Point loc = pointToCell(e.getPoint());
				DayData data = getDayAtRow(loc.y, true);
				if(data == null)
					return;
				Statistics stats = activityList.getStatistics();
				stats.setDayData(data, activityOrder);
				int n = stats.getActivity(loc.x).index;
				int[] sorted = new int[activityOrder.length];
				sorted[0] = n;
				int i=0;
				for(; activityOrder[i]!=n; i++) {
					sorted[i+1] = activityOrder[i];
				}
				i++;
				for(; i<activityOrder.length; i++) {
					sorted[i] = activityOrder[i];
				}
				activityOrder = sorted;
				repaint();
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(selecting) {
				sortSelection(selectionStart, selectionSize);
			}
			selecting = false;
			if(dragNote != null) {
				NoteData notes = TableData.getNotes();
				Calendar c = getRowCalendar(dragNotePosition.y);
				dragNote.setDayCol(c, dragNotePosition.x);
				Note oldNote = notes.getNoteAt(c, dragNotePosition.x);
				if(oldNote!=null) {
					switch(OptionPane.showMessageDialog(
							"The note has been dragged over another. "
									+ "Do you want to replace the existing node in this cell or to merge these two notes?",
							"Merge notes", OptionPane.QUESTION_ICON, new String[] {"Merge",
									"Replace", "Cancel"})) {
						case 0:
							oldNote.text += " " + dragNote.text;
							notes.update(oldNote);
							break;
						case 1:
							notes.remove(oldNote);
							notes.add(dragNote);
							break;
						default:
							notes.add(dragNote);
							break;
					}
				}
				else {
					notes.add(dragNote);
				}
				dragNote = null;
				getRootPane().repaint();
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(selecting) {
			Point loc = snapToScreen(pointToCell(e.getPoint()));
			selectionSize.setLocation(loc.x - selectionStart.x, loc.y - selectionStart.y);
			repaint();
		}
		else if(dragNote != null) {
			dragNotePosition = snapToScreen(pointToCell(e.getPoint()));
			repaint();
		}
		else if(parent.getMode()==DailyTable.MODE_OBSERVE
				|| parent.getMode()==DailyTable.MODE_EDIT_NOTES) {
			Note prevNote = showingNote;
			Point loc = pointToCell(e.getPoint());
			if(isOnScreen(loc)) {
				showingNote = TableData.getNotes().getNoteAt(startDate, loc.y, loc.x);
				if(showingNote!=null)
					showingNotePoint = loc;
			} else
				showingNote = null;
			if(showingNote != prevNote) {
				repaint();
			}
		}
	}
	
	public void moveStartDate(int delta) {
		startDate.add(Calendar.DAY_OF_MONTH, delta);
		if(selectionStart != null) {
			selectionStart.y -= delta;
		}
		showingNote = null;
		repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		moveStartDate(e.getUnitsToScroll());
	}
	
	public void moveStartCol(int delta) {
		startCol += delta;
		if(startCol<0) {
			startCol = 0;
		}
		else if(startCol>=getMaxGridCols()*HOUR_COLS) {
			startCol = getMaxGridCols()*HOUR_COLS-1;
		}
		showingNote = null;
		repaint();
	}
}
