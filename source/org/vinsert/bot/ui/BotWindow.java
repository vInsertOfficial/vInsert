package org.vinsert.bot.ui;

import org.vinsert.Configuration;
import org.vinsert.bot.Bot;
import org.vinsert.bot.util.Callback;
import org.vinsert.bot.util.VBLogin;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;


/**
 * The bot UI window
 *
 * @author tommo
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

    public void init() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    JDialog.setDefaultLookAndFeelDecorated(true);

                    frame = new JFrame(Configuration.BOT_NAME + " " + Configuration.BOT_VERSION_MAJOR + "." + Configuration.BOT_VERSION_MINOR);

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
        tabs = new BotTabPane(this);
        frame.add(tabs, BorderLayout.CENTER);
    }

    /**
     * Adds and initializes a new bot instance
     */
    public void addNewBot() {
        if (VBLogin.self == null) {
            return;
        }

        final Bot bot = new Bot(this);
        final BotPanel panel = new BotPanel(bot);
        int usergroup = VBLogin.self.getUsergroupId();
        if (usergroup != VBLogin.auth_admin && usergroup != VBLogin.auth_vip && usergroup != VBLogin.auth_sw && usergroup != VBLogin.auth_sm
                && usergroup != VBLogin.auth_mod && usergroup != VBLogin.auth_contrib && usergroup != VBLogin.auth_sponsor && usergroup != VBLogin.auth_dev
                && tabs.getTabCount() >= 2) {
            warn("Oops!", "Reached maximum amount of allowed tabs! Become a VIP or Sponsor to have unlimited tabs.");
            return;
        }

        final int index = tabs.addTab(panel);

        panel.getLogger().log(new LogRecord(Level.INFO, "Loading new bot..."));
        bot.setCallback(new Callback() {
            public void call() {
                bot.setLogger(panel.getLogger());
                panel.loadApplet();
                panel.getLogger().log(new LogRecord(Level.INFO, "Bot successfully loaded!"));
            }
        });
        bot.load();
        bots.put(index, bot);
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
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void warn(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

}
