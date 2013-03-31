package org.vinsert.component.debug;

import java.awt.Graphics2D;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class DebugVarbitSettings extends Debugger {
	
	private int[] array;

	@Override
	public void draw(Graphics2D g) {
		array = new int[getContext().getClient().getBitConfigs().length];
		for (int i = 0; i < getContext().getClient().getBitConfigs().length; i++) {
			if (i >= getContext().getClient().getBitConfigs().length) break;
			int latest = getContext().getClient().getBitConfigs()[i];
			if (array[i] != latest) {
				if (latest != -1) {
					getContext().getBot().getLogger().log(new LogRecord(Level.INFO, "varbit index " + i + " changed from value " + array[i] + " to " + latest));
				}
			}
		}
		for (int i = 1; i < getContext().getClient().getBitConfigs().length; i++) {
			array[i] = getContext().getClient().getBitConfigs()[i];
		}
	}
	
}
