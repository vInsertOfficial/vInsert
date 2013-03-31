package org.vinsert.bot.script.api;


/**
 * Wraps the Item class in the client
 * @author tommo
 *
 */
public class Item {
	
	private int id;
	private int amount;
	
	public Item(int id, int amount) {
		this.id = id;
		this.amount = amount;
	}
	
	/**
	 * @return The item's id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return The amount of items in the stack
	 */
	public int getAmount() {
		return amount;
	}
	
	/**
	 * Checks if this item is valid
	 * @return <b>true</b> if the item is valid, <b>false</b> if not
	 */
	public boolean isValid() {
		return id != -1 && amount != 0;
	}
	
	@Override
	public String toString() {
		return "[id=" + id + " amt=" + amount + "]";
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Item)) return false;
		Item o = (Item) other;
		return (o.getId() == id);
	}

}
