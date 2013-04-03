package org.vinsert.bot.util;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class SettingsDialog extends JDialog {
	
	private String[] themes = { "System", "Graphite" };
	private JComboBox themeSelect;

	public SettingsDialog(JFrame window) {
		super(window, "Settings", true);
		this.setSize(new Dimension(200, 150));
		this.setResizable(false);
		this.setLocationRelativeTo(window);
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		
		JLabel themeLabel = new JLabel("Theme:");
		themeSelect = new JComboBox(themes);
		themeSelect.setMaximumSize(new Dimension(themeSelect.getPreferredSize().width + 50, themeSelect.getPreferredSize().height));
		themeSelect.setSelectedItem(Settings.get("theme"));
		themeSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Settings.set("theme", ((String)themeSelect.getSelectedItem()));
			}
		});
		
		JLabel scriptDirLabel = new JLabel("Script Directory:");
		JButton browseButton = new JButton("Browse");
		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser dir = new JFileChooser(Settings.get("script_dir"));
				dir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int val = dir.showOpenDialog(SettingsDialog.this);
				if(val == JFileChooser.APPROVE_OPTION) {
					Settings.set("script_dir", dir.getSelectedFile().getAbsolutePath());
				}
			}
		});
		
		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Settings.save();
				SettingsDialog.this.setVisible(false);
				SettingsDialog.this.dispose();
			}
		});
		JButton close = new JButton("Cancel");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SettingsDialog.this.setVisible(false);
				SettingsDialog.this.dispose();
			}
		});
		
		JPanel row1 = new JPanel();
		row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));
		row1.add(Box.createHorizontalStrut(10));
		row1.add(themeLabel);
		row1.add(Box.createHorizontalGlue());
		row1.add(themeSelect);
		row1.add(Box.createHorizontalStrut(10));
		
		JPanel row2 = new JPanel();
		row2.setLayout(new BoxLayout(row2, BoxLayout.X_AXIS));
		row2.add(Box.createHorizontalStrut(10));
		row2.add(scriptDirLabel);
		row2.add(Box.createHorizontalGlue());
		row2.add(browseButton);
		row2.add(Box.createHorizontalStrut(10));
		
		JPanel row3 = new JPanel();
		row3.setLayout(new BoxLayout(row3, BoxLayout.X_AXIS));
		row3.add(save);
		row3.add(Box.createHorizontalStrut(5));
		row3.add(close);
		
		this.add(Box.createVerticalStrut(5));
		this.add(row1);
		this.add(Box.createVerticalStrut(5));
		this.add(row2);
		this.add(Box.createVerticalGlue());
		this.add(row3);
	}
}
