package org.vinsert.component.debug;

import org.vinsert.bot.script.api.Actor;
import org.vinsert.bot.script.api.Model;
import org.vinsert.bot.script.api.Npc;

import java.awt.*;


public class DebugNpcHulls extends Debugger {

    private Color color = new Color(255, 1, 1, 60);

    @Override
    public void draw(Graphics2D g) {
        for (Npc npc : context.npcs.getAll()) {
            draw(g, npc, color);
        }
    }

    public void draw(Graphics2D g, Actor actor, Color color) {
        Model model = actor.getModel();

        if (model == null || !model.isValid()) return;

        g.setColor(color);
        Polygon p = actor.hull();
        if (p != null) {
            g.fillPolygon(p);
        }
    }

}
