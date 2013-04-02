package org.vinsert.bot.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
	 * The tab buttons
	 */
	private List<JPanel> buttons = new LinkedList<JPanel>();
	
	/**
	 * The tab components
	 */
	private List<BotPanel> tabs = new LinkedList<BotPanel>();
	
	/**
	 * The panel toolbar
	 */
	private BotToolBar toolbar;
	
	/**
	 * The content panel
	 */
	private JPanel contentPanel;
	
	public BotTabPane(BotWindow window, boolean log) {
		setPreferredSize(new Dimension(Configuration.BOT_APPLET_WIDTH + 8, Configuration.BOT_APPLET_HEIGHT + Configuration.BOT_LOGGER_HEIGHT + 40));
		setBorder(null);
		toolbar = new BotToolBar(window, log);
		contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		contentPanel.setBorder(null);
		add(toolbar, BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);
	}
	
	public JPanel createTabButton(final BotPanel botPanel) {
		final JPanel panel = new JPanel();
		final JButton button = new JButton("Bot " + (tabs.size()));
		final JPopupMenu closeMenu = new JPopupMenu();
		final JMenuItem closeItem = new JMenuItem("Close");
		
		button.setIcon(Configuration.icon("res/icon_tab_small.png"));
		button.setBounds(0, 0, 84, 24);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setContent(botPanel);
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
			public void actionPerformed(ActionEvent e) {
				//TODO tab closing
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
	 * Adds a new tab to the tab pane
	 * @param panel The bot panel
	 * @return The index of the new tab
	 */
	public int addTab(BotPanel panel) {
		tabs.add(panel);
		buttons.add(createTabButton(panel));
		
		List<JComponent> components = new ArrayList<JComponent>();
		components.addAll(buttons);
		toolbar.updateComponents(components);
		setContent(panel);
		return tabs.size();
	}
	
	/**
	 * Sets the active content panel
	 * @param panel The new content panel
	 */
	public void setContent(JPanel panel) {
		remove(contentPanel);
		contentPanel = panel;
		add(contentPanel, BorderLayout.CENTER);
		revalidate();
	}
	
	/**
	 * Returns the selected bot tab
	 * @return
	 */
	public BotPanel getTab() {
		return (BotPanel) contentPanel;
	}
	
	public int getTabCount() {
		return tabs.size();
	}

}
