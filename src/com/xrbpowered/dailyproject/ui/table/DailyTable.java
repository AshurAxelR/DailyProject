package com.xrbpowered.dailyproject.ui.table;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import com.xrbpowered.dailyproject.DailyProject;
import com.xrbpowered.dailyproject.data.activities.ActivityList;
import com.xrbpowered.dailyproject.ui.Button;
import com.xrbpowered.dailyproject.ui.RenderUtils;

public class DailyTable extends JPanel {

	public static final int MODE_OBSERVE = 0;
	public static final int MODE_STATS = 1;
	public static final int MODE_EDIT_ACTIVITIES = 2;
	public static final int MODE_EDIT_NOTES = 3;
	public static final int MODE_FINANCE = 4;
	
	private Button btnObserve;
	private Button btnHome;
	private Button btnStats;
	private Button btnSummary;
	private Button btnActivities;
	private Button btnNotes;
	
	private int mode;
	public boolean statsSummary = false;
	
	private DailyProject frame;
	private DailyTableGrid table;
	
	public DailyTable(final DailyProject frame, ActivityList activityList) throws Exception {
		this.frame = frame;
		setLayout(new BorderLayout());
		table = new DailyTableGrid(this, activityList); 
		add(table, BorderLayout.CENTER);
		
		JPanel toolPane = new JPanel() {
			@Override
			public void paint(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(RenderUtils.GRAY240);
				g2.fillRect(0, 0, getWidth(), getHeight());
				g2.setColor(RenderUtils.GRAY224);
				g2.drawLine(0, 0, 0, getHeight());
				paintChildren(g);
			}
		};
		toolPane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		int y = 0;
		c.gridy = y++;
		c.insets.left = 4;
		c.insets.right = 4;
		c.insets.top = 32;
		btnObserve = new Button("Observe");
		btnObserve.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setMode(MODE_OBSERVE);
			}
		});
		toolPane.add(btnObserve, c);
		
		c.gridy = y++;
		c.insets.top = 0;
		btnHome = new Button("Today");
		btnHome.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				table.scrollToToday();
			}
		});
		toolPane.add(btnHome, c);
		
		c.gridy = y++;
		c.insets.top = 8;
		btnStats = new Button("Statistics");
		btnStats.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setMode(MODE_STATS);
			}
		});
		toolPane.add(btnStats, c);
		
		c.gridy = y++;
		c.insets.top = 0;
		btnSummary = new Button("Summary");
		btnSummary.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				statsSummary = !statsSummary;
				btnSummary.setSelected(statsSummary);
				repaint();
			}
		});
		toolPane.add(btnSummary, c);

		c.gridy = y++;
		c.insets.top = 8;
		btnActivities = new Button("Activities");
		btnActivities.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setMode(MODE_EDIT_ACTIVITIES);
			}
		});
		toolPane.add(btnActivities, c);

		c.gridy = y++;
		c.insets.top = 0;
		btnNotes = new Button("Notes");
		btnNotes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setMode(MODE_EDIT_NOTES);
			}
		});
		toolPane.add(btnNotes, c);
		
		c.gridy = y++;
		c.insets.top = 0;
		c.insets.bottom = 0;
		c.weighty = 1;
		c.fill = GridBagConstraints.VERTICAL;
		JPanel dummy = new JPanel();
		dummy.setBackground(RenderUtils.GRAY240);
		toolPane.add(dummy, c);
		
		add(toolPane, BorderLayout.EAST);
		setMode(MODE_OBSERVE);
		
		new KeyboardController(this);
	}
	
	public int getMode() {
		return mode;
	}
	
	public void setMode(int mode) {
		this.mode = mode;
		btnObserve.setSelected(mode==MODE_OBSERVE);
		btnStats.setSelected(mode==MODE_STATS);
		btnActivities.setSelected(mode==MODE_EDIT_ACTIVITIES);
		btnNotes.setSelected(mode==MODE_EDIT_NOTES);
		btnSummary.setEnabled(mode==MODE_STATS);
		table.deselect();
		frame.repaint();
	}
	
	public int getMaxTableWidth() {
		return table.getMaxGridWidth()+90;
	}
	
	public DailyTableGrid getTable() {
		return table;
	}
}
