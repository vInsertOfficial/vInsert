package org.vinsert.component.debug;

import org.vinsert.bot.script.api.Item;

import java.awt.*;


public class DebugInventory extends Debugger {

    @Override
    public void draw(Graphics2D graphics) {
        graphics.setColor(Color.WHITE);
        for (int i = 0; i < context.inventory.getCapacity(); i++) {
            if (context.inventory.getItemAt(i) != null) {
                Item item = context.inventory.getItemAt(i);
                Point point = context.inventory.getLocation(i);
                graphics.drawString("" + item.getId(), point.x, point.y);
            }
        }
    }

}
