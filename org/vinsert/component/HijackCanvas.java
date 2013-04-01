package org.vinsert.component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class HijackCanvas extends Canvas {

    private static final long serialVersionUID = 1L;
    private BufferedImage botBuffer = new BufferedImage(765, 503, BufferedImage.TYPE_INT_RGB);
    private BufferedImage gameBuffer = new BufferedImage(765, 503, BufferedImage.TYPE_INT_RGB);

    private List<ProjectionListener> listeners = new ArrayList<ProjectionListener>();

    @Override
    public Graphics getGraphics() {
        Graphics g = botBuffer.getGraphics();
        g.drawImage(gameBuffer, 0, 0, null);
        g.dispose();
        Graphics2D rend = (Graphics2D) super.getGraphics();
        for (ProjectionListener listener : listeners) {
            listener.render((Graphics2D) botBuffer.getGraphics());
        }
        rend.drawImage(botBuffer, 0, 0, null);
        return gameBuffer.getGraphics();
    }

    public synchronized List<ProjectionListener> getListeners() {
        return listeners;
    }

    public BufferedImage getGameBuffer() {
        return gameBuffer;
    }

}