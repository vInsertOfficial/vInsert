package org.vinsert.component;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.vinsert.bot.Bot;

public class HijackCanvas extends Canvas {
	
	public static final int FRAMESTEP = 1000 / 60;

    private static final long serialVersionUID = 1L;
    private BufferedImage botBuffer = new BufferedImage(765, 503, BufferedImage.TYPE_INT_RGB);
    private BufferedImage gameBuffer = new BufferedImage(765, 503, BufferedImage.TYPE_INT_RGB);

    private List<ProjectionListener> listeners = new ArrayList<ProjectionListener>();
    
    private Bot bot;

    @Override
    public Graphics getGraphics() {
        Graphics g = botBuffer.getGraphics();
        /*
         * Only draw if this canvas it the active one
         */
        if (bot != null && bot.getWindow().getActiveBot() != null && bot.getWindow().getActiveBot() == bot) {
	        g.drawImage(gameBuffer, 0, 0, null);
	        g.dispose();
	        Graphics2D rend = (Graphics2D) super.getGraphics();
	        for (ProjectionListener listener : listeners) {
	            listener.render((Graphics2D) botBuffer.getGraphics());
	        }
	        rend.drawImage(botBuffer, 0, 0, null);
        }
        try {
			Thread.sleep(FRAMESTEP);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        return gameBuffer.getGraphics();
    }
    
    public void setBot(Bot bot) {
    	this.bot = bot;
    }

    public synchronized List<ProjectionListener> getListeners() {
        return listeners;
    }

    public BufferedImage getGameBuffer() {
        return gameBuffer;
    }

}