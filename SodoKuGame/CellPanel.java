package SodoKuGame;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import java.awt.event.*;
import java.util.*;

/* CellPanel.java
 * Thành phần giao diện cho mỗi ô Sudoku (hiển thị số, ghi chú, chọn ô)
 */
public class CellPanel extends JPanel {
    private int r, c;
    private GameModel model;
    private GameUI parent;
    private JLabel bigLabel, notesLabel;
    private boolean selected = false;

    public CellPanel(int r, int c, GameModel model, GameUI parent) {
        this.r = r;
        this.c = c;
        this.model = model;
        this.parent = parent;

        setLayout(new BorderLayout());
        setBorder(new LineBorder(Color.GRAY, 1));
        setPreferredSize(new Dimension(60, 60));

        bigLabel = new JLabel("", SwingConstants.CENTER);
        bigLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

        notesLabel = new JLabel("", SwingConstants.LEFT);
        notesLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        notesLabel.setForeground(Color.DARK_GRAY);

        add(bigLabel, BorderLayout.CENTER);
        add(notesLabel, BorderLayout.SOUTH);

        // click chọn ô
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.setSelectedCell(r, c);
                // double-click để xoá nhanh ô
                if (e.getClickCount() == 2) {
                    model.clearCell(r, c);
                    model.getNotes(r, c).clear();
                    updateView();
                }
            }
        });
    }

    public void setModel(GameModel m) {
        this.model = m;
    }

    public void setSelected(boolean s) {
        selected = s;
        setBorder(new LineBorder(selected ? Color.BLUE : Color.GRAY, selected ? 2 : 1));
        repaint();
    }

    public void updateView() {
        int val = model.getCell(r, c);
        if (val != 0) {
            bigLabel.setText(String.valueOf(val));
            notesLabel.setText("");
            if (model.isFixed(r, c)) {
                bigLabel.setForeground(Color.BLACK);
            } else {
                // hiển thị đỏ nếu nhập sai
                if (model.getSolution()[r][c] != val) {
                    bigLabel.setForeground(Color.RED);
                } else {
                    bigLabel.setForeground(new Color(0, 128, 0));
                }
            }
        } else {
            bigLabel.setText("");
            Set<Integer> ns = model.getNotes(r, c);
            if (ns.isEmpty()) {
                notesLabel.setText("");
            } else {
                List<Integer> list = new ArrayList<>(ns);
                Collections.sort(list);
                StringBuilder sb = new StringBuilder();
                for (int x : list) {
                    if (sb.length() > 0) sb.append(",");
                    sb.append(x);
                }
                notesLabel.setText("<html><font size='2'>" + sb + "</font></html>");
            }
        }

        // đổi màu block để dễ nhìn
        int br = (r / 3), bc = (c / 3);
        if ((br + bc) % 2 == 0)
            setBackground(new Color(245, 245, 245));
        else
            setBackground(Color.WHITE);
    }
}
