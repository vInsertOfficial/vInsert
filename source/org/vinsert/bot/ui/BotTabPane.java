package org.vinsert.bot.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.vinsert.Configuration;

/**
 * A tabbedpane workaround using buttons and a content panel
 * @author tommo
 *
 */
public class BotTabPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The tab list
	 */
	private List<Tab> tabs = new LinkedList<Tab>();
	
	/**
	 * The panel toolbar
	 */
	private BotToolBar toolbar;
	
	/**
	 * The active tab
	 */
	private Tab selected;
	
	public BotTabPane(BotWindow window, boolean log) {
		setPreferredSize(new Dimension(Configuration.BOT_APPLET_WIDTH + 8, Configuration.BOT_APPLET_HEIGHT + Configuration.BOT_LOGGER_HEIGHT + 40));
		setBorder(null);
		toolbar = new BotToolBar(window, log);
		add(toolbar, BorderLayout.NORTH);
	}
	
	public JPanel createTabButton(final Tab tab) {
		final JPanel panel = new JPanel();
		final JButton button = new JButton("Bot");
		final JPopupMenu closeMenu = new JPopupMenu();
		final JMenuItem closeItem = new JMenuItem("Close");
		
		button.setIcon(Configuration.icon("res/icon_tab_small.png"));
		button.setBounds(0, 0, 84, 24);
		button.setFocusable(false);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateTabs(tab);
			}
		});
		button.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				checkForTriggerEvent(e);
			}

			public void mouseReleased(MouseEvent e) {
				checkForTriggerEvent(e);
			}

			private void checkForTriggerEvent(MouseEvent e) {
				if (e.isPopupTrigger()) {
					closeMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		
		closeItem.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				closeTab(tab);
				if (tab.getContent().getApplet() != null) {
					tab.getContent().getApplet().stop();
					tab.getContent().getApplet().destroy();
					tab.getContent().getBot().getThread().interrupt();
					tab.getContent().getBot().getThread().stop();
				}
			}
		});
		closeMenu.add(closeItem);
		
		panel.setPreferredSize(new Dimension(84, 32));
		panel.setMinimumSize(new Dimension(84, 32));
		panel.setMaximumSize(new Dimension(84, 32));
		panel.setLayout(null);
		panel.add(button);
		return panel;
	}
	
	/**
	 * Closes a tab
	 * @param tab The tab to close
	 */
	public void closeTab(Tab tab) {
		tabs.remove(tab);
		if (selected != null && selected.getIndex() == tab.getIndex()) {
			clearContent();
		}
		updateTabs(null);
	}
	
	public void clearContent() {
		if (selected != null) {
			remove(selected.getContent());
			selected = null;
			revalidate();
			repaint();
		}
	}
	
	/**
	 * Adds a new tab to the tab pane
	 * @param panel The bot panel
	 * @return The index of the new tab
	 */
	public int addTab(BotPanel panel) {
		int index = tabs.size();
		Tab tab = new Tab();
		tab.setIndex(index);
		tab.setContent(panel);
		tab.setButton(createTabButton(tab));
		tabs.add(tab);
		updateTabs(tab);
		return index;
	}
	
	public void updateTabs(Tab newSelected) {
		List<JComponent> components = new ArrayList<JComponent>();
		for (Tab t : tabs) {
			components.add(t.getButton());
		}
		toolbar.updateComponents(components);
		
		if (newSelected != null) {
			clearContent();
			selected = newSelected;
			for (Tab t : tabs) {
				t.getButton().setBorder(null);
			}
			selected.getButton().setBorder(BorderFactory.createLineBorder(Color.GRAY));
			add(selected.getContent(), BorderLayout.CENTER);
			selected.getContent().revalidate();
			revalidate();
		}
	}
	
	/**
	 * Returns the selected bot tab
	 * @return
	 */
	public Tab getTab() {
		return selected;
	}
	
	public int getTabCount() {
		return tabs.size();
	}
	
	public static class Tab {
		
		private int index;
		private JPanel button;
		private BotPanel content;
		
		public Tab() {
			
		}
		
		public Tab(int index, JPanel button, BotPanel content) {
			this.index = index;
			this.button = button;
			this.content = content;
		}

		public int getIndex() {
			return index;
		}

		public JPanel getButton() {
			return button;
		}

		public BotPanel getContent() {
			return content;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public void setButton(JPanel button) {
			this.button = button;
		}

		public void setContent(BotPanel content) {
			this.content = content;
		}
	}

}
