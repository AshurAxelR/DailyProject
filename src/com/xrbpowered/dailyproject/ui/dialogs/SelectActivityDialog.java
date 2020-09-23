package com.xrbpowered.dailyproject.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.xrbpowered.dailyproject.data.activities.Activity;
import com.xrbpowered.dailyproject.data.activities.ActivityList;
import com.xrbpowered.dailyproject.ui.Button;
import com.xrbpowered.dailyproject.ui.RenderUtils;

public class SelectActivityDialog extends JDialog {

	private Activity selectedActivity = null;
	
	private SelectActivityDialog(ActivityList activityList) {
		setTitle("Set activity");
		JPanel cp = new JPanel(new BorderLayout());
		setBackground(RenderUtils.GRAY240);
		
		StatusPane status = new StatusPane();
		status.setContext(ActivityButton.Context.getInstance());
		status.setPreferredSize(new Dimension(120, 40));

		int nRows = (activityList.activities.length+7)/8;
		
		JPanel palette = new JPanel(new GridBagLayout());
		palette.setBackground(RenderUtils.GRAY248);
		palette.setPreferredSize(new Dimension(23*8+24, 18*nRows+12));
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weightx = 0;
		c.weighty = 0;

		for(int i=0; i<activityList.activities.length; i++) {
			c.gridx = i%8;
			c.gridy = i/8;
			c.insets.top = (c.gridy==0)?8:0;
			c.insets.bottom = (c.gridy==(activityList.activities.length-1)/8)?4:0;
			c.insets.left = (c.gridx==0)?12:0;
			c.insets.right = (c.gridx==7)?12:0;
			final ActivityButton button = new ActivityButton(activityList.activities[i]);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					selectedActivity = button.getActivity();
					dispose();
				}
			});
			palette.add(button, c);
		}
		
		cp.add(palette, BorderLayout.NORTH);
		cp.add(status, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(RenderUtils.GRAY224);
				g2.drawLine(0, 0, getWidth(), 0);
			}
		};
		buttonPanel.setBackground(RenderUtils.GRAY240);
		buttonPanel.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.weightx = 1;
		c.insets.top = 4;
		c.insets.bottom = 4;
		c.insets.left = 4;
		c.insets.right = 1;
		c.anchor = GridBagConstraints.EAST;
		Button btnClear = new Button("Clear");
		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedActivity = Activity.nullActivity();
				dispose();
			}
		});
		buttonPanel.add(btnClear, c);
		c.insets.left = 0;
		c.insets.right = 4;
		c.weightx = 0;
		c.gridx = 1;
		final Button btnCancel = new Button("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedActivity = null;
				dispose();
			}
		});
		buttonPanel.add(btnCancel, c);
		cp.add(buttonPanel, BorderLayout.SOUTH);
		
		setContentPane(cp);
		pack();
		setResizable(false);
		getRootPane().registerKeyboardAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnCancel.doClick();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	
	public static Activity show(Point position, ActivityList activityList) {
		SelectActivityDialog dlg = new SelectActivityDialog(activityList);
		dlg.setModal(true);
		if(position!=null) {
			position.translate(-dlg.getWidth()/2, -dlg.getHeight()/2);
			if(position.x<0)
				position.x = 0;
			if(position.y<0)
				position.y = 0;
			Dimension screen = dlg.getToolkit().getScreenSize();
			if(position.x+dlg.getWidth()>screen.width)
				position.x = screen.width-dlg.getWidth();
			if(position.y+dlg.getHeight()>screen.height)
				position.y = screen.height-dlg.getHeight();
			dlg.setLocation(position);
		}
		dlg.setVisible(true);
		return dlg.selectedActivity;
	}
}
