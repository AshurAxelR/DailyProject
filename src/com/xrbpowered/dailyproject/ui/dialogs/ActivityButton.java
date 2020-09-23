package com.xrbpowered.dailyproject.ui.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

import com.xrbpowered.dailyproject.data.activities.Activity;
import com.xrbpowered.dailyproject.ui.RenderUtils;

public class ActivityButton extends JButton implements MouseListener {

	private Activity activity = null;
	private boolean isMouseOver = false;
	private boolean isButtonDown = false;
	
	public ActivityButton(Activity activity) {
		this.activity = activity;
		setPreferredSize(new Dimension(23, 18));
		addMouseListener(this);
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		if(isButtonDown)
			g2.setColor(RenderUtils.GRAY160);
		else
			g2.setColor(isMouseOver?RenderUtils.GRAY192:RenderUtils.GRAY240);
		g2.fillRect(0, 0, getWidth(), getHeight());
		
		RenderUtils.renderActivityThumb(g2, activity, 1, 1);
	}

	public Activity getActivity() {
		return activity;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		isMouseOver = true;
		Context.activity = activity;
		Context.getInstance().repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		isMouseOver = false;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		isButtonDown = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isButtonDown = false;
	}
	
	public static class Context extends RenderableContext {
		public static Activity activity = null;
		
		private static Context inst = null;
		
		public static Context getInstance() {
			if(inst==null)
				inst = new Context();
			return inst;
		}
		
		@Override
		public void paint(Graphics2D g2) {
			if(activity==null || activity.isNull())
				return;
			RenderUtils.renderActivityThumb(g2, activity, 18, 8);
			
			g2.setFont(RenderUtils.FONT11);
			g2.setColor(Color.LIGHT_GRAY);
			g2.drawString("Category: "+activity.group.getCaption(), 48, 16);
			g2.setColor(Color.BLACK);
			RenderUtils.renderMultilineText(g2, activity.getCaption(), 48, 19, component.getWidth()-64);
		}
		
	}
}
