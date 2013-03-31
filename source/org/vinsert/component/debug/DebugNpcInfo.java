package org.vinsert.component.debug;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.util.Perspective;


public class DebugNpcInfo extends Debugger {

	@Override
	public void draw(Graphics2D graphics) {
		FontMetrics metrics = graphics.getFontMetrics();
		for (Npc npc : context.npcs.getAll()) {
			final Point point = Perspective.trans_tile_screen(context.getClient(), npc.getLocation(), 0.5, 0.5, npc.getModelHeight() / 2);
			String loc = npc.getLocation().toString();
			String name = npc.getName();
			String id = "id: " + String.valueOf(npc.getId());
			String anim = "anim: " + String.valueOf(npc.getAnimation());
			graphics.setColor(Color.GREEN);
			graphics.fillOval(point.x - 2, point.y - 2, 4, 4);
			graphics.setColor(Color.WHITE);
			graphics.drawString(loc, point.x - metrics.stringWidth(loc) / 2, point.y);
			graphics.drawString(id, point.x - metrics.stringWidth(id) / 2, point.y + 15);
			graphics.drawString(anim, point.x - metrics.stringWidth(anim) / 2, point.y + 30);
			graphics.drawString(name, point.x - metrics.stringWidth(name) / 2, point.y + 45);
		}
	}

}
