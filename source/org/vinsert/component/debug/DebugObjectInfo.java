package org.vinsert.component.debug;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.util.Perspective;


public class DebugObjectInfo extends Debugger {

	@Override
	public void draw(Graphics2D graphics) {
		FontMetrics metrics = graphics.getFontMetrics();
		for (GameObject obj : context.objects.getWithinDistance(10)) {
			final Point point = Perspective.trans_tile_screen(context.getClient(), obj.getLocation(), 0.5, 0.5, 20);
			String id = String.valueOf(obj.getId());
			graphics.setColor(Color.YELLOW);
			graphics.fillOval(point.x - 2, point.y - 2, 4, 4);
			graphics.setColor(Color.GREEN);
			graphics.drawString(id, point.x - metrics.stringWidth(id) / 2, point.y + 15);
		}
	}

}
