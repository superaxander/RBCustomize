package alexanders.mods.rbcustomize.questing;

import com.google.gson.annotations.JsonAdapter;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.UUID;

public class Quest {
    public String name;
    public String description;
    public TriggerType triggerType;

    @JsonAdapter(TriggerObjectAdapter.class)
    public Object triggerObject = null;
    public ArrayList<Quest> requiredQuests;
    public ArrayList<Quest> dependantQuests;
    public ArrayList<UUID> fulfilledBy;

    public float x;
    public float y;
    @JsonAdapter(ResourceNameAdapter.class)
    public ResourceName itemIcon;

    public Quest(String name, String description, TriggerType triggerType, Object triggerObject) {
        this(name, description, triggerType, triggerObject, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 94, 69, null);
    }

    public Quest(String name, String description, TriggerType triggerType, Object triggerObject, ArrayList<Quest> requiredQuests, ArrayList<Quest> dependantQuests, ArrayList<UUID> fulfilledBy, float x, float y, ResourceName itemIcon) {
        this.name = name;
        this.description = description;
        this.triggerType = triggerType;
        this.requiredQuests = requiredQuests;
        this.dependantQuests = dependantQuests;
        this.fulfilledBy = fulfilledBy;
        this.itemIcon = itemIcon;
        this.x = x;
        this.y = y;

        setTriggerObject(triggerObject);
    }

    public void fulfill(UUID player, Object triggerObject) {
        fulfilledBy.add(player);
        if (this.triggerObject instanceof LuaValue) {
            ((LuaValue) this.triggerObject).invoke(LuaValue.valueOf("FULFILL"), LuaValue.userdataOf(triggerObject));
        }
    }

    public void setTriggerObject(Object triggerObject) {
        this.triggerObject = triggerObject;
        switch (triggerType) {
            case GATHER_ITEM:
                if (triggerObject instanceof Item) {
                    QuestEventListener.addItemPickupTrigger(this, (Item) triggerObject);
                } else if (triggerObject instanceof Tile) {
                    QuestEventListener.addItemPickupTrigger(this, ((Tile) triggerObject).getItem()); //TODO: Null check
                } else {
                    throw new IllegalArgumentException("Expected the trigger object of a gather_item quest to be an item or a tile");
                }
                break;
            case BREAK_TILE:
                if (triggerObject instanceof Tile) {
                    QuestEventListener.addTileBreakTrigger(this, (Tile) triggerObject);
                } else {
                    throw new IllegalArgumentException("Expected the trigger object of a break_tile quest to be a tile");
                }
                break;
            case PLACE_TILE:
                if (triggerObject instanceof Tile) {
                    QuestEventListener.addTilePlaceTrigger(this, (Tile) triggerObject);
                } else {
                    throw new IllegalArgumentException("Expected the trigger object of a place_tile quest to be a tile");
                }
                break;
            case CONSTRUCT_ITEM:
                if (triggerObject instanceof IRecipe) {
                    QuestEventListener.addConstructTrigger(this, (IRecipe) triggerObject);
                } else {
                    throw new IllegalArgumentException("Expected the trigger object of a construct_item quest to be a recipe");
                }
                break;
            case CUSTOM:
                if (triggerObject instanceof LuaValue) {
                    try {
                        ((LuaValue) triggerObject).invoke(LuaValue.valueOf("init"));
                    } catch (LuaError e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public enum TriggerType {
        GATHER_ITEM("Gather item"), BREAK_TILE("Break tile"), PLACE_TILE("Place tile"), CONSTRUCT_ITEM("Construct item"), CUSTOM("Script defined");

        private final String name;

        TriggerType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
