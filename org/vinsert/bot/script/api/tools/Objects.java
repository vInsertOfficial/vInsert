package org.vinsert.bot.script.api.tools;

import java.util.LinkedHashSet;
import java.util.Set;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Actor;
import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.util.Filter;
import org.vinsert.insertion.IClient;
import org.vinsert.insertion.IFloorDecoration;
import org.vinsert.insertion.ISceneObject;
import org.vinsert.insertion.ISceneTile;
import org.vinsert.insertion.IWall;
import org.vinsert.insertion.IWallDecoration;


/**
 * Object utilities
 * @author tommo
 * @author `Discardedx2
 */
public class Objects {

	public static final int TYPE_INTERACTABLE = 0x1;
	public static final int TYPE_FLOOR_DECORATION = 0x2;
	public static final int TYPE_BOUNDARY = 0x4;
	public static final int TYPE_WALL_DECORATION = 0x8;
	public static final int TYPE_ALL = 0x1 | 0x2 | 0x4 | 0x8;

	private ScriptContext ctx;

	public Objects(ScriptContext ctx) {
		this.ctx = ctx;
	}

	/**
	 * Returns an array of all game objects within a given distance from the local player
	 * @param distance The maximum distance, in tiles
	 * @return
	 */
	public GameObject[] getWithinDistance(final int distance) {
		return getWithinDistance(ctx.players.getLocalPlayer(), distance, TYPE_ALL);
	}
	
	/**
	 * Returns an array of all game objects within a given distance from the local player
	 * @param distance The maximum distance, in tiles
	 * @param the object type mask (all = Objects.TYPE_ALL)
	 * @return
	 */
	public GameObject[] getWithinDistance(final int distance, final int mask) {
		return getWithinDistance(ctx.players.getLocalPlayer(), distance, mask);
	}

	/**
	 * Returns an array of all game objects within a given distance
	 * @param actor The actor
	 * @param distance The maximum distance, in tiles
	 * @return
	 */
	public GameObject[] getWithinDistance(final Actor actor, final int distance, final int mask) {
		return getAll(new Filter<GameObject>() {
			public boolean accept(GameObject element) {
				return actor.getLocation().distanceTo(element.getLocation()) <= distance;
			}
		}, mask);
	}

	/**
	 * Returns an array of all objects on a given tile
	 * @param tile The tile to lookup
	 * @return
	 */
	public GameObject[] getAllAt(Tile tile) {
		Set<GameObject> objects = getAtLocal(tile.getX() - ctx.getClient().getOriginX(), tile.getY() - ctx.getClient().getOriginY(), TYPE_INTERACTABLE);
		return objects.toArray(new GameObject[objects.size()]);
	}

	/**
	 * Returns the first object at the given tile, if any
	 * @param tile The tile to lookup
	 * @return
	 */
	public GameObject getAt(Tile tile) {
		GameObject[] objs = getAllAt(tile);
		if (objs == null || objs.length == 0 || objs[0] == null) {
			return null;
		}
		return objs[0];
	}

	/**
	 * Returns the nearest object from the local player which satisfies the filter
	 * @param filter The filter to accept objects, can be null
	 * @return The nearest object
	 */
	public GameObject getNearest(Filter<GameObject> filter) {
		GameObject[] objects = getAll(null, TYPE_ALL);
		GameObject nearest = null;
		Actor player = ctx.players.getLocalPlayer();
		for (GameObject obj : objects) {
			if (obj == null) continue;

			if (nearest == null || (player.getLocation().distanceTo(obj.getLocation()) < player.getLocation().distanceTo(nearest.getLocation())
					&& (filter == null || filter.accept(obj)))) {
				nearest = obj;
			}
		}
		return nearest;
	}

	/**
	 * Returns an array of all game objects which satisfy the filter
	 * @param filter The filter
	 * @return
	 */
	public GameObject[] getAll(Filter<GameObject> filter, int mask) {
		Set<GameObject> objects = new LinkedHashSet<GameObject>();
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				for (GameObject o : getAtLocal(x, y, mask)) {
					if (filter == null || filter.accept(o)) {
						objects.add(o);
					}
				}
			}
		}
		return objects.toArray(new GameObject[objects.size()]);
	}

	public Set<GameObject> getAtLocal(int x, int y, int mask) {
		IClient client = ctx.getClient();
		Set<GameObject> objects = new LinkedHashSet<GameObject>();
		if (client.getScene().getSceneTiles() == null) {
			return objects;
		}

		try {
			int plane = client.getPlane();
			ISceneTile tile = client.getScene().getSceneTiles()[plane][x][y];

			if (tile != null) {

				x += client.getOriginX();
				y += client.getOriginY();

				// Interactable (e.g. Trees)
				if ((mask & TYPE_INTERACTABLE) != 0) {
					for (ISceneObject obj : tile.getInteractableObjects()) {
						if (obj != null) {
							GameObject gameobj = new GameObject(ctx, obj, GameObject.Type.INTERACTABLE, plane, new Tile(x, y, obj.getGridX(), obj.getGridY()));

							if (gameobj.getId() != -1) {
								objects.add(gameobj);
							}
						}
					}
				}
				
				if ((mask & TYPE_FLOOR_DECORATION) != 0) {
					if (tile.getFloorDecoration() != null) {
						IFloorDecoration dec = tile.getFloorDecoration();
						
						GameObject gameobj = new GameObject(ctx, dec, GameObject.Type.FLOOR_DECORATION, plane, new Tile(x, y, dec.getGridX(), dec.getGridY()));
						if (gameobj.getId() != -1) {
							objects.add(gameobj);
						}
					}
				}
				
				if ((mask & TYPE_WALL_DECORATION) != 0) {
					if (tile.getWallDecoration() != null) {
						IWallDecoration dec = tile.getWallDecoration();
						GameObject gameobj = new GameObject(ctx, dec, GameObject.Type.WALL_DECORATION, plane, new Tile(x, y, dec.getGridX(), dec.getGridY()));
						
						if (gameobj.getId() != -1) {
							objects.add(gameobj);
						}
					}
				}
				
				if ((mask & TYPE_BOUNDARY) != 0) {
					if (tile.getBoundary() != null) {
						IWall dec = tile.getBoundary();
						GameObject gameobj = new GameObject(ctx, dec, GameObject.Type.BOUNDARY, plane, new Tile(x, y, dec.getGridX(), dec.getGridY()));
						
						if (gameobj.getId() != -1) {
							objects.add(gameobj);
						}
					}
				}
			}
		} catch (Exception ignored) {
		}
		return objects;
	}

}
