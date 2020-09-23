package com.xrbpowered.dailyproject.data.activities;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.w3c.dom.Element;

import com.xrbpowered.dailyproject.ui.images.ActivityImageHolder;
import com.xrbpowered.dailyproject.ui.table.DailyTableGrid;


public class ActivityGroup implements ActivityImageHolder {
	private String caption;
	private Color color;
	private BufferedImage[] images = null;
	
	public int index = -1;

	public ActivityGroup(Element el) throws IOException {
		caption = el.getAttribute("caption");
		color = new Color(Integer.parseInt(el.getAttribute("color"), 16));
		BufferedImage image = DailyTableGrid.createColorisedActivityImage(color);
		if(image!=null) {
			images = new BufferedImage[NUM_IMAGES];
			images[FULL] = image;
			DailyTableGrid.splitActivityImage(images);
		}
	}

	public String getCaption() {
		return caption;
	}

	public Color getColor() {
		return color;
	}
	
	public BufferedImage getImage(int type) {
		if(images==null)
			return null;
		else
			return images[type];
	}
	
	public boolean isNull() {
		return images==null;
	}
}
