package alexanders.mods.rbcustomize.lua;

import alexanders.mods.rbcustomize.RBCustomize;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.data.set.ModBasedDataSet;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemBasic;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.item.ToolProperty;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.TwoArgFunction;

public class ItemsLib extends TwoArgFunction {
    public static LuaValue itemInstanceToLua(ItemInstance instance) {
        if (instance == null) return NIL;
        LuaValue set = NIL;
        if (instance.hasAdditionalData()) set = LuaEnvironment.globals.get("DataSet").call(userdataOf(instance.getAdditionalData()));
        return LuaEnvironment.globals.get("ItemInstance")
                .invoke(new LuaValue[]{valueOf(instance.getItem().getName().toString()), valueOf(instance.getAmount()), valueOf(instance.getMeta()), set}).arg1();
    }

    public static ItemInstance parseItemInstance(int i, LuaValue table) {
        LuaValue lItem = table.get("item");
        LuaValue lMeta = table.get("meta");
        LuaValue lAmount = table.get("amount");
        LuaValue lSet = table.get("set");
        if (!lItem.isstring()) throw new LuaError(argerror(i, "Expected a string value for field 'item' in ItemInstance"));
        if (!lMeta.isint()) throw new LuaError(argerror(i, "Expected an int value for field 'meta' in ItemInstance"));
        if (!lAmount.isint()) throw new LuaError(argerror(i, "Expected an int value for field 'amount' in ItemInstance"));
        ResourceName name;
        try {
            name = new ResourceName(lItem.tojstring());
        } catch (IllegalArgumentException e) {
            throw new LuaError(argerror(i, "Specified name is not a valid resource name"));
        }
        Item item = Registries.ITEM_REGISTRY.get(name);
        if (item == null) throw new LuaError(argerror(i, "No item with the specified name was found"));
        int meta = lMeta.toint();
        if (meta > item.getHighestPossibleMeta()) throw new LuaError(argerror(i, "The specified meta is too high for this item"));
        ModBasedDataSet set;
        if (lSet.istable()) {
            LuaValue lBackingData = lSet.get("backingData");
            if (!lBackingData.isuserdata(ModBasedDataSet.class)) throw new LuaError(argerror(i, "Specified data set was not of type ModBasedDataSet"));
            set = (ModBasedDataSet) lBackingData.touserdata();
        } else if (lSet.isnil()) {
            set = null;
        } else {
            throw new LuaError(argerror(i, "Expected a DataSet value for field 'set' in ItemInstance"));
        }

        ItemInstance instance = new ItemInstance(item, lAmount.toint(), meta);
        instance.setAdditionalData(set);
        return instance;
    }

    public static LuaValue argerror(int i, String expected) {
        if (i == -1) return error(expected);
        else return LuaValue.argerror(i, expected);
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable items = new LuaTable();
        items.set("add", new FunctionWrapper(this::addItem));
        items.set("remove", new FunctionWrapper(this::remove));
        items.set("getMaxAmount", new FunctionWrapper(this::getMaxAmount));
        env.set("items", items);
        return items;
    }

    private Varargs remove(Varargs varargs) {
        String lName = varargs.checkjstring(1);
        if (!Util.isResourceName(lName)) return argerror(1, "Expected a ResourceName for argument 'item'");
        Registries.ITEM_REGISTRY.unregister(new ResourceName(lName));
        return NIL;
    }

    private Varargs getMaxAmount(Varargs varargs) {
        String sItem = varargs.checkjstring(1);
        if (!Util.isResourceName(sItem)) return argerror(1, "Expected a resource name for argument 'item'");
        Item item = Registries.ITEM_REGISTRY.get(new ResourceName(sItem));
        if (item == null) return argerror(1, "No item with the specified name was found");
        return valueOf(item.getMaxAmount());
    }

    private Varargs addItem(Varargs varargs) { // name, description, max amount, tooltype, mining speed, interactionFunction -> item, ok
        if (varargs.narg() < 1) return argerror("Expected at leas 1 argument");
        LuaValue lName = varargs.arg(1);
        if (!lName.isstring()) return argerror(1, "Expected a string value for argument 'name'");
        ResourceName name;
        try {
            name = new ResourceName(lName.tojstring());
        } catch (IllegalArgumentException e) {
            return argerror(1, "Specified name was not a resource name");
        }

        if (Registries.ITEM_REGISTRY.get(name) != null) return argerror(1, "Specified name already in use");

        String[] description;
        LuaValue lDescription = varargs.arg(2);
        if (lDescription.istable()) {
            description = new String[lDescription.length()];
            for (int i = 1; i <= description.length; i++) {
                LuaValue lLine = lDescription.get(i);
                if (!lLine.isstring()) return argerror(2, "Specified description table must contain only strings");
                description[i - 1] = lLine.tojstring();
            }
        } else if (lDescription.isstring()) {
            description = new String[]{lDescription.tojstring()};
        } else if (lDescription.isnil()) {
            description = new String[0];
        } else {
            return argerror(2, "Expected a table, string or nil value for argument 'description'");
        }

        int maxAmount;
        LuaValue lMaxAmount = varargs.arg(3);
        if (lMaxAmount.isint()) {
            maxAmount = lMaxAmount.toint();
        } else if (lMaxAmount.isnil()) {
            maxAmount = 999;
        } else {
            return argerror(3, "Expected an int or nil value for argument 'maxAmount'");
        }

        HashMap<ToolProperty, Integer> toolProperties = new HashMap<>();
        LuaValue lToolProperties = varargs.arg(4);
        if (lToolProperties.istable()) {
            for (int i = 1; i <= lToolProperties.length(); i++) {
                LuaValue lToolProperty = lToolProperties.get(i);
                if (!lToolProperty.istable()) return argerror(4, "Specified toolLevels table must contain only tables");
                try {
                    LuaValue lType = lToolProperty.get("tp");
                    if (!lType.isstring()) return argerror(4, "Specified property(tp) in ToolLevel table must be a string value");
                    String sType = lType.tojstring();
                    if (!Util.isResourceName(sType)) return argerror(4, "Specified property(tp) in ToolLevel table must be a resource name");
                    LuaValue lLevel = lToolProperty.get("level");
                    if (!lLevel.isint()) return argerror(4, "Specified level in ToolLevel table must be a int value");
                    toolProperties.put(Registries.TOOL_PROPERTY_REGISTRY.get(new ResourceName(sType)), lLevel.toint());
                } catch (IllegalArgumentException e) {
                    return argerror(4, "Specified ToolProperty was not recognized!");
                }
            }
        } else if (!lToolProperties.isnil()) {
            return argerror(4, "Expected a table or nil value for argument 'toolProperty'");
        }


        float miningspeed;
        LuaValue lMiningSpeed = varargs.arg(5);
        if (lMiningSpeed.isnumber()) {
            miningspeed = (float) lMiningSpeed.todouble();
        } else if (lMiningSpeed.isnil()) {
            miningspeed = 1.0f;
        } else {
            return argerror(5, "Expected an int or nil value for argument 'miningSpeed");
        }

        LuaFunction function = null;
        LuaValue lFunction = varargs.arg(6);
        if (lFunction.isfunction()) {
            function = lFunction.checkfunction();
        } else if (!lFunction.isnil()) {
            return argerror(6, " Expected a function or nil value for argument 'interactionFunction'");
        }

        new LuaItem(name, description, maxAmount, toolProperties, miningspeed, function).register();
        return varargsOf(valueOf(name.toString()), TRUE);
    }

    private static class LuaItem extends ItemBasic {
        private final String[] description;
        private final Map<ToolProperty, Integer> toolProperties;
        private final float miningSpeed;
        private final LuaFunction function;

        private LuaItem(ResourceName name, String[] description, int maxAmount, HashMap<ToolProperty, Integer> toolProperties, float miningspeed, LuaFunction function) {
            super(name);
            this.description = description;
            setMaxAmount(maxAmount);
            this.toolProperties = Collections.unmodifiableMap(toolProperties);
            this.miningSpeed = miningspeed;
            this.function = function;
        }

        @Override
        public void describeItem(IAssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced) {
            super.describeItem(manager, instance, desc, isAdvanced);
            Collections.addAll(desc, description);
        }

        @Override
        public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player, ItemInstance instance) {
            try {
                LuaValue lInstance = itemInstanceToLua(instance);
                Varargs returnVal = function.invoke(varargsOf(
                        new LuaValue[]{valueOf(x), valueOf(y), valueOf(layer.getName().toString()), valueOf(mouseX), valueOf(mouseY), valueOf(player.getUniqueId().toString()),
                                lInstance}));
                instance.setAmount(lInstance.get("amount").checkint());
                instance.setMeta(lInstance.get("meta").checkint());
                LuaValue set = lInstance.get("set");
                if (set.isnil()) instance.setAdditionalData(null);
                else instance.setAdditionalData((ModBasedDataSet) set.get("backingData").checkuserdata(ModBasedDataSet.class));
                if (returnVal.arg1().isboolean()) {
                    return returnVal.arg1().toboolean();
                }
            } catch (LuaError e) {
                RBCustomize.logger.log(Level.WARNING, "Execution of script assigned to item: " + getName() + " failed!", e);
            }
            return super.onInteractWith(world, x, y, layer, mouseX, mouseY, player, instance);
        }

        @Override
        public Map<ToolProperty, Integer> getToolProperties(ItemInstance instance) {
            return Collections.unmodifiableMap(toolProperties);
        }

        @Override
        public float getMiningSpeed(IWorld world, int x, int y, TileLayer layer, Tile tile, boolean isRightTool, ItemInstance instance) {
            return super.getMiningSpeed(world, x, y, layer, tile, isRightTool, instance) * miningSpeed;
        }
    }
}
