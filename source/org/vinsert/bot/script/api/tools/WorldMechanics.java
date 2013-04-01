package org.vinsert.bot.script.api.tools;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Player;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.util.Utils;

/*
 * @Author: Soxs
 * Basically misc stuff that I don't think fits anywhere else.
 */

public class WorldMechanics {
  
  private ScriptContext ctx;
	
	private enum direction {
        NORTH(0), EAST(1), SOUTH(2), WEST(3);
        int value;
        direction(int value) {
                this.value = value;
        }
	}
	
	public int getFacing(Npc n) { // (0 north, 1 east, 2 south, 3 west)
		/*
		 * Basically a simpler version of orientation working with tiles.
		 * 
		 * Should only really be used if the user knows for sure that the npc will be moving
		 * or could potentially get stuck in an never ending loop.
		 */
		Player me = new Player(ctx, ctx.getClient().getLocalPlayer());
		if (n != null && n.isMoving()) {
			Tile first = n.getLocation();
			int fx = first.getX();
			int fy = first.getY();
			int dis = n.getLocation().distanceTo(me.getLocation());
			long now = System.currentTimeMillis();
			while (n != null && dis == n.getLocation().distanceTo(me.getLocation())) {
				Utils.sleep(75, 100);
				//-NPC n did not move.
				if (System.currentTimeMillis() - now > Utils.random(2000, 2500)) {
					return 0; //waited for more than 2 seconds, times out. (lag etc)
				}
			}	
			Tile second = n.getLocation();
			int sx = second.getX();
			int sy = second.getY();
			if (sy > fy) {
				return direction.NORTH.value; //north
			} else if (sx > fx) {
				return direction.EAST.value; //east
			} else if (sy < fy) {
				return direction.SOUTH.value; //south
			} else if (sx < fx) {
				return direction.WEST.value; //west
			}
		}
		return 0;
	}
	
}
