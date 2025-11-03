package SodoKuGame;

import java.util.*;

/* BacktrackingGenerator.java
 * Sinh một Sudoku đầy đủ (đã giải)
 */
public class BacktrackingGenerator {
    private static final int N=9;
    private int[][] board;
    private Random rand = new Random();

    public int[][] generateSolvedBoard(){
        board = new int[N][N];
        fillBoard(0,0);
        return board;
    }

    private boolean fillBoard(int r,int c){
        if (r==N) return true;
        int nr = (c==N-1) ? r+1 : r;
        int nc = (c==N-1) ? 0 : c+1;
        List<Integer> nums = new ArrayList<>();
        for (int i=1;i<=9;i++) nums.add(i);
        Collections.shuffle(nums, rand);
        for (int val: nums) {
            if (isSafe(r,c,val)){
                board[r][c] = val;
                if (fillBoard(nr,nc)) return true;
                board[r][c] = 0;
            }
        }
        return false;
    }

    private boolean isSafe(int r,int c,int val){
        for (int i=0;i<9;i++)
            if (board[r][i]==val || board[i][c]==val) return false;
        int br = (r/3)*3, bc = (c/3)*3;
        for (int i=br;i<br+3;i++) for (int j=bc;j<bc+3;j++)
            if (board[i][j]==val) return false;
        return true;
    }
}
