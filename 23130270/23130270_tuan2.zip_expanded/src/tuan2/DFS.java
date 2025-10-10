package tuan2;

import java.util.*;

public class DFS {
    public Node dfsUsingStack(Node start, int n) {
        Stack<Node> stack = new Stack<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            Node node = stack.pop();
            if (node.state.size() == n) {
                System.out.println("DFS goal: " + node.state);
                return node;
            }
            for (Node child : node.getNeighbours()) {
                stack.push(child);
            }
        }
        return null;
    }
}
