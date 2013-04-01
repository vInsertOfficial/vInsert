package org.vinsert.component.debug;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

import org.vinsert.bot.script.api.Actor;
import org.vinsert.bot.script.api.Model;
import org.vinsert.bot.script.api.Player;


public class DebugPlayerHulls extends Debugger {
	
	private Color color = new Color(255, 1, 1, 60);

	@Override
	public void draw(Graphics2D g) {
		draw(g, context.players.getLocalPlayer(), color);
		for (Player player : context.players.getAll()) {
			draw(g, player, color);
		}
	}
	
	public void draw(Graphics2D g, Actor actor, Color color) {
		Model model = actor.getModel();

		if (model == null || !model.isValid()) return;
		
		g.setColor(color);
		Polygon p = actor.hull();
		if (p != null) {
			g.fillPolygon(p);
		}
	}

}
