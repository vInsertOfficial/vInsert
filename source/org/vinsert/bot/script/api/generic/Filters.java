package org.vinsert.bot.script.api.generic;

import org.vinsert.bot.script.api.Actor;
import org.vinsert.bot.script.api.Area;
import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.GroundItem;
import org.vinsert.bot.script.api.Item;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Player;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.util.Filter;

/**
 * Generic filter implementations
 * <p>
 * Simply for ease of life
 * @author tommo
 *
 */
public class Filters {
	
	/**
	 * Returns a filter which filters items which don't satisfy the predicate of
	 * <code>type.equals(element)</code>
	 * @param type The type to match
	 * @return The filter
	 */
	public static <T> Filter<T> $(final T type) {
		return new Filter<T>() {
			@Override
			public boolean accept(T element) {
				return element.equals(type);
			}
		};
	}
	
	public static Filter<GameObject> objectId(final int ... ids) {
		return new Filter<GameObject>() {
			@Override
			public boolean accept(GameObject element) {
				for (int id : ids) {
					if (element.getId() == id) return true;
				}
				return false;
			}
		};
	}
	
	public static Filter<GameObject> objectInRadius(final Tile center, final int radius) {
		return new Filter<GameObject>() {
			@Override
			public boolean accept(GameObject element) {
				return center.distanceTo(element.getLocation()) <= radius;
			}
		};
	}
	
	public static Filter<GameObject> objectInArea(final Area area) {
		return new Filter<GameObject>() {
			@Override
			public boolean accept(GameObject element) {
				return area.contains(element);
			}
		};
	}
	
	public static Filter<GroundItem> groundItemInRadius(final Tile center, final int radius) {
		return new Filter<GroundItem>() {
			@Override
			public boolean accept(GroundItem element) {
				return center.distanceTo(element.getLocation()) <= radius;
			}
		};
	}
	
	public static Filter<GroundItem> groundItemInArea(final Area area) {
		return new Filter<GroundItem>() {
			@Override
			public boolean accept(GroundItem element) {
				return area.contains(element);
			}
		};
	}
	
	public static Filter<GroundItem> groundItemId(final int ... ids) {
		return new Filter<GroundItem>() {
			@Override
			public boolean accept(GroundItem element) {
				for (int id : ids) {
					if (element.getId() == id) return true;
				}
				return false;
			}
		};
	}
	
	public static Filter<Item> itemId(final int ... ids) {
		return new Filter<Item>() {
			@Override
			public boolean accept(Item element) {
				for (int id : ids) {
					if (element.getId() == id) return true;
				}
				return false;
			}
		};
	}
	
	public static Filter<Npc> npcId(final int ... ids) {
		return new Filter<Npc>() {
			@Override
			public boolean accept(Npc element) {
				for (int id : ids) {
					if (element.getId() == id) return true;
				}
				return false;
			}
		};
	}
	
	public static Filter<Actor> actorName(final String name) {
		return new Filter<Actor>() {
			@Override
			public boolean accept(Actor element) {
				return element.getName().equals(name);
			}
		};
	}
	
	public static Filter<Actor> actorInArea(final Area area) {
		return new Filter<Actor>() {
			@Override
			public boolean accept(Actor element) {
				return area.contains(element);
			}
		};
	}
	
	public static Filter<Actor> actorInRadius(final Tile center, final int radius) {
		return new Filter<Actor>() {
			@Override
			public boolean accept(Actor element) {
				return center.distanceTo(element.getLocation()) <= radius;
			}
		};
	}
	
	public static Filter<Actor> actorAtLocation(final Tile tile) {
		return new Filter<Actor>() {
			@Override
			public boolean accept(Actor element) {
				return element.getLocation().equals(tile);
			}
		};
	}
	
	public static Filter<Npc> npcName(final String name) {
		return new Filter<Npc>() {
			@Override
			public boolean accept(Npc element) {
				return element.getName().equals(name);
			}
		};
	}
	
	public static Filter<Player> playerName(final String name) {
		return new Filter<Player>() {
			@Override
			public boolean accept(Player element) {
				return element.getName().equals(name);
			}
		};
	}
	

}
