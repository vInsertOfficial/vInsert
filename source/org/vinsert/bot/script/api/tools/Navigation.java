package org.vinsert.bot.script.api.tools;

import java.awt.Point;
import java.util.Deque;
import java.util.LinkedList;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Path;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.api.tools.Game.Tabs;
import org.vinsert.bot.util.Perspective;
import org.vinsert.bot.util.Utils;


/**
 * Navigation utilites
 * @author tommo
 *
 */
public class Navigation {
	
	/**
	 * The maximum threshold for transitioning between tiles in a smooth manner
	 */
	public static final int TRANSITION_THRESHOLD_MAX = 8;
	
	/**
	 * The minimum threshold for transitioning between tiles in a smooth manner
	 */
	public static final int TRANSITION_THRESHOLD_MIN = 3;
	
	/**
	 * The last path
	 */
	private Path lastPath;

	private ScriptContext ctx;
	
	public Navigation(ScriptContext ctx) {
		this.ctx = ctx;
	}
	
	/**
	 * Navigates the player through the given path.
	 * <p>
	 * The path's traversal state is retained so navigation can be continued after stopping
	 * @param path The path to navigate through
	 * @param deviation The maximum deviation for tile positions
	 * @param policy The method to use for navigation, NavigationPolicy.MINIMAP is suggested
	 * @return true if we reached the end of the path, false if not
	 */
	public boolean navigate(Path path, int deviation, NavigationPolicy policy) {
		if (path.getNext() == null) {
			//the path is finished
			return true;
		}
		
		if (lastPath == null) {
			lastPath = path;
		} else if (lastPath != path) {
			lastPath.reset();
		}
		
		if (path.getTarget() == null) {
			//interject into a path, to closest tile
			Tile target = getFurthestWalkableTile(path);
			int index = path.indexOf(target);
			if (index >= 1) {
				path.setCurrent(index);
			}
			path.setTarget(deviate(target, deviation, deviation), Utils.random(TRANSITION_THRESHOLD_MIN, TRANSITION_THRESHOLD_MAX));
		}
		
		if (path.getNext().equals(path.getFinish())) {
			//if we're on the last path node, we won't be transitioning so we have to end up at the exact position
			if (!ctx.players.getLocalPlayer().isMoving()) {
				navigate(path.getNext(), policy);
			}
		} else {
			//check if the player exceeded the path node's transition threshold
			//or if the player has already reached the destination
			if (ctx.players.getLocalPlayer().getLocation().distanceTo(path.getTarget()) <= deviation || 
					ctx.players.getLocalPlayer().getLocation().distanceTo(path.getTarget()) <= path.getTargetThreshold()) {
				//we exceeded the transition threshold, so advance through the path
				path.advance();
				Tile target = deviate(path.getNext(), deviation, deviation);
				path.setTarget(target, Utils.random(TRANSITION_THRESHOLD_MIN, TRANSITION_THRESHOLD_MAX));
				navigate(path.getNext(), policy);
			} else if (!ctx.players.getLocalPlayer().isMoving()) {
				navigate(path.getNext(), policy);
			}
		}
		
		return false;
	}
	
	/**
	 * Calculates the next viable tile (e.g. the furthest tile away from the player which is walkable on the minimap)
	 * @param path The path
	 * @return The furthest tile
	 */
	public Tile getFurthestWalkableTile(Path path) {
		Deque<Tile> list = new LinkedList<Tile>();
		Tile last = path.getFinish();
		for (Tile t : path.getTiles()) {
			if (list.isEmpty()) {
				list.addLast(t);
			}
			
			Point p = Perspective.trans_tile_minimap(ctx.getClient(), t.getX(), t.getY());
			if (p.x != -1 && p.y != -1) {
				if (last.distanceTo(t) < last.distanceTo(list.peekFirst())) {
					list.addLast(t);
				} else {
					list.addFirst(t);
				}
			}
		}
		return list.peekFirst();
	}
	
	private void navigateMinimap(Tile tile) {
		Point p = Perspective.trans_tile_minimap(ctx.getClient(), tile.getX(), tile.getY());
		if (p.x != -1 && p.y != -1) {
			ctx.mouse.click(p.x, p.y);
		}
	}
	
	private void navigateScreen(Tile tile) {
		Point point = Perspective.trans_tile_screen(ctx.getClient(), tile, Math.random(), Math.random(), 
				Perspective.get_tile_height(ctx.getClient(), ctx.getClient().getPlane(), tile.getX(), tile.getY()));
		ctx.mouse.click(point.x, point.y);
	}
	
	/**
	 * Clicks the tile in regards to the navigation policy
	 * @param tile The tile to click
	 * @param policy The policy determining how to click the tile
	 */
	public void navigate(Tile tile, NavigationPolicy policy) {
		if (policy == NavigationPolicy.MINIMAP) {
			navigateMinimap(tile);
		} else if (policy == NavigationPolicy.SCREEN) {
			if (Perspective.on_screen(ctx.getClient(), tile)) {
				navigateScreen(tile);
			} else {
				ctx.camera.rotateToTile(tile);
				navigateScreen(tile);
			}
		} else if (policy == NavigationPolicy.MIXED) {
			//minimap walking is preferred, but small chance of screen clicking
			int rnd = Utils.random(1, 10);
			if (rnd > 3) {
				navigateMinimap(tile);
			} else {
				if (Perspective.on_screen(ctx.getClient(), tile)) {
					navigateScreen(tile);
				} else {
					ctx.camera.rotateToTile(tile);
					navigateScreen(tile);
				}
			}
		}
	}
	
	/**
	 * Deviates a tile by the random offset
	 * @param tile
	 * @param deviationX The maximum x offset
	 * @param deviationY The maximum y offset
	 * @return The new, randomized tile
	 */
	public Tile deviate(Tile tile, int deviationX, int deviationY) {
		if (deviationX == 0 && deviationY == 0) return tile;
		int x = tile.getX() + (-deviationX + Utils.random(0, deviationX * 2));
		int y = tile.getY() + (-deviationY + Utils.random(0, deviationY * 2));
		return new Tile(x, y);
	}
	
	/**
	 * Checks if running is toggled
	 * @return true if the player is running, false if not
	 */
	public boolean isRunning() {
		return ctx.settings.get(Settings.TOGGLE_RUN) == 1;
	}
	
	/**
	 * Toggles running
	 * @return true if running after being toggled, false if not
	 */
	public boolean toggleRunning() {
		if (isRunning()) {
			setRunning(false);
			return false;
		} else {
			setRunning(true);
			return true;
		}
	}
	
	/**
	 * Toggles running
	 * @param running The running flag
	 * @return true if run was toggles, false if not
	 */
	public boolean setRunning(boolean running) {
		if ((isRunning() && running) || (!isRunning() && !running)) return false;
		ctx.game.openTab(Tabs.OPTIONS);
		Widget widget = ctx.widgets.get(261, 0);
		widget.click();
		return true;
	}
	
	/**
	 * @return The player's current energy
	 */
	public int getEnergy() {
		return Integer.parseInt(ctx.widgets.get(261, 40).getText());
	}
	
	/**
	 * The policy determining which method the navigator will use to
	 * navigate to a position
	 * @author tommo
	 *
	 */
	public enum NavigationPolicy {
		/**
		 * Use only the minimap to navigate
		 */
		MINIMAP, 
		/**
		 * Use only tiles on screen to navigate
		 */
		SCREEN,
		/**
		 * Use a mixed policy of minimap and screen navigation
		 */
		MIXED;
	}
	
}
