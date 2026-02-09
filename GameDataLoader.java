import com.google.gson.Gson;
import java.awt.Rectangle;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.Reader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;

public class GameDataLoader {

    // --- DTO Classes for JSON Parsing ---

    public static class GameData {
        public ItemData[] items;
        public SceneData[] scenes;
        public String startSceneId;
    }

    public static class ItemData {
        public String id;
        public String name;
        public String desc;
    }

    public static class SceneData {
        public String id;
        public String title;
        public String description;
        public HotspotData[] hotspots;
    }

    public static class HotspotData {
        public String id;
        public String name;
        public int[] bounds; // [x, y, width, height]
        public InteractionData[] interactions;
    }

    public static class InteractionData {
        public String type; 
        public String requiredItem; 
        public String command; 
        public String feedback; 
    }
    
    // --- Item Map for Quick Lookup ---
    private Map<String, Item> itemMap = new HashMap<>(); 

    public void loadContent(Game game, String filePath) {
        Gson gson = new Gson();
        InputStream inputStream = null;

        // STEP 1: Try to load as a Resource (Standard for Java projects/JARs)
        // This looks inside the 'src' folder or classpath
        inputStream = getClass().getClassLoader().getResourceAsStream(filePath);

        // STEP 2: If not found, try to load from the local file system
        // This looks in the root project folder
        if (inputStream == null) {
            try {
                inputStream = new FileInputStream(filePath);
            } catch (IOException e) {
                System.err.println("ERROR: Could not find '" + filePath + "' in classpath OR filesystem.");
                return;
            }
        }

        // STEP 3: Read the stream
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            GameData data = gson.fromJson(reader, GameData.class);

            if (data == null) {
                System.err.println("ERROR: JSON was found but parsed data is null.");
                return;
            }

            // 1. Populate the Item Map
            if (data.items != null) {
                for (ItemData itemData : data.items) {
                    Item item = new Item(itemData.id, itemData.name, itemData.desc);
                    itemMap.put(item.id, item);
                }
            }

            // 2. Process Scenes, Hotspots, and Interactions
            if (data.scenes != null) {
                for (SceneData sceneData : data.scenes) {
                    Scene scene = new Scene(sceneData.id, sceneData.title, sceneData.description);
                    
                    if (sceneData.hotspots != null) {
                        for (HotspotData hotspotData : sceneData.hotspots) {
                            int[] b = hotspotData.bounds;
                            Hotspot hotspot = new Hotspot(hotspotData.id, hotspotData.name, new Rectangle(b[0], b[1], b[2], b[3]));
                            
                            if (hotspotData.interactions != null) {
                                for (InteractionData idata : hotspotData.interactions) {
                                    Interaction interaction = createInteraction(idata, hotspot, scene);
                                    hotspot.addInteraction(interaction);
                                }
                            }
                            scene.addHotspot(hotspot);
                        }
                    }
                    game.addScene(scene);
                }
            }
            
            // 3. Set the starting scene
            if (data.startSceneId != null) {
                game.changeScene(data.startSceneId);
            }

        } catch (IOException e) {
            System.err.println("Error loading game data from " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // --- The Core Logic Converter ---
    private Interaction createInteraction(InteractionData idata, Hotspot currentHotspot, Scene currentScene) {
        InteractionType type = InteractionType.valueOf(idata.type);
        
        // Handle cases where command might be missing
        if (idata.command == null) {
            return new Interaction(type, (g, h) -> true, (g, h) -> {});
        }

        String[] parts = idata.command.split(":");
        String commandType = parts[0];
        
        // 1. Condition (Item Requirement)
        Condition condition = (g, h) -> {
            if (idata.requiredItem == null || idata.requiredItem.isEmpty()) return true;
            return g.getInventory().getSelected() != null && g.getInventory().getSelected().id.equals(idata.requiredItem);
        };

        // 2. Action (Execution Logic)
        Action action = (g, h) -> {
            if (idata.feedback != null && !idata.feedback.isEmpty()) {
                g.showFeedback(idata.feedback, h.bounds.getLocation());
            }

            switch (commandType) {
                case "CHANGE_SCENE":
                    if(parts.length > 1) g.changeScene(parts[1]); 
                    break;

                case "GIVE_ITEM":
                    if(parts.length > 1) {
                        Item itemToGive = itemMap.get(parts[1]);
                        if(itemToGive != null) g.getInventory().add(itemToGive);
                    }
                    break;

                case "EXAMINE_DESC":
                    if(parts.length > 1) g.infoPanel.updateDescription(parts[1]);
                    break;
                    
                case "ITEM_USE_RESULT":
                    // Format: ITEM_USE_RESULT:REMOVE_ITEM:rustyKey:ADD_ITEM:brassHandle:REPLACE_HOTSPOT:lockedPantry:openPantry
                    if (parts.length > 1 && parts[1].equals("REMOVE_ITEM")) {
                        g.getInventory().remove(g.getInventory().getSelected());
                    }
                    if (parts.length > 4 && parts[3].equals("ADD_ITEM")) {
                         if(!parts[4].equals("NONE")) g.getInventory().add(itemMap.get(parts[4]));
                    }

                    if (parts.length > 6 && parts[5].equals("REPLACE_HOTSPOT")) {
                        String oldHotspotId = parts[6];
                        String newHotspotId = parts[7];
                        
                        currentScene.hotspots.remove(oldHotspotId);
                        
                        if (newHotspotId.equals("openPantry")) {
                            Hotspot openPantry = new Hotspot(newHotspotId, "Pantry Door (Open)", h.bounds);
                            openPantry.addInteraction(new Interaction(InteractionType.EXAMINE, null, (gameInner, hotspotInner) -> 
                                gameInner.infoPanel.updateDescription("The pantry is now open, nothing else of interest.")));
                            currentScene.addHotspot(openPantry);
                        }
                        
                        if (newHotspotId.equals("mechanismReady")) {
                             Hotspot mechanismReady = new Hotspot("mechanismReady", "Dial Mechanism (Ready)", h.bounds);
                            mechanismReady.addInteraction(new Interaction(InteractionType.USE, (gameInner, hotspotInner) -> 
                                    gameInner.getInventory().contains("crypticSymbol"), (gameInner, hotspotInner) -> {
                                gameInner.startPuzzle("DialPuzzle", "C_A_D", new PuzzleCallback() {
                                    @Override public void onSolve() {
                                        gameInner.showFeedback("The drawer slides open! Success!", hotspotInner.bounds.getLocation());
                                        gameInner.infoPanel.updateDescription("The drawer opens! You found the Coded Dossier. Game Complete!");
                                        gameInner.getInventory().add(itemMap.get("codedDossier"));
                                        currentScene.hotspots.remove("mechanismReady");
                                        gameInner.getInventory().selected = null;
                                        gameInner.inventoryPanel.rebuild();
                                        gameInner.endPuzzle();
                                    }
                                    @Override public void onExit() { gameInner.endPuzzle(); }
                                });
                            }));
                            mechanismReady.addInteraction(new Interaction(InteractionType.EXAMINE, null, (gameInner, hotspotInner) -> 
                                gameInner.infoPanel.updateDescription("The brass handle is now in place. A triple dial is visible (A, B, C, D). You need a sequence.")));
                            currentScene.addHotspot(mechanismReady);
                        }
                        
                        if (newHotspotId.equals("mainAttic")) {
                            Hotspot mainAttic = new Hotspot("mainAttic", "Hatch to the Attic", new Rectangle(300, 100, 400, 200));
                            mainAttic.addInteraction(new Interaction(InteractionType.USE, null, (gameInner, hotspotInner) -> gameInner.changeScene("attic_interior")));
                            currentScene.addHotspot(mainAttic);
                            currentScene.hotspots.remove("hatchChain");
                        }
                    }
                    g.getInventory().selected = null;
                    g.inventoryPanel.rebuild();
                    break;

                case "START_PUZZLE":
                    String puzzleName = parts[1];
                    String puzzleCode = parts[2];
                    String hotspotToRemove = parts[3].equals("NONE") ? null : parts[3];
                    String postSolveCommand = parts.length > 4 ? parts[4] + ":" + parts[5] : null;

                    g.startPuzzle(puzzleName, puzzleCode, new PuzzleCallback() {
                        @Override
                        public void onSolve() {
                            g.showFeedback("Success!", h.bounds.getLocation());
                            
                            if (postSolveCommand != null) {
                                if (parts[4].equals("GIVE_ITEM")) {
                                    if(itemMap.get(parts[5]) != null) {
                                        g.getInventory().add(itemMap.get(parts[5]));
                                        g.showFeedback("Acquired " + itemMap.get(parts[5]).name + ".", h.bounds.getLocation());
                                    }
                                }
                            }
                            
                            if (hotspotToRemove != null) {
                                currentScene.hotspots.remove(hotspotToRemove);
                                
                                if (hotspotToRemove.equals("safe")) {
                                    Hotspot openSafe = new Hotspot("openSafe", "Open Safe (Empty)", h.bounds);
                                    currentScene.addHotspot(openSafe);
                                }
                                if (hotspotToRemove.equals("tilePuzzleBox")) {
                                    Hotspot openBox = new Hotspot("openBox", "Open Storage Box (Empty)", h.bounds);
                                    currentScene.addHotspot(openBox);
                                }
                                
                                if (hotspotToRemove.equals("leverMechanism_hatchChain")) {
                                    currentScene.hotspots.remove("leverMechanism");
                                    currentScene.hotspots.remove("hatchChain");
                                    
                                    Hotspot mainAttic = new Hotspot("mainAttic", "Hatch to the Attic", new Rectangle(300, 100, 400, 200));
                                    mainAttic.addInteraction(new Interaction(InteractionType.USE, null, (gameInner, hotspotInner) -> gameInner.changeScene("attic_interior")));
                                    currentScene.addHotspot(mainAttic);
                                }
                            }
                            g.endPuzzle();
                        }
                        @Override
                        public void onExit() {
                            g.showFeedback("You backed away.", h.bounds.getLocation());
                            g.endPuzzle();
                        }
                    });
                    break;
                
                default:
                    g.infoPanel.updateDescription("Unknown command: " + commandType);
                    break;
            }
        };
        
        return new Interaction(type, condition, action);
    }
}