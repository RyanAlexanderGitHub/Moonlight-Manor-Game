import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class LeverPuzzlePanel extends JPanel {
    private JButton[] levers = new JButton[3];
    private boolean[] currentPositions = new boolean[3]; 
    private final String targetSequence = "DOWN_UP_DOWN"; 
    private PuzzleCallback callback;
    private Game g;
    
    public LeverPuzzlePanel(Game g) {
        this.g = g;
        setLayout(new GridBagLayout());
        setBackground(new Color(25, 20, 25));
        
        JPanel puzzleArea = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        puzzleArea.setPreferredSize(new Dimension(600, 300));
        puzzleArea.setBackground(new Color(40, 40, 50));
        puzzleArea.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), 
            "Lever Mechanism (Target: DOWN, UP, DOWN)", 
            javax.swing.border.TitledBorder.CENTER, 
            javax.swing.border.TitledBorder.TOP, 
            new Font("Serif", Font.BOLD, 20), 
            Color.WHITE
        ));

        for (int i = 0; i < levers.length; i++) {
            JButton lever = new JButton("DOWN"); 
            lever.setPreferredSize(new Dimension(100, 150));
            
            lever.setVerticalTextPosition(SwingConstants.BOTTOM); 
            lever.setHorizontalTextPosition(SwingConstants.CENTER);
            lever.setFont(new Font("Serif", Font.BOLD, 24)); 
            lever.setFocusPainted(false);
            
            final int index = i; 
            lever.addActionListener(e -> handleLeverPull(index)); 
            
            levers[i] = lever;
            puzzleArea.add(lever);
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
        Arrays.fill(currentPositions, false); // Initialize all to DOWN (false)
        drawLevers();
        g.infoPanel.updateDescription("Pull the levers in the correct sequence (U or D). (Check your Journal Note for the clue!)");
    }
    
    private void drawLevers() {
        for (int i = 0; i < levers.length; i++) {
            boolean isUp = currentPositions[i];
            
            levers[i].setText(isUp ? "UP" : "DOWN");
            
            if (isUp) {
                // UP State: Gold/Yellow with dark text
                levers[i].setVerticalAlignment(SwingConstants.TOP);
                levers[i].setBackground(new Color(255, 193, 7)); 
                levers[i].setForeground(new Color(50, 50, 50)); 
                levers[i].setToolTipText("Position: UP");
            } else {
                // DOWN State: Light silver/grey with black text
                levers[i].setVerticalAlignment(SwingConstants.BOTTOM);
                levers[i].setBackground(new Color(192, 192, 192)); 
                levers[i].setForeground(Color.BLACK); 
                levers[i].setToolTipText("Position: DOWN");
            }
        }
    }
    
    private boolean checkSolved() {
         String current = "";
         for (int i = 0; i < currentPositions.length; i++) {
             current += (currentPositions[i] ? "UP" : "DOWN");
             if (i < currentPositions.length - 1) {
                 current += "_";
             }
         }
         return current.equals(targetSequence);
     }
    
    private void handleLeverPull(int index) {
        currentPositions[index] = !currentPositions[index]; // Toggle position
        
        String move = currentPositions[index] ? "UP" : "DOWN";
        drawLevers();
        
        g.showFeedback(levers[index].getText() + " moved " + move, new Point(300, 300));
        
        if (checkSolved()) {
            g.infoPanel.updateDescription("Sequence Correct! A hidden passage opens.");
            
            new javax.swing.Timer(500, event -> {
                ((javax.swing.Timer)event.getSource()).stop();
                if(callback != null) callback.onSolve();
                g.endPuzzle();
            }).start();
        } else {
            g.infoPanel.updateDescription("Lever position changed. The mechanism is still locked. Keep adjusting.");
        }
    }
}