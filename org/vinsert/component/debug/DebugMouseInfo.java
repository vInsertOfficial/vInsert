package org.vinsert.component.debug;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class DebugMouseInfo extends IncrementalDebugger {

	@Override
	public void draw(Graphics2D graphics, Point point) {
		graphics.setColor(Color.WHITE);
		int x = context.getBot().getInputHandler().getPosition().x;
		int y = context.getBot().getInputHandler().getPosition().y;
		graphics.drawString("Mouse: x=" + x + ", y=" + y, point.x, point.y);
	}

}
