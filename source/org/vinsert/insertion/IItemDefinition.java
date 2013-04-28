package org.vinsert.insertion;

/**
 * @author iJava
 */
public interface IItemDefinition {

    public String getName();

    public int getModelId();

    public String[] getActions();

    public String[] getGroundActions();

    public int[] getStackIds();

    public int[] getStackAmounts();

    public int getValue();
}
