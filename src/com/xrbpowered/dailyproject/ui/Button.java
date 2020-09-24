package com.xrbpowered.dailyproject.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;


public class Button extends JButton implements MouseListener {

	private String caption = null;
	private boolean isMouseOver = false;
	private boolean isButtonDown = false;
	
	public Button(String caption) {
		this.caption = caption;
		setPreferredSize(new Dimension(80, 23));
		addMouseListener(this);
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		if(isButtonDown)
			g2.setPaint(new GradientPaint(0, 0, RenderUtils.GRAY216, 0, getHeight(), RenderUtils.GRAY240));
		else
			g2.setPaint(new GradientPaint(0, 0, RenderUtils.GRAY240, 0, getHeight(), RenderUtils.GRAY224));
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setColor(isMouseOver?RenderUtils.GRAY144:RenderUtils.GRAY192);
		g2.drawRect(1, 1, getWidth()-3, getHeight()-3);
		
		if(caption!=null) {
			g2.setFont(RenderUtils.FONT11BOLD);
			g2.setColor(isEnabled()?Color.BLACK:Color.LIGHT_GRAY);
			g2.drawString(caption, getWidth()-g2.getFontMetrics().stringWidth(caption)-10, (getHeight()-11)/2+10);
		}
		
		if(isSelected()) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(isEnabled()?Color.BLACK:Color.LIGHT_GRAY);
			int cy = getHeight()/2+1;
			g2.fillPolygon(new int[] {2, 8, 2}, new int[] {cy-5, cy, cy+5}, 3);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		isMouseOver = true;
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
	
}
