import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenu extends JPanel {

    // Hold a reference to the outer JFrame to call initGameContent
    private Main parentFrame;

    public MainMenu(Main parentFrame) {
        this.parentFrame = parentFrame;
        initializeMenu();
    }

    private void initializeMenu() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(new Color(80, 82, 102));

        JLabel titleP1 = new JLabel("MYSTERY OF");
        titleP1.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel titleP2 = new JLabel("MOONLIGHT MANOR");
        titleP2.setAlignmentX(Component.CENTER_ALIGNMENT);

        titleP1.setFont(new Font("Serif", Font.BOLD, 60));
        titleP1.setForeground(new Color(169, 177, 217));
        titleP2.setFont(new Font("Serif", Font.BOLD, 60));
        titleP2.setForeground(new Color(169, 177, 217));

        this.add(Box.createVerticalStrut(100));
        this.add(titleP1);
        this.add(Box.createVerticalStrut(10));
        this.add(titleP2);
        this.add(Box.createVerticalStrut(45));

        JButton newGameButton = createButton("PLAY GAME");
        newGameButton.addActionListener(e -> startNewGame());

        JButton settingsButton = createButton("SETTINGS");
        settingsButton.addActionListener(e -> openSettings());

        JButton creditsButton = createButton("CREDITS");
        creditsButton.addActionListener(e -> openCredits());

        JButton exitButton = createButton("QUIT GAME");
        exitButton.addActionListener(e -> System.exit(0));

        this.add(newGameButton);
        this.add(Box.createVerticalStrut(20));
        this.add(settingsButton);
        this.add(Box.createVerticalStrut(20));
        this.add(creditsButton);
        this.add(Box.createVerticalStrut(20));
        this.add(exitButton);
    }

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

    private void startNewGame() {
        if (parentFrame != null) {
            parentFrame.initGameContent();
        }
    }

    private void openSettings() {
        Settings settingsPanel = new Settings(parentFrame.getGame(), parentFrame);
        parentFrame.setContentPane(settingsPanel);
        parentFrame.revalidate();
    }

    private void openCredits() {
        System.out.println("Opening credits...");
        parentFrame.setContentPane(new Credits(parentFrame));
        parentFrame.revalidate();
        parentFrame.repaint();
    }
}
