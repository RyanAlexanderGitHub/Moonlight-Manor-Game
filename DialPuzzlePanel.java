import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

/**
 * The DialPuzzlePanel class represents the triple-dial lock puzzle used in the
 * game.
 * It displays three rotating letter dials (A–D) that the player can interact
 * with,
 * checks whether the entered sequence matches the target pattern, and
 * communicates
 * puzzle success or exit events back to the main Game controller.
 *
 * <p>
 * This class manages all UI and logic related to the dial puzzle, including:
 * <ul>
 * <li>Displaying and styling the three rotating dials</li>
 * <li>Handling user clicks to cycle letters A–D</li>
 * <li>Resetting puzzle state and updating displayed values</li>
 * <li>Checking whether the player's current dial positions match the puzzle
 * solution</li>
 * <li>Triggering callbacks when the puzzle is solved or exited</li>
 * <li>Sending feedback messages to the Game's InfoPanel</li>
 * </ul>
 *
 * <p>
 * Although this panel handles all puzzle-specific behavior, it relies on the
 * Game class for puzzle lifecycle control, scene transitions, and UI updates.
 *
 * @author Ryan Matthew Alexander
 * @author Harshdeep Gill
 * @author Keenan Rodricks
 * @author Om Kothare
 * @author Evan Grawey
 * @version 1.0
 */
public class DialPuzzlePanel extends JPanel {
    private JButton[] dials = new JButton[3];
    private int[] positions = {0, 0, 0}; 
    private String targetSequence = ""; 
    private final String[] letters = {"A", "B", "C", "D"};
    private PuzzleCallback callback;
    private Game g;
    
    /**
     * Creates the dial puzzle panel and builds the full UI.
     * This includes the dial area, puzzle title, and the exit button.
     *
     * @param g the active Game instance controlling puzzle transitions
     */
    public DialPuzzlePanel(Game g) {
        this.g = g;
        setLayout(new GridBagLayout());
        setBackground(new Color(20, 25, 25));
        
        JPanel puzzleArea = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        puzzleArea.setPreferredSize(new Dimension(500, 200));
        puzzleArea.setBackground(new Color(45, 45, 50));
        puzzleArea.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), 
            "Triple Dial Lock", 
            javax.swing.border.TitledBorder.CENTER, 
            javax.swing.border.TitledBorder.TOP, 
            new Font("Monospaced", Font.BOLD, 20), 
            new Color(255, 200, 150)
        ));

        for (int i = 0; i < dials.length; i++) {
            JButton dial = new JButton("A");
            dial.setPreferredSize(new Dimension(80, 80));
            dial.setFont(new Font("Monospaced", Font.BOLD, 30));
            dial.setBackground(new Color(100, 100, 120));
            dial.setForeground(new Color(255, 200, 150));
            dial.setFocusPainted(false);
            dial.setActionCommand(String.valueOf(i));
            dial.addActionListener(this::handleDialTurn);
            dials[i] = dial;
            puzzleArea.add(dial);
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

    /**
     * Initializes the puzzle for a new attempt by setting the target sequence,
     * resetting all dials to the default "A" position, and updating the InfoPanel.
     *
     * @param target the solution sequence the player must match
     * @param cb     callback for reporting puzzle events
     */
    public void init(String target, PuzzleCallback cb) {
        this.targetSequence = target;
        this.callback = cb;
        Arrays.fill(positions, 0); 
        drawDials();
        g.infoPanel.updateDescription("Turn the dials to match the discovered sequence (A-D).");
    }
    
    /**
     * Updates each dial's visible letter based on its internal position value.
     */
    private void drawDials() {
        for (int i = 0; i < dials.length; i++) {
            dials[i].setText(letters[positions[i]]);
        }
    }
    
    /**
     * Handles user interaction when a dial is clicked.
     * Cycles the dial forward one letter (A→B→C→D→A), redraws the UI,
     * and checks whether the puzzle has been solved.
     *
     * @param e the action event identifying which dial was clicked
     */
    private void handleDialTurn(ActionEvent e) {
        int index = Integer.parseInt(e.getActionCommand());
        positions[index] = (positions[index] + 1) % letters.length; 
        drawDials();
        
        if (checkSolved()) {
            g.infoPanel.updateDescription("The lock clicks open!");
            
            new javax.swing.Timer(500, event -> {
                ((javax.swing.Timer)event.getSource()).stop();
                if(callback != null) callback.onSolve();
                g.endPuzzle();
            }).start();
        }
    }

    /**
     * Checks whether the current dial positions match the target sequence.
     *
     * @return true if the combination matches the solution; false otherwise
     */
    private boolean checkSolved() {
        String current = letters[positions[0]] + "_" + letters[positions[1]] + "_" + letters[positions[2]];
        return current.equals(targetSequence);
    }
}