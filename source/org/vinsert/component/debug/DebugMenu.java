package org.vinsert.component.debug;

import java.awt.*;
import java.util.Arrays;

public class DebugMenu extends IncrementalDebugger {

    @Override
    public void draw(Graphics2D graphics, Point point) {
        graphics.setColor(Color.WHITE);
        StringBuilder text = new StringBuilder();
        text.append("Menu: x=").append(context.getClient().getMenuX()).append(", y=").append(context.getClient().getMenuY())
                .append(", choices=").append(Arrays.toString(context.menu.getChoices().toArray(new String[context.menu.getChoices().size()])));
        graphics.drawString(text.toString(), point.x, point.y);
    }

}
