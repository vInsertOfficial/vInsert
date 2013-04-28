package org.vinsert.bot.script.randevent.impl;

import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.script.randevent.RandomEvent;

import java.awt.Point;

/**
 * @author mcpedro
 */
@ScriptManifest(name = "Pillory Random Solver", authors = {"mcpedro"}, description = "Solves the pillory random")
public class Pillory extends RandomEvent {
  
	private static final int CAGE_ID = 6836;
	private static final int PARENT_ID = 189;

    @Override
    public boolean init() {
        return checkPosition();
    }

    @Override
    public int pulse() {
    	
    	GameObject cage = objects.getNearest(Filters.objectId(CAGE_ID));
		Point p = cage.centerPoint(cage.getLocation().getPolygon(this.getContext()));
		if (p != null) {
			mouse.move(p.x, p.y+8);
			mouse.click(true);
			int f = 0;
			boolean foundUnlock = false;
			for (String t : menu.getChoices()) {
				if (t.indexOf("Unlock") > -1) {
					foundUnlock = true;
					break;
				}
				f++;
			}
			if (foundUnlock) {
				p = menu.getClickPoint(f);
				mouse.click(p.x, p.y);
			}
			sleep(1000, 1500);
			
			while (widgets.get(PARENT_ID) != null) {
				int answer = widgets.get(PARENT_ID, 2).getModelId();
				Polygon polygon = null;
				for (int i = 0; i < 4; i++) {
					switch (i) {
					case 0:
						polygon = Polygon.CIRCLE;
						break;
					case 1:
						polygon = Polygon.TRIANGLE;
						break;
					case 2:
						polygon= Polygon.SQUARE;
						break;
					case 3:
						polygon= Polygon.DIAMOND;
						break;
					}
					if (polygon.getCorrectId() == answer)
						break;
				}
				
				for (int i = 2; i < 6; i++) {
					if (polygon.getModelId() == widgets.get(PARENT_ID, i).getModelId()) {
						widgets.get(PARENT_ID, i).click();
						sleep(1000, 1500);
						break;
					}
				}
			}
			sleep(1500, 2500);
			if (!checkPosition()) {
				requestExit();
				return 200;
			}
		}
    	
		return 300;
    }

    @Override
    public void close() {
        log("Pillory solved! Made by mcpedro");
    }

    @Override
    public RandomEventPriority priority() {
        return RandomEventPriority.HIGH;
    }
    
    public boolean checkPosition() {
    	Tile cageTile = objects.getNearest(Filters.objectId(CAGE_ID)).getLocation();
    	if (cageTile != null) {
    		if (localPlayer.getLocation().distanceTo(cageTile) < 4) {
    			keyboard.press(38);
    	        sleep(1500, 1750);
    	        keyboard.release(38);
    			camera.rotateToTile(cageTile);
    			return true;
    		}
    	}
    	return false;
    }

    private enum Polygon {
    	CIRCLE(9751, 9755),
    	TRIANGLE(9752, 9756),
    	SQUARE(9750, 9754),
    	DIAMOND(9749, 9753);
    	

        private int modelId;
        private int correctModelId;

        private Polygon(int modelId, int correctModelId) {
            this.modelId = modelId;
            this.correctModelId = correctModelId;
        }


        public int getModelId() {
            return modelId;
        }
        
        public int getCorrectId() {
            return correctModelId;
        }
    }
}
