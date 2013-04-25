package org.vinsert.component.debug;

import org.vinsert.bot.script.api.GroundItem;
import org.vinsert.bot.script.api.GroundItem.ModelStackType;
import org.vinsert.bot.script.api.Model;

import java.awt.*;


public class DebugGroundHulls extends Debugger {

    private Color color = new Color(255, 1, 1, 60);

    @Override
    public void draw(Graphics2D g) {
        for (GroundItem item : getContext().groundItems.getAll()){
            if(item != null) {
            draw(g, item, color);
            }
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
