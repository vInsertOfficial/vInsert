package org.vinsert.bot.script.api.tools;

import java.util.Vector;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Item;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.script.api.tools.Game.Tabs;

/**
 * Equipment utilities
 * 
 * @author tommo
 * 
 */
public class Equipment {

	public static final int INTERFACE_ID = 387;

	private ScriptContext ctx;

	public Equipment(ScriptContext ctx) {
		this.ctx = ctx;
	}
	
	/**
	 * Equips the item in the inventory
	 * @param item The item to equip
	 * @return true if the item was equipped, false if not
	 */
	public boolean equip(Item item) {
		if (item == null || !ctx.inventory.contains(Filters.itemId(item.getId()))) return false;
		
		if (ctx.inventory.interact(ctx.inventory.indexOf(item), "Wield")) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Unequips an item
	 * @param item The item to unequip
	 * @return true if the item was unequipped, false if not
	 */
	public boolean unequip(Item item) {
		if (item == null || !isEquipped(item.getId())) return false;
		
		for (Slot slot : Slot.values()) {
			if (getEquippedItemID(slot) == item.getId()) {
				Widget widget = getSlotWidget(slot);
				widget.click();
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Unequips an item
	 * @param id The item id to unequip
	 * @return true if the item was unequipped, false if not
	 */
	public boolean unequip(final int id) {
		return unequip(new Item(id, 1));
	}

	/**
	 * @return Widgets of all the equipment slots.
	 */
	public Widget[] getSlotWidgets() {
		Widget[] ica = new Widget[11];
		int index = 0;
		for (Slot slot : Slot.values()) {
			int id = slot.id();
			ica[index] = ctx.widgets.get(INTERFACE_ID, id);
			index++;
		}
		return ica;
	}

	/**
	 * @param itemNames
	 *            Item(s) name to check for.
	 * @return Whether or not the specified item is currently equipped.
	 */
	public boolean isEquipped(String... itemNames) {
		setTab();
		for (Widget slot : getSlotWidgets()) {
			for (String s : itemNames)
				if (slot.containsAction(s)) {
					return true;
				}
		}
		return false;
	}

	/**
	 * @param itemIDs
	 *            ID(s) to check for.
	 * @return Whether or not the specified item is currently equipped.
	 */
	public boolean isEquipped(int... itemIDs) {
		setTab();
		for (Widget slot : getSlotWidgets()) {
			for (int i : itemIDs)
				if (slot.getParentId() == i) {
					return true;
				}
		}
		return false;
	}

	/**
	 * @return Array of all currently equipped item IDs.
	 */
	public int[] getSlotItemIDs() {
		Vector<Integer> v = new Vector<Integer>();
		for (Widget slot : getSlotWidgets()) {
			int i = slot.getParentId();
			if (i != -1) {
				v.add(i);
			}
		}
		int[] ia = new int[v.size()];
		int index = 0;
		for (int i : v) {
			ia[index] = i;
			index++;
		}
		return ia;
	}

	/**
	 * @param slot
	 *            Equipment slot to check.
	 * @return Whether or not the specified slot is empty.
	 */
	public boolean isEmpty(Slot slot) {
		return getEquippedItemID(slot) == -1;
	}

	/**
	 * @param slot
	 *            Slot to get the item name of.
	 * @return ID of item equipped in the specified equipment slot.
	 */
	public int getEquippedItemID(Slot slot) {
		setTab();
		return getSlotWidget(slot).getParentId();
	}

	/**
	 * Opens the equipment tab.
	 */
	public void setTab() {
		//TODO
//		if (game.getCurrentTab() != Tabs.EQUIPMENT) {
//			game.openTab(Tabs.EQUIPMENT);
//		}
		ctx.game.openTab(Tabs.EQUIPMENT);
	}

	/**
	 * @param s
	 *            Slot to get Widget of.
	 * @return Widget of the specified equipment slot.
	 */
	public Widget getSlotWidget(Slot s) {
		return ctx.widgets.get(INTERFACE_ID, s.id());
	}

	/**
	 * Equipment slots
	 * 
	 * @author tommo
	 */
	public static enum Slot {
		HEAD(12), CAPE(13), NECK(14), WEAPON(16), CHEST(17), SHIELD(18), LEGS(
				26), HANDS(21), FEET(20), RING(22), AMMO(15);

		private int id;

		Slot(int id) {
			this.id = id;
		}

		public int id() {
			return id;
		}
	}

}
