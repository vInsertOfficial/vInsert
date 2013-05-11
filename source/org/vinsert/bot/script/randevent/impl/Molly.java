package org.vinsert.bot.script.randevent.impl;

import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Actor;
import org.vinsert.bot.script.api.Model;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.randevent.RandomEvent;
import org.vinsert.bot.util.Filter;
import org.vinsert.bot.util.Perspective;
import org.vinsert.bot.util.Vec3;

import java.awt.*;

/**
 * @author iJava
 */
@ScriptManifest(name="Evil Twin Solver", authors = {"iJava"}, description = "Solves evil twin random")
public class Molly extends RandomEvent {

    private static final int MOLLY_ID = 3894;
    private static final int DOOR_CLOSED_ID = 14982;
    private static final int KNOW_PARENT_ID = 228;
    private static final int KNOW_CHILD_ID = 1;
    private static final int CONTROL_ID = 14978;
    private static final int CLAW_PARENT_ID = 240;
    private static final int CLAW_NORTH_ID = 6;
    private static final int CLAW_SOUTH_ID = 11;
    private static final int CLAW_WEST_ID = 16;
    private static final int CLAW_EAST_ID = 21;
    private static final int CLAW_DROP_ID = 23;
    private Model twinModel;
    private Actor twinActor;


    @Override
    public boolean init() {
        if (game.isLoggedIn()) {
            Npc goodTwin = npcs.getNearest(MOLLY_ID);
            if (goodTwin != null) {
                twinModel = goodTwin.getModel();
                return true;
            }
        }
        return false;
    }

    @Override
    public int pulse() {
        if(twinModel == null) {
            return 200;
        }
        twinActor = npcs.getNearest(localPlayer.getLocation(), new Filter<Npc>() {
            @Override
            public boolean accept(Npc element) {
                return element.getModel() == twinModel;
            }
        });
        return 1000;
    }

    @Override
    public void close() {
        log("Finished Evil Twin Random Solver By iJava");
    }

    public void drawModel(Graphics2D graphics, Actor actor, Color color) {
            Model model = actor.getModel();

            if (model == null || !model.isValid()) return;
            Vec3[][] vectors = model.getVectors();

            graphics.setColor(color);

            int gx = actor.getLocation().getGx();
            int gy = actor.getLocation().getGy();
            for (Vec3[] points : vectors) {
                Vec3 pa = points[0];
                Vec3 pb = points[1];
                Vec3 pc = points[2];

                Point a = Perspective.trans_tile_cam(getContext().getClient(), gx + (int) pa.x, gy + (int) pc.x, 0 - (int) pb.x);
                Point b = Perspective.trans_tile_cam(getContext().getClient(), gx + (int) pa.y, gy + (int) pc.y, 0 - (int) pb.y);
                Point c = Perspective.trans_tile_cam(getContext().getClient(), gx + (int) pa.z, gy + (int) pc.z, 0 - (int) pb.z);

                if (Perspective.on_screen(a) && Perspective.on_screen(b) && Perspective.on_screen(c)) {
                    graphics.drawPolygon(new Polygon(new int[]{
                            a.x, b.x, c.x
                    }, new int[]{
                            a.y, b.y, c.y
                    }, 3));
                }
            }
        }

    @Override
    public void render(Graphics2D g) {
        if(twinActor != null) {
            drawModel(g, twinActor, Color.GRAY);
        }
    }

    @Override
    public RandomEventPriority priority() {
        return RandomEventPriority.HIGH;
    }

    public void controlClaw() {

    }
}
