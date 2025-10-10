package tuan1;

import java.util.*;

public class BFS {
	public void bfsUsingQueue(Node initial, int goal) {
		Queue<Node> queue = new LinkedList<>();
		initial.visited = true;
		queue.add(initial);

		while (!queue.isEmpty()) {
			Node node = queue.remove();
			System.out.print(node.state + " ");
			if (node.state == goal) {
				System.out.println("\nDa tim thay dich " + goal);
				System.out.print("Duong di BFS: ");
				printPath(node);
				return;
			}

			List<Node> neighbours = node.getNeighbours();
			for (Node neighbour : neighbours) {
				if (neighbour != null && !neighbour.visited) {
					neighbour.visited = true;
					neighbour.parent = node;
					queue.add(neighbour);
				}
			}
		}
		System.out.println("\nKo tim thay duong di den dich " + goal);
	}

	private void printPath(Node node) {
		String path = "";
		while (node != null) {
			path = node.state + (path.isEmpty() ? "" : " -> ") + path;
			node = node.parent;
		}
		System.out.println(path);
	}
}
