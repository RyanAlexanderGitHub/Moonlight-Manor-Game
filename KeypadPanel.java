import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class KeypadPanel extends JPanel {
    JTextField display;
    String currentInput = "";
    String targetCode = "";
    PuzzleCallback callback;
    Game g;

    public KeypadPanel(Game g) {
        this.g = g;
        setLayout(new GridBagLayout());
        setBackground(new Color(20, 20, 25));
        
        JPanel keypadFrame = new JPanel(new BorderLayout(10, 10));
        keypadFrame.setBackground(new Color(40, 40, 50));
        keypadFrame.setBorder(new EmptyBorder(20, 20, 20, 20));

        display = new JTextField();
        display.setFont(new Font("Monospaced", Font.BOLD, 40));
        display.setHorizontalAlignment(JTextField.CENTER);
        display.setEditable(false);
        display.setBackground(Color.BLACK);
        display.setForeground(Color.GREEN);
        display.setPreferredSize(new Dimension(200, 60));
        keypadFrame.add(display, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(4, 3, 10, 10));
        buttons.setOpaque(false);

        String[] keys = {"7", "8", "9", "4", "5", "6", "1", "2", "3", "CLR", "0", "ENT"};
        for (String k : keys) {
            JButton btn = new JButton(k);
            btn.setFont(new Font("SansSerif", Font.BOLD, 24));
            btn.setFocusPainted(false);
            btn.addActionListener(e -> handleInput(k));
            buttons.add(btn);
        }
        keypadFrame.add(buttons, BorderLayout.CENTER);
        
        JButton exitBtn = new JButton("STOP PUZZLE");
        exitBtn.addActionListener(e -> {
           if(callback != null) callback.onExit(); 
           g.endPuzzle();
        });
        keypadFrame.add(exitBtn, BorderLayout.SOUTH);

        add(keypadFrame);
    }

    public void init(String code, PuzzleCallback cb) {
        this.targetCode = code;
        this.callback = cb;
        this.currentInput = "";
        display.setText("");
        display.setForeground(Color.GREEN);
        g.infoPanel.updateDescription("Enter the 4-digit code...");
    }

    void handleInput(String k) {
        if (k.equals("CLR")) {
            currentInput = "";
            display.setText("");
            display.setForeground(Color.GREEN);
        } else if (k.equals("ENT")) {
            if (currentInput.equals(targetCode)) {
                display.setForeground(Color.CYAN);
                display.setText("SUCCESS");
                
                javax.swing.Timer t = new javax.swing.Timer(500, e -> {
                    ((javax.swing.Timer)e.getSource()).stop();
                    if(callback != null) callback.onSolve();
                    g.endPuzzle();
                });
                t.start();
            } else {
                display.setForeground(Color.RED);
                display.setText("ERROR");
                
                javax.swing.Timer t = new javax.swing.Timer(500, e -> {
                    ((javax.swing.Timer)e.getSource()).stop();
                    currentInput = "";
                    display.setText("");
                    display.setForeground(Color.GREEN);
                });
                t.start();
            }
        } else {
            if (currentInput.length() < 8) {
                currentInput += k;
                display.setText(currentInput);
            }
        }
    }
}