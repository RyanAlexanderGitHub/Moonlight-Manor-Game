import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Settings extends JPanel {
    private Game game;
    private Main parentFrame;

    public Settings(Game game, Main parentFrame) {
        this.game = game;
        this.parentFrame = parentFrame;
        initialize();
    }

    private void initialize() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(new Color(117, 122, 148));

        JLabel title = new JLabel("Settings");
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setFont(new Font("Serif", Font.BOLD, 50));
        title.setForeground(new Color(169, 177, 217));

        JLabel musicLabel = new JLabel("Music Volume");
        musicLabel.setFont(new Font("Serif", Font.BOLD, 30));
        musicLabel.setAlignmentX(CENTER_ALIGNMENT);
        musicLabel.setForeground(new Color(169, 177, 217));
        JSlider musicSlider = new JSlider(0, 100, 50);
        musicSlider.setMaximumSize(new Dimension(600, 40));

        JLabel sfxLabel = new JLabel("SoundFX Volume");
        sfxLabel.setFont(new Font("Serif", Font.BOLD, 30));
        sfxLabel.setAlignmentX(CENTER_ALIGNMENT);
        sfxLabel.setForeground(new Color(169, 177, 217));
        JSlider sfxSlider = new JSlider(0, 100, 50);
        sfxSlider.setMaximumSize(new Dimension(600, 40));

        JButton backButton = createButton("< Back");
        backButton.setAlignmentX(CENTER_ALIGNMENT);
        backButton.addActionListener(e -> goBack());

        this.add(Box.createVerticalStrut(140));
        this.add(title);
        this.add(Box.createVerticalStrut(50));
        this.add(musicLabel);
        this.add(Box.createVerticalStrut(20));
        this.add(createSliderWithLabel(musicSlider));
        this.add(Box.createVerticalStrut(40));
        this.add(sfxLabel);
        this.add(Box.createVerticalStrut(20));
        this.add(createSliderWithLabel(sfxSlider));
        this.add(Box.createVerticalStrut(50));
        this.add(backButton);
    }

    // Helper method to create slider + % label
    private JPanel createSliderWithLabel(JSlider slider) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);

        Dimension fixedSize = new Dimension(600, slider.getPreferredSize().height);
        panel.setMaximumSize(fixedSize);
        panel.setMinimumSize(fixedSize);
        panel.setPreferredSize(fixedSize);

        JLabel percentLabel = new JLabel(slider.getValue() + "%");
        percentLabel.setFont(new Font("Serif", Font.BOLD, 24));
        percentLabel.setForeground(new Color(169, 177, 217));

        slider.addChangeListener(e -> {
            percentLabel.setText(slider.getValue() + "%");
        });

        panel.add(slider);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(percentLabel);

        return panel;
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

    private void goBack() {
        MainMenu mainMenuPanel = new MainMenu(parentFrame);

        // 2. Set the content pane to the new MainMenu
        parentFrame.setContentPane(mainMenuPanel);

        // 3. Update the display
        parentFrame.revalidate();
        parentFrame.repaint(); // Add repaint for good measure
    }
}
