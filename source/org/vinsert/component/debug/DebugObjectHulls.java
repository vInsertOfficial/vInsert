package org.vinsert.component.debug;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.Model;


public class DebugObjectHulls extends Debugger {
	
	private Color color = new Color(255, 1, 1, 60);

	@Override
	public void draw(Graphics2D g) {
		for (GameObject obj : context.objects.getWithinDistance(5)) {
			draw(g, obj, color);
		}
	}
	
	public void draw(Graphics2D g, GameObject obj, Color color) {
		Model model = obj.getModel();

		if (model == null || !model.isValid()) return;
		
		g.setColor(color);
		Polygon p = obj.hull();
		if (p != null) {
			g.fillPolygon(p);
		}
	}

}
