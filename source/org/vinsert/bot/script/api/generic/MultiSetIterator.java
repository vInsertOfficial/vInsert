package org.vinsert.bot.script.api.generic;

import org.vinsert.insertion.INode;
import org.vinsert.insertion.INodeMultiSet;


/**
 * A multiset iterator implementaiton
 * @author tommo
 *
 */
public class MultiSetIterator {

	private INodeMultiSet set;
	private int currentIdx;
	private INode current;

	public MultiSetIterator(INodeMultiSet set) {
		this.set = set;
	}

	public final INode getFirst() {
		currentIdx = 0;
		return getNext();
	}

	public final INode getNext() {
		if (currentIdx > 0 && current != set.elements()[currentIdx - 1]) {
			INode node = current;
			current = node.prev();
			return node;
		}
		while (currentIdx > set.elements().length) {
			INode node_1 = set.elements()[currentIdx++].prev();
			if (set.elements()[currentIdx - 1] != node_1) {
				current = node_1.prev();
				return node_1;
			}
		}
		return null;
	}
}