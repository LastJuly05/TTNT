package SodoKuGame;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;

/* GameUI.java
 * Quản lý giao diện chính của Sudoku
 */
public class GameUI {
    private GameModel model;
    private JFrame frame;
    private CellPanel[][] cellPanels;
    private JLabel mistakesLabel;
    private boolean pencilMode = false;
    private JButton[] numberButtons;
    private JButton pencilButton, hintButton, solveButton;
    private JProgressBar progressBar;

    public GameUI(GameModel model){
        this.model = model;
        createUI();
    }

    public void show(){ frame.setVisible(true); }

    private void createUI(){
        frame = new JFrame("Sudoku - Simulated Annealing Solver");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900,700);
        frame.setLayout(new BorderLayout());

        JPanel boardPanel = new JPanel(new GridLayout(3,3));
        boardPanel.setBorder(new EmptyBorder(10,10,10,10));
        cellPanels = new CellPanel[9][9];

        for (int br=0;br<3;br++)
            for (int bc=0;bc<3;bc++){
                JPanel block = new JPanel(new GridLayout(3,3));
                block.setBorder(new LineBorder(Color.BLACK,2));
                for (int r=br*3;r<br*3+3;r++)
                    for (int c=bc*3;c<bc*3+3;c++){
                        CellPanel cp = new CellPanel(r,c,model,this);
                        cellPanels[r][c] = cp;
                        block.add(cp);
                    }
                boardPanel.add(block);
            }
        frame.add(boardPanel, BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(new EmptyBorder(10,10,10,10));
        mistakesLabel = new JLabel("Mistakes: 0 / " + model.getMaxMistakes());
        right.add(mistakesLabel);

        JPanel pad = new JPanel(new GridLayout(3,3,5,5));
        numberButtons = new JButton[9];
        for (int i=0;i<9;i++){
            int v=i+1;
            JButton b=new JButton(String.valueOf(v));
            b.addActionListener(e->onNumberPressed(v));
            numberButtons[i]=b; pad.add(b);
        }
        right.add(Box.createRigidArea(new Dimension(0,10)));
        right.add(pad);

        pencilButton=new JButton("Pencil: OFF");
        pencilButton.addActionListener(e->togglePencil());
        right.add(pencilButton);

        JButton clearBtn=new JButton("Clear Cell");
        clearBtn.addActionListener(e->clearSelectedCell());
        right.add(clearBtn);

        hintButton=new JButton("Hint");
        hintButton.addActionListener(e->onHint());
        right.add(hintButton);

        solveButton=new JButton("AI Solve");
        solveButton.addActionListener(e->onAISolve());
        right.add(solveButton);

        progressBar=new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);
        right.add(progressBar);

        JButton newGame=new JButton("New Game");
        newGame.addActionListener(e->newGame());
        right.add(newGame);

        frame.add(right, BorderLayout.EAST);
        refreshBoardUI();
    }

    private int selR=-1, selC=-1;
    public void setSelectedCell(int r,int c){
        if(selR!=-1) cellPanels[selR][selC].setSelected(false);
        selR=r; selC=c;
        cellPanels[r][c].setSelected(true);
    }

    private void onNumberPressed(int v){
        if(selR==-1) return;
        if(pencilMode){
            model.toggleNote(selR,selC,v);
            refreshCell(selR,selC);
        }else{
            int res=model.putNumber(selR,selC,v);
            if(res==2) JOptionPane.showMessageDialog(frame,"Ô cố định!");
            else if(res==1){
                updateMistakes(); refreshCell(selR,selC);
                if(model.isGameOver()){
                    JOptionPane.showMessageDialog(frame,"Game Over!");
                    model.applySolverSolution(model.getSolution());
                    refreshBoardUI();
                }
            }else{
                refreshCell(selR,selC);
                if(model.isSolved()) JOptionPane.showMessageDialog(frame,"Hoàn thành!");
            }
        }
    }

    private void togglePencil(){
        pencilMode=!pencilMode;
        pencilButton.setText("Pencil: "+(pencilMode?"ON":"OFF"));
    }

    private void clearSelectedCell(){
        if(selR==-1) return;
        model.clearCell(selR,selC);
        model.getNotes(selR,selC).clear();
        refreshCell(selR,selC);
    }

    private void onHint(){
        model.revealOneCell(); refreshBoardUI();
    }

    private void onAISolve(){
        setControls(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        progressBar.setString("AI solving...");
        new Thread(()->{
            int[][] board=new int[9][9]; boolean[][] fixed=new boolean[9][9];
            for(int r=0;r<9;r++)for(int c=0;c<9;c++){board[r][c]=model.getCell(r,c);fixed[r][c]=model.isFixed(r,c);}
            SimulatedAnnealingSolver solver=new SimulatedAnnealingSolver(board,fixed);
            int[][] sol = solver.solve(3_000_000, 15000, 4, null);
            javax.swing.SwingUtilities.invokeLater(()->{
                progressBar.setVisible(false);
                setControls(true);
                if(sol!=null){model.applySolverSolution(sol);refreshBoardUI();JOptionPane.showMessageDialog(frame,"AI đã giải xong!");}
                else JOptionPane.showMessageDialog(frame,"AI không tìm được lời giải!");
            });
        }).start();
    }

    private void setControls(boolean en){
        for(JButton b:numberButtons)b.setEnabled(en);
        pencilButton.setEnabled(en); hintButton.setEnabled(en); solveButton.setEnabled(en);
    }

    private void newGame(){
        model=new GameModel();
        for(int r=0;r<9;r++)for(int c=0;c<9;c++)cellPanels[r][c].setModel(model);
        refreshBoardUI();
    }

    private void updateMistakes(){
        mistakesLabel.setText("Mistakes: "+model.getMistakes()+" / "+model.getMaxMistakes());
    }

    private void refreshBoardUI(){
        for(int r=0;r<9;r++)for(int c=0;c<9;c++)cellPanels[r][c].updateView();
        updateMistakes();
    }

    private void refreshCell(int r,int c){
        cellPanels[r][c].updateView();
        updateMistakes();
    }
}
