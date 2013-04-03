package org.vinsert.bot.ui;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.vinsert.Configuration;
import org.vinsert.bot.Bot;


/**
 * Wraps a jpanel with the enclosed bot and applet instances
 * @author tommo
 *
 */
public class BotPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private Bot bot;
	private BotLogger logger;
	private BotLoadingIcon loading;
	private Applet applet;
	
	public BotPanel(Bot bot) {
		this.bot = bot;
		setBorder(null);
		setLayout(new BorderLayout());
		JPanel loggerPanel = new JPanel();
		loggerPanel.setLayout(new BoxLayout(loggerPanel, BoxLayout.Y_AXIS));
		loggerPanel.add(Box.createVerticalStrut(3));
		logger = new BotLogger();
		loggerPanel.add(logger.createScrollPane());
		loading = new BotLoadingIcon();
		loading.setPreferredSize(new Dimension(Configuration.BOT_APPLET_WIDTH, Configuration.BOT_APPLET_HEIGHT));
		add(loading, BorderLayout.CENTER);
//		add(logger.createScrollPane(), BorderLayout.SOUTH);
		add(loggerPanel, BorderLayout.SOUTH);
	}
	
	public void loadApplet() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				remove(loading);
				applet = bot.getApplet();
				applet.setPreferredSize(new Dimension(Configuration.BOT_APPLET_WIDTH, Configuration.BOT_APPLET_HEIGHT));
				add(applet, BorderLayout.CENTER);
		        try {
		            Thread.sleep(3000);
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
		        validate();
			}
		});
	}
	
	public Bot getBot() {
		return bot;
	}
	
	public Applet getApplet() {
		return this.applet;
	}
	
	public BotLogger getLogger() {
		return logger;
	}

}
