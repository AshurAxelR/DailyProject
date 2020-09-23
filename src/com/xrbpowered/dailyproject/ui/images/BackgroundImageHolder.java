package com.xrbpowered.dailyproject.ui.images;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.xrbpowered.dailyproject.ui.table.DailyTableGrid;

public class BackgroundImageHolder implements ActivityImageHolder {

	private BufferedImage[] images = null;

	public BackgroundImageHolder(BufferedImage image) {
		images = new BufferedImage[NUM_IMAGES];
		images[FULL] = image;
		DailyTableGrid.splitActivityImage(images);
	}
	
	public boolean isNull() {
		return images==null;
	}

	public Color getColor() {
		return Color.WHITE;
	}
	
	public BufferedImage getImage(int type) {
		if(images==null)
			return null;
		else
			return images[type];
	}

}
