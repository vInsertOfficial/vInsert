package org.vinsert.component.debug;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.util.Perspective;


public class DebugTiles extends Debugger {

	@Override
	public void draw(Graphics2D graphics) {
		FontMetrics metrics = graphics.getFontMetrics();
		Tile loc = context.players.getLocalPlayer().getLocation();
		for (int x = loc.getX() - 20; x < loc.getX() + 20; x++) {
			for (int y = loc.getY() - 20; y < loc.getY() + 20; y++) {
				Tile t = new Tile(x, y);
				if (!Perspective.on_screen(context.getClient(), t)) continue;
				Point point = Perspective.trans_tile_screen(context.getClient(), t, 0.5, 0.5, 1);
				graphics.setColor(Color.GREEN);
				graphics.fillOval(point.x - 2, point.y - 2, 4, 4);
				graphics.setColor(Color.WHITE);
				graphics.drawString(t.toString(), point.x - metrics.stringWidth(t.toString()) / 2, point.y + 15);
			}
		}
	}

}
