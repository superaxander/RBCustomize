package alexanders.mods.rbcustomize.lua;

import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.UUID;

public class EntityLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable entity = new LuaTable();
        entity.set("getMotionX", new FunctionWrapper(this::getMotionX));
        entity.set("getMotionY", new FunctionWrapper(this::getMotionY));
        entity.set("setMotionX", new FunctionWrapper(this::setMotionX));
        entity.set("setMotionY", new FunctionWrapper(this::setMotionY));
        entity.set("isOnGround", new FunctionWrapper(this::isOnGround));
        entity.set("getSelectedSlot", new FunctionWrapper(this::getSelectedSlot));
        entity.set("getInv", new FunctionWrapper(this::getInv));
        env.set("entity", entity);
        return entity;
    }

    private Varargs isOnGround(Varargs varargs) { // uuid --> isOnGround
        Entity entity;
        LuaValue lUUID = varargs.arg(1);
        if (!lUUID.isstring()) {
            return argerror(1, "Expected a string value for argument 'uuid'");
        }
        try {
            entity = WorldLib.world.getEntity(UUID.fromString(lUUID.tojstring()));
            if (entity == null)
                return argerror(1, "Specified uuid was not found");
        } catch (IllegalArgumentException e) {
            return argerror(1, "Specified uuid was not a valid UUID");
        }
        return valueOf(entity.onGround);
    }

    private Varargs getMotionX(Varargs varargs) { // uuid --> motionX
        Entity entity;
        LuaValue lUUID = varargs.arg(1);
        if (!lUUID.isstring()) {
            return argerror(1, "Expected a string value for argument 'uuid'");
        }
        try {
            entity = WorldLib.world.getEntity(UUID.fromString(lUUID.tojstring()));
            if (entity == null)
                return argerror(1, "Specified uuid was not found");
        } catch (IllegalArgumentException e) {
            return argerror(1, "Specified uuid was not a valid UUID");
        }
        return valueOf(entity.motionX);
    }

    private Varargs getMotionY(Varargs varargs) { // uuid --> motionY
        Entity entity;
        LuaValue lUUID = varargs.arg(1);
        if (!lUUID.isstring()) {
            return argerror(1, "Expected a string value for argument 'uuid'");
        }
        try {
            entity = WorldLib.world.getEntity(UUID.fromString(lUUID.tojstring()));
            if (entity == null)
                return argerror(1, "Specified uuid was not found");
        } catch (IllegalArgumentException e) {
            return argerror(1, "Specified uuid was not a valid UUID");
        }
        return valueOf(entity.motionY);
    }

    private Varargs setMotionX(Varargs varargs) { // uuid, motion --> NIL
        Entity entity;
        LuaValue lUUID = varargs.arg(1);
        if (!lUUID.isstring()) {
            return argerror(1, "Expected a string value for argument 'uuid'");
        }
        try {
            entity = WorldLib.world.getEntity(UUID.fromString(lUUID.tojstring()));
            if (entity == null)
                return argerror(1, "Specified uuid was not found");
        } catch (IllegalArgumentException e) {
            return argerror(1, "Specified uuid was not a valid UUID");
        }

        LuaValue lMotion = varargs.arg(2);
        if (!lMotion.isnumber())
            return argerror(2, "Expected a number value for argument 'motion'");
        entity.motionX = lMotion.todouble();
        return NIL;
    }

    private Varargs setMotionY(Varargs varargs) { // uuid, motion --> NIL
        Entity entity;
        LuaValue lUUID = varargs.arg(1);
        if (!lUUID.isstring()) {
            return argerror(1, "Expected a string value for argument 'uuid'");
        }
        try {
            entity = WorldLib.world.getEntity(UUID.fromString(lUUID.tojstring()));
            if (entity == null)
                return argerror(1, "Specified uuid was not found");
        } catch (IllegalArgumentException e) {
            return argerror(1, "Specified uuid was not a valid UUID");
        }

        LuaValue lMotion = varargs.arg(2);
        if (!lMotion.isnumber())
            return argerror(2, "Expected a number value for argument 'motion'");
        entity.motionY = lMotion.todouble();
        return NIL;
    }

    private Varargs getInv(Varargs varargs) { // uuid -> inv
        Entity entity;
        LuaValue lUUID = varargs.arg(1);
        if (!lUUID.isstring()) {
            return argerror(1, "Expected a string value for argument 'uuid'");
        }
        try {
            entity = WorldLib.world.getEntity(UUID.fromString(lUUID.tojstring()));
            if (entity == null)
                return argerror(1, "Specified uuid was not found");
        } catch (IllegalArgumentException e) {
            return argerror(1, "Specified uuid was not a valid UUID");
        }

        if (entity instanceof AbstractEntityPlayer) {
            return LuaValue.userdataOf(((AbstractEntityPlayer) entity).getInv());
        } else {
            return argerror(1, "Specified uuid did not correspond to a player");
        }
    }

    private Varargs getSelectedSlot(Varargs varargs) { // uuid --> int
        if (WorldLib.world == null)
            return error("The world is not available right now");

        Entity entity;
        LuaValue lUUID = varargs.arg(1);
        if (!lUUID.isstring()) {
            return argerror(1, "Expected a string value for argument 'uuid'");
        }
        try {
            entity = WorldLib.world.getEntity(UUID.fromString(lUUID.tojstring()));
            if (entity == null)
                return argerror(1, "Specified uuid was not found");
        } catch (IllegalArgumentException e) {
            return argerror(1, "Specified uuid was not a valid UUID");
        }

        if (entity instanceof AbstractEntityPlayer) {
            return valueOf(((AbstractEntityPlayer) entity).getSelectedSlot());
        } else {
            return argerror(1, "Specified uuid did not correspond to a player");
        }
    }
}
