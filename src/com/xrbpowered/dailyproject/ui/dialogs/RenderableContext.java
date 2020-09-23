package com.xrbpowered.dailyproject.ui.dialogs;

import java.awt.Component;
import java.awt.Graphics2D;

public abstract class RenderableContext {
	protected Component component;

	public void repaint() {
		if(component!=null)
			component.repaint();
	}
	
	public abstract void paint(Graphics2D g2);
}
