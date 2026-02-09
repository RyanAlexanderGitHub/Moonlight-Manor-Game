import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

public class Hotspot {
    public String id, name;
    public Rectangle bounds;
    List<Interaction> interactions = new ArrayList<>();
    
    public Hotspot(String id, String name, Rectangle b) {
        this.id = id; this.name = name; this.bounds = b;
    }
    
    public void addInteraction(Interaction i) { interactions.add(i); }
    
    public boolean contains(Point p) { return bounds.contains(p); }
    
    public Interaction getInteraction(InteractionType t, Game g) {
        for (Interaction i : interactions) {
            if (i.getType() == t && i.isAvailable(g, this)) return i;
        }
        return null;
    }
}