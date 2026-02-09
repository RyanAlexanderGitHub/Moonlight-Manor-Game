import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class TilePuzzlePanel extends JPanel {
    private JButton[] tiles;
    private int[] board = {1, 2, 3, 4, 5, 6, 7, 8, 0}; 
    private PuzzleCallback callback;
    private Game g;
    private static final int GRID_SIZE = 3;
    
    public TilePuzzlePanel(Game g) {
        this.g = g;
        setLayout(new GridBagLayout());
        setBackground(new Color(80, 82, 102));
        
        JPanel puzzleArea = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE, 5, 5));
        puzzleArea.setPreferredSize(new Dimension(450, 450));
        tiles = new JButton[GRID_SIZE * GRID_SIZE];

        for (int i = 0; i < tiles.length; i++) {
            JButton tile = new JButton();
            tile.setFont(new Font("Serif", Font.BOLD, 40));
            tile.setFocusPainted(false);
            tile.setBackground(new Color(169, 177, 217));
            tile.setForeground(new Color(80, 82, 102));
            tile.addActionListener(new TileListener(i));
            tiles[i] = tile;
            puzzleArea.add(tile);
        }
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0;
        add(puzzleArea, gbc);
        
        JButton exitBtn = new JButton("STOP PUZZLE");
        exitBtn.addActionListener(e -> {
           if(callback != null) callback.onExit(); 
           g.endPuzzle();
        });
        gbc.gridy = 1;
        add(exitBtn, gbc);
    }

    public void init(PuzzleCallback cb) {
        this.callback = cb;
        do {
            shuffleBoard();
        } while (!isSolvable(board) || isSolved());
        drawBoard();
        g.infoPanel.updateDescription("Solve the sliding tile puzzle...");
    }

    private void drawBoard() {
        for (int i = 0; i < tiles.length; i++) {
            if (board[i] == 0) {
                tiles[i].setText("");
                tiles[i].setEnabled(false);
                tiles[i].setBackground(new Color(60, 60, 90));
            } else {
                tiles[i].setText(String.valueOf(board[i]));
                tiles[i].setEnabled(true);
                tiles[i].setBackground(new Color(169, 177, 217));
            }
        }
    }
    
    private void shuffleBoard() {
        Random rand = new Random();
        for (int i = board.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = board[i];
            board[i] = board[j];
            board[j] = temp;
        }
    }
    
    private boolean isSolvable(int[] board) {
        int inversions = 0;
        for (int i = 0; i < board.length - 1; i++) {
            for (int j = i + 1; j < board.length; j++) {
                if (board[i] != 0 && board[j] != 0 && board[i] > board[j]) {
                    inversions++;
                }
            }
        }
        return inversions % 2 == 0;
    }

    private int findEmpty() {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) return i;
        }
        return -1;
    }

    private boolean canMove(int tileIndex, int emptyIndex) {
        int tileRow = tileIndex / GRID_SIZE;
        int tileCol = tileIndex % GRID_SIZE;
        int emptyRow = emptyIndex / GRID_SIZE;
        int emptyCol = emptyIndex % GRID_SIZE;

        return (tileRow == emptyRow && Math.abs(tileCol - emptyCol) == 1) ||
               (tileCol == emptyCol && Math.abs(tileRow - emptyRow) == 1);
    }

    private void swapTiles(int i, int j) {
        int temp = board[i];
        board[i] = board[j];
        board[j] = temp;
    }

    private boolean isSolved() {
        for (int i = 0; i < board.length - 1; i++) {
            if (board[i] != i + 1) return false;
        }
        return board[board.length - 1] == 0;
    }

    private class TileListener implements ActionListener {
        int index;
        public TileListener(int index) { this.index = index; }

        @Override
        public void actionPerformed(ActionEvent e) {
            int emptyIndex = findEmpty();
            if (canMove(index, emptyIndex)) {
                swapTiles(index, emptyIndex);
                drawBoard();
                if (isSolved()) {
                    
                    new javax.swing.Timer(500, event -> {
                        ((javax.swing.Timer)event.getSource()).stop();
                        if(callback != null) callback.onSolve();
                        g.endPuzzle();
                    }).start();
                }
            }
        }
    }
}