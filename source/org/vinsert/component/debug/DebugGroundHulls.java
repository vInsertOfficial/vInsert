package org.vinsert.component.debug;

import org.vinsert.bot.script.api.*;
import org.vinsert.bot.script.api.GroundItem.ModelStackType;

import java.awt.*;


public class DebugGroundHulls extends Debugger {

    private Color color = new Color(255, 1, 1, 60);

    @Override
    public void draw(Graphics2D g) {
        Player player = getContext().players.getLocalPlayer();

        Tile loc = player.getLocation();
        for (GroundItem item : getContext().groundItems.getAll(new Area(new Tile(loc.getX() - 15, loc.getY() - 15), new Tile(loc.getX() + 15, loc.getX() + 15)))) {
            draw(g, item, color);
        }
    }

    public void draw(Graphics2D g, GroundItem item, Color color) {
        Model model = item.getModel(ModelStackType.TOP);

        if (model == null || !model.isValid()) return;

        g.setColor(color);
        Polygon p = item.hull();
        if (p != null) {
            g.fillPolygon(p);
        }
    }

}
