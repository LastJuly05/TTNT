package SodoKuGame;

/* SudokuGame.java
 * Entry point cho ứng dụng Sudoku
 */

public class SudokuGame {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            GameModel model = new GameModel();
            GameUI ui = new GameUI(model);
            ui.show();
        });
    }
}
