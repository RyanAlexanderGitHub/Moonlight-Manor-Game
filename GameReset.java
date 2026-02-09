import javax.swing.*;
import java.awt.*;

/**
 * Utility class responsible for resetting the entire game and returning the
 * user to the main menu. This class disposes of the current {@link Main}
 * window and constructs a fresh one, effectively clearing all game state,
 * panels, puzzles, and inventory data.
 *
 * <p>This mechanism is used when the player finishes the game or chooses
 * to restart from the main menu. The old game instance and all associated
 * UI components are safely disposed.</p>
 *
 * @author Ryan Matthew Alexander
 * @author Harshdeep Gill
 * @author Keenan Rodricks
 * @author Om Kothare
 * @author Evan Grawey
 * @version 1.0
 */
public class GameReset {

    /**
     * Resets the running game instance by:
     * <ol>
     *     <li>Locating the top-level {@link Window} that contains the
     *         component which triggered the reset.</li>
     *     <li>Checking whether that window is an instance of {@link Main}.</li>
     *     <li>Creating a brand new Main window (which opens the main menu).</li>
     *     <li>Positioning it relative to the old one.</li>
     *     <li>Disposing the old Main window to free resources and remove
     *         gameplay state.</li>
     * </ol>
     *
     * @param source The UI component from which the reset action originated.
     */
    public static void resetToMainMenu(Component source) {
        Window window = SwingUtilities.getWindowAncestor(source);

        if (window instanceof Main) {
            Main oldMain = (Main) window;

            // Create a brand new Main menu window 
            Main newMain = new Main();

            newMain.setLocationRelativeTo(oldMain);
            
            // Dispose old Game, Inventory, UI, etc
            oldMain.dispose();
        }
    }
}