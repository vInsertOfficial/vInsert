package org.vinsert.bot.ui;

import org.vinsert.Configuration;
import org.vinsert.bot.Bot;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;


/**
 * Wraps a jpanel with the enclosed bot and applet instances
 *
 * @author tommo
 */
public class BotPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private Bot bot;
    private BotLogger logger;
    private BotLoadingIcon loading;

    public BotPanel(Bot bot) {
        this.bot = bot;
        setBorder(null);
        setLayout(new BorderLayout());
        logger = new BotLogger(bot);
        bot.setLogger(logger);
        loading = new BotLoadingIcon();
        loading.setPreferredSize(new Dimension(Configuration.BOT_APPLET_WIDTH, Configuration.BOT_APPLET_HEIGHT));
        add(loading, BorderLayout.CENTER);
        add(logger.createScrollPane(), BorderLayout.SOUTH);
    }

    public void loadApplet() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                remove(loading);
                bot.getApplet().setPreferredSize(new Dimension(Configuration.BOT_APPLET_WIDTH, Configuration.BOT_APPLET_HEIGHT));
                add(bot.getApplet(), BorderLayout.CENTER);
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
        return bot.getApplet();
    }

    public BotLogger getLogger() {
        return logger;
    }

}
