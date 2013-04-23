package org.vinsert.bot.script.randevent.impl;

import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.randevent.RandomEvent;
import org.vinsert.bot.util.Filter;
import org.vinsert.bot.util.Timer;

import java.util.List;
 
/**
 * @author iJava, Rick
 */
@ScriptManifest(name = "Mime Solver", description = "Solves Mime Random", authors = {"iJava", "Rick"}, version = 1.0)
public class Mime extends RandomEvent {
    private static final int WIDGET_PARENT_ID = 188;
    private static final int MIME_ID = 1056;
    private Animation lastAnimation;
    private Animation lastPerformed;
 
 
    @Override
    public boolean init() {
        return npcs.getNearest(MIME_ID) != null;
    }
 
    @Override
    public int pulse() {
        Npc mime = npcs.getNearest(MIME_ID);
        if (mime != null) {
            Animation anim = Animation.forAnim(mime.getAnimation());
            if (anim != null && anim != lastAnimation) {
                lastAnimation = anim;
            }
        }
 
        if (lastPerformed == lastAnimation) {
            return 50;
        }
 
        List<Widget> validatedWidgets = widgets.getValidated(ANIM_FILTER);
        if (validatedWidgets.size() == 1) { // This should work fine..?
            Widget widget = validatedWidgets.get(0);
            if (widget.isValid()) {
                widget.click();
                Timer t = new Timer(1000);
                while (t.isRunning() && widget.isValid()) {
                    sleep(50);
                }
                lastPerformed = lastAnimation;
                return 50;
            }
        }
        return 100;
    }
 
    @Override
    public void close() {
        log("Mime Solver by iJava Finished.");
    }
 
    @Override
    public RandomEventPriority priority() {
        return RandomEventPriority.HIGH;
    }
 
    private final Filter<Widget> ANIM_FILTER = new Filter<Widget>() {
        @Override
        public boolean accept(Widget w) {
            return w.getParentId() == WIDGET_PARENT_ID
                    && w.getId() == lastAnimation.widget;
        }
    };
 
    private static enum Animation {
 
        CRY(860, 2),
        THINK(857, 3),
        LAUGH(861, 4),
        DANCE(866, 5),
        CLIMB_ROPE(1130, 6),
        LEAN(1129, 7),
        GLASS_WALL(1128, 8),
        GLASS_BOX(1131, 9);
 
        int anim, widget;
 
        Animation(int anim, int widget) {
            this.anim = anim;
            this.widget = widget;
        }
 
        static Animation forAnim(int anim) {
            for (Animation animation : values()) {
                if (anim == animation.anim) {
                    return animation;
                }
            }
            return null;
        }
    }
}