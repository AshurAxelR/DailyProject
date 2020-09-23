package com.xrbpowered.dailyproject.ui.dialogs;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import com.xrbpowered.dailyproject.ui.RenderUtils;


public class StatusPane extends JPanel {
	
	private RenderableContext context = null;
	
	public void setContext(RenderableContext context) {
		this.context = context;
		context.component = this;
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), RenderUtils.GRAY240));
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setColor(RenderUtils.GRAY240);
		g2.drawLine(0, 0, getWidth(), 0);
		
		if(context!=null)
			context.paint(g2);
	}

}
