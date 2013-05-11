/**
package org.vinsert.bot.script.randevent.impl;

import org.vinsert.bot.script.api.*;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.script.randevent.RandomEvent;
import org.vinsert.bot.util.Filter;
import org.vinsert.bot.util.Timer;
import org.vinsert.bot.util.Utils;

*/
/**
 * @author iJava
 *//*

public class EvilTwin extends RandomEvent {

    private static final int MOLLY_ID = 3894;
    private static final int DOOR_CLOSED_ID = 14982;
    private static final int KNOW_PARENT_ID = 228;
    private static final int KNOW_CHILD_ID = 1;
    private static final int CONTROL_ID = 14978;
    private static final int CLAW_PARENT_ID = 240;
    private static final int CLAW_NORTH_ID = 6;
    private static final int CLAW_SOUTH_ID = 11;
    private static final int CLAW_WEST_ID = 16;
    private static final int CLAW_EAST_ID = 21;
    private static final int CLAW_DROP_ID = 23;
    private boolean finished = false;
    private int xCheck = 0;
    private Model model;

    @Override
      public boolean init() {
          if (game.isLoggedIn()) {
              Npc goodTwin = npcs.getNearest(MOLLY_ID);
              if (goodTwin != null) {
                  twinModel = goodTwin.getModel();
                  return true;
              }
          }
          return false;
      }

    @Override
    	public int pulse() {
    		final Npc molly = npcs.getNearest(localPlayer.getLocation(), new Filter<Npc>() {
    			@Override
    			public boolean accept(final Npc npc) {
    				return npc.getName().equalsIgnoreCase("Molly");
    			}
    		});
    		if (molly != null) {
    			xCheck = molly.getLocation().getX() + 4;
    		}


    		if (widgets.get(1188, 3).isValid()) {
    			widgets.get(1188, 3).click();
    			Utils.sleep(Utils.random(1000, 2000));
    			return 500;
    		}
    		if (widgets.clickContinue()) {
    			Utils.sleep(Utils.random(1500, 2200));
    			final Timer timer = new Timer(2000);
    			while (timer.isRunning() && !widgets.canContinue()) {
    				Utils.sleep(150);
    			}
    			return 500;
    		}

    		if (molly != null && localPlayer.getLocation().getX() <= xCheck && settings.get(334) != 0x2 && !finished) {
    			model = molly.getModel();
    			final GameObject location = objects.getNearest(Filters.objectId(DOOR_CLOSED_ID);
    			if (location != null && location.interact("Open")) {
    				final Timer t = new Timer(2000);
    				while (t.isRunning()) {
    					if (localPlayer.isMoving()) {
    						t.reset();
    					}
    					Utils.sleep(150);
    				}
    			}
    			return 500;
    		}

    		if (molly == null && localPlayer.getLocation().getX() > xCheck) {
    			final Widget widget = widgets.get(CLAW_PARENT_ID, CLAW_NORTH_ID);
    			if (widget.isValid()) {
    				navigateClaw();
    				final Timer t = new Timer(12000);
    				while (!widgets.canContinue() && t.isRunning()) {
    					Utils.sleep(150);
    				}
    				return 500;
    			}
    			final GameObject control = objects.getNearest(Filters.objectId(14978));
    			if (control != null) {
    				if (!control.isOnScreen()) {
    					camera.rotateToTile(control.getLocation());
    				} else {
    					if (control.interact("Use")) {
    						final Timer t = new Timer(5000);
    						while (t.isRunning()) {
    							if (widgets.get(CLAW_PARENT_ID, CLAW_NORTH_ID).isValid()) {
    								break;
    							}
    						}
    					}
    				}
    			}
    			return 500;
    		}

    		if (molly != null && localPlayer.getLocation().getX() > xCheck) {
    			finished = true;
    			final GameObject location = objects.getNearest(Filters.objectId(DOOR_CLOSED_ID);
    			if (location != null) {
    				if (!location.isOnScreen()) {
    					//camera.
    					camera.rotateTo(location);
    				} else if (location.interact("Open")) {
    					final Timer t = new Timer(2000);
    					while (t.isRunning()) {
    						if (localPlayer.isMoving()) {
    							t.reset();
    						}
    						Utils.sleep(150);
    					}
    				}
    			}
    		}

    		if (molly != null) {
    			if (!molly.isOnScreen()) {
    				camera.rotateTo(molly);
    				return 500;
    			}

    			if (molly.interact("Talk-to")) {
    				final Timer timer = new Timer(5000);
    				while (timer.isRunning() && !widgets.canContinue()) {
    					Utils.sleep(150);
    				}
    				if (widgets.canContinue()) {
    					finished = false;
    				}
    			}
    		}
    	}

    	private void navigateClaw() {
    		GameObject claw;
    		Npc suspect;
    		verbose("NAVIGATION: BEGIN");
    		while ((claw = objects.getNearest(Filters.objectId(LOCATION_ID_CLAW)) != null && (suspect = npcs.getNearest(new Filter<Npc>() {
    			@Override
    			public boolean accept(final Npc npc) {
    				return npc.getModel().equals(model);
    			}
    		})) != null) {
    			final Tile clawLoc = claw.getLocation();
    			final Tile susLoc = suspect.getLocation();
    			verbose("Claw: " + clawLoc.toString());
    			verbose("Molly's twin: " + susLoc.toString());
    			final ArrayList<Integer> options = new ArrayList<Integer>();
    			if (susLoc.getX() > clawLoc.getX()) {
    				options.add(WIDGET_CONTROLS_LEFT);
    			}
    			if (susLoc.getX() < clawLoc.getX()) {
    				options.add(WIDGET_CONTROLS_RIGHT);
    			}
    			if (susLoc.getY() > clawLoc.getY()) {
    				options.add(WIDGET_CONTROLS_DOWN);
    			}
    			if (susLoc.getY() < clawLoc.getY()) {
    				options.add(WIDGET_CONTROLS_UP);
    			}
    			if (options.isEmpty()) {
    				options.add(WIDGET_CONTROLS_GRAB);
    			}
    			final Widget i = widgets.get(WIDGET_CONTROLS);
    			if (i != null && i.isValid()) {
    				i.getChild(options.get(Utils.random(0, options.size()))).click(true);
    			}
    			final Timer timer = new Timer(3500);
    			while (!hasClawMoved(clawLoc) && timer.isRunning()) {
    				Utils.sleep(10);
    			}
    		}
    		verbose("NAVIGATION: END");
    	}

    	private boolean hasClawMoved(final Tile prevClawLoc) {
    		final GameObject claw = objects.getNearest(Filters.objectId(LOCATION_ID_CLAW);
    		return claw != null && !prevClawLoc.equals(claw.getLocation());
    	}


    @Override
    public void close() {
        log("Evil Twin Utils Solver Finished");
    }

    @Override
    public RandomEventPriority priority() {
        return RandomEventPriority.HIGH;
    }
}*/
