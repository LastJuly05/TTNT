package SodoKuGame;

import java.util.*;

/*
 * SimulatedAnnealingSolver.java
 *
 * Cải tiến Simulated Annealing cho Sudoku:
 * - Tính delta cost chỉ với các hàng/cột ảnh hưởng (hiệu năng cao)
 * - Adaptive T0 & cooling, periodic restarts
 * - Giới hạn bằng iterations và thời gian (ms)
 * - Progress listener để UI cập nhật
 *
 * Sử dụng:
 *   SimulatedAnnealingSolver solver = new SimulatedAnnealingSolver(board, fixed);
 *   int[][] solved = solver.solve(5_000_000, 10_000, 5, listener);
 *     -> maxIters, maxTimeMillis, restarts
 */
public class SimulatedAnnealingSolver {
    private static final int N = 9;
    private final int[][] initial;
    private final boolean[][] fixed;
    private final Random rand;

    public SimulatedAnnealingSolver(int[][] board, boolean[][] fixed) {
        this.initial = new int[N][N];
        this.fixed = new boolean[N][N];
        for (int r = 0; r < N; r++) {
            System.arraycopy(board[r], 0, this.initial[r], 0, N);
            System.arraycopy(fixed[r], 0, this.fixed[r], 0, N);
        }
        this.rand = new Random();
    }

    /**
     * Solve bằng Simulated Annealing.
     *
     * @param maxIters tổng số iter cho mỗi restart
     * @param maxTimeMillis giới hạn thời gian (ms) tổng cho toàn bộ quá trình (tối đa)
     * @param restarts số lần restart random (>=1)
     * @param listener (nullable) nhận progress để UI hiển thị
     * @return board đã giải (cost==0) hoặc null nếu thất bại
     */
    public int[][] solve(int maxIters, long maxTimeMillis, int restarts, SolverProgressListener listener) {
        long startTime = System.currentTimeMillis();
        for (int attempt = 0; attempt < Math.max(1, restarts); attempt++) {
            // nếu quá thời gian thì dừng luôn
            if (System.currentTimeMillis() - startTime > maxTimeMillis) break;

            // 1) Khởi tạo state bằng cách điền các block 3x3 với các số missing ngẫu nhiên
            int[][] state = initByBlocks();
            int cost = computeFullCost(state);

            if (listener != null) listener.onProgress(state, cost);

            if (cost == 0) return state;

            // 2) adaptive T0: dựa trên cost (khoảng)
            double T = Math.max(0.5, cost / 10.0); // khởi tạo nhiệt độ từ cost (heuristic)
            double cooling = Math.pow(1e-4, 1.0 / Math.max(1, maxIters)); // giảm dần để đến ~1e-4 cuối cùng

            int iter = 0;
            int unchangedSince = 0;
            int bestCost = cost;
            int[][] bestState = deepCopy(state);

            while (iter < maxIters && (System.currentTimeMillis() - startTime) <= maxTimeMillis) {
                // chọn 1 block và 2 ô không cố định trong block
                int br = rand.nextInt(3), bc = rand.nextInt(3);
                List<int[]> freeCells = new ArrayList<>();
                for (int r = br * 3; r < br * 3 + 3; r++) {
                    for (int c = bc * 3; c < bc * 3 + 3; c++) {
                        if (!fixed[r][c]) freeCells.add(new int[]{r, c});
                    }
                }
                if (freeCells.size() < 2) {
                    // không thể swap trong block này
                    iter++;
                    T *= cooling;
                    continue;
                }
                int ia = rand.nextInt(freeCells.size());
                int ib = rand.nextInt(freeCells.size());
                if (ia == ib) { iter++; T *= cooling; continue; }

                int r1 = freeCells.get(ia)[0], c1 = freeCells.get(ia)[1];
                int r2 = freeCells.get(ib)[0], c2 = freeCells.get(ib)[1];

                // tính delta cost chỉ cho các hàng/cols bị ảnh hưởng
                int delta = deltaCostForSwap(state, r1, c1, r2, c2);

                boolean accept = false;
                if (delta <= 0) accept = true;
                else {
                    double prob = Math.exp(-delta / T);
                    if (rand.nextDouble() < prob) accept = true;
                }

                if (accept) {
                    // thực hiện swap
                    int tmp = state[r1][c1]; state[r1][c1] = state[r2][c2]; state[r2][c2] = tmp;
                    cost += delta;

                    if (cost < bestCost) {
                        bestCost = cost;
                        bestState = deepCopy(state);
                        unchangedSince = 0;
                    } else {
                        unchangedSince++;
                    }
                }

                // cập nhật temperature & iter
                T *= cooling;
                iter++;

                // cập nhật listener định kỳ
                if (listener != null && (iter % 500 == 0 || cost == 0)) {
                    listener.onProgress(deepCopy(state), cost);
                }

                if (cost == 0) {
                    return state;
                }

                // heuristic: nếu đã lâu không cải thiện, có thể break sớm để restart
                if (unchangedSince > 20000 && iter > maxIters / 10) break;
            } // end inner SA loop

            // nếu tìm thấy bestCost nhỏ hơn global threshold, thử đặt state = bestState và tiếp tục
            if (bestCost == 0) return bestState;

            // nếu chưa giải xong, sẽ restart (cố gắng khác ngẫu nhiên)
            if (listener != null) listener.onProgress(bestState, bestCost);
        } // end restarts

        return null; // thất bại
    }

    /* --- Helpers --- */

    private int[][] initByBlocks() {
        int[][] state = new int[N][N];
        for (int r = 0; r < N; r++) System.arraycopy(this.initial[r], 0, state[r], 0, N);

        for (int br = 0; br < 3; br++) {
            for (int bc = 0; bc < 3; bc++) {
                boolean[] present = new boolean[10];
                for (int r = br * 3; r < br * 3 + 3; r++) for (int c = bc * 3; c < bc * 3 + 3; c++) {
                    int v = state[r][c];
                    if (v >= 1 && v <= 9) present[v] = true;
                }
                List<Integer> missing = new ArrayList<>();
                for (int v = 1; v <= 9; v++) if (!present[v]) missing.add(v);
                Collections.shuffle(missing, rand);
                int idx = 0;
                for (int r = br * 3; r < br * 3 + 3; r++) for (int c = bc * 3; c < bc * 3 + 3; c++) {
                    if (state[r][c] == 0) state[r][c] = missing.get(idx++);
                }
            }
        }
        return state;
    }

    private int computeFullCost(int[][] s) {
        int cost = 0;
        for (int r = 0; r < N; r++) {
            int[] cnt = new int[10];
            for (int c = 0; c < N; c++) cnt[s[r][c]]++;
            for (int v = 1; v <= 9; v++) if (cnt[v] > 1) cost += cnt[v] - 1;
        }
        for (int c = 0; c < N; c++) {
            int[] cnt = new int[10];
            for (int r = 0; r < N; r++) cnt[s[r][c]]++;
            for (int v = 1; v <= 9; v++) if (cnt[v] > 1) cost += cnt[v] - 1;
        }
        return cost;
    }

    /**
     * Tính delta cost khi hoán đổi (r1,c1) với (r2,c2) bằng cách
     * chỉ tính lại các hàng r1,r2 và cột c1,c2 (vì hoán đổi chỉ ảnh hưởng những hàng/cột này).
     *
     * Công thức: delta = newAffectedCost - oldAffectedCost
     */
    private int deltaCostForSwap(int[][] s, int r1, int c1, int r2, int c2) {
        // nếu cùng hàng và cùng cột -> delta = 0 (hiếm)
        // Lấy các hàng/cols ảnh hưởng
        Set<Integer> rows = new HashSet<>(Arrays.asList(r1, r2));
        Set<Integer> cols = new HashSet<>(Arrays.asList(c1, c2));

        int before = 0, after = 0;

        // tính trước
        for (int r : rows) {
            int[] cnt = new int[10];
            for (int c = 0; c < N; c++) cnt[s[r][c]]++;
            for (int v = 1; v <= 9; v++) if (cnt[v] > 1) before += cnt[v] - 1;
        }
        for (int c : cols) {
            int[] cnt = new int[10];
            for (int r = 0; r < N; r++) cnt[s[r][c]]++;
            for (int v = 1; v <= 9; v++) if (cnt[v] > 1) before += cnt[v] - 1;
        }

        // giả sử swap
        int temp = s[r1][c1];
        s[r1][c1] = s[r2][c2];
        s[r2][c2] = temp;

        // tính sau
        for (int r : rows) {
            int[] cnt = new int[10];
            for (int c = 0; c < N; c++) cnt[s[r][c]]++;
            for (int v = 1; v <= 9; v++) if (cnt[v] > 1) after += cnt[v] - 1;
        }
        for (int c : cols) {
            int[] cnt = new int[10];
            for (int r = 0; r < N; r++) cnt[s[r][c]]++;
            for (int v = 1; v <= 9; v++) if (cnt[v] > 1) after += cnt[v] - 1;
        }

        // revert swap
        temp = s[r1][c1];
        s[r1][c1] = s[r2][c2];
        s[r2][c2] = temp;

        return after - before;
    }

    private int[][] deepCopy(int[][] a) {
        int[][] b = new int[N][N];
        for (int i = 0; i < N; i++) System.arraycopy(a[i], 0, b[i], 0, N);
        return b;
    }

    public interface SolverProgressListener {
        void onProgress(int[][] partialState, int cost);
    }
}

