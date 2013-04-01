package org.vinsert.component.debug;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.vinsert.bot.Bot;


/**
 * An incremental debugger is a debugger which has an index within the list of other incremental debuggers.
 * <p>
 * This is useful because to have 5 debuggers drawing different values in a list they have to know their index
 * so they dont overwrite previous debuggers
 */
public abstract class IncrementalDebugger extends Debugger {
	
	/**
	 * The static debugger list
	 */
	public static List<IncrementalDebugger> list = new ArrayList<IncrementalDebugger>();
	
	/**
	 * The debuggers index
	 */
	public int index;
	
	@Override
	public void draw(Graphics2D graphics) {
		draw(graphics, new Point(30, 50 + (index * 20)));
	}

	public abstract void draw(Graphics2D graphics, Point point);
	
	@Override
	public void install(Bot bot) {
		super.install(bot);
		IncrementalDebugger.list.add(this);
		this.index = IncrementalDebugger.list.indexOf(this);
	}
	
	@Override
	public void uninstall() {
		//create an ordered list of previous elements, with the this debugger removed
		List<IncrementalDebugger> organise = new LinkedList<IncrementalDebugger>();
		IncrementalDebugger.list.remove(this);
		//now recreate the original list with the new ordered list
		for (IncrementalDebugger dbg : IncrementalDebugger.list) {
			organise.add(dbg);
		}
		IncrementalDebugger.list.clear();
		IncrementalDebugger.list.addAll(organise);
		//now set each debugger's index for repositioning
		for (IncrementalDebugger dbg : IncrementalDebugger.list) {
			dbg.index = IncrementalDebugger.list.indexOf(dbg);
		}
	}

}
