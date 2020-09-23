package com.xrbpowered.dailyproject.ui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.xrbpowered.dailyproject.DailyProject;
import com.xrbpowered.dailyproject.data.log.TableData;

public class ClockThread extends Thread {
	private DailyProject frame;
	
	public ClockThread(DailyProject frame) {
		this.frame = frame;
	}
	
	@Override
	public void run() {
		try {
			for(;;) {
				sleep(30000);
				synchronized(TableData.getInstance()) {
					if((frame.getExtendedState() & JFrame.ICONIFIED) == 0) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								frame.repaint();
							}
						});
					}
					TableData.getInstance().save(false);
				}
			}
		}
		catch (InterruptedException e) {
		}
	}
}
