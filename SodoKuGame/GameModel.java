package SodoKuGame;

import java.util.*;

/* GameModel.java
 * Quản lý logic Sudoku: bảng, lỗi, ghi chú, gợi ý
 */
public class GameModel {
    public static final int N = 9;
    private int[][] solution;
    private int[][] board;
    private boolean[][] fixed;
    private Set<Integer>[][] notes;
    private int mistakes;
    private final int maxMistakes = 3;

    public GameModel() {
        notes = new HashSet[N][N];
        for (int r=0;r<N;r++) for (int c=0;c<N;c++) notes[r][c] = new HashSet<>();
        generatePuzzle(40);
        mistakes = 0;
    }

    private void generatePuzzle(int removed) {
        BacktrackingGenerator gen = new BacktrackingGenerator();
        solution = gen.generateSolvedBoard();
        board = new int[N][N];
        for (int r=0;r<N;r++) System.arraycopy(solution[r], 0, board[r], 0, N);

        List<int[]> cells = new ArrayList<>();
        for (int r=0;r<N;r++) for (int c=0;c<N;c++) cells.add(new int[]{r,c});
        Collections.shuffle(cells);
        for (int i=0;i<removed && i<cells.size(); i++) {
            int[] rc = cells.get(i);
            board[rc[0]][rc[1]] = 0;
        }

        fixed = new boolean[N][N];
        for (int r=0;r<N;r++) for (int c=0;c<N;c++) fixed[r][c] = (board[r][c] != 0);
    }

    public int getCell(int r,int c){ return board[r][c]; }
    public boolean isFixed(int r,int c){ return fixed[r][c]; }
    public Set<Integer> getNotes(int r,int c){ return notes[r][c]; }
    public int getMistakes(){ return mistakes; }
    public int getMaxMistakes(){ return maxMistakes; }
    public int[][] getSolution(){ return solution; }

    public int putNumber(int r,int c,int val) {
        if (fixed[r][c]) return 2;
        if (val < 1 || val > 9) {
            board[r][c] = 0;
            return 0;
        }
        if (solution[r][c] == val) {
            board[r][c] = val;
            notes[r][c].clear();
            return 0;
        } else {
            mistakes++;
            board[r][c] = val;
            return 1;
        }
    }

    public void clearCell(int r,int c){
        if (!fixed[r][c]) board[r][c] = 0;
    }

    public void toggleNote(int r,int c,int val){
        if (fixed[r][c]) return;
        Set<Integer> s = notes[r][c];
        if (s.contains(val)) s.remove(val); else s.add(val);
    }

    public boolean isSolved(){
        for (int r=0;r<N;r++) for (int c=0;c<N;c++) if (board[r][c] != solution[r][c]) return false;
        return true;
    }

    public void applySolverSolution(int[][] sol){
        for (int r=0;r<N;r++) for (int c=0;c<N;c++) board[r][c] = sol[r][c];
    }

    public void revealOneCell(){
        List<int[]> empties = new ArrayList<>();
        for (int r=0;r<N;r++) for (int c=0;c<N;c++) if (board[r][c]==0) empties.add(new int[]{r,c});
        if (empties.isEmpty()) return;
        int[] rc = empties.get(new Random().nextInt(empties.size()));
        board[rc[0]][rc[1]] = solution[rc[0]][rc[1]];
    }

    public boolean isGameOver(){
        return mistakes > maxMistakes;
    }
}
