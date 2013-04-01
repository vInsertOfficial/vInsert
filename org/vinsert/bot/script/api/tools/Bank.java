package org.vinsert.bot.script.api.tools;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Item;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.util.Filter;
import org.vinsert.bot.util.Utils;
import org.vinsert.insertion.IWidget;


/**
 * Bank utilities
 * @author tommo
 * @author Discardedx2
 */
public class Bank {

	/**
	 * The "X" widget.
	 */
	public static final int BANK_EXIT = 103;
	/**
	 * The bank group id.
	 */
	public static final int INTERFACE_ID = 12;
	/**
	 * The bank pane id.
	 */
	public static final int ITEM_PANE_ID = 89;
	/**
	 * The capacity of the bank.
	 */
	public static final int CAPACITY = 400;
	/**
	 * The script context.
	 */
	private ScriptContext ctx;

	public Bank(ScriptContext ctx) {
		this.ctx = ctx;
	}

	/**
	 * Checks if the bank interface is currently open
	 * @return true if the bank is open, false if not
	 */
	public boolean isOpen() {
		if (ctx.getClient().getWidgets()[INTERFACE_ID] == null
				|| ctx.getClient().getWidgets()[INTERFACE_ID][ITEM_PANE_ID] == null) return false;

		IWidget pane = ctx.getClient().getWidgets()[INTERFACE_ID][ITEM_PANE_ID];
		
		for (int i = 0; i < pane.getSlotContents().length; i++) {
			if (pane.getSlotContents()[i] == 0 && pane.getSlotSizes()[i] > 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns all items in the bank pane.
	 * @return A list of items.
	 */
	public Item[] getItems() {
		if (!isOpen()) {
			return null;
		}

		Widget itemPane = new Widget(ctx, ctx.getClient().getWidgets()[INTERFACE_ID][ITEM_PANE_ID]);
		List<Item> items = new ArrayList<Item>();

		for (int i = 0; i < CAPACITY; i++) {
			if (itemPane.getSlotContents()[i] != 0) {
				items.add(new Item(itemPane.getSlotContents()[i], itemPane.getSlotSizes()[i]));
			}
		}
		return items.toArray(new Item[0]);
	}

	/**
	 * Gets an item at a specified index.
	 * @param index The index.
	 * @return The item.
	 */
	public Item getItem(int index) {
		if (index < 0 || index > CAPACITY) {
			return null;
		}
		return getItems()[index];
	}

	/**
	 * Calculates the screen position of a
	 * bank slot
	 * @return The point on screen
	 */
	public Point getMidpoint(int slot) {

		Widget child = new Widget(ctx, ctx.getClient().getWidgets()[12][89]);

		Rectangle area = child.getBounds();

		//TODO scroll h/v max
//		int x = (slot % 8) * 47 + area.x + 57;
//		int y = (((slot / 8) * 37 + area.y + 75) - child.getScrollVMax());
		int x = (slot % 8) * 47 + area.x + 57;
		int y = (((slot / 8) * 37 + area.y + 75));
		return new Point(x, y);
	}
	
	/**
	 * Deposits a specified item id
	 * @param item The item to deposit
	 * @param amount The amount. (0 deposits all)
	 */
	public void deposit(Item item, int amount) {
		if (!isOpen()) return;
		
		if (ctx.inventory.indexOf(item) == -1) return;
		
		switch (amount) {
		case 0:
			Point point = ctx.inventory.getClickPoint(ctx.inventory.indexOf(item));
			ctx.mouse.move(point.x, point.y);
			Utils.sleep(Utils.random(100, 200));
			ctx.mouse.click(true);
			
			int index = ctx.menu.getIndex("Store All");
			if (index != -1) {
				Point menuPoint = ctx.menu.getClickPoint(index);
				ctx.mouse.move(menuPoint.x, menuPoint.y);
				Utils.sleep(Utils.random(100, 200));
				ctx.mouse.click();
				Utils.sleep(Utils.random(200, 300));
			}
			break;
			
		case 1:
			Point p = ctx.inventory.getClickPoint(ctx.inventory.indexOf(item));
			ctx.mouse.move(p.x, p.y);
			Utils.sleep(Utils.random(100, 250));
			ctx.mouse.click();
			Utils.sleep(Utils.random(100, 250));
			break;
			default:
                p = ctx.inventory.getClickPoint(ctx.inventory.indexOf(item));
                ctx.mouse.move(p.x, p.y);
                Utils.sleep(Utils.random(100, 250));
                ctx.mouse.click(false);
                long start = System.currentTimeMillis();
                while(System.currentTimeMillis() - start < 3000) {
                    if(ctx.menu.getBounds() != null) {
                        break;
                    }
                   Utils.sleep(100);
                }
                p = ctx.menu.getClickPoint(ctx.menu.getIndex("Deposit-X"));
                ctx.mouse.move(p.x, p.y);
                Utils.sleep(Utils.random(100, 250));
                ctx.mouse.click();
                Utils.sleep(Utils.random(100, 250));
                ctx.keyboard.type(String.valueOf(amount));
                Utils.sleep(Utils.random(100, 250));
                break;
		}
	}
	
	/**
	 * Deposits all inventory items
	 */
	public void depositAll() {
		depositAllExcept(null);
	}
	
	/**
	 * Deposits all inventory items except for the given ids
	 * @param exceptions
	 */
	public void depositAllExcept(Filter<Item> exceptions) {
		Item[] items = ctx.inventory.getUniqueItems();
		for (Item item : items) {
			if (!item.isValid()) continue;
			if (exceptions != null && exceptions.accept(item))continue;
			
			if (ctx.inventory.getCount(true, item.getId()) > 1) {
				deposit(item, 0);
			} else {
				deposit(item, 1);
			}
		}
	}
	
	/**
	 * Checks if the bank contains atleast 1 of the items accepted by the filter
	 * @param filter The items to check for
	 * @return true if the bank contains one of the items, false if not
	 */
	public boolean contains(Filter<Item> filter) {
		if (!isOpen()) return false;
		
		Item[] items = getItems();
		
		for (int i = 0; i < items.length; i++) {
			if (items[i] == null) continue;
			Item item = items[i];
			
			if (filter.accept(item)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Withdraw an item from the bank.
	 * @param withdrawals The filter to accept which items to withdraw
	 * @param amount The amount of the items to withdraw (0 is withdraw all)
	 * @return {@code true} If the item was withdrawn.
	 */
	public boolean withdraw(Filter<Item> withdrawals, int amount) {
		Item[] items = getItems();

		for (int slot = 0; slot < items.length; slot++) {
			Item i = items[slot];
			if (i != null && withdrawals.accept(i)) {
				Point point = getClickPoint(slot);
				ctx.mouse.move(point.x, point.y);
				Utils.sleep(Utils.random(125, 250));
				ctx.mouse.click(true);

				int index = -1;
				if (amount == 0) {
					index = ctx.menu.getIndex("Withdraw All");
				} else {
					ctx.menu.getIndex("Withdraw "+amount);
				}

				if (index == -1) {
					if (!ctx.menu.getBounds().contains(ctx.mouse.getPosition())) {
						Utils.sleep(Utils.random(20, 50));
					}

					if (!ctx.menu.getBounds().contains(ctx.mouse.getPosition())) {
						return false;
					}

					Point p = ctx.menu.getClickPoint(4);
					Utils.sleep(Utils.random(125, 250));
					ctx.mouse.click(p.x, p.y);

					Utils.sleep(Utils.random(900, 1350));

					ctx.keyboard.type(String.valueOf(amount));
					ctx.keyboard.hold(KeyEvent.VK_ENTER, Utils.random(20, 50));
					Utils.sleep(Utils.random(750, 1200));
					return true;
				}

				if (!ctx.menu.getBounds().contains(ctx.mouse.getPosition())) {
					Utils.sleep(Utils.random(20, 50));
				}

				Point p = ctx.menu.getClickPoint(index);
				ctx.mouse.click(p.x, p.y);
				Utils.sleep(Utils.random(500, 900));
				return true;
			}
		}
		return false;
	}

	/**
	 * Closes the bank.
	 * @return {@code true} If the bank was closed.
	 */
	public boolean close() {
		if (!isOpen()) {
			return false;
		}

		IWidget widget = ctx.getClient().getWidgets()[12][103];

		if (widget != null) {
			Point point = new Point(widget.getX() + 10 + Utils.random(5, 10), widget.getY() + 10 + Utils.random(5, 10));
			ctx.mouse.move(point.x, point.y);
			
			Utils.sleep(Utils.random(125, 400));
			ctx.mouse.click();
			Utils.sleep(Utils.random(250, 750));
			return true;
		}
		return false;
	}

	/**
	 * Generates a random click position for a given slot
	 * in the bank
	 * @param slot The slot to click for
	 * @return The point
	 */
	public Point getClickPoint(int slot) {
		Point loc = getMidpoint(slot);
		loc.setLocation(loc.x + -20 + Utils.random(0, 30) + Utils.random(0, 5), loc.y + -25 + Utils.random(0, 15) + Utils.random(10, 20));
		return loc;
	}

}
