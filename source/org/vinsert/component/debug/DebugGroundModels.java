package org.vinsert.component.debug;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

import org.vinsert.bot.script.api.Area;
import org.vinsert.bot.script.api.GroundItem;
import org.vinsert.bot.script.api.GroundItem.ModelStackType;
import org.vinsert.bot.script.api.Model;
import org.vinsert.bot.script.api.Player;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.util.Perspective;
import org.vinsert.bot.util.Vec3;

public class DebugGroundModels extends Debugger {
	
	private Color colorTop = new Color(1, 200, 1, 85);
	private Color colorMid = new Color(200, 1, 1, 85);
	private Color colorBottom = new Color(1, 1, 200, 85);
	
	public void draw(Graphics2D g) {
		Player player = getContext().players.getLocalPlayer();
		
		Tile loc = player.getLocation();
		for (GroundItem item : context.groundItems.getAll(new Area(new Tile(loc.getX() - 15, loc.getY() - 15), new Tile(loc.getX() + 15, loc.getX() + 15)))) {
			drawModel(g, item);
		}
	}
	
	public void drawModel(Graphics2D graphics, GroundItem ground) {
		Model model = ground.getModel(ModelStackType.TOP);
		render(graphics, model, ground, colorTop);
		
		model = ground.getModel(ModelStackType.MIDDLE);
		render(graphics, model, ground, colorMid);
		
		model = ground.getModel(ModelStackType.BOTTOM);
		render(graphics, model, ground, colorBottom);
	}
	
	public void render(Graphics2D graphics, Model model, GroundItem ground, Color color) {
		if (model == null || !model.isValid()) return;
		Vec3[][] vectors = model.getVectors();

		graphics.setColor(color);

		int gx = (ground.getLocation().getGx() << 7) + 64;
		int gy = (ground.getLocation().getGy() << 7) + 64;
		for (Vec3[] points : vectors) {
			Vec3 pa = points[0];
			Vec3 pb = points[1];
			Vec3 pc = points[2];
			Point a = Perspective.trans_tile_cam(getContext().getClient(), gx + (int) pa.x, gy + (int) pc.x, 0 - (int) pb.x);
			Point b = Perspective.trans_tile_cam(getContext().getClient(), gx + (int) pa.y, gy + (int) pc.y, 0 - (int) pb.y);
			Point c = Perspective.trans_tile_cam(getContext().getClient(), gx + (int) pa.z, gy + (int) pc.z, 0 - (int) pb.z);
			
			if (Perspective.on_screen(a) && Perspective.on_screen(b) && Perspective.on_screen(c)) {
				graphics.drawPolygon(new Polygon(new int[] {
						a.x, b.x, c.x 
				}, new int[] {
						a.y, b.y, c.y 
				}, 3));
			}
		}
	}

}
