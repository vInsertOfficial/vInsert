package org.vinsert.component.debug;

import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.util.Perspective;

import java.awt.*;


public class DebugObjectInfo extends ObjectDebugger {
    @Override
    public void draw(Graphics2D graphics) {
        FontMetrics metrics = graphics.getFontMetrics();
        for (GameObject obj : context.objects.getWithinDistanceType(10, getType())) {
            final Point point = Perspective.trans_tile_screen(context.getClient(), obj.getLocation(), 0.5, 0.5, 20);
            String id = String.valueOf(obj.getId());
            graphics.setColor(Color.YELLOW);
            graphics.fillOval(point.x - 2, point.y - 2, 4, 4);
            graphics.setColor(Color.GREEN);
            graphics.drawString(id, point.x - metrics.stringWidth(id) / 2, point.y + 15);
        }
    }

}
