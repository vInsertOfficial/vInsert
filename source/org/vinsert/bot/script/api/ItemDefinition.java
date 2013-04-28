package org.vinsert.bot.script.api;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.insertion.IItemDefinition;

/**
 * @author iJava
 */
public class ItemDefinition {

    private IItemDefinition iItemDefinition;
    private ScriptContext ctx;


    public ItemDefinition(ScriptContext ctx, IItemDefinition iItemDefinition) {
        this.iItemDefinition = iItemDefinition;
        this.ctx = ctx;
    }

    public String getName() {
        return iItemDefinition.getName();
    }

    public int getModelId() {
        return iItemDefinition.getModelId();
    }

    public int getValue() {
        return iItemDefinition.getValue();
    }

    public String[] getActions() {
        return iItemDefinition.getActions();
    }

    public String[] getGroundActions() {
       return iItemDefinition.getGroundActions();
    }

    public int[] getStackIds() {
        return iItemDefinition.getStackIds();
    }

    public int[] getStackAmounts() {
        return iItemDefinition.getStackAmounts();
    }
}
