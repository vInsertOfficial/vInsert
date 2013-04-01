package org.vinsert.component.debug;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.GameObject.Type;
import org.vinsert.bot.script.api.Model;
import org.vinsert.bot.util.Perspective;
import org.vinsert.bot.util.Vec3;


public class DebugObjectModels extends Debugger {

	private Color color = new Color(220, 220, 0, 85);

	@Override
	public void draw(Graphics2D g) {
		for (GameObject obj : context.objects.getWithinDistance(5)) {
			drawModel(g, obj, color);
		}
	}

	public void drawModel(Graphics2D graphics, GameObject obj, Color color) {
		Model model = obj.getModel();
		if (model == null || !model.isValid()) {
			return;
		}

		Vec3[][] vectors = model.getVectors();

		if (obj.getType() == Type.BOUNDARY) {
			graphics.setColor(color);
		} else if (obj.getType() == Type.FLOOR_DECORATION) {
			graphics.setColor(Color.RED);
		} else if (obj.getType() == Type.INTERACTABLE) {
			graphics.setColor(Color.BLUE);
		} else {
			graphics.setColor(Color.MAGENTA);
		}

		int gx = obj.getLocation().getGx();
		int gy = obj.getLocation().getGy();

		for (Vec3[] points : vectors) {
			Vec3 pa = points[0];
			Vec3 pb = points[1];
			Vec3 pc = points[2];

			Point a = Perspective.trans_tile_cam(getContext().getClient(), gx + (int) pa.x, gy + (int) pc.x, 0 - (int) pb.x);
			Point b = Perspective.trans_tile_cam(getContext().getClient(), gx + (int) pa.y, gy + (int) pc.y, 0 - (int) pb.y);
			Point c = Perspective.trans_tile_cam(getContext().getClient(), gx + (int) pa.z, gy + (int) pc.z, 0 - (int) pb.z);

			if (a.x >= 0 && b.x >= 0 && c.x >= 0) {
				Polygon poly = new Polygon(new int[]{ a.x, b.x, c.x }, new int[]{ a.y, b.y, c.y }, 3);

				graphics.drawPolygon(poly);
			}
		}
	}
}
