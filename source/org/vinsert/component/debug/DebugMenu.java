package org.vinsert.component.debug;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Arrays;

public class DebugMenu extends IncrementalDebugger {

	@Override
	public void draw(Graphics2D graphics, Point point) {
		graphics.setColor(Color.WHITE);
		StringBuilder text = new StringBuilder();
		text.append("Menu: x=").append(context.getClient().getMenuX()).append(", y=").append(context.getClient().getMenuY())
		.append(", choices=").append(Arrays.toString(context.menu.getChoices().toArray(new String[0])));
		graphics.drawString(text.toString(), point.x, point.y);
	}

}
