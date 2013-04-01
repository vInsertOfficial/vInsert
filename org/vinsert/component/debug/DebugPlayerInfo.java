package org.vinsert.component.debug;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import org.vinsert.bot.script.api.Tile;


public class DebugPlayerInfo extends IncrementalDebugger {

	@Override
	public void draw(Graphics2D graphics, Point point) {
		graphics.setColor(Color.WHITE);
		StringBuilder text = new StringBuilder();
		text.append("Player: ");
		int anim = context.players.getLocalPlayer().getAnimation();
		text.append("anim=").append(anim);
		Tile loc = context.players.getLocalPlayer().getLocation();
		text.append(", location=").append(loc.toString());
		String speech = context.players.getLocalPlayer().getSpeech();
		text.append(", speech=").append(speech);
		graphics.drawString(text.toString(), point.x, point.y);
	}
	
}
