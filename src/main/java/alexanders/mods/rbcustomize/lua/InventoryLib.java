package alexanders.mods.rbcustomize.lua;

import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.inventory.Inventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

public class InventoryLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable inventory = new LuaTable();
        inventory.set("instantiate", new FunctionWrapper(this::instantiate));
        inventory.set("get", new FunctionWrapper(this::get));
        inventory.set("set", new FunctionWrapper(this::set));
        inventory.set("getSlotAmount", new FunctionWrapper(this::getSlotAmount));
        inventory.set("load", new FunctionWrapper(this::load));
        inventory.set("save", new FunctionWrapper(this::save));
        env.set("inventory", inventory);
        return inventory;
    }

    private Varargs instantiate(Varargs varargs) { // size
        int size = varargs.checkint(1);
        return userdataOf(new Inventory(size));
    }
    
    private Varargs load(Varargs varargs) { // inventory, set
        LuaValue lInv = varargs.arg(1);
        if (!lInv.isuserdata(Inventory.class)) return argerror(1, "Expected an Inventory value for argument 'inventory'");
        Inventory inventory = (Inventory) lInv.touserdata();
        LuaValue lDataSet = varargs.arg(2);
        if (!lDataSet.isuserdata(DataSet.class)) return argerror(2, "Expected a DataSet value for argument 'set'");
        inventory.load((DataSet) lDataSet.touserdata());
        return NIL;
    }

    private Varargs save(Varargs varargs) { // inventory, set
        LuaValue lInv = varargs.arg(1);
        if (!lInv.isuserdata(Inventory.class)) return argerror(1, "Expected an Inventory value for argument 'inventory'");
        Inventory inventory = (Inventory) lInv.touserdata();
        LuaValue lDataSet = varargs.arg(2);
        if (!lDataSet.isuserdata(DataSet.class)) return argerror(2, "Expected a DataSet value for argument 'set'");
        inventory.save((DataSet) lDataSet.touserdata());
        return NIL;
    }

    private Varargs set(Varargs varargs) { // inventory, idx, itemInstance
        LuaValue lInv = varargs.arg(1);
        if (!lInv.isuserdata(IInventory.class)) return argerror(1, "Expected an IInventory value for argument 'inventory'");
        IInventory inventory = (IInventory) lInv.touserdata();

        LuaValue lIdx = varargs.arg(2);
        if (!lIdx.isint()) return argerror(2, "Expected an int value for argument 'idx'");
        int idx = lIdx.toint();
        if (idx >= inventory.getSlotAmount()) return argerror(2, "Index is out of range for this inventory");

        ItemInstance instance;
        LuaValue lInstance = varargs.arg(3);
        if (lInstance.isnil()) {
            instance = null;
        } else if (lInstance.istable()) {
            instance = ItemsLib.parseItemInstance(3, lInstance);
        } else {
            return argerror(3, "Expected a table or nil value for argument 'itemInstance'");
        }
        inventory.set(idx, instance);
        return NIL;
    }

    private Varargs getSlotAmount(Varargs varargs) { // inventory -> slotAmount
        LuaValue lInv = varargs.arg(1);
        if (!lInv.isuserdata(IInventory.class)) return argerror(1, "Expected an IInventory value for argument 'inventory'");
        IInventory inventory = (IInventory) lInv.touserdata();

        return valueOf(inventory.getSlotAmount());
    }

    private Varargs get(Varargs varargs) { // inventory, idx -> itemInstance
        LuaValue lInv = varargs.arg(1);
        if (!lInv.isuserdata(IInventory.class)) return argerror(1, "Expected an IInventory value for argument 'inventory'");
        IInventory inventory = (IInventory) lInv.touserdata();

        LuaValue lIdx = varargs.arg(2);
        if (!lIdx.isint()) return argerror(2, "Expected an int value for argument 'idx'");
        int idx = lIdx.toint();
        if (idx >= inventory.getSlotAmount()) return argerror(2, "Index is out of range for this inventory");
        return ItemsLib.itemInstanceToLua(inventory.get(idx));
    }
}
