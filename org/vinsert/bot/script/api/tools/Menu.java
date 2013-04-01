package org.vinsert.bot.script.api.tools;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.util.Utils;
import org.vinsert.insertion.IClient;


/**
 * Tools for the right click menu.
 * @author `Discardedx2
 */
public class Menu {

	/**
	 * The context.
	 */
	private ScriptContext ctx;

	public Menu(ScriptContext ctx) {
		this.ctx = ctx;
	}

	/**
	 * Gets the index of a choice in the choices list.
	 * @param choice The choice to check for.
	 * @return The index of the choice, or -1 if none.
	 */
	public int getIndex(String choice) {
		choice = choice.toLowerCase();
		List<String> choices = getChoices();
		for (String s : choices) {
			if (s.toLowerCase().contains(choice.toLowerCase())) {
				return choices.indexOf(s);
			}
		}
		return -1;
	}

	/**
	 * Gets a choice at a specified index.
	 * @param index The index.
	 * @return The choice.
	 */
	public String getChoice(int index) {
		List<String> choices = getChoices();

		if (index >= choices.size() || index < 0) {
			return null;
		}
		return choices.get(index);
	}
	
	/**
	 * Gets a choice target at a specified index.
	 * @param index The index.
	 * @return The target.
	 */
	public String getTarget(int index) {
		List<String> targets = getTargets();

		if (index >= targets.size() || index < 0) {
			return null;
		}
		return targets.get(index);
	}

	/**
	 * Gets all, non null choices and sorts them in an ArrayList.
	 * @return The choices.
	 */
	public List<String> getChoices() {
		List<String> choices = new ArrayList<String>();

		String[] menuChoices = ctx.getClient().getMenuActions();
		for (int i = menuChoices.length - 1; i >= 0; i--) {
			if (menuChoices[i] != null) {
				choices.add(String.valueOf(menuChoices[i]));
			}
		}
		return choices.subList(choices.size() - ctx.getClient().getMenuSize(), choices.size());
	}
	
	/**
	 * Returns a list of all valid menu targets, which are the
	 * strings to the right of the menu choices
	 * @return
	 */
	public List<String> getTargets() {
		List<String> choices = new ArrayList<String>();

		String[] targets = ctx.getClient().getMenuTargets();
		for (int i = targets.length - 1; i >= 0; i--) {
			if (targets[i] != null) {
				choices.add(String.valueOf(targets[i]));
			}
		}
		return choices.subList(choices.size() - ctx.getClient().getMenuSize(), choices.size());
	}
	
	/**
	 * Checks to see if this menu is currently open.
	 * @return {@code true} if the menu is open.
	 */
	public boolean isMenuOpen() {
		return ctx.getClient().isMenuOpen();
	}

	/**
	 * Gets the current bounds of the menu.
	 * @return The menu bounds.
	 */
	public Rectangle getBounds() {
		IClient client = ctx.getClient();
		return new Rectangle(client.getMenuX(), client.getMenuY(), client.getMenuWidth(), client.getMenuHeight());
	}

	/**
	 * Gets a clickable point at a certain index on the menu.
	 * @param index The slot to click.
	 * @return The point.
	 */
	public Point getClickPoint(int index) {
		Rectangle bounds = getBounds();
		Point menu = new Point(bounds.x + 4, bounds.y + 4);

		int pad = Math.random() > 0.5 ? bounds.width / 4 : bounds.width / 5;
		menu.x += pad;
		
		return new Point(menu.x + Utils.random(4, bounds.width - 4), menu.y + Utils.random(23, 30) + 15 * index);
	}

	/**
	 * Clicks an index in the menu
	 * @param index
	 * @return true if the index was clicked, false if not
	 */
	public boolean click(int index) {
		Point p = getClickPoint(index);
		ctx.mouse.click(p.x, p.y);
		return true;
	}
	
	/**
	 * Clicks an index in the menu which contains an action
	 * @param action The action to click
	 * @return true if the action was clicked, false if not
	 */
	public boolean click(String action) {
		int index = getIndex(action);
		if (index >= 0) {
			Point p = getClickPoint(index);
			ctx.mouse.click(p.x, p.y);
			return true;
		}
		return false;
	}
	
}
