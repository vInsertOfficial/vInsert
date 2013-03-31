package org.vinsert.component.debug;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import org.vinsert.bot.script.api.Item;


public class DebugInventory extends Debugger {

	@Override
	public void draw(Graphics2D graphics) {
		graphics.setColor(Color.WHITE);
		for (int i = 0; i < context.inventory.getCapacity(); i++) {
			if (context.inventory.getItem(i) != null) {
				Item item = context.inventory.getItem(i);
				Point point = context.inventory.getLocation(i);
				graphics.drawString("" + item.getId(), point.x, point.y);
			}
		}
	}

}
