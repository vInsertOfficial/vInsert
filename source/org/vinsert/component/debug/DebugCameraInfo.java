package org.vinsert.component.debug;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class DebugCameraInfo extends IncrementalDebugger {
	
	@Override
	public void draw(Graphics2D graphics, Point point) {
		graphics.setColor(Color.WHITE);
		String string = "Camera: angle=" + context.camera.getAngle() + ", pitch=" + context.camera.getPitch() + ", x=" 
		+ context.camera.getX() + ", y=" + context.camera.getY() + ", z=" + context.camera.getZ() + ", compass=" + context.camera.getCompassAngle();
		graphics.drawString(string, point.x, point.y);
	}

}
