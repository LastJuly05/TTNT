package tuan2;

import java.util.*;

public class Node {
    int n;
    List<Integer> state;
    List<Node> neighbours;

    public Node(int n) {
        this.n = n;
        this.state = new ArrayList<>();
        this.neighbours = new ArrayList<>();
    }

    public Node(int n, List<Integer> state) {
        this.n = n;
        this.state = new ArrayList<>(state);
        this.neighbours = new ArrayList<>();
    }

    public void addNeighbours(Node neighbourNode) {
        this.neighbours.add(neighbourNode);
    }

    public boolean isValid(List<Integer> state) {
        int col = state.size() - 1;
        int row = state.get(col);
        for (int i = 0; i < col; i++) {
            int otherRow = state.get(i);
            if (otherRow == row) return false; // cùng hàng
            if (Math.abs(otherRow - row) == Math.abs(i - col)) return false; // cùng đường chéo
        }
        return true;
    }

    private List<Integer> place(int x) {
        List<Integer> newState = new ArrayList<>(state);
        newState.add(x);
        if (isValid(newState)) return newState;
        return null;
    }

    public List<Node> getNeighbours() {
        neighbours.clear();
        if (state.size() == n) return neighbours;
        for (int i = 0; i < n; i++) {
            List<Integer> newState = place(i);
            if (newState != null) {
                neighbours.add(new Node(n, newState));
            }
        }
        return neighbours;
    }
}
