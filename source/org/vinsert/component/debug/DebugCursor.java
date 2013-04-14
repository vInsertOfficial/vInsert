package org.vinsert.component.debug;

import org.vinsert.Configuration;

import java.awt.*;


public class DebugCursor extends Debugger {

    @Override
    public void draw(Graphics2D graphics) {
        graphics.setColor(Color.WHITE);
        graphics.drawLine(0, context.mouse.getPosition().y, Configuration.BOT_APPLET_WIDTH, context.mouse.getPosition().y);
        graphics.drawLine(context.mouse.getPosition().x, 0, context.mouse.getPosition().x, Configuration.BOT_APPLET_HEIGHT);
    }

}
