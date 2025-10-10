package tuan1;

import java.util.ArrayList;
import java.util.List;

public class Node {
 int state;
 boolean visited;
 List<Node> neighbours;
 Node parent;
public Node(int state) {
	this.state = state;
	this.neighbours = new ArrayList<>() ;
	this.parent = null;
}
 public void addNeighbours(Node neighbourNode) {
	 this.neighbours.add(neighbourNode);
 }
public List getNeighbours() {
	return neighbours;
}
 
}
