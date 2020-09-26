package com.xrbpowered.dailyproject.ui.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import com.xrbpowered.dailyproject.data.log.DayData;

public class KeyboardController {

	private void addAction(final DailyTable table, int keyCode, int keyMods, ActionListener action) {
		table.registerKeyboardAction(action,
				KeyStroke.getKeyStroke(keyCode, keyMods),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
	}
	
	@SuppressWarnings("serial")
	public KeyboardController(final DailyTable table) {
		final DailyTableGrid tableGrid = table.getTable();
		
		addAction(table, KeyEvent.VK_ESCAPE, 0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.setMode(DailyTable.MODE_OBSERVE);
			}
		});
		
		addAction(table, KeyEvent.VK_F1, 0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableGrid.scrollToToday();
			}
		});

		addAction(table, KeyEvent.VK_F2, 0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.setMode(DailyTable.MODE_STATS);
			}
		});

		addAction(table, KeyEvent.VK_F3, 0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.toggleSummary();
			}
		});

		addAction(table, KeyEvent.VK_F4, 0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.setMode(DailyTable.MODE_EDIT_ACTIVITIES);
			}
		});

		addAction(table, KeyEvent.VK_F5, 0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.setMode(DailyTable.MODE_EDIT_NOTES);
			}
		});

		addAction(table, KeyEvent.VK_HOME, 0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableGrid.resetStartCol();
			}
		});

		addAction(table, KeyEvent.VK_LEFT, 0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableGrid.moveStartCol(-DayData.HOUR_COLS);
			}
		});
	
		addAction(table, KeyEvent.VK_RIGHT, 0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableGrid.moveStartCol(DayData.HOUR_COLS);
			}
		});
		
		addAction(table, KeyEvent.VK_UP, 0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableGrid.moveStartDate(-1);
			}
		});

		addAction(table, KeyEvent.VK_DOWN, 0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableGrid.moveStartDate(1);
			}
		});
		
		addAction(table, KeyEvent.VK_PAGE_UP, 0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableGrid.moveStartDate(-tableGrid.getMaxGridRows()+7);
			}
		});
	
		addAction(table, KeyEvent.VK_PAGE_DOWN, 0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableGrid.moveStartDate(tableGrid.getMaxGridRows()-7);
			}
		});
		
		addAction(table, KeyEvent.VK_PAGE_UP, KeyEvent.CTRL_DOWN_MASK, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableGrid.moveStartDate(-365);
			}
		});
	
		addAction(table, KeyEvent.VK_PAGE_DOWN, KeyEvent.CTRL_DOWN_MASK, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableGrid.moveStartDate(365);
			}
		});
	}
}
