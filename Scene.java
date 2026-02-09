import java.awt.Point;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Scene {
    public String id, title, description;
    public Map<String, Hotspot> hotspots = new LinkedHashMap<>();
    
    public Scene(String id, String title, String description) { 
        this.id = id; this.title = title; this.description = description;
    }
    
    public void addHotspot(Hotspot h) { hotspots.put(h.id, h); }
    
    public Hotspot getHotspotByPoint(Point p) {
        for (Hotspot h : hotspots.values()) {
            if (h.contains(p)) return h;
        }
        return null;
    }
    
    public Collection<Hotspot> getHotspots() { return hotspots.values(); }
}