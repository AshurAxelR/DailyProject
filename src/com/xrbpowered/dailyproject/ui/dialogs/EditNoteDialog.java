package com.xrbpowered.dailyproject.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.xrbpowered.dailyproject.data.log.Note;
import com.xrbpowered.dailyproject.ui.Button;
import com.xrbpowered.dailyproject.ui.RenderUtils;



public class EditNoteDialog extends JDialog {
	
	private Note sourceNote, targetNote;
	
	private EditNoteDialog(Note note, final boolean canRemove) {
		sourceNote = note;
		targetNote = note.copyNote();
		setTitle("Edit note");
		JPanel cp = new JPanel(new BorderLayout());
		setBackground(RenderUtils.GRAY240);
		
		GridBagConstraints c;

		JLabel labelTime = new JLabel(note.formatTimeStamp());
		labelTime.setFont(RenderUtils.FONT11);
		labelTime.setForeground(Color.GRAY);
		labelTime.setBackground(Color.WHITE);
		labelTime.setOpaque(true);
		labelTime.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
		labelTime.setPreferredSize(new Dimension(280, 16));
		cp.add(labelTime, BorderLayout.NORTH);

		final JTextField edit = new JTextField();
		edit.setFont(RenderUtils.FONT11);
		edit.setText(targetNote.text);
		edit.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 0, 0, 0, RenderUtils.GRAY224),
				BorderFactory.createEmptyBorder(4, 4, 4, 4)));
		edit.setPreferredSize(new Dimension(280, 20));
		cp.add(edit, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(RenderUtils.GRAY224);
				g2.drawLine(0, 0, getWidth(), 0);
			}
		};
		buttonPanel.setBackground(RenderUtils.GRAY240);
		buttonPanel.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.weightx = 1;
		c.insets.top = 4;
		c.insets.bottom = 4;
		c.insets.left = 4;
		c.insets.right = 1;
		c.anchor = GridBagConstraints.EAST;
		Button btnAccept = new Button("Accept");
		btnAccept.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				targetNote.text = edit.getText();
				dispose();
			}
		});
		buttonPanel.add(btnAccept, c);
		getRootPane().setDefaultButton(btnAccept);
		
		c.insets.left = 0;
		c.insets.right = 0;
		c.weightx = 0;
		c.gridx = 1;
		Button btnRemove = new Button("Remove");
		btnRemove.setEnabled(canRemove);
		btnRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				targetNote = null;
				dispose();
			}
		});
		buttonPanel.add(btnRemove, c);
		c.insets.left = 0;
		c.insets.right = 4;
		c.weightx = 0;
		c.gridx = 2;
		final Button btnCancel = new Button("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				targetNote = canRemove?sourceNote:null;
				dispose();
			}
		});
		buttonPanel.add(btnCancel, c);
		cp.add(buttonPanel, BorderLayout.SOUTH);
		
		setContentPane(cp);
		pack();
		setResizable(false);
		getRootPane().registerKeyboardAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnCancel.doClick();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	public static Note show(Point position, Note note, boolean canRemove) {
		EditNoteDialog dlg = new EditNoteDialog(note, canRemove);
		dlg.setModal(true);
		if(position!=null) {
			position.translate(-dlg.getWidth()/2, -dlg.getHeight()/2);
			if(position.x<0)
				position.x = 0;
			if(position.y<0)
				position.y = 0;
			Dimension screen = dlg.getToolkit().getScreenSize();
			if(position.x+dlg.getWidth()>screen.width)
				position.x = screen.width-dlg.getWidth();
			if(position.y+dlg.getHeight()>screen.height)
				position.y = screen.height-dlg.getHeight();
			dlg.setLocation(position);
		}
		dlg.setVisible(true);
		return dlg.targetNote;
	}
}
