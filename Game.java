import java.awt.CardLayout;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The Game class functions as the central controller for the entire game.
 * It maintains global state, manages scenes, handles player interaction logic,
 * triggers puzzle transitions, updates UI components, and routes input from the
 * PlayPanel to the appropriate Hotspot.
 *
 * <p>This class bridges gameplay logic with user interface components, coordinating:
 * <ul>
 *   <li>Scene navigation</li>
 *   <li>Inventory management</li>
 *   <li>Hotspot interaction resolution</li>
 *   <li>Puzzle activation and completion</li>
 *   <li>Feedback messaging</li>
 *   <li>Interaction tracking</li>
 * </ul>
 *
 * @author Ryan Matthew Alexander
 * @author Harshdeep Gill
 * @author Keenan Rodricks
 * @author Om Kothare
 * @author Evan Grawey
 * @version 1.0
 */
public class Game {
    Map<String, Scene> scenes = new HashMap<>();
    Inventory inventory = new Inventory(6);
    Scene currentScene;
    GameState state = GameState.IDLE;
    
    // UI component references (now public fields for easier access from other classes)
    public JLabel messageListener; // Unused in this version, but kept for structure
    public InfoPanel infoPanel; 
    public InventoryPanel inventoryPanel; 
    public PlayPanel playPanel;
    public JPanel centerContainer;
    public CardLayout cardLayout;
    
    // Puzzle Panel references
    public KeypadPanel keypad;
    public TilePuzzlePanel tilePuzzle;
    public LeverPuzzlePanel leverPuzzle; 
    public DialPuzzlePanel dialPuzzle; 
    
    int interactionCount = 0;

    /**
     * Assigns all UI and puzzle panel references to the Game instance, allowing
     * it to switch between panels and update UI elements throughout gameplay.
     *
     * @param container The container that holds the main scene and puzzle panels.
     * @param layout The CardLayout managing view switching.
     * @param kp The keypad puzzle panel.
     * @param tp The tile puzzle panel.
     * @param lp The lever puzzle panel.
     * @param dp The dial puzzle panel.
     * @param ip The informational display panel.
     * @param ipanel The inventory panel.
     * @param pp The gameplay panel containing hotspots.
     */
    public void setupViews(JPanel container, CardLayout layout, KeypadPanel kp, TilePuzzlePanel tp, LeverPuzzlePanel lp, DialPuzzlePanel dp, InfoPanel ip, InventoryPanel ipanel, PlayPanel pp) {
        this.centerContainer = container;
        this.cardLayout = layout;
        this.keypad = kp;
        this.tilePuzzle = tp;
        this.leverPuzzle = lp; 
        this.dialPuzzle = dp; 
        this.infoPanel = ip;
        this.inventoryPanel = ipanel;
        this.playPanel = pp;
    }

    /**
     * Registers a new scene with the game under its unique ID.
     *
     * @param s The scene to add.
     */
    public void addScene(Scene s) { scenes.put(s.id, s); }
    
    /**
     * Returns the player's inventory.
     *
     * @return The Inventory object.
     */
    public Inventory getInventory() { return inventory; }
    
    /**
     * Displays a floating feedback message on the PlayPanel at a specific location.
     *
     * @param msg The message text.
     * @param location The on-screen location where the message should appear.
     */
    public void showFeedback(String msg, Point location) {
        if (playPanel != null) {
            playPanel.addFeedback(msg, location);
        }
    }

    /**
     * Changes the current scene to the one matching the provided ID.
     * Updates the InfoPanel accordingly. Prints an error if the scene does not exist.
     *
     * @param id The ID of the scene to switch to.
     */
    public void changeScene(String id) {
        Scene next = scenes.get(id);
        if (next == null) {
            System.out.println("Can't go there.");
            return;
        }
        currentScene = next;
        if (infoPanel != null) {
            infoPanel.updateLocation(currentScene.title, currentScene.description);
        }
        System.out.println("Location: " + currentScene.title);
    }

    /**
     * Initiates a puzzle by switching the UI into puzzle mode and initializing
     * the appropriate puzzle panel. Updates UI text and interaction count.
     *
     * @param puzzleName The name/type of puzzle ("Keypad", "TilePuzzle", etc.).
     * @param code Optional initialization value used by certain puzzles.
     * @param callback A callback object invoked when the puzzle is solved or exited.
     */
    public void startPuzzle(String puzzleName, String code, PuzzleCallback callback) {
        state = GameState.PUZZLE;
        infoPanel.updateDescription("Starting " + puzzleName + "...");

        if (puzzleName.equals("Keypad")) {
            keypad.init(code, callback);
            cardLayout.show(centerContainer, "KEYPAD");
        } else if (puzzleName.equals("TilePuzzle")) {
            tilePuzzle.init(callback);
            cardLayout.show(centerContainer, "TILE_PUZZLE");
        } else if (puzzleName.equals("LeverPuzzle")) { 
            leverPuzzle.init(callback);
            cardLayout.show(centerContainer, "LEVER_PUZZLE");
        } else if (puzzleName.equals("DialPuzzle")) { 
             dialPuzzle.init("C_A_D", callback);
             cardLayout.show(centerContainer, "DIAL_PUZZLE");
        }
        infoPanel.updateInteractionCount(interactionCount);
    }

    /**
     * Ends the current puzzle, returns the UI to scene view, restores the
     * previous description, and resets the game state to IDLE.
     */
    public void endPuzzle() {
        state = GameState.IDLE;
        cardLayout.show(centerContainer, "SCENE");
        infoPanel.updateInteractionCount(interactionCount);
        if (currentScene != null) {
            infoPanel.updateDescription(currentScene.description);
        }
    }

    /**
     * Handles a click on the PlayPanel and determines what type of interaction
     * should occur. Interaction priority is:
     * <ol>
     *   <li>USE (with selected item)</li>
     *   <li>PICKUP (if no item selected)</li>
     *   <li>EXAMINE</li>
     * </ol>
     *
     * <p>If no valid actions apply, appropriate feedback messages are shown.
     * Interaction count is incremented after every attempt.</p>
     *
     * @param p The location of the click.
     */
    public void handleClick(Point p) {
        if (state != GameState.IDLE || currentScene == null) return;
        
        Hotspot h = currentScene.getHotspotByPoint(p);

        if (h == null) {
            showFeedback("Nothing here.", p); 
            infoPanel.updateDescription("Nothing here.");
            return;
        }

        Item sel = inventory.getSelected();
        
        // Try USE
        Interaction useInt = h.getInteraction(InteractionType.USE, this);
        if (useInt != null && useInt.isAvailable(this, h)) {
            
            useInt.execute(this, h);
            
            // Only deselect if an item was selected, as the Action logic handles item removal/replacement.
            boolean requiresItem = sel != null && useInt.cond != null;
            if (requiresItem) {
                showFeedback("Used " + sel.name + " on " + h.name + ".", p);
                infoPanel.updateDescription("Used " + sel.name + " on " + h.name + ".");
                
                inventory.selected = null;
            }
            this.inventoryPanel.rebuild(); 
            
            interactionCount++;
            infoPanel.updateInteractionCount(interactionCount); 
            return;
        }

        // Try PICKUP
        if (sel == null) {
            Interaction pick = h.getInteraction(InteractionType.PICKUP, this);
            if (pick != null && pick.isAvailable(this, h)) {
                pick.execute(this, h);
                currentScene.hotspots.remove(h.id); 
                infoPanel.updateDescription("Picked up " + h.name + ".");
                this.inventoryPanel.rebuild(); 
                
                interactionCount++; 
                infoPanel.updateInteractionCount(interactionCount);
                return;
            }
        }

        // Try EXAMINE
        Interaction ex = h.getInteraction(InteractionType.EXAMINE, this);
        if (ex != null && ex.isAvailable(this, h)) {
            ex.execute(this, h); 
            
            interactionCount++; 
            infoPanel.updateInteractionCount(interactionCount);
            return;
        }
        
        // If the hotspot was clicked, but no action was available:
        if (sel != null) {
            showFeedback("Can't use " + sel.name + " on " + h.name + ".", p);
            infoPanel.updateDescription("Can't use " + sel.name + " on " + h.name + ".");
        } else {
            showFeedback("Nothing happens at the " + h.name + ".", p);
            infoPanel.updateDescription("Nothing happens at the " + h.name + ".");
        }
        
        interactionCount++; 
        infoPanel.updateInteractionCount(interactionCount);
    }
}