package org.vinsert.bot.script.randevent.impl;

import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.GroundItem;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.script.api.tools.Navigation.NavigationPolicy;
import org.vinsert.bot.script.randevent.RandomEvent;

/**
 * @Credits to: Tombraider
 */
@ScriptManifest(authors = { "Soxs" }, name = "FreakyForest")
public class FreakyForest extends RandomEvent {

	public static int freakyForester = 2458;
	public static int interactFF = 32769;
	public static boolean completed = false;
	public static int chickentokill = 0;
	public static boolean gotMeat = false;
	static int meats = 6179;
	
	Npc lumberjack = npcs.getNearest(freakyForester);
	GroundItem pheasantMeat = groundItems.getNearest(players.getLocalPlayer().getLocation(), Filters.groundItemId(6179));
	GameObject forestportal = objects.getNearest(Filters.objectId(8972));
	
	@Override
	public void close() {
		log("Finished Freaky Forester.");
	}

	@Override
	public boolean init() {
		log("Started Freaky Forester.");
		return lumberjack != null;
	}

	@Override
	public RandomEventPriority priority() {
		return RandomEventPriority.HIGH;
	}

	@Override
	public int pulse() {
		if (lumberjack != null) {
			if (chickentokill == 0) {
				if (!camera.isVisible(lumberjack.getLocation())) {
					camera.rotateToTile(lumberjack.getLocation()); //somewhat failsafe.
					sleep(350, 600);
				}
				if (lumberjack.interact("Talk-to")) {
					sleep(350, 500);
				}
				String chickentokilltext = null;
				Widget Wid = widgets.get(243, 2);
				if (Wid != null && Wid.isValid()) {
					chickentokilltext = Wid.getTooltip();
				} else {
					try {
						chickentokilltext = Wid.getTooltip();
					} catch (Exception e) {
						log("Exception caught: " + e.toString());
					}
				}
				String one = "one";
				String two = "two";
				String three = "three";
				String four = "four";
				if (chickentokilltext.indexOf(one) != -1) {
					chickentokill = 2459;
					log("1");
					sleep(150,250);
				} else if (chickentokilltext.indexOf(two) != -1) {
					chickentokill = 2460;
					log("2");
					sleep(150,250);
				} else if (chickentokilltext.indexOf(three) != -1) {
					chickentokill = 2461;
					log("3");
					sleep(150,250);
				} else if (chickentokilltext.indexOf(four) != -1) {
					chickentokill = 2462;
					log("4");
					sleep(150,250);
				} else {
					//TODO get rest of the ID's
				}
				sleep(750, 1250);
				pheasantMeat = groundItems.getNearest(players.getLocalPlayer().getLocation(), Filters.groundItemId(meats));
			} else {
				if (!inventory.contains(Filters.itemId(meats)) && gotMeat == false) {
					if (pheasantMeat == null) {
						npcs.getNearest(chickentokill).interact("Attack");
						sleep(750,1200);
						while (players.getLocalPlayer().getAnimation() != -1 || players.getLocalPlayer().isMoving()) {
							sleep(50, 75);
						}
						pheasantMeat = groundItems.getNearest(players.getLocalPlayer().getLocation(), Filters.groundItemId(meats));
					} else if(pheasantMeat != null && !inventory.isFull()) {
						pheasantMeat = groundItems.getNearest(players.getLocalPlayer().getLocation(), Filters.groundItemId(meats));
						pheasantMeat.interact("Take");
						gotMeat = true;
						sleep(750,1200);
						while (players.getLocalPlayer().getAnimation() != -1 || players.getLocalPlayer().isMoving()) {
							sleep(50, 75);
						}
					} else if (pheasantMeat != null && inventory.isFull()) {
						log("Inventory full cannot complete random");
						return -1;
					}
				}
			}
			if(gotMeat == true) {
				camera.rotateToTile(lumberjack.getLocation());
				sleep(500);
				npcs.getNearest(freakyForester).interact("Talk-to");
				sleep(4000);
				completed = true;
			}
			if(completed == true) {
				navigation.navigate(forestportal.getLocation(), NavigationPolicy.MINIMAP);
				sleep(750,1200);
				while (players.getLocalPlayer().getAnimation() != -1 || players.getLocalPlayer().isMoving()) {
					sleep(50, 75);
				}
				camera.rotateToTile(forestportal.getLocation());
				sleep(500);
				objects.getNearest(Filters.objectId(8972)).interact("Enter");
				sleep(750,1200);
				while (players.getLocalPlayer().getAnimation() != -1 || players.getLocalPlayer().isMoving()) {
					sleep(50, 75);
				}
				if(widgets.get(566, 18) != null) {
					widgets.get(566, 18).click();
				}
				sleep(5000);
				lumberjack = npcs.getNearest(freakyForester);
			}
		}
		return random(50, 75);
	}

}