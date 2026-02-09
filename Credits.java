import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The Credits class displays the credits screen for the game.
 * It provides a simple, scroll–free layout presenting the development team
 * and offers a single navigation option to return to the Main Menu.
 *
 * <p>
 * This panel is responsible for organizing and presenting:
 * <ul>
 * <li>Project contributor names</li>
 * <li>Header styling for the credits section</li>
 * <li>A back navigation button</li>
 * <li>UI formatting and color consistency with the rest of the game</li>
 * </ul>
 *
 * <p>
 * The class contains no game logic; instead, it serves as a polished
 * informational UI component that fits seamlessly into the game's
 * menu navigation structure.
 *
 * @author Ryan Matthew Alexander
 * @author Harshdeep Gill
 * @author Keenan Rodricks
 * @author Om Kothare
 * @author Evan Grawey
 * @version 1.0
 */
public class Credits extends JPanel {

    private Main main; 

    /**
     * Constructor initializes the credits screen.
     *
     * @param main reference to the main window for scene switching
     */
    public Credits(Main main) {
        this.main = main;
        initialize();
    }

    /**
     * Sets up the entire credits screen layout, including the title,
     * contributor names, spacing, and back button.
     */
    private void initialize() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(new Color(117, 122, 148));

        JLabel title = new JLabel("Credits");
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setFont(new Font("Serif", Font.BOLD, 50));
        title.setForeground(new Color(169, 177, 217));

        // Names 
        JLabel createdBy = createLabel("Created By:");
        JLabel n1 = createLabel("Ryan Matthew Alexander");
        JLabel n2 = createLabel("Evan Grawey");
        JLabel n3 = createLabel("Harshdeep Gill");
        JLabel n4 = createLabel("Keenan Rodricks");
        JLabel n5 = createLabel("Om Kothare");

        JButton backButton = createButton("< Back");
        backButton.addActionListener(e -> goBack());

        this.add(Box.createVerticalStrut(120));
        this.add(title);
        this.add(Box.createVerticalStrut(50));
        this.add(createdBy);
        this.add(Box.createVerticalStrut(20));
        this.add(n1);
        this.add(Box.createVerticalStrut(10));
        this.add(n2);
        this.add(Box.createVerticalStrut(10));
        this.add(n3);
        this.add(Box.createVerticalStrut(10));
        this.add(n4);
        this.add(Box.createVerticalStrut(10));
        this.add(n5);
        this.add(Box.createVerticalStrut(70));
        this.add(backButton);
    }

    /**
     * Creates a styled JLabel for use in the credits list.
     *
     * @param text the label's displayed text
     * @return the formatted JLabel
     */
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(CENTER_ALIGNMENT);
        label.setFont(new Font("Serif", Font.BOLD, 30));
        label.setForeground(new Color(169, 177, 217));
        return label;
    }

    /**
     * Creates a custom button with styling and hover effects.
     *
     * @param text text displayed on the button
     * @return fully styled JButton
     */
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Serif", Font.PLAIN, 37));
        button.setForeground(new Color(169, 177, 217));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(new Color(169, 177, 217));
            }
        });

        return button;
    }

    /**
     * Returns the user to the Main Menu by replacing the content pane.
     * Ensures UI is refreshed afterward.
     */
    private void goBack() {
        main.setContentPane(new MainMenu(main));
        main.revalidate();
        main.repaint();
    }
}
