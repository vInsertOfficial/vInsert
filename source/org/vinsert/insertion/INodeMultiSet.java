package org.vinsert.insertion;

public interface INodeMultiSet {

	public INode[] elements();

	public INode top();

	public INode bottom();

	public int position();
	
	public int capacity();

}

