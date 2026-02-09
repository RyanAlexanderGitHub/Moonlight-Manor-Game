import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }

    // --- Main panels and Game State ---
    // ... existing component fields ...
    Game game = new Game();
    InfoPanel infoPanel = new InfoPanel(game);
    PlayPanel playPanel = new PlayPanel(game);
    KeypadPanel keypadPanel = new KeypadPanel(game);
    TilePuzzlePanel tilePuzzlePanel = new TilePuzzlePanel(game);
    LeverPuzzlePanel leverPuzzlePanel = new LeverPuzzlePanel(game);
    DialPuzzlePanel dialPuzzlePanel = new DialPuzzlePanel(game);
    InventoryPanel inventoryPanel = new InventoryPanel(game);

    JPanel mainPanel = new JPanel(new BorderLayout());
    JPanel centerContainer = new JPanel();
    CardLayout cardLayout = new CardLayout();

    public Main() {
        setTitle("Mystery Of Moonlight Manor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLayout(new BorderLayout());
        
        // Pass a reference to this JFrame to the MainMenu for the startNewGame action
        setContentPane(new MainMenu(this)); // MainMenu is assumed to exist
        
        setVisible(true);
    }
    
    
    public Game getGame() {
        return game;
    }

    // This method is called by MainMenu.startNewGame()
    void initGameContent() {
        mainPanel.removeAll();
        
        centerContainer.setLayout(cardLayout);
        centerContainer.add(playPanel, "SCENE");
        centerContainer.add(keypadPanel, "KEYPAD");
        centerContainer.add(tilePuzzlePanel, "TILE_PUZZLE");
        centerContainer.add(leverPuzzlePanel, "LEVER_PUZZLE");
        centerContainer.add(dialPuzzlePanel, "DIAL_PUZZLE");
        
        // Setup the Game class with references to all views
        game.setupViews(centerContainer, cardLayout, keypadPanel, tilePuzzlePanel, leverPuzzlePanel, dialPuzzlePanel, infoPanel, inventoryPanel, playPanel);    

        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(centerContainer, BorderLayout.CENTER);
        mainPanel.add(inventoryPanel, BorderLayout.SOUTH);
        
        // --- UPDATED LOADING LOGIC ---
        GameDataLoader loader = new GameDataLoader();
        loader.loadContent(game, "gamedata.json"); // Loads content from JSON
        
        inventoryPanel.rebuild();
        
        // Switch the JFrame content to the main game view
        setContentPane(mainPanel);
        revalidate();
        repaint();
    }
    
    // The old loadDemoContent() method is now removed.
}