package com.xrbpowered.dailyproject.data.activities;

import java.io.IOException;

import org.w3c.dom.Element;

public class ActivityGroup {
	private String caption;
	
	public ActivityGroup(Element el) throws IOException {
		caption = el.getAttribute("caption");
	}

	public String getCaption() {
		return caption;
	}
}
