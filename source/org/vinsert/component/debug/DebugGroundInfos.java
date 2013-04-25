package org.vinsert.component.debug;

import org.vinsert.bot.script.api.GroundItem;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.util.Perspective;

import java.awt.*;

public class DebugGroundInfos extends Debugger {

    @Override
    public void draw(Graphics2D g) {
        FontMetrics metrics = g.getFontMetrics();

        Tile loc = getContext().players.getLocalPlayer().getLocation();
        for (GroundItem item : getContext().groundItems.getAll()) {
            final Point point = Perspective.trans_tile_screen(context.getClient(), item.getLocation(), 0.5, 0.5, 1);
            g.setColor(Color.GREEN);
            g.fillOval(point.x - 2, point.y - 2, 4, 4);
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(item.getId()), point.x - metrics.stringWidth(String.valueOf(item.getId())) / 2, point.y + 15);
        }
    }

}
