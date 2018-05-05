package alexanders.mods.rbcustomize.lua;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.UUID;

public class EntityLib extends TwoArgFunction {
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
        LuaTable entity = new LuaTable();
        entity.set("remove", new FunctionWrapper(this::remove));
        entity.set("getMotionX", new FunctionWrapper(this::getMotionX));
        entity.set("getMotionY", new FunctionWrapper(this::getMotionY));
        entity.set("setMotionX", new FunctionWrapper(this::setMotionX));
        entity.set("setMotionY", new FunctionWrapper(this::setMotionY));
        entity.set("isOnGround", new FunctionWrapper(this::isOnGround));
        entity.set("getSelectedSlot", new FunctionWrapper(this::getSelectedSlot));
        entity.set("getInv", new FunctionWrapper(this::getInv));
        entity.set("getFacing", new FunctionWrapper(this::getFacing));
        env.set("entity", entity);
        return entity;
    }

    private Varargs remove(Varargs varargs) {
        String lName = varargs.checkjstring(1);
        if(!Util.isResourceName(lName)) return argerror(1, "Expected a ResourceName for argument 'entity'");
        RockBottomAPI.ENTITY_REGISTRY.unregister(new ResourceName(lName));
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
            return LuaValue.userdataOf(((AbstractEntityPlayer) entity).getInv());
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
