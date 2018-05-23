package alexanders.mods.rbcustomize.lua;

import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.container.ContainerSlot;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.NameRegistry;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

import static alexanders.mods.rbcustomize.Util.nilToNull;

public class ContainerLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable container = new LuaTable();
        container.set("add", new FunctionWrapper(this::add));
        container.set("remove", new FunctionWrapper(this::remove));
        container.set("instantiate", new FunctionWrapper(this::instantiate));
        container.set("instantiateSlot", new FunctionWrapper(this::instantiateSlot));
        container.set("addPlayerInventory", new FunctionWrapper(this::addPlayerInventory));
        container.set("addSlotGrid", new FunctionWrapper(this::addSlotGrid));
        container.set("addSlot", new FunctionWrapper(this::addSlot));
        container.set("slotGet", new FunctionWrapper(this::slotGet));
        container.set("slotSet", new FunctionWrapper(this::slotSet));
        env.set("container", container);
        return container;
    }

    private Varargs addPlayerInventory(Varargs varargs) { // container, player, x, y
        ItemContainer container = (ItemContainer) varargs.checkuserdata(1, ItemContainer.class);
        Entity e = EntityLib.parseUUID(varargs, 2);
        int x = varargs.checkint(3);
        int y = varargs.checkint(4);
        if (e instanceof AbstractEntityPlayer) {
            container.addPlayerInventory((AbstractEntityPlayer) e, x, y);
        } else {
            return argerror(2, "Expected a player's uuid");
        }
        return NIL;
    }

    private Varargs addSlotGrid(Varargs varargs) { // container, inventory, start, end, xStart, yStart, width
        ItemContainer container = (ItemContainer) varargs.checkuserdata(1, ItemContainer.class);
        IInventory inventory = (IInventory) varargs.checkuserdata(2, IInventory.class);
        int start = varargs.checkint(3);
        int end = varargs.checkint(4);
        int xStart = varargs.checkint(5);
        int yStart = varargs.checkint(6);
        int width = varargs.checkint(7);
        container.addSlotGrid(inventory, start, end, xStart, yStart, width);
        return NIL;
    }

    private Varargs instantiateSlot(Varargs varargs) { //inventory, slot, x, y
        IInventory inventory = (IInventory) varargs.checkuserdata(1, IInventory.class);
        int slot = varargs.checkint(2);
        int x = varargs.checkint(3);
        int y = varargs.checkint(4);
        return userdataOf(new ContainerSlot(inventory, slot, x, y));
    }

    private Varargs addSlot(Varargs varargs) { // container, slot
        ItemContainer container = (ItemContainer) varargs.checkuserdata(1, ItemContainer.class);
        ContainerSlot slot = (ContainerSlot) varargs.checkuserdata(2, ContainerSlot.class);
        container.addSlot(slot);
        return NIL;
    }

    private Varargs slotGet(Varargs varargs) { // slot
        ContainerSlot slot = (ContainerSlot) varargs.checkuserdata(1, ContainerSlot.class);
        return ItemsLib.itemInstanceToLua(slot.get());
    }

    private Varargs slotSet(Varargs varargs) { // slot, instance
        ContainerSlot slot = (ContainerSlot) varargs.checkuserdata(1, ContainerSlot.class);
        slot.set(ItemsLib.parseItemInstance(2, varargs.checktable(2)));
        return NIL;
    }

    private Varargs add(Varargs varargs) { // name, init, onOpened, onClosed
        String sName = varargs.checkjstring(1);
        if (!Util.isResourceName(sName)) return argerror(1, "Expected a resource name for argument 'name'");

        LuaValue init = nilToNull(varargs.arg(2));
        LuaValue onOpened = nilToNull(varargs.arg(3));
        LuaValue onClosed = nilToNull(varargs.arg(4));
        LuaContainer.CONTAINER_REGISTRY.register(new ResourceName(sName), new LuaContainerData(init, onOpened, onClosed));
        return NIL;
    }

    private Varargs remove(Varargs varargs) { // name
        String sName = varargs.checkjstring(1);
        if (!Util.isResourceName(sName)) return argerror(1, "Expected a resource name for argument 'name'");
        LuaContainer.CONTAINER_REGISTRY.unregister(new ResourceName(sName));
        return NIL;
    }

    private Varargs instantiate(Varargs varargs) { // name, player
        String sName = varargs.checkjstring(1);
        if (!Util.isResourceName(sName)) return argerror(1, "Expected a resource name for argument 'name'");
        ResourceName name = new ResourceName(sName);
        Entity e = EntityLib.parseUUID(varargs, 2);
        if (e instanceof AbstractEntityPlayer) {
            if (LuaContainer.CONTAINER_REGISTRY.get(name) == null) return error("No container with that name was found");
            return userdataOf(new LuaContainer((AbstractEntityPlayer) e, name));
        } else {
            return argerror(2, "Expected a player's uuid");
        }
    }

    private static final class LuaContainer extends ItemContainer {
        private static final NameRegistry<LuaContainerData> CONTAINER_REGISTRY = new NameRegistry<>("LuaContainerRegistry", true);

        private final ResourceName name;
        private final LuaContainerData data;

        private LuaContainer(AbstractEntityPlayer player, ResourceName name) {
            super(player);
            this.data = CONTAINER_REGISTRY.get(name);
            this.name = name;

            if (this.data.init != null) LuaEnvironment.executeScript(this.data.init, userdataOf(this), valueOf(player.getUniqueId().toString()));
        }

        @Override
        public void onOpened() {
            if (data.onOpened != null) LuaEnvironment.executeScript(data.onOpened, userdataOf(this), valueOf(player.getUniqueId().toString()));
        }

        @Override
        public void onClosed() {
            if (data.onClosed != null) LuaEnvironment.executeScript(data.onClosed, userdataOf(this), valueOf(player.getUniqueId().toString()));
        }

        @Override
        public ResourceName getName() {
            return name;
        }
    }

    private static final class LuaContainerData {
        private final LuaValue init;        // container, player
        private final LuaValue onOpened;    // container, player
        private final LuaValue onClosed;    // container, player

        private LuaContainerData(LuaValue init, LuaValue onOpened, LuaValue onClosed) {
            this.init = init;
            this.onOpened = onOpened;
            this.onClosed = onClosed;
        }
    }
}
