package tuan3;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class LocalSearch {
	public int checkHorizontal(Node node) {
		int xungdotngang = 0;
		for (int i = 0; i < node.n; i++) {
			for (int j = i + 1; j < node.n; j++) {
				if (node.state.get(i).equals(node.state.get(j))) {
					xungdotngang++;

				}
			}

		}
		return xungdotngang;

	}

	public int checkDiagonal(Node node) {
		int xungdotcheo = 0;
		for (int i = 0; i < node.n; i++) {
			for (int j = i + 1; j < node.n; j++) {
				int hangconlai = Math.abs(node.state.get(i) - node.state.get(j));
				int cotconlai = Math.abs(i - j);
				if (hangconlai == cotconlai) {
					xungdotcheo++;

				}

			}
		}

		return xungdotcheo;

	}

	public int heuristic(Node node) {
		return checkDiagonal(node) + checkHorizontal(node);

	}

	public int tryMovingOneQueen(Node node, int x, int y) {
		List<Integer> stateMoi = new ArrayList<Integer>(node.state);
		stateMoi.set(x, y);
		Node nodeMoi = new Node(node.n, stateMoi);
		return heuristic(nodeMoi);
	}
	public SortedMap<Integer,Node> generateNeighbours(Node node){
		SortedMap< Integer, Node> SM1 = new TreeMap<>();
		
		for (int cot = 0; cot < node.n; cot++) {
			for (int hang = 0; hang < node.n; hang++) {
				if (hang!=node.state.get(cot)) {
					List<Integer> stateMoi = new ArrayList<>(node.state);
					stateMoi.set(cot,hang);
					Node QuanHauhangxom = new Node(node.n, stateMoi);
					int h  = heuristic(QuanHauhangxom);
					if (!SM1.containsKey(h)) {
						SM1.put(h, QuanHauhangxom);
						
					}
				}
				
			}
			
		}
		return SM1;
	}


	public void run() {
		Node initial = new Node(8, null); // hoặc 4,5,6,7
		if (heuristic(initial) == 0) // goal
		{
			System.out.println(initial.state);
			return;
		}
		System.out.println("Initial state is: " + initial.state);
		Node node = initial;
		SortedMap<Integer, Node> neighbours = generateNeighbours(node);
		Integer bestHeuristic = neighbours.firstKey();
		while (bestHeuristic < heuristic(node)) {
			node = neighbours.get(bestHeuristic);
			neighbours = generateNeighbours(node);
			bestHeuristic = neighbours.firstKey();
		}
		if (heuristic(node) == 0) {
			System.out.println("Goal is: " + node.state);
		} else
			System.out.println("Cannot find goal state! Best state is: " + node.state);
	}

}