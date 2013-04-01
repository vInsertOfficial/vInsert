package org.vinsert.bot.script.api;

import org.vinsert.insertion.INode;
import org.vinsert.insertion.INodeDeque;


/**
 * An implementation of the deque structure, specifically for client nodes.
 * @author `Discardedx2
 */
@SuppressWarnings("unchecked")
public class NodeDeque<T> {


	/**
	 * The node currently at the front of the deque.
	 */
	private INode front;
	/**
	 * The deque this class is based upon.
	 */
	private INodeDeque deque;

	/**
	 * Constructs a new NodeLinkeddeque.
	 * @param deq The deque this data structure is based off of.
	 */
	public NodeDeque(INodeDeque deq) {
		this.deque = deq;
	}

	/**
	 * Gets the node at the front of this deque.
	 * @return The front node.
	 */
	public T front() {
		INode node = deque.tail().next();

		if (node == deque.tail()) {
			front = null;
			return null;
		}

		front = node.next();
		return (T) front;
	}

	/**
	 * Gets the node at the end of this deque.
	 * @return The end node.
	 */
	public T tail() {
		INode node = deque.tail().prev();

		if (node == deque.tail()) {
			front = null;
			return null;
		}

		front = node.prev();
		return (T) front;
	}

	/**
	 * Advances the front node and returns the next value.
	 * @return The next node.
	 */
	public T next() {
		INode node = front;

		if (node == deque.tail()) {
			front = null;
			return null;
		}

		front = node.next();

		return (T) front;
	}
	
	/**
	 * Gets the size of this deque.
	 * @return The deque size.
	 */
	public int size() {
		int size = 0;
		
		INode n = deque.tail().prev();
		
		while(n != deque.tail()) {
			n = n.prev();
			size++;
		}
		return size;
	}

}
