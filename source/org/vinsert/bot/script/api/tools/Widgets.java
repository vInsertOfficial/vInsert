package org.vinsert.bot.script.api.tools;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.util.Filter;
import org.vinsert.insertion.IWidget;

import java.util.ArrayList;
import java.util.List;


/**
 * Tools for the on screen widgets.
 *
 * @author `Discardedx2
 */
public class Widgets {

    /**
     * The context.
     */
    private ScriptContext ctx;

    public Widgets(ScriptContext ctx) {
        this.ctx = ctx;
    }

    /*
     * returns an array of all valid group ids
     */
    public ArrayList<Integer> getValidParentIds() {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for (Widget w : getValidated()) {
            if (!ids.contains(w.getParentId())) {
                ids.add(w.getParentId());
            }
        }
        return ids;
    }

    /**
     * Returns an array of all of the widgets in a group
     *
     * @param group The group index
     * @return The array of widgets, or null if not applicable
     */
    public Widget[] get(int group) {
        if (group >= ctx.getClient().getWidgets().length
                || ctx.getClient().getWidgets()[group] == null) return new Widget[0];
        IWidget[] widgets = ctx.getClient().getWidgets()[group];
        Widget[] valid = new Widget[widgets.length];

        for (int j = 0; j < widgets.length; j++) {
            if (widgets[j] == null) continue;

            Widget w = new Widget(ctx, ctx.getClient().getWidgets()[group][j]);
            valid[j] = w;
        }

        return valid;
    }

    /**
     * Returns the widget at the specified indexes
     *
     * @param group The group index
     * @param child The child index
     * @return The widget
     */
    public Widget get(int group, int child) {
        Widget[] widgets = get(group);
        if (widgets != null && child < widgets.length && widgets[child] != null) {
            return widgets[child];
        }
        return null;
    }

    /**
     * Gets the non null widgets.
     *
     * @param filter The filter.
     * @return the valid widgets.
     */
    public List<Widget> getValidated(Filter<Widget> filter) {
        List<Widget> valid = new ArrayList<Widget>();
        for (int i = 0; i < ctx.getClient().getWidgets().length; i++) {
            if (ctx.getClient().getWidgets()[i] == null) continue;
            for (int j = 0; j < ctx.getClient().getWidgets()[i].length; j++) {
                if (ctx.getClient().getWidgets()[i][j] == null) continue;

                Widget w = new Widget(ctx, ctx.getClient().getWidgets()[i][j]);
                if (filter == null || filter.accept(w)) {
                    valid.add(w);
                }
            }
        }
        return valid;
    }

    /**
     * @return <tt>true</tt> if continue component is valid; otherwise
     *         <tt>false</tt>.
     * @credits PhaseCoder
     */
    public boolean canContinue() {
        return getContinueComponent() != null;
    }

    /**
     * @return <tt>true</tt> if continue component was clicked; otherwise
     *         <tt>false</tt>.
     * @credits PhaseCoder
     */
    public boolean clickContinue() {
        Widget cont = getContinueComponent();
        if (cont != null && cont.isValid()) {
            cont.click();
            return true;
        }
        return false;
    }

    /**
     * @return <tt>RSComponent</tt> containing "Click here to continue";
     *         otherwise null.
     * @credits PhaseCoder, tholomew
     */
    public Widget getContinueComponent() {
        if (ctx.getClient().getWidgets() == null) {
            return null;
        }
        List<Widget> valid = getValidated();
        for (Widget iface : valid) {
            if (iface.getParentId() != 137) {
                if (iface.getText().contains("Click here to continue")
                        && iface.isValid() && iface.getRelativeX() > 10
                        && iface.getRelativeY() > 300) {
                    return iface;
                }
            }
        }
        return null;
    }

    /**
     * Gets the non null widgets.
     *
     * @return the valid widgets.
     */
    public List<Widget> getValidated() {
        return getValidated(null);
    }

    /**
     * Gets a widget (loaded)
     *
     * @param parent the parent id
     * @param child  the child id
     * @return the Widget
     */
    public Widget getWidget(final int parent, final int child) {
        return ctx.widgets.getValidated(new Filter<Widget>() {
            @Override
            public boolean accept(Widget element) {
                return element.getParentId() == parent && element.getId() == child;
            }
        }).get(0);
    }

    /**
     * Gets all possible widgets.
     *
     * @param filter The filter to apply if any.
     * @return The widgets.
     */
    public Widget[][] getAll(Filter<Widget> filter) {
        final IWidget[][] iWidgets = ctx.getClient().getWidgets();
        Widget[][] widgets = new Widget[iWidgets.length][];

        for (int i = 0; i < widgets.length; i++) {
            if (iWidgets[i] != null) {
                widgets[i] = new Widget[iWidgets[i].length];

                for (int j = 0; j < widgets[i].length; j++) {

                    if (iWidgets[i][j] != null) {
                        Widget rsw = new Widget(ctx, iWidgets[i][j]);

                        if (filter != null) {
                            if (filter.accept(rsw)) {
                                widgets[i][j] = rsw;
                            }
                        } else {
                            widgets[i][j] = rsw;
                        }
                    }
                }
            }
        }
        return widgets;
    }

}
