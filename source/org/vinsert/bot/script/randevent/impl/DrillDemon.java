package org.vinsert.bot.script.randevent.impl;

import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.randevent.RandomEvent;

/**
 * @author iJava
 */
@ScriptManifest(name = "DrillDemon Solver", authors = {"iJava"}, description = "Solves drill demon random")
public class DrillDemon extends RandomEvent {

    private static final int DRILL_DEMON = 2790;
    private String lastInsn = null;

    @Override
    public boolean init() {
        Npc demon = npcs.getNearest(DRILL_DEMON);
        if (demon != null && game.isLoggedIn()) {
            return true;
        }
        return false;
    }

    @Override
    public int pulse() {
        game.logout();
        requestExit();
        return 200;
    }

    @Override
    public void close() {
        log("Drill Demon Solver by iJava Finished");
    }

    @Override
    public RandomEventPriority priority() {
        return RandomEventPriority.HIGH;
    }

    private static enum Instruction {
        STARJUMP(36026, "star"), PUSHUP(36027, "push"), SITUP(36028, "sit"), JOG(36029, "jog");
        private int id;
        private String insnKey;

        private Instruction(int id, String insnKey) {
            this.id = id;
            this.insnKey = insnKey;
        }

        private static Instruction fromSentence(String sentence) {
            for (Instruction insn : values()) {
                if (sentence.contains(insn.getInsnKey())) {
                    return insn;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }

        public String getInsnKey() {
            return insnKey;
        }
    }
}
