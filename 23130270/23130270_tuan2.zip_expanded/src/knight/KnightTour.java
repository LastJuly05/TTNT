package knight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class KnightTour {
    private int N;
    private int[][] board;
    // 8 knight moves
    private final int[] dx = {2, 1, -1, -2, -2, -1, 1, 2};
    private final int[] dy = {1, 2, 2, 1, -1, -2, -2, -1};

    public KnightTour(int N) {
        this.N = N;
        board = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) board[i][j] = -1; // -1 => unvisited
        }
    }

    private boolean isSafe(int x, int y) {
        return x >= 0 && x < N && y >= 0 && y < N && board[x][y] == -1;
    }

    // Warnsdorff's heuristic: choose move with minimum onward moves
    private int getDegree(int x, int y) {
        int count = 0;
        for (int k = 0; k < 8; k++) {
            int nx = x + dx[k];
            int ny = y + dy[k];
            if (isSafe(nx, ny)) count++;
        }
        return count;
    }

    private List<int[]> nextMovesSorted(int x, int y) {
        List<int[]> moves = new ArrayList<>();
        for (int k = 0; k < 8; k++) {
            int nx = x + dx[k];
            int ny = y + dy[k];
            if (isSafe(nx, ny)) moves.add(new int[]{nx, ny});
        }
        Collections.sort(moves, new Comparator<int[]>() {
            @Override
            public int compare(int[] a, int[] b) {
                return Integer.compare(getDegree(a[0], a[1]), getDegree(b[0], b[1]));
            }
        });
        return moves;
    }

    private boolean solveKTUtil(int x, int y, int moveCount) {
        board[x][y] = moveCount;
        if (moveCount == N * N) return true; // tour complete

        List<int[]> moves = nextMovesSorted(x, y);
        for (int[] mv : moves) {
            int nx = mv[0], ny = mv[1];
            if (solveKTUtil(nx, ny, moveCount + 1)) return true;
        }

        // backtrack
        board[x][y] = -1;
        return false;
    }

    public boolean solve(int startX, int startY) {
        if (!isSafe(startX, startY)) return false;
        // initialize board already done in constructor
        return solveKTUtil(startX, startY, 1);
    }

    public void printSolution() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.printf("%3d ", board[i][j]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        int N = 8;
        int sx = 0, sy = 0;
        if (args.length >= 1) {
            try {
                N = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid board size, using 8.");
            }
        }
        if (args.length >= 3) {
            try {
                sx = Integer.parseInt(args[1]);
                sy = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid start coordinates, using 0 0.");
            }
        }

        if (N <= 0) {
            System.err.println("Board size must be > 0");
            return;
        }

        KnightTour kt = new KnightTour(N);
        boolean solved = kt.solve(sx, sy);
        if (solved) {
            System.out.println("Knight's tour found (move numbers start at 1):");
            kt.printSolution();
        } else {
            System.out.println("No solution found from starting position (" + sx + "," + sy + ") on " + N + "x" + N + " board.");
        }
    }
}
