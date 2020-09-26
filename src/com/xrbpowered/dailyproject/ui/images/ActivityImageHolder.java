package com.xrbpowered.dailyproject.ui.images;

import java.awt.Color;
import java.awt.image.BufferedImage;

public interface ActivityImageHolder {
	public static final int FULL = 0;
	public static final int START = 1;
	public static final int START_HOUR = 2;
	public static final int LEFT = 3;
	public static final int END = 4;
	public static final int END_HOUR = 5;
	public static final int RIGHT = 6;
	
	public static final int NUM_IMAGES = 7;
	
	public BufferedImage getImage(int type);
	public Color getColor();
	//public boolean isNull();
}
