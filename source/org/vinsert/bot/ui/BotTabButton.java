package org.vinsert.bot.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.vinsert.Configuration;
import org.vinsert.bot.ui.BotTabPane.Tab;
public class BotTabButton extends JPanel implements ActionListener, MouseListener{
	

	private static ImageIcon icon = Configuration.icon("res/icon_tab_small.png");
	private static Image image = icon.getImage();
	private Tab tab; 
	private BotTabPane pane;
	
	private JPopupMenu closeMenu = new JPopupMenu();
	private JMenuItem closeItem = new JMenuItem("Close");
	
	public BotTabButton(BotTabPane pane, Tab tab) {
		this.tab = tab;
		this.pane = pane;
		this.setLayout(null);
		this.setPreferredSize(new Dimension(84, 32));
		this.setMaximumSize(this.getPreferredSize());
		this.setBounds(0, 0, 84, 24);
		this.setFocusable(false);		
		closeItem.addActionListener(this);
		closeMenu.add(closeItem);
		this.addMouseListener(this);
	}
	
	public void paintComponent(Graphics g) {
		g.setColor(Color.gray);
		g.drawImage(image, 15, 3, null);
		g.drawString("Bot " + (tab.getIndex()+1), 40, 15);
	}
	
	private void checkForTriggerEvent(MouseEvent e) {
		if (e.isPopupTrigger()) {
			closeMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e)) {
			pane.updateTabs(tab);
		} else if(SwingUtilities.isRightMouseButton(e)) {
			closeMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		pane.closeTab(tab);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}
	
	@Override
	public void mouseReleased(MouseEvent arg0) {}
	
	
}