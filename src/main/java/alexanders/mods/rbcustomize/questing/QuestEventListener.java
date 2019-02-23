package alexanders.mods.rbcustomize.questing;

import alexanders.mods.rbcustomize.RBCustomize;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.compendium.ICompendiumRecipe;
import de.ellpeck.rockbottom.api.event.Event;
import de.ellpeck.rockbottom.api.event.IEventListener;
import de.ellpeck.rockbottom.api.event.impl.*;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemTile;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public final class QuestEventListener {
    private static final ArrayList<IEventListener> listeners;
    private static final HashMap<Item, Quest> itemPickupTriggers;
    private static final HashMap<Tile, Quest> tileBreakTriggers;
    private static final HashMap<Tile, Quest> tilePlaceTriggers;
    private static final HashMap<ICompendiumRecipe, Quest> constructTriggers;

    static {
        listeners = new ArrayList<>();
        itemPickupTriggers = new HashMap<>();
        tileBreakTriggers = new HashMap<>();
        tilePlaceTriggers = new HashMap<>();
        constructTriggers = new HashMap<>();

        addListener(PlaceTileEvent.class, (result, event) -> {
            Tile tile = ((ItemTile) event.instance.getItem()).getTile();
            if (tilePlaceTriggers.containsKey(tile)) tilePlaceTriggers.get(tile).fulfill(event.player.getUniqueId(), event.instance);
            return result;
        });
        addListener(BreakEvent.class, (result, event) -> {
            TileState state = RockBottomAPI.getGame().getWorld().getState(event.layer, event.x, event.y);
            Tile tile = state.getTile();
            if (tileBreakTriggers.containsKey(tile)) tileBreakTriggers.get(tile).fulfill(event.player.getUniqueId(), state);
            return result;
        });
        addListener(ItemPickupEvent.class, (result, event) -> {
            if (itemPickupTriggers.containsKey(event.instance.getItem())) itemPickupTriggers.get(event.instance.getItem()).fulfill(event.player.getUniqueId(), event.instance);
            return result;
        });
        addListener(ConstructEvent.class, (result, event) -> {
            if (constructTriggers.containsKey(event.recipe)) constructTriggers.get(event.recipe).fulfill(event.player.getUniqueId(), event.inputInventory);
            return result;
        });

        addListener(WorldLoadEvent.class, (result, event) -> {
            if (event.world.isStoryMode()) {
                // Create questing files
                try {
                    Path questingDirectoryPath = Paths.get(event.world.getFolder().getAbsolutePath(), "questing");
                    File questingDirectory = questingDirectoryPath.toFile();
                    if (!questingDirectory.isDirectory()) {
                        if (!questingDirectory.mkdirs()) throw new IOException("Directory creation failed!! Path: " + questingDirectoryPath);
                    }
                    File questBookFile = questingDirectoryPath.resolve("questBook.json").toFile();
                    if (!questBookFile.exists()) {
                        if (!questBookFile.createNewFile()) throw new IOException("Quest book file creation failed: " + questBookFile);
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(questBookFile), StandardCharsets.UTF_8));
                        Util.GSON.toJson(new QuestBook(), writer);
                        writer.close();
                    }
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(questBookFile), StandardCharsets.UTF_8));
                    RBCustomize.questBook = Util.GSON.fromJson(reader, QuestBook.class);
                    reader.close();
                    if(RBCustomize.questBook == null)
                        RBCustomize.questBook = new QuestBook();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
            return result;
        });
        
        addListener(WorldSaveEvent.class, (result, event) ->{
            if(event.world.isStoryMode()) {
                try {
                    Path questingDirectoryPath = Paths.get(event.world.getFolder().getAbsolutePath(), "questing");
                    File questingDirectory = questingDirectoryPath.toFile();
                    if (!questingDirectory.isDirectory()) {
                        if (!questingDirectory.mkdirs()) throw new IOException("Directory creation failed!! Path: " + questingDirectoryPath);
                    }
                    File questBookFile = questingDirectoryPath.resolve("questBook.json").toFile();
                    if (!questBookFile.exists()) {
                        if (!questBookFile.createNewFile()) throw new IOException("Quest book file creation failed: " + questBookFile);
                    }
                    if(RBCustomize.questBook != null) {
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(questBookFile), StandardCharsets.UTF_8));
                        Util.GSON.toJson(RBCustomize.questBook, writer);
                        writer.close();
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
            return result;});
    }

    public static void addItemPickupTrigger(Quest quest, Item triggerObject) {
        itemPickupTriggers.put(triggerObject, quest);
    }

    public static void addTileBreakTrigger(Quest quest, Tile triggerObject) {
        tileBreakTriggers.put(triggerObject, quest);
    }

    public static void addTilePlaceTrigger(Quest quest, Tile triggerObject) {
        tilePlaceTriggers.put(triggerObject, quest);
    }

    public static void addConstructTrigger(Quest quest, ICompendiumRecipe triggerObject) {
        constructTriggers.put(triggerObject, quest);
    }

    public static <T extends Event> void addListener(Class<T> clazz, IEventListener<T> listener) {
        listeners.add(listener);
        RockBottomAPI.getEventHandler().registerListener(clazz, listener);
    }

    public static <T extends Event> void removeListener(Class<T> clazz, IEventListener<T> listener) {
        listeners.remove(listener);
        RockBottomAPI.getEventHandler().unregisterListener(clazz, listener);
    }
}
