/**
 * Represents an item that can be stored in the player's inventory and used
 * within interactions throughout the game. Each item has a unique identifier,
 * a display name, and a descriptive text shown when examined.
 *
 * <p>Items are lightweight data objects with no interactive behavior by
 * themselves; instead, their meaning and functionality arise from interactions
 * defined in Hotspots.</p>
 *
 * @author Ryan Matthew Alexander
 * @author Harshdeep Gill
 * @author Keenan Rodricks
 * @author Om Kothare
 * @author Evan Grawey
 * @version 1.0
 */
public class Item {
    public String id, name, desc;
    
    /**
     * Constructs a new Item with the given identifier, display name, and description.
     *
     * @param id   The internal unique string ID of the item.
     * @param name The user-facing name that appears in the inventory.
     * @param desc A descriptive text shown when examining the item.
     */
    public Item(String id, String name, String desc) {
        this.id = id; this.name = name; this.desc = desc;
    }
    
    /**
     * Returns the item's display name. This is used by UI components such as
     * inventory lists and debug messages.
     *
     * @return The name of the item.
     */
    @Override
    public String toString() { return name; }
}