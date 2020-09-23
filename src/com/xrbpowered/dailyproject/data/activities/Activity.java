package com.xrbpowered.dailyproject.data.activities;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.w3c.dom.Element;

import com.xrbpowered.dailyproject.data.InvalidFormatException;
import com.xrbpowered.dailyproject.ui.images.ActivityImageHolder;
import com.xrbpowered.dailyproject.ui.table.DailyTableGrid;



public class Activity implements ActivityImageHolder {
	private String caption = null;
	private Color color = null;
	private BufferedImage[] images = null;
	private boolean inactive = false;
	
	public String id = null;
	public ActivityGroup group = null;
	
	public int index = -1;
	
	private static Activity nullActivity = new Activity();
	
	private Activity() {
	}
	
	public Activity(Element el) throws IOException, InvalidFormatException {
		id = el.getAttribute("id");
		if(id==null || id.isEmpty())
			throw new InvalidFormatException();
		caption = el.getAttribute("caption");
		color = new Color(Integer.parseInt(el.getAttribute("color"), 16));
		BufferedImage image = DailyTableGrid.createColorisedActivityImage(color);
		if(image!=null) {
			images = new BufferedImage[NUM_IMAGES];
			images[FULL] = image;
			DailyTableGrid.splitActivityImage(images);
		}
		inactive = el.getAttribute("inactive").equals("1");
	}
	
	public boolean isNull() {
		return images==null;
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

	public boolean isInactive() {
		return inactive;
	}
	
	public static Activity nullActivity() {
		return nullActivity;
	}

	
}
