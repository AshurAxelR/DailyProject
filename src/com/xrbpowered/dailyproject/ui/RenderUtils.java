package com.xrbpowered.dailyproject.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import javax.imageio.ImageIO;

import com.xrbpowered.dailyproject.ui.images.ActivityImageHolder;



public abstract class RenderUtils {
	
	private static final String IMAGE_PACKAGE = "com/xrbpowered/dailyproject/images";
	
	public static final Color GRAY144 = new Color(144, 144, 144);
	public static final Color GRAY160 = new Color(160, 160, 160);
	public static final Color GRAY192 = new Color(192, 192, 192);
	public static final Color GRAY208 = new Color(208, 208, 208);
	public static final Color GRAY216 = new Color(216, 216, 216);
	public static final Color GRAY224 = new Color(224, 224, 224);
	public static final Color GRAY240 = new Color(240, 240, 240);
	public static final Color GRAY248 = new Color(248, 248, 248);
	
	public static final Color LIGHT_RED192 = new Color(255, 192, 192);
	public static final Color LIGHT_RED240 = new Color(255, 240, 240);

	public static Font FONT10 = null;
	public static Font FONT11 = null;
	public static Font FONT11BOLD = null;
	
	public static final String[] QUARTERS = {"", "\u00bc", "\u00bd", "\u00be"};
	
	public static void initFonts() {
		FONT10 = /*UIManager.getFont("Button.font").deriveFont(Font.PLAIN, 10);*/ new Font("Tahoma", Font.PLAIN, 10);
		FONT11 = FONT10.deriveFont(11.0f);
		FONT11BOLD = FONT11.deriveFont(Font.BOLD);
	}
	
	public static String formatTimeStamp(int day, int month, int hour, int minute) {
		return String.format("%02d %s, %02d:%02d",
				day, RichInfoPane.MONTH_NAMES[month], hour, minute);
	}
	
	public static String formatDuration(int value) {
		if(value>0 && value<4)
			return QUARTERS[value];
		return String.format("%d%s", value/4, QUARTERS[value%4]);
	}
	
	public static void renderMultilineText(Graphics2D g2, String text, int x, int y, int width) {
		AttributedString attributedString = new AttributedString(text);
		attributedString.addAttribute(TextAttribute.FONT, g2.getFont());
		attributedString.addAttribute(TextAttribute.FOREGROUND, g2.getColor());

		AttributedCharacterIterator characterIterator = attributedString.getIterator();
		FontRenderContext fontRenderContext = g2.getFontRenderContext();
		LineBreakMeasurer measurer = new LineBreakMeasurer(characterIterator, fontRenderContext);
		while(measurer.getPosition() < characterIterator.getEndIndex()) {
			TextLayout textLayout = measurer.nextLayout(width);
			y += textLayout.getAscent();
			textLayout.draw(g2, x, y);
			y += textLayout.getDescent() + textLayout.getLeading();
		}
	}
	
	public static BufferedImage cutImage(Image img, int x, int y, int w, int h) {
		BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		out.getGraphics().drawImage(img, 0, 0, w, h, x, y, x+w, y+h, null);
		return out;
	}
	
	public static void renderActivityThumb(Graphics2D g2, ActivityImageHolder activity, int x, int y) {
		if(activity==null)
			return;
		g2.drawImage(activity.getImage(ActivityImageHolder.FULL), x, y, x+15, y+16, 0, 0, 15, 16, null);
		g2.drawImage(activity.getImage(ActivityImageHolder.FULL), x+15, y, x+20, y+16, 75, 0, 80, 16, null);
	}

	public static void renderLongActivityThumb(Graphics2D g2, ActivityImageHolder activity, int x, int y) {
		if(activity==null)
			return;
		g2.drawImage(activity.getImage(ActivityImageHolder.FULL), x, y, x+15, y+16, 0, 0, 15, 16, null);
		g2.drawImage(activity.getImage(ActivityImageHolder.FULL), x+15, y, x+40, y+16, 55, 0, 80, 16, null);
	}
	
	public static BufferedImage loadImage(String name) throws IOException {
		URL res = ClassLoader.getSystemResource(IMAGE_PACKAGE+"/"+name);
		if(res==null) {
			throw new IOException("Resource not found: "+name);
		}
		return ImageIO.read(res);
	}
}
