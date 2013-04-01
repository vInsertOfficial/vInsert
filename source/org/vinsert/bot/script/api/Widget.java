package org.vinsert.bot.script.api;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.generic.MultiSetIterator;
import org.vinsert.bot.util.Filter;
import org.vinsert.bot.util.Utils;
import org.vinsert.insertion.IWidget;
import org.vinsert.insertion.IWidgetNode;


/**
 * Wraps the widget (aka Interface) class in the client
 * @author tommo
 */
public class Widget {
	
	private ScriptContext ctx;
	private IWidget widget;
	
	public Widget(final ScriptContext ctx, IWidget widget) {
		this.ctx = ctx;
		this.widget = widget;
	}
	
	public String getString1() {
		return widget.getString1();
	}
	
	public String getString2() {
		return widget.getString2();
	}
	
	/**
	 * @return The widget's model id
	 */
	public int getModelId() {
		return widget.getModelId();
	}
	
	/**
	 * @return The selected action
	 */
	public String getSelectedAction() {
		return widget.getSelectedAction();
	}
	
	/**
	 * Gets the text on this widget, if applicable.
	 * @return The text.
	 */
	public String getText() {
		return widget.getText();
	}
	
	/**
	 * Gets the name of this widget.
	 * @return The name.
	 */
	public String getName() {
		return widget.getName();
	}
	
	/**
	 * @return The spell name, if applicable
	 */
	public String getSpellName() {
		return widget.getSpellName();
	}
	
	/**
	 * Returns an array of actions belonging to the widget
	 * @return
	 */
	public String[] getActions() {
		return widget.getActions();
	}
	
	/**
	 * Clicks a random point within the widget
	 */
	public void click() {
		Point point = getClickPoint();
		ctx.mouse.click(point.x, point.y);
		Utils.sleep(Utils.random(45, 150));
	}
	
	/**
	 * Searches all child components for a given phrase
	 * @param phrase The phrase to search for
	 * @return <b>true</b> if found, <b>false</b> if not.
	 */
	public boolean containsAction(final String phrase) {
		if (widget.getActions() == null || widget.getActions().length == 0) {
			return false;
		}
		
		for (int i = 0; i < widget.getActions().length; i++) {
			if (widget.getActions()[i] != null && widget.getActions()[i].contains(phrase)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Searches child widgets for the given id
	 * @param id The id to search for
	 * @return The child widget, if not null
	 */
	public Widget getChild(final int id) {
		if (widget.getChildren() == null) {
			return null;
		}
		
		return new Widget(ctx, widget.getChildren()[id]);
	}
	
	/**
	 * @return A list of all valid child widgets
	 */
	public List<Widget> getChildren() {
		return getChildren(null);
	}
	
	/**
	 * @param filter The filter to deduct the appliance of a widget in the list
	 * @return A list of all valid child widgets which satisfy the filter
	 */
	public List<Widget> getChildren(Filter<Widget> filter) {
		List<Widget> children = new ArrayList<Widget>();
		
		if (widget.getChildren() == null || widget.getChildren().length == 0) {
			return children;
		}
		
		for (int i = 0; i < widget.getChildren().length; i++) {
			if (widget.getChildren()[i] != null) {
				Widget w = new Widget(ctx, widget.getChildren()[i]);
				if (filter == null || filter.accept(w)) {
					children.add(w);
				}
			}
		}
		return children;
	}
	
	/**
	 * @return The widget's child count
	 */
	public int getChildCount() {
		if (widget.getChildren() == null) {
			return 0;
		} else {
			return widget.getChildren().length;
		}
	}
	
	/**
	 * @return The widget's id
	 */
	public int getId() {
		return widget.getId();
	}
	
	/**
	 * @return The widget's parent id
	 */
	public int getParentId() {
		return widget.getParentId();
	}
	
	/**
	 * @return The widget's parent index
	 */
	public int getParentIndex() {
		return widget.getId() << 16;
	}
	
	/**
	 * @return The widget index
	 */
	public int getIndex() {
		return widget.getId() & 0xFFFF;
	}
	
	/**
	 * Generates a clickable point within the bounds
	 * of this widget
	 * @return The click point
	 */
	public Point getClickPoint() {
		int x = Utils.random(getX() + 4, getX() + getBounds().width - 4);
		int y = Utils.random(getY() + 4, getY() + getBounds().height - 4);
		return new Point(x, y);
	}
	
	/**
	 * The widget's absolute X position
	 * @return
	 */
	public int getX() {
		if (widget == null) {
			return -1;
		}
		
		Widget parent = getParent();
		int x = 0;
		if (parent != null) {
			x = parent.getX();
		} else {
			int[] posx = ctx.getClient().getPaneXPos();
			if (widget.getStaticPos() != -1 && posx[widget.getStaticPos()] > 0) {
				return (posx[widget.getStaticPos()] + widget.getX());
			}
		}
		return (widget.getX() + x);
	}
	
	/**
	 * The widget's absolute Y position
	 * @return
	 */
	public int getY() {
		if (widget == null) {
			return -1;
		}
		
		Widget parent = getParent();
		int y = 0;
		if (parent != null) {
			y = parent.getY();
		} else {
			int[] posy = ctx.getClient().getPaneYPos();
			if (widget.getStaticPos() != -1 && posy[widget.getStaticPos()] > 0) {
				return (posy[widget.getStaticPos()] + widget.getY());
			}
		}
		return (widget.getY() + y);
	}
	
	/**
	 * @return The widget's x coordinate relative to the parent
	 */
	public int getRelativeX() {
		return widget.getX();
	}
	
	/**
	 * @return The widget's y coordinate relative to the parent
	 */
	public int getRelativeY() {
		return widget.getY();
	}
	
	/**
	 * @return The width of the interface
	 */
	public int getWidth() {
		return widget.getWidth();
	}
	
	/**
	 * @return The height of the interface
	 */
	public int getHeight() {
		return widget.getHeight();
	}
	
	/**
	 * @return A {@link Rectangle} representing the effective area covered by the widget
	 */
	public Rectangle getBounds() {
		return new Rectangle(getX(), getY(), widget.getWidth(), widget.getHeight());
	}
	
	/**
	 * @return <b>true</b> if the widget has a valid parent widget, <b>false</b> if not
	 */
	public boolean hasParent() {
		return getParentId() >= 0 && getParentId() < ctx.getClient().getWidgets().length;
	}
	
	/**
	 * @return The widget's parent, may be null
	 */
	public Widget getParent() {
        if (widget == null) {
            return null;
        }
        int uid = getParentId();
        if (uid == -1) {
            int groupIdx = getId() >>> 16;
            MultiSetIterator hti = new MultiSetIterator(ctx.getClient().getInterfaceNodes());
            for (IWidgetNode n = (IWidgetNode) hti.getFirst(); n != null; n = (IWidgetNode) hti.getNext()) {
                if (n.getId() == groupIdx) {
                    uid = (int) n.getId();
                }
            }
        }
        if (uid == -1) {
            return null;
        }
        int parent = uid >> 16;
        int child = uid & 0xffff;
        return ctx.widgets.get(parent, child);
    }
	
	/**
	 * Returns the root widget
	 * @return
	 */
	public Widget getRoot() {
		return new Widget(ctx, widget.getRoot());
	}
	
	/**
	 * @return The tooltip string, may be null
	 */
	public String getTooltip() {
		return widget.getTooltip();
	}
	
	/**
	 * @return The widget's slot content ids
	 */
	public int[] getSlotContents() {
		return widget.getSlotContents();
	}
	
	/**
	 * @return The widget's slot content stack sizes
	 */
	public int[] getSlotSizes() {
		return widget.getSlotSizes();
	}

	public int getScrollVMax() {
		return widget.getScrollVMax();
	}
	
	public int getScrollHMax() {
		return widget.getScrollHMax();
	}

	public boolean isValid() {
		return widget != null;
	}

}
