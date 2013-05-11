package org.vinsert.bot.ui;

import org.vinsert.Configuration;
import org.vinsert.bot.Bot;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author iJava
 */
public class BotViewer extends JPanel implements Runnable {

    private BotTabPane tabbedPane;
    private List<DisplayPanel> displayPanels;

    public BotViewer(BotTabPane tabbedPane) {
        this.tabbedPane = tabbedPane;
        setBorder(null);
        setSize(new Dimension(Configuration.BOT_APPLET_WIDTH, Configuration.BOT_APPLET_HEIGHT));
        setLayout(new GridLayout(2, 2));
        displayPanels = new ArrayList<>();
    }


    @Override
    public void run() {
    }

    private class DisplayPanel extends JPanel {

        private Bot bot;

        public DisplayPanel(Bot bot) {
            this.bot = bot;
        }

        @Override
        public void paint(Graphics g) {
            g.drawImage(bot.getCanvas().getGameBuffer(), 0, 0, null);
        }
    }
}
