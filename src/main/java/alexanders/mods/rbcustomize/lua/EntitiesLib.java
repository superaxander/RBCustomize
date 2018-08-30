package alexanders.mods.rbcustomize.lua;

import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.UUID;

public class EntitiesLib extends TwoArgFunction {
    public static Entity parseUUID(Varargs varargs, int i) {
        Entity entity = null;
        LuaValue lUUID = varargs.arg(i);
        if (!lUUID.isstring()) {
            argerror(i, "Expected a string value for argument 'uuid'");
        }
        try {
            entity = WorldLib.world.getEntity(UUID.fromString(lUUID.tojstring()));
            if (entity == null) argerror(i, "Specified uuid was not found");
        } catch (IllegalArgumentException e) {
            argerror(i, "Specified uuid was not a valid UUID");
        }
        return entity;
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable entities = new LuaTable();
        entities.set("remove", new FunctionWrapper(this::remove));
        entities.set("getX", new FunctionWrapper(this::getX));
        entities.set("getY", new FunctionWrapper(this::getY));
        entities.set("getMotionX", new FunctionWrapper(this::getMotionX));
        entities.set("getMotionY", new FunctionWrapper(this::getMotionY));
        entities.set("setMotionX", new FunctionWrapper(this::setMotionX));
        entities.set("setMotionY", new FunctionWrapper(this::setMotionY));
        entities.set("isOnGround", new FunctionWrapper(this::isOnGround));
        entities.set("getSelectedSlot", new FunctionWrapper(this::getSelectedSlot));
        entities.set("getInv", new FunctionWrapper(this::getInv));
        entities.set("getFacing", new FunctionWrapper(this::getFacing));
        entities.set("openGui", new FunctionWrapper(this::openGui));
        entities.set("openGuiContainer", new FunctionWrapper(this::openGuiContainer));
        env.set("entities", entities);
        return entities;
    }

    private Varargs openGui(Varargs varargs) { // uuid, gui
        Entity entity = parseUUID(varargs, 1);
        Gui gui = (Gui) varargs.checkuserdata(2, Gui.class);

        if (entity instanceof AbstractEntityPlayer) {
            return valueOf(((AbstractEntityPlayer) entity).openGui(gui));
        } else {
            return argerror(1, "Specified uuid did not correspond to a player");
        }
    }

    private Varargs openContainer(Varargs varargs) { // uuid, container
        Entity entity = parseUUID(varargs, 1);
        ItemContainer container = (ItemContainer) varargs.checkuserdata(3, ItemContainer.class);

        if (entity instanceof AbstractEntityPlayer) {
            return valueOf(((AbstractEntityPlayer) entity).openContainer(container));
        } else {
            return argerror(1, "Specified uuid did not correspond to a player");
        }
    }

    private Varargs openGuiContainer(Varargs varargs) { // uuid, gui, container
        Entity entity = parseUUID(varargs, 1);
        GuiContainer gui = (GuiContainer) varargs.checkuserdata(2, GuiContainer.class);
        ItemContainer container = (ItemContainer) varargs.checkuserdata(3, ItemContainer.class);

        if (entity instanceof AbstractEntityPlayer) {
            return valueOf(((AbstractEntityPlayer) entity).openGuiContainer(gui, container));
        } else {
            return argerror(1, "Specified uuid did not correspond to a player");
        }
    }

    private Varargs remove(Varargs varargs) {
        String lName = varargs.checkjstring(1);
        if (!Util.isResourceName(lName)) return argerror(1, "Expected a ResourceName for argument 'entity'");
        Registries.ENTITY_REGISTRY.unregister(new ResourceName(lName));
        return NIL;
    }

    private Varargs getFacing(Varargs varargs) {
        Entity entity = parseUUID(varargs, 1);
        return LuaEnvironment.globals.get("Direction").get(entity.facing.name());
    }

    private Varargs isOnGround(Varargs varargs) { // uuid --> isOnGround
        Entity entity = parseUUID(varargs, 1);
        return valueOf(entity.onGround);
    }

    private Varargs getX(Varargs varargs) { // uuid --> x
        Entity entity = parseUUID(varargs, 1);
        return valueOf(entity.getX());
    }

    private Varargs getY(Varargs varargs) { // uuid --> y
        Entity entity = parseUUID(varargs, 1);
        return valueOf(entity.getY());
    }

    private Varargs getMotionX(Varargs varargs) { // uuid --> motionX
        Entity entity = parseUUID(varargs, 1);
        return valueOf(entity.motionX);
    }

    private Varargs getMotionY(Varargs varargs) { // uuid --> motionY
        Entity entity = parseUUID(varargs, 1);
        return valueOf(entity.motionY);
    }

    private Varargs setMotionX(Varargs varargs) { // uuid, motion --> NIL
        Entity entity = parseUUID(varargs, 1);

        LuaValue lMotion = varargs.arg(2);
        if (!lMotion.isnumber()) return argerror(2, "Expected a number value for argument 'motion'");
        entity.motionX = lMotion.todouble();
        return NIL;
    }

    private Varargs setMotionY(Varargs varargs) { // uuid, motion --> NIL
        Entity entity = parseUUID(varargs, 1);

        LuaValue lMotion = varargs.arg(2);
        if (!lMotion.isnumber()) return argerror(2, "Expected a number value for argument 'motion'");
        entity.motionY = lMotion.todouble();
        return NIL;
    }

    private Varargs getInv(Varargs varargs) { // uuid -> inv
        Entity entity = parseUUID(varargs, 1);

        if (entity instanceof AbstractEntityPlayer) {
            return userdataOf(((AbstractEntityPlayer) entity).getInv());
        } else {
            return argerror(1, "Specified uuid did not correspond to a player");
        }
    }

    private Varargs getSelectedSlot(Varargs varargs) { // uuid --> int
        if (WorldLib.world == null) return error("The world is not available right now");

        Entity entity = parseUUID(varargs, 1);

        if (entity instanceof AbstractEntityPlayer) {
            return valueOf(((AbstractEntityPlayer) entity).getSelectedSlot());
        } else {
            return argerror(1, "Specified uuid did not correspond to a player");
        }
    }

}
