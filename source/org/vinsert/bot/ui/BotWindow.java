package org.vinsert.bot.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.vinsert.Application;
import org.vinsert.Configuration;
import org.vinsert.bot.Bot;
import org.vinsert.bot.util.Callback;
import org.vinsert.bot.util.VBLogin;


/**
 * The bot UI window
 * 
 * @author tommo
 * 
 */
public class BotWindow {

	/**
	 * The bot threads
	 */
	private Map<Integer, Bot> bots = new HashMap<Integer, Bot>();

	private JFrame frame;
	private JTabbedPane botTabs;
	private BotToolBar toolBar;

	public BotWindow() {

	}

	public void init() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					JPopupMenu.setDefaultLightWeightPopupEnabled(false);

					frame = new JFrame(Configuration.BOT_NAME + " " + Configuration.BOT_VERSION_MAJOR+"."+Configuration.BOT_VERSION_MINOR);

					frame.setLayout(new BorderLayout());
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setResizable(false);

					initComponents();
					frame.pack();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
					VBLogin.create();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void initComponents() {
		botTabs = new JTabbedPane();
		toolBar = new BotToolBar(this);
		toolBar.setPreferredSize(new Dimension(Configuration.BOT_TOOLBAR_WIDTH, Configuration.BOT_TOOLBAR_HEIGHT));
		botTabs.setBorder(null);
		botTabs.setBackground(Color.DARK_GRAY);
		botTabs.setPreferredSize(new Dimension(Configuration.BOT_APPLET_WIDTH + 8, Configuration.BOT_APPLET_HEIGHT + Configuration.BOT_LOGGER_HEIGHT + 40));
		frame.add(toolBar, BorderLayout.NORTH);
		frame.add(botTabs, BorderLayout.CENTER);
	}

	/**
	 * Adds and initializes a new bot instance
	 *
	 */
	public void addNewBot() {
		if (VBLogin.self == null) {
			return;
		}
		
		final Bot bot = new Bot();
		final BotPanel panel = new BotPanel(bot);
		int usergroup = VBLogin.self.getUsergroupId();
		if (usergroup != VBLogin.auth_admin && usergroup != VBLogin.auth_vip && usergroup != VBLogin.auth_sw && usergroup != VBLogin.auth_sm
				&& usergroup != VBLogin.auth_mod && usergroup != VBLogin.auth_contrib && usergroup != VBLogin.auth_sponsor && usergroup != VBLogin.auth_dev
				&& botTabs.getTabCount() >= 2) {
			warn("Oops!", "Reached maximum amount of allowed tabs! Become a VIP or Sponsor to have unlimited tabs.");
			return;
		}

		/*
		 * Ugly tab formatting code
		 */
		botTabs.addTab(null, panel);
		final int index = botTabs.indexOfComponent(panel);
		bot.setBotIndex(index);
		JPanel tabPanel = new JPanel();
		tabPanel.setPreferredSize(new Dimension(90, 20));
		tabPanel.setLayout(new BorderLayout());
		tabPanel.setOpaque(false);
		tabPanel.setFocusable(false);
		JLabel tabLabel = new JLabel("Bot " + (botTabs.getTabCount()));
		tabLabel.setForeground(Color.GRAY);
		JButton tabClose = new JButton();
		tabClose.setIcon(null);
		tabClose.setIcon(BotToolBar.icon("res/icon_exit_small_greyed.png"));
		tabClose.setRolloverIcon(BotToolBar.icon("res/icon_exit_small.png"));
		tabClose.setContentAreaFilled(false);
		tabClose.setRolloverEnabled(true);
		tabClose.setBorderPainted(false);
		tabClose.setFocusable(false);
		tabPanel.add(tabLabel, BorderLayout.CENTER);
		tabPanel.add(tabClose, BorderLayout.EAST);
		botTabs.setTabComponentAt(index, tabPanel);
		tabClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Bot b = bots.get(index);
				((BotPanel) botTabs.getSelectedComponent()).removeApplet();
				b.exit();
				botTabs.remove(panel);
			}
		});
		botTabs.setSelectedComponent(panel);

		panel.getLogger().log(new LogRecord(Level.INFO, "Loading new bot..."));
		bot.setCallback(new Callback() {
			public void call() {
				bot.setLogger(panel.getLogger());
				panel.loadApplet();
				panel.getLogger().log(new LogRecord(Level.INFO, "Bot successfully loaded!"));
			}
		});
		Thread t = new Thread(bot);
		t.setName("bot" + index);
		bot.setThread(t);
		bots.put(index, bot);
		t.start();
	}

	public Bot getActiveBot() {
		if (botTabs.getSelectedComponent() == null) return null;
		return (Bot) ((BotPanel)botTabs.getSelectedComponent()).getBot();
	}

	public JFrame getFrame() {
		return frame;
	}

	public static void error(String title, String message) {
		JOptionPane.showMessageDialog(Application.getBotWindow().getFrame(), message, title, JOptionPane.ERROR_MESSAGE);
	}

	public static void warn(String title, String message) {
		JOptionPane.showMessageDialog(Application.getBotWindow().getFrame(), message, title, JOptionPane.WARNING_MESSAGE);
	}

}
