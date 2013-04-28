package org.vinsert.insertion;

/**
 * @author iJava
 */
public interface IHashTable {

    public INode[] elements();

    public INode top();

    public INode bottom();

    public int position();

    public int capacity();
}
