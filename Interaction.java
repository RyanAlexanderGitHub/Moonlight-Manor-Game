// --- Game engine structures (Interfaces and Enums) ---

// Define enums outside to be public
enum GameState { IDLE, CUTSCENE, PUZZLE }
enum InteractionType { EXAMINE, PICKUP, USE }

// Define interfaces outside to be public
interface Action { void execute(Game g, Hotspot h); }
interface Condition { boolean isMet(Game g, Hotspot h); }
interface PuzzleCallback { void onSolve(); void onExit(); }

/**
 * Represents a single interaction available on a Hotspot. An {@code Interaction}
 * defines:
 * <ul>
 *     <li>The type of interaction ({@link InteractionType})</li>
 *     <li>An optional {@link Condition} that restricts when it is available</li>
 *     <li>An {@link Action} executed when the interaction occurs</li>
 * </ul>
 *
 * <p>An Interaction is stateless and relies on the Game and Hotspot for context.</p>
 * @author Ryan Matthew Alexander
 * @author Harshdeep Gill
 * @author Keenan Rodricks
 * @author Om Kothare
 * @author Evan Grawey
 * @version 1.0
 */
public class Interaction {
    InteractionType type;
    Condition cond; 
    Action action;
    
    /**
     * Constructs a new Interaction with the specified type, condition, and action.
     *
     * @param t The type of interaction.
     * @param c The condition required for activation (may be {@code null}).
     * @param a The action to execute when triggered.
     */
    public Interaction(InteractionType t, Condition c, Action a) {
        this.type = t; this.cond = c; this.action = a;
    }
    
    /**
     * Determines whether this interaction is currently available.
     * A null condition means the interaction is always available.
     *
     * @param g The active game instance.
     * @param h The hotspot being interacted with.
     * @return {@code true} if the condition is satisfied or nonexistent.
     */
    public boolean isAvailable(Game g, Hotspot h) {
        return cond == null || cond.isMet(g, h);
    }
    
    /**
     * Executes the action associated with this interaction.
     *
     * @param g The game instance.
     * @param h The hotspot targeted by the interaction.
     */
    public void execute(Game g, Hotspot h) {
        action.execute(g, h);
    }
    
    /**
     * Returns the type of this interaction.
     *
     * @return The {@link InteractionType} of this interaction.
     */
    public InteractionType getType() { return type; }
}