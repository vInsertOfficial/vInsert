package org.vinsert.bot.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import org.vinsert.Configuration;


/**
 * Simple UI loading icon
 * @author tommo
 */
public class BotLoadingIcon extends JPanel {
	
    private static final long serialVersionUID = 223086939802246968L;
    private static final int DELAY = 100;
    private static final int RADIUS = 30;
    private static final int NUMBER_OF_CIRCLES = 10;
    
    private Color color = Color.LIGHT_GRAY;
    private int darkCircle = 0;
    private Timer timer;
    private int value = 0;

    public BotLoadingIcon() {
        timer = new Timer(DELAY, new TimerActionListener());
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);
        g2d.setColor(color);

        int centerX = Configuration.BOT_UI_WIDTH / 2 - RADIUS;
        int centerY = Configuration.BOT_UI_HEIGHT / 2 - RADIUS;
        int circularX, circularY;
        for (int i = 0; i < NUMBER_OF_CIRCLES; i++) {
            circularX = centerX + (int) (RADIUS * Math.sin((360 / NUMBER_OF_CIRCLES) * i * 3.14 / 180));
            circularY = centerY + (int) (RADIUS * Math.cos((360 / NUMBER_OF_CIRCLES) * i * 3.14 / 180));
            if(darkCircle == i) {
                g2d.fillOval(circularX, circularY, 10, 10);
            } else {
                g2d.drawOval(circularX, circularY, 10, 10);
            }
        }

        darkCircle = NUMBER_OF_CIRCLES - 1 - (value % NUMBER_OF_CIRCLES);
    }

    private class TimerActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
        	value++;
            darkCircle = NUMBER_OF_CIRCLES - 1 - (value % NUMBER_OF_CIRCLES);
            repaint();
        }
    }
}