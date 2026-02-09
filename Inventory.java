import java.util.ArrayList;
import java.util.List;

/**
 * Represents the player's inventory, storing a limited number of {@link Item}
 * objects and allowing for item selection, addition, removal, and lookup.
 *
 * <p>The inventory maintains:
 * <ul>
 *     <li>A fixed capacity</li>
 *     <li>A list of currently held items</li>
 *     <li>An optional currently selected item</li>
 * </ul>
 *
 * <p>The inventory does not enforce item uniqueness; it simply respects capacity.
 * Selection acts as a toggle—selecting an already selected item will deselect it.</p>
 * 
 * @author Ryan Matthew Alexander
 * @author Harshdeep Gill
 * @author Keenan Rodricks
 * @author Om Kothare
 * @author Evan Grawey
 * @version 1.0
 */
public class Inventory {
    List<Item> items = new ArrayList<>();
    int capacity = 6;
    public Item selected = null;
    
    /**
     * Constructs a new inventory with the specified capacity.
     *
     * @param cap The maximum number of items allowed.
     */
    public Inventory(int cap) { capacity = cap; }
    
    /**
     * Attempts to add an item to the inventory.
     *
     * @param i The item to add.
     * @return {@code true} if added successfully, or {@code false} if full.
     */
    public boolean add(Item i) {
        if (items.size() >= capacity) return false;
        items.add(i);
        return true;
    }
    
    /**
     * Removes the specified item from the inventory, if present.
     *
     * @param i The item to remove.
     */
    public void remove(Item i) { items.remove(i); }
    
    /**
     * Toggles the selection state of an item.
     * <ul>
     *     <li>If the item is currently selected, it becomes unselected.</li>
     *     <li>If a different item is selected, selection moves to the new item.</li>
     * </ul>
     *
     * @param i The item to select or deselect.
     */
    public void select(Item i) {
        if (selected == i) selected = null;
        else selected = i;
    }
    
    /**
     * Returns the currently selected item.
     *
     * @return The selected item, or {@code null} if none is selected.
     */
    public Item getSelected() { return selected; }
    
    /**
     * Returns the list of all items in the inventory.
     *
     * @return A list of held items.
     */
    public List<Item> getItems() { return items; }

    /**
     * Checks whether the inventory contains an item with the specified ID.
     *
     * @param id The item ID to search for.
     * @return {@code true} if an item with the given ID exists, otherwise {@code false}.
     */
    public boolean contains(String id) {
        return items.stream().anyMatch(item -> item.id.equals(id));
    }
}