package BFSNode;

import java.util.ArrayList;
import java.util.List;

public class Node {
	int state;
	boolean visited;
	List<Node> neighbours;
	Node parent;
	public Node(int state) {
		super();
		this.state = state;
		//this.visited = visited;
		this.neighbours = new ArrayList<Node>();
		this.parent = null;
	}
	public void addNeighbours(Node neighbourNode) {
		this.neighbours.add(neighbourNode);
	
	}
	public List<Node> getNeighbours() {
		return neighbours;}

}
