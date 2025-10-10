package tuan2;

import java.util.*;

public class BFS {
	public Node bfsUsingQueue(Node start, int n) {
		Queue<Node> queue = new LinkedList<>();
		queue.add(start);

		while (!queue.isEmpty()) {
			Node node = queue.poll();
			if (node.state.size() == n) {
				System.out.println("BFS goal: " + node.state);
				return node;
			}
			for (Node child : node.getNeighbours()) {
				queue.add(child);
			}
		}
		return null;
	}

}
