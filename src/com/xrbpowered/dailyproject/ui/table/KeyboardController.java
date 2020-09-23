package com.xrbpowered.dailyproject.ui.table;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class KeyboardController {
	private static final String SCROLL_UP = "UP";
	private static final String SCROLL_DOWN = "DOWN";
	private static final String SCROLL_LEFT = "LEFT";
	private static final String SCROLL_RIGHT = "RIGHT";

	private static final String MODE_OBSERVE = "control 1";
	private static final String MODE_STATS = "control 2";
	private static final String MODE_EDIT_ACTIVITIES = "control 3";
	private static final String MODE_EDIT_NOTES = "control 4";

	@SuppressWarnings("serial")
	public KeyboardController(final DailyTable table) {
		final DailyTableGrid tableGrid = table.getTable();
		
		InputMap keys = tableGrid.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actions = tableGrid.getActionMap();
		
		keys.put(KeyStroke.getKeyStroke(SCROLL_UP), SCROLL_UP);
		actions.put(SCROLL_UP, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableGrid.moveStartDate(-1);
			}
		});
		keys.put(KeyStroke.getKeyStroke(SCROLL_DOWN), SCROLL_DOWN);
		actions.put(SCROLL_DOWN, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableGrid.moveStartDate(1);
			}
		});
		keys.put(KeyStroke.getKeyStroke(SCROLL_LEFT), SCROLL_LEFT);
		actions.put(SCROLL_LEFT, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableGrid.moveStartCol(-DailyTableGrid.HOUR_COLS);
			}
		});
		keys.put(KeyStroke.getKeyStroke(SCROLL_RIGHT), SCROLL_RIGHT);
		actions.put(SCROLL_RIGHT, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableGrid.moveStartCol(DailyTableGrid.HOUR_COLS);
			}
		});

		keys.put(KeyStroke.getKeyStroke(MODE_OBSERVE), MODE_OBSERVE);
		actions.put(MODE_OBSERVE, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.setMode(DailyTable.MODE_OBSERVE);
			}
		});
		keys.put(KeyStroke.getKeyStroke(MODE_STATS), MODE_STATS);
		actions.put(MODE_STATS, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.setMode(DailyTable.MODE_STATS);
			}
		});
		keys.put(KeyStroke.getKeyStroke(MODE_EDIT_ACTIVITIES), MODE_EDIT_ACTIVITIES);
		actions.put(MODE_EDIT_ACTIVITIES, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.setMode(DailyTable.MODE_EDIT_ACTIVITIES);
			}
		});
		keys.put(KeyStroke.getKeyStroke(MODE_EDIT_NOTES), MODE_EDIT_NOTES);
		actions.put(MODE_EDIT_NOTES, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.setMode(DailyTable.MODE_EDIT_NOTES);
			}
		});
	}
}
