package org.vinsert.component.debug;

import java.awt.Graphics2D;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class DebugWidgetSettings extends Debugger {
	
	private int[] array = new int[2000];

	@Override
	public void draw(Graphics2D g) {
		for (int i = 0; i < array.length; i++) {
			int latest = getContext().settings.get(i);
			if (array[i] != latest) {
				if (latest != -1) {
					getContext().getBot().getLogger().log(new LogRecord(Level.INFO, "index " + i + " changed from value " + array[i] + " to " + latest));
				}
			}
		}
		for (int i = 1; i < array.length; i++) {
			array[i] = getContext().settings.get(i);
		}
	}
	
}
