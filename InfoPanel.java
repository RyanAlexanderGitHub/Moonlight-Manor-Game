import javax.swing.*;
import java.awt.*;

public class InfoPanel extends JPanel {
    JLabel interactionsLabel;
    JLabel locationLabel;
    JTextArea descriptionArea;

    public InfoPanel(Game g) {
        setPreferredSize(new Dimension(1024, 100));
        setBackground(new Color(60, 60, 90));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // ------------------ Interactions Label ------------------
        interactionsLabel = new JLabel("Interactions: 0");
        interactionsLabel.setForeground(Color.WHITE);
        interactionsLabel.setFont(new Font("Serif", Font.BOLD, 23));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.gridheight = 2;
        this.add(interactionsLabel, gbc);

        // ------------------ Location Title ------------------
        locationLabel = new JLabel("Location Name", SwingConstants.LEFT);
        locationLabel.setForeground(new Color(169, 177, 217));
        locationLabel.setFont(new Font("Serif", Font.BOLD, 24));
        gbc.gridx = 1; // shifted left to fill gap
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.gridheight = 1;
        this.add(locationLabel, gbc);

        // ------------------ Description Text ------------------
        descriptionArea = new JTextArea("Welcome to Moonlight Manor...");
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(new Color(60, 60, 90));
        descriptionArea.setForeground(Color.WHITE);
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.gridheight = 1;
        this.add(descriptionArea, gbc);

        // ------------------ Main Menu Button ------------------
        JButton pauseButton = new JButton("Main Menu");
        pauseButton.setBackground(new Color(40, 40, 60));
        pauseButton.setForeground(Color.BLACK);
        pauseButton.setFocusPainted(false);

        pauseButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to return to the Main Menu?\nYour current progress will not be saved.",
                    "Return to Main Menu",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                GameReset.resetToMainMenu(this);
            }
        });

        gbc.gridx = 2; // moved left since placeholder is gone
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.gridheight = 2;
        this.add(pauseButton, gbc);
    }

    public void updateInteractionCount(int count) {
        interactionsLabel.setText("Interactions: " + count + " (turn count)");
    }

    public void updateLocation(String title, String description) {
        locationLabel.setText(title);
        descriptionArea.setText(description);
    }

    public void updateDescription(String narrative) {
        descriptionArea.setText(narrative);
    }
}