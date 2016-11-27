package jrtr;

import java.util.LinkedList;

public abstract class Group implements Node {
	
	protected LinkedList<Node> children;
	
	public Group() {
		children = new LinkedList<Node>();
	}
	
	public void addNode(Node node){
		children.add(node);
	}
	
	/**
	 * Removes the first occurrence of the specified node from the children list, if it is present.
	 * @param node the node to remove
	 * @return true if the node was successfully removed.
	 */
	public boolean removeNode(Node node){
		return children.remove(node);
	}
	
	@Override
	public LinkedList<Node> getChildren() {
			return children;
	}
	
	@Override
	public Object get3dObject() {
		return null;
	}
}
