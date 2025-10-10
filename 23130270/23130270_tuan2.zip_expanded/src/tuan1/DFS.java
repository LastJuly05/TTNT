package tuan1;

import java.util.*;

public class DFS {
	public void dfsUsingStack(Node initial, int goal) {
		Stack<Node> stack = new Stack<>();
		initial.visited = true;
		stack.push(initial);

		while (!stack.isEmpty()) {
			Node node = stack.pop();
			System.out.print(node.state + " ");

			if (node.state == goal) {
				System.out.println("\nDa tim thay dich " + goal);
				System.out.print("Duong di DFS: ");
				printPath(node);
				return;
			}

			List<Node> neighbours = node.getNeighbours();
			for (Node neighbour : neighbours) {
				if (neighbour != null && !neighbour.visited) {
					neighbour.visited = true;
					neighbour.parent = node;
					stack.push(neighbour);
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

