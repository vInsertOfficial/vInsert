package org.vinsert.bot.script.randevent.impl;

import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.randevent.RandomEvent;
import org.vinsert.bot.util.Filter;
import org.vinsert.bot.util.Timer;

import java.util.List;

/**
 * @author iJava
 */
@ScriptManifest(name = "Iles Random Solver", authors = {"iJava"}, description = "Solves the iles random")
public class Iles extends RandomEvent {

    private final static int[] NPC_IDS = {2538, 2536, 2537};
    private final static int PARENT_ID = 184;
    private final static int MODEL_W_ID = 7;

    @Override
    public boolean init() {
        Npc iles = npcs.getNearest(NPC_IDS);
        if (iles != null) {
            if (iles.getSpeech() != null) {
                if (iles.getSpeech().contains(localPlayer.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int pulse() {
        if (localPlayer.isMoving() || localPlayer.getAnimation() != -1) {
            return 300;
        }
        if(widgets.get(242, 4) != null) {
            widgets.get(242, 4).click();
            Timer t = new Timer(5000);
            while(t.isRunning()) {
                Npc iles = npcs.getNearest(NPC_IDS);
                if(iles == null) {
                    break;
                }
                sleep(100);
            }
        }
        List<Widget> valid = widgets.getValidated(new Filter<Widget>() {
            @Override
            public boolean accept(Widget element) {
                return element.getParentId() == PARENT_ID;
            }
        });
        if (valid.size() == 14) {
            Answer answer = Answer.fromModel(valid.get(MODEL_W_ID).getModelId());
            if (answer != null) {
                for (Widget widget : widgets.getValidated(new Filter<Widget>() {
                    @Override
                    public boolean accept(Widget element) {
                        return element.getParentId() == PARENT_ID && element.getId() > 0 && element.getId() < 4;
                    }
                })) {
                    if (widget.getText().contains(answer.getName())) {
                        Widget actual = widgets.get(PARENT_ID, widget.getId() + 7);
                        actual.click();
                        Timer t = new Timer(3000);
                        while (t.isRunning()) {
                            if (answer.getModelId() != valid.get(MODEL_W_ID).getModelId()) {
                                break;
                            }
                            sleep(150);
                        }
                    }
                }
            }
        }
        Npc iles = npcs.getNearest(NPC_IDS);
        if (iles != null) {
            if (iles.interact("Talk-to")) {
                Timer t = new Timer(1500);
                while (t.isRunning()) {
                    if (widgets.getValidated(new Filter<Widget>() {
                        @Override
                        public boolean accept(Widget element) {
                            return element.getParentId() == PARENT_ID;
                        }
                    }).size() == 14) {
                        break;
                    }
                    sleep(100);
                }
            }
            return 500;
        }
        return 200;
    }

    @Override
    public void close() {
        log("Iles Random Solver By IJava Finished!");
    }

    @Override
    public RandomEventPriority priority() {
        return RandomEventPriority.MEDIUM;
    }

    private static enum Answer {
        FISH(8829, "fish"), RING(8834, "ring"), SWORD(8836, "sword"),
        SPADE(8837, "spade"), SHIELD(8832, "shield"), AXE(8828, "axe"),
        SHEARS(8835, "shears"), HELMET(8833, "helmet");

        private int modelId;
        private String name;

        private Answer(int modelId, String name) {
            this.modelId = modelId;
            this.name = name;
        }

        private static Answer fromModel(int model) {
            for (Answer answer : values()) {
                if (answer.getModelId() == model) {
                    return answer;
                }
            }
            return null;
        }

        public String getName() {
            return name;
        }

        public int getModelId() {
            return modelId;
        }
    }
}
