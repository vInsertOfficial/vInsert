package org.vinsert.component.debug;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

import org.vinsert.bot.script.api.Player;
import org.vinsert.bot.util.Perspective;


public class DebugPlayersInfo extends Debugger {

	@Override
	public void draw(Graphics2D graphics) {
		FontMetrics metrics = graphics.getFontMetrics();
		for (Player player : context.players.getAll()) {
			final Point point = Perspective.trans_tile_screen(context.getClient(), player.getLocation(), 0.5, 0.5, player.getModelHeight() / 2);
			String loc = player.getLocation().toString();
			String name = player.getName();
			graphics.setColor(Color.GREEN);
			graphics.fillOval(point.x - 2, point.y - 2, 4, 4);
			graphics.setColor(Color.WHITE);
			graphics.drawString(loc, point.x - metrics.stringWidth(loc) / 2, point.y);
			graphics.drawString(name, point.x - metrics.stringWidth(name) / 2, point.y + 15);
		}
	}

}
