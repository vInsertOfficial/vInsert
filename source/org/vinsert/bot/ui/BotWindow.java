package org.vinsert.bot.ui;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel;
import org.vinsert.Application;
import org.vinsert.Configuration;
import org.vinsert.bot.Bot;
import org.vinsert.bot.util.Callback;


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
	private BotTabPane tabs;

	public BotWindow() {

	}

	public void init(final boolean log) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					UIManager.setLookAndFeel(new SubstanceGraphiteGlassLookAndFeel());
					JFrame.setDefaultLookAndFeelDecorated(true);
					JPopupMenu.setDefaultLightWeightPopupEnabled(false);

					frame = new JFrame(Configuration.BOT_NAME + " " + Configuration.BOT_VERSION_MAJOR+"."+Configuration.BOT_VERSION_MINOR);

					frame.setLayout(new BorderLayout());
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setResizable(false);

					initComponents(log);
					frame.pack();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
					//VBLogin.create(log);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void initComponents(final boolean log) {
		tabs = new BotTabPane(this, log);
		frame.add(tabs, BorderLayout.CENTER);
	}

	/**
	 * Adds and initializes a new bot instance
	 *
	 */
	public void addNewBot(final boolean log) {
//		if (VBLogin.self == null) {
//			return;
//		}
		
		final Bot bot = new Bot(log);
		final BotPanel panel = new BotPanel(bot);
//		int usergroup = VBLogin.self.getUsergroupId();
//		if (usergroup != VBLogin.auth_admin && usergroup != VBLogin.auth_vip && usergroup != VBLogin.auth_sw && usergroup != VBLogin.auth_sm
//				&& usergroup != VBLogin.auth_mod && usergroup != VBLogin.auth_contrib && usergroup != VBLogin.auth_sponsor && usergroup != VBLogin.auth_dev
//				&& tabs.getTabCount() >= 2) {
//			warn("Oops!", "Reached maximum amount of allowed tabs! Become a VIP or Sponsor to have unlimited tabs.");
//			return;
//		}

		final int index = tabs.addTab(panel);

		panel.getLogger().log(new LogRecord(Level.INFO, "Loading new bot..."));
		bot.setCallback(new Callback() {
			public void call() {
				bot.setLogger(panel.getLogger());
				panel.loadApplet();
				panel.getLogger().log(new LogRecord(Level.INFO, "Bot successfully loaded!"));
			}
		});
		Thread t = new Thread(bot);
		t.setName("bot-thread-" + index);
		bot.setThread(t);
		bots.put(index, bot);
		t.start();
	}

	public Bot getActiveBot() {
		BotPanel panel = tabs.getTab().getContent();
		if (panel == null) return null;
		
		return panel.getBot();
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
