package org.vinsert.bot.script.api.tools;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Actor;
import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.Item;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.util.Filter;
import org.vinsert.bot.util.Utils;


/**
 * Inventory utilities
 * @author tommo
 *
 */
public class Inventory {
	
	public static final int INVENTORY_ID = 149;

	private ScriptContext ctx;
	
	public Inventory(ScriptContext ctx) {
		this.ctx = ctx;
	}
	
	/**
	 * @return The amount of items in the inventory
	 */
	public int size() {
		int count = 0;
		Item[] items = getItems();
		for (Item item : items) {
			if (item != null && item.isValid()) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Returns an array of unique items (that is, only 1 of each item type
	 * in the inventory)
	 * @return
	 */
	public Item[] getUniqueItems() {
		List<Item> unique = new ArrayList<Item>();
		Item[] items = getItems();
		for (Item item : items) {
			if (!unique.contains(item)) {
				unique.add(item);
			}
		}

		return unique.toArray(new Item[unique.size()]);
	}
	
	/**
	 * Calculates the amount of items with a given id in the inventory
	 * @param countStacks Should stack sizes be counted
	 * @param ids The ids to check for
	 * @return The amount of items with the given id
	 */
	public int getCount(boolean countStacks, int... ids) {
        Item[] items = getItems();
        int count = 0;
        for(Item item : items)
                for(int i : ids) {
                        if(item.getId() == i)
                                count += countStacks ? item.getAmount() : 1;
                }
        return count;
	}
	
	/**
	 * Checks if the inventory is full
	 * @return true if the inventory is full, false if not
	 */
	public boolean isFull() {
		return size() >= 28;
	}
	
	/**
	 * Checks if the inventory is empty
	 * @return true if the inventory is empty, false if not
	 */
	public boolean isEmpty() {
		return size() <= 0;
	}
	
	/**
	 * checks free space in the inventory.
	 * @return amount of free spaces in inventory.
	 */
	public int freeSpace() {
		return getCapacity() - size();
	}
	
	/**
	 * Checks if the inventory contains an item which
	 * satisfies the filter
	 * @param filter The item filter
	 * @return <i>true</i> if the inventory contains such an item, <i>false</i> if not
	 */
	public boolean contains(Filter<Item> filter) {
		Item[] items = getItems();
        if(items == null) {
        	return false;
        }
        
        for (int i = 0; i < items.length; i++) {
        	if (items[i] == null) continue;
        	if (filter == null || filter.accept(items[i])) {
        		return true;
        	}
        }
        return false;
	}
	
	/**
	 * Returns an array of {@link Item}'s in the inventory
	 * @return The items in the inventory
	 */
	public Item[] getItems() {
		return getItems(new Filter<Item>() {
			public boolean accept(Item element) {
				return true;
			}
		});
	}
	
	/**
	 * Returns the first item with a matching id
	 * @param id The ids to match
	 * @return The item
	 */
	public Item getItem(final int ... ids) {
		Item[] items = getItems();
		for (Item item : items) {
			for (int id : ids) {
				if (item.getId() == id) {
					return item;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns an array of {@link Item}'s in the inventory which satisfy the filter
	 * @param filter The item filter
	 * @return The items in the inventory
	 */
	public Item[] getItems(Filter<Item> filter) {
		if (ctx.getClient().getWidgets()[INVENTORY_ID][0] == null) return null;
		Widget inventory = new Widget(ctx, ctx.getClient().getWidgets()[INVENTORY_ID][0]);

		int[] ids = inventory.getSlotContents();
		int[] stacks = inventory.getSlotSizes();
		if (ids == null || stacks == null) return null;
		
		Item[] items = new Item[ids.length];
		for (int i = 0; i < ids.length; i++) {
			Item item = new Item(ids[i], stacks[i]);
			if (filter == null || filter.accept(item)) {
				items[i] = item;
			}
		}
		
		return items;
	}
	
	/**
	 * Returns an array of items which match one of the given ids
	 * @param id The ids to match
	 * @return The items
	 */
	public Item[] getItems(final int ... ids) {
		Item[] items = getItems();
		List<Item> list = new ArrayList<Item>();
		for (Item item : items) {
			for (int id : ids) {
				if (item.getId() == id) {
					list.add(item);
				}
			}
		}
		return list.toArray(new Item[list.size()]);
	}
	
	/**
	 * Returns the {@link Item} at the specified slot
	 * @param slot The slot in the inventory
	 * @return The {@link Item}, or null if slot wasn't valid
	 */
	public Item getItem(int slot) {
		if (slot < 0 || slot > 28) return null;
		Item[] items = getItems();
		if (items != null) {
			return getItems()[slot];
		} else {
			return null;
		}
	}
	
	/**
	 * Returns the slot which the item is located in
	 * @param item The item to check for
	 * @return The slot, of -1 if not found
	 */
	public int getSlot(Item item) {
		return indexOf(item);
	}
	
	/**
	 * Calculates the first index of an item in the inventory
	 * which matches the given item
	 * @param item The item for search for
	 * @return The index in the inventory of the item, or -1 if not present
	 */
	public int indexOf(Item item) {
		Item[] items = getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i] == null) continue;
			
			if (items[i].equals(item)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Calculates the first index of an item in the inventory
	 * which satisfies the filter
	 * @param filter The filter to accept an item
	 * @return The index in the inventory of the item, or -1 if not present
	 */
	public int indexOf(Filter<Item> filter) {
		Item[] items = getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i] == null) continue;
			
			if (filter.accept(items[i])) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Calculates the screen position of an
	 * inventory slot
	 * @return The point on screen
	 */
	public Point getLocation(int slot) {
		int col = (slot % 4);
		int row = (slot / 4);
		int x = 580 + (col * 42);
		int y = 228 + (row * 36);
		return new Point(x, y);
	}

	/**
	 * @return The maximum capacity of the inventory
	 */
	public int getCapacity() {
		return 28;
	}
	
	/**
	 * Generates a random click position for a given slot
	 * in the inventory
	 * @param slot The slot to click for
	 * @return The point
	 */
	public Point getClickPoint(int slot) {
		Point loc = getLocation(slot);
		loc.setLocation(loc.x + (-12 + Utils.random(0, 24)), loc.y + (-12 + Utils.random(0, 24)));
		return loc;
	}
	
	/**
	 * Interacts with an item at the given slot in the inventory
	 * @param slot The slot (found by <code>indexOf(id)</code>
	 * @param action The action to search for
	 * @return true if interaction was successful, false if not
	 */
	public boolean interact(int slot, String action) {
		Point point = getClickPoint(slot);
		ctx.mouse.move(point.x, point.y);
		Utils.sleep(Utils.random(100, 400));
		
		int index = ctx.menu.getIndex(action);
		
		if (index == -1) return false;
		else if (index == 0) {
			ctx.mouse.click();
			Utils.sleep(Utils.random(200, 400));
		} else {
			ctx.mouse.click(true);

			Point menuPoint = ctx.menu.getClickPoint(index);
			ctx.mouse.click(menuPoint.x, menuPoint.y);
			Utils.sleep(Utils.random(350, 650));
		}
		
		return true;
	}
	
	/**
	 * Uses the inventory item at the specified slot with an game object
	 * @param slot The slot of the inventory item. (Can be found by <code>indexOf()</code>
	 * @param object The object to use the item with
	 * @return true if the item was used, false if not
	 */
	public boolean useItem(int slot, GameObject object) {
		Point point = getClickPoint(slot);
		ctx.mouse.move(point.x, point.y);
		Utils.sleep(Utils.random(100, 400));
		int index = ctx.menu.getIndex("Use");
		if (index == -1) return false;
		
		if (index == 0) {
			ctx.mouse.click();
			Utils.sleep(Utils.random(200, 400));
		} else {
			ctx.mouse.click(true);

			Point menuPoint = ctx.menu.getClickPoint(index);
			ctx.mouse.click(menuPoint.x, menuPoint.y);
			Utils.sleep(Utils.random(350, 650));
		}
		
		Point objPoint = object.hullPoint(object.hull());
		ctx.mouse.move(objPoint.x, objPoint.y);
		Utils.sleep(Utils.random(45, 100));
		ctx.mouse.click();
		
		return true;
	}
	
	/**
	 * Uses the inventory item at the specified slot with an actor
	 * @param slot The slot of the inventory item. (Can be found by <code>indexOf()</code>
	 * @param actor The actor to use the item with
	 * @return true if the item was used, false if not
	 */
	public boolean useItem(int slot, Actor actor) {
		Point point = getClickPoint(slot);
		ctx.mouse.move(point.x, point.y);
		Utils.sleep(Utils.random(50, 250));
		int index = ctx.menu.getIndex("Use");
		if (index == -1) return false;
		
		if (index == 0) {
			ctx.mouse.click();
			Utils.sleep(Utils.random(200, 400));
		} else {
			ctx.mouse.click(true);

			Point menuPoint = ctx.menu.getClickPoint(index);
			ctx.mouse.click(menuPoint.x, menuPoint.y);
			Utils.sleep(Utils.random(350, 650));
		}
		
		Point objPoint = actor.hullPoint(actor.hull());
		ctx.mouse.move(objPoint.x, objPoint.y);
		Utils.sleep(Utils.random(45, 100));
		ctx.mouse.click();
		
		return true;
	}
	
	/**
	 * Uses the inventory item at the specified slot with another item
	 * @param slot The slot of the inventory item. (Can be found by <code>indexOf()</code>
	 * @param otherSlot the slot of the item to use the first with
	 * @return true if the item was used, false if not
	 */
	public boolean useItem(int slot, int otherSlot) {
		if (slot == -1 || otherSlot == -1) return false;
		
		Point point = getClickPoint(slot);
		ctx.mouse.move(point.x, point.y);
		Utils.sleep(Utils.random(100, 400));
		int index = ctx.menu.getIndex("Use");
		if (index == -1) return false;
		
		if (index == 0) {
			ctx.mouse.click();
			Utils.sleep(Utils.random(200, 400));
		} else {
			ctx.mouse.click(true);

			Point menuPoint = ctx.menu.getClickPoint(index);
			ctx.mouse.click(menuPoint.x, menuPoint.y);
			Utils.sleep(Utils.random(350, 650));
		}
		
		Point otherPoint = getClickPoint(otherSlot);
		ctx.mouse.move(otherPoint.x, otherPoint.y);
		Utils.sleep(Utils.random(100, 400));
		ctx.mouse.click();
		
		return true;
	}
	
}
