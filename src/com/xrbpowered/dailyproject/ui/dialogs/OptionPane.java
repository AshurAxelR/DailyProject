package com.xrbpowered.dailyproject.ui.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.xrbpowered.dailyproject.ui.Button;
import com.xrbpowered.dailyproject.ui.RenderUtils;

public class OptionPane extends JPanel {

	private static final String[] ICON_PATHS = {"err.bmp", "wrn.bmp", "inf.bmp", "ask.bmp"};

	public static final int NO_ICON = 0;
	public static final int ERROR_ICON = 1;
	public static final int WARNING_ICON = 2;
	public static final int INFO_ICON = 3;
	public static final int QUESTION_ICON = 4;

	private int modalResult = -1;
	
	private OptionPane(final JDialog dlg, final String msg, final int icon, final String[] buttons) {
		super(new GridBagLayout());
		setBackground(RenderUtils.GRAY240);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = buttons.length+1;
		c.weightx = 1;
		c.weighty = 1;
		JPanel msgPanel = new JPanel() {

			@Override
			public void paint(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
				g2.setColor(RenderUtils.GRAY248);
				g2.fillRect(0, 0, getWidth(), getHeight());
				g2.setColor(RenderUtils.GRAY224);
				g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
				
				int dx = 48;
				if(icon>NO_ICON) {
					BufferedImage img;
					try {
						img = RenderUtils.loadImage(ICON_PATHS[icon-1]);
						g2.drawImage(img, 12, 8, null);
					}
					catch(IOException e) {
						dx = 12;
					}
				}
				else
					dx = 12;
				
				g2.setFont(RenderUtils.FONT11);
				g2.setColor(Color.BLACK);
				RenderUtils.renderMultilineText(g2, msg, dx, 8, getWidth()-dx-12);
			}
		};
		msgPanel.setPreferredSize(new Dimension(Math.max(250, buttons.length*81+40), 60));
		add(msgPanel, c);

		c.gridwidth = 1;
		c.gridy = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets.top = 4;
		c.insets.bottom = 4;
		c.insets.right = 4;
		c.insets.left = 4;
		c.anchor = GridBagConstraints.EAST;
		
		add(new JPanel(), c);

		c.weightx = 0;
		c.insets.right = 1;
		c.insets.left = 0;

		for(int i=0; i<buttons.length; i++) {
			c.gridx = i+1;
			if(i==buttons.length-1) c.insets.right = 4;
			Button btn = new Button(buttons[i]);
			final int res = i;
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					modalResult = res;
					dlg.dispose();
				}
			});
			add(btn, c);
		}
	}
	
	public static int showMessageDialog(String msg, String title, int icon, String[] buttons) {
		JDialog dlg = new JDialog();
		dlg.setTitle(title);
		OptionPane dlgPane = new OptionPane(dlg, msg, icon, buttons);
		dlg.setContentPane(dlgPane);
		dlg.pack();
		dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dlg.setModal(true);
		dlg.setResizable(false);
		dlg.setLocationRelativeTo(null);
		dlg.setVisible(true);
		return dlgPane.modalResult;
	}
}
