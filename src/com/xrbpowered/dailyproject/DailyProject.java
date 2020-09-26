package com.xrbpowered.dailyproject;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.xrbpowered.dailyproject.data.activities.ActivityList;
import com.xrbpowered.dailyproject.data.log.TableData;
import com.xrbpowered.dailyproject.ui.ClockThread;
import com.xrbpowered.dailyproject.ui.RenderUtils;
import com.xrbpowered.dailyproject.ui.RichInfoPane;
import com.xrbpowered.dailyproject.ui.dialogs.OptionPane;
import com.xrbpowered.dailyproject.ui.table.DailyTable;



public class DailyProject extends JFrame {
	private static final String TITLE = "Daily";
	
	public static final int DEFAULT_INFO_PANEL_WIDTH = 250;
	public static final int DEFAULT_STATUS_BAR_WIDTH = DEFAULT_INFO_PANEL_WIDTH+200;
	
	private static DailyProject frame = null;
	
	private ClockThread clock;
	private RichInfoPane rpane;
	
	public void quit() {
		clock.interrupt();
		if(TableData.getInstance().save(true))
			System.exit(0);
	}
	
	public DailyProject() throws Exception {
		super(TITLE);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		

		final DailyTable table = new DailyTable(this, ActivityList.getInstance());
		rpane = new RichInfoPane();

		JPanel cp = new JPanel() {
			@Override
			public void doLayout() {
				int w = table.getMaxTableWidth();
				int h = getHeight()-1;
				if(!rpane.isVisible()) {
					table.setBounds(0, 1, getWidth(), h);
				}
				else if(getWidth()<w+DEFAULT_INFO_PANEL_WIDTH) {
					table.setBounds(0, 0, getWidth()-DEFAULT_INFO_PANEL_WIDTH, h);
					rpane.setBounds(getWidth()-DEFAULT_INFO_PANEL_WIDTH, 0, DEFAULT_INFO_PANEL_WIDTH, h);
				}
				else {
					table.setBounds(0, 1, w, h);
					rpane.setBounds(w, 1, getWidth()-w, h);
				}
			}
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				g.setColor(RenderUtils.GRAY192);
				((Graphics2D) g).drawLine(0, 0, getWidth(), 0);
			}
		};
		
		cp.add(table, BorderLayout.CENTER);
		cp.add(rpane, BorderLayout.EAST);
		
		setContentPane(cp);
		GraphicsEnvironment genv=GraphicsEnvironment.getLocalGraphicsEnvironment();
		setBounds(genv.getMaximumWindowBounds());
		
		(clock = new ClockThread(this)).start();
	}
	
	public void setInfoPaneVisible(boolean visible) {
		rpane.setVisible(visible);
		validate();
	}
	
	public boolean isInfoPaneVisible() {
		return rpane.isVisible();
	}
	
	public static void main(String[] args) {
		if(args.length>0) {
			boolean ui = true;
			for(int i=0; i<args.length; i++) {
				switch(args[i]) {
					case "-migratexml":
						TableData.migrateXml();
						ui = false;
						break;
				}
			}
			if(!ui) return;
		}
		
		System.out.println("Starting UI...");
		try {
			frame = new DailyProject();
			RenderUtils.initFonts();
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			frame.setVisible(true);
		} catch(Exception e) {
			e.printStackTrace();
			OptionPane.showMessageDialog("Application failed to initialise"+((e.getMessage()==null)?
					".":" with the following error message: "+e.getMessage()), "Initialisation error", OptionPane.ERROR_ICON,
					new String[] {"Close"});
		}
	}
}
