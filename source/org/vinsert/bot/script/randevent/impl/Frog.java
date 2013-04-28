package org.vinsert.bot.script.randevent.impl;

import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.script.api.tools.Navigation.NavigationPolicy;
import org.vinsert.bot.script.randevent.RandomEvent;

import java.awt.Point;

/**
 * @author mcpedro
 */
@ScriptManifest(name = "Frog Random Solver", authors = {"mcpedro"}, description = "Talks to the frog princess")
public class Frog extends RandomEvent {
  
	private static final int PRINCESS_ID = 567;
	private static final int HERALD_ID = 568;

    @Override
    public boolean init() {
        return checkFrog();
    }

    @Override
    public int pulse() {
    	Npc princess = npcs.getNearest(PRINCESS_ID);
		camera.rotateToActor(princess);
		Tile tile = princess.getLocation();
		if (localPlayer.getLocation().distanceTo(tile) > 6)
			navigation.navigate(tile, NavigationPolicy.MINIMAP);
		Point p = princess.centerPoint(princess.getLocation().getPolygon(this.getContext()));
		if (p != null) {
			mouse.move(p.x, p.y);
			mouse.click();
			sleep(500, 750);
			while (widgets.canContinue()) {
				widgets.clickContinue();
			}
			requestExit();
			return 200;
		}
		return 300;
    }

    @Override
    public void close() {
        log("Frog talked to! Made by mcpedro");
    }

    @Override
    public RandomEventPriority priority() {
        return RandomEventPriority.HIGH;
    }
    
    public boolean checkFrog() {
    	if (npcs.getNpcs(Filters.npcId(HERALD_ID)) != null) {
    		for (Npc npc : npcs.getNpcs(Filters.npcId(HERALD_ID))) {
    			if (npc.getSpeech().contains(players.getLocalPlayer().getName()))
    				return true;
    		}
    	}
    	return false;
    }
}
