package alexanders.mods.rbcustomize.lua;

import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StatesLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable states = new LuaTable();
        states.set("getEnumValue", new FunctionWrapper(this::getEnumValue));
        states.set("setEnumValue", new FunctionWrapper(this::setEnumValue));
        states.set("getIntValue", new FunctionWrapper(this::getIntValue));
        states.set("setIntValue", new FunctionWrapper(this::setIntValue));
        states.set("getStringValue", new FunctionWrapper(this::getStringValue));
        states.set("setStringValue", new FunctionWrapper(this::setStringValue));
        states.set("getBooleanValue", new FunctionWrapper(this::getBooleanValue));
        states.set("setBooleanValue", new FunctionWrapper(this::setBooleanValue));
        states.set("getSpecificIntValue", new FunctionWrapper(this::getSpecificIntValue));
        states.set("setSpecificIntValue", new FunctionWrapper(this::setSpecificIntValue));
        env.set("states", states);
        return states;
    }

    private Varargs getEnumValue(Varargs varargs) {
        String sState = varargs.checkjstring(1);
        if (!Util.isResourceName(sState)) argerror(1, "Specified state was not a resource name");
        TileState state = Registries.TILE_STATE_REGISTRY.get(new ResourceName(sState));
        if (state == null) argerror(1, "Specified state does not exist");
        String prop = varargs.checkjstring(2);
        return valueOf(callGet(Enum.class, state, prop).name());
    }

    private Varargs setEnumValue(Varargs varargs) {
        String sState = varargs.checkjstring(1);
        if (!Util.isResourceName(sState)) argerror(1, "Specified state was not a resource name");
        TileState state = Registries.TILE_STATE_REGISTRY.get(new ResourceName(sState));
        if (state == null) argerror(1, "Specified state does not exist");
        String prop = varargs.checkjstring(2);
        String sEnum = varargs.checkjstring(3);
        String enumName = varargs.checkjstring(4);
        try {
            Class<?> clazz = Class.forName(enumName);
            if (!clazz.isEnum()) argerror(1, "The found class was not an enum");
            Object[] constants = clazz.getEnumConstants();
            Method method = clazz.getMethod("name");
            Enum<?> value = null;
            for (Object constant : constants) {
                if (method.invoke(constant).equals(sEnum)) {
                    value = (Enum<?>) constant;
                    break;
                }
            }
            if (value == null) argerror(4, "That value was not found in this enum");
            return valueOf(callSet(state, prop, value));
        } catch (ClassNotFoundException e) {
            argerror(4, "No enum with that name was found");
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("An enum without a name method does not exist");
        } catch (IllegalAccessException | InvocationTargetException e) {
            argerror(4, "There was a problem invoking the enum's name method");
        }
        return NIL;
    }

    private Varargs getIntValue(Varargs varargs) {
        String sState = varargs.checkjstring(1);
        if (!Util.isResourceName(sState)) argerror(1, "Specified state was not a resource name");
        TileState state = Registries.TILE_STATE_REGISTRY.get(new ResourceName(sState));
        if (state == null) argerror(1, "Specified state does not exist");
        String prop = varargs.checkjstring(2);
        return valueOf(callGet(int.class, state, prop));
    }

    private Varargs setIntValue(Varargs varargs) {
        String sState = varargs.checkjstring(1);
        if (!Util.isResourceName(sState)) argerror(1, "Specified state was not a resource name");
        TileState state = Registries.TILE_STATE_REGISTRY.get(new ResourceName(sState));
        if (state == null) argerror(1, "Specified state does not exist");
        String prop = varargs.checkjstring(2);
        int value = varargs.checkint(3);
        return valueOf(callSet(state, prop, value));
    }

    private Varargs getStringValue(Varargs varargs) {
        String sState = varargs.checkjstring(1);
        if (!Util.isResourceName(sState)) argerror(1, "Specified state was not a resource name");
        TileState state = Registries.TILE_STATE_REGISTRY.get(new ResourceName(sState));
        if (state == null) argerror(1, "Specified state does not exist");
        String prop = varargs.checkjstring(2);
        return valueOf(callGet(String.class, state, prop));
    }

    private Varargs setStringValue(Varargs varargs) {
        String sState = varargs.checkjstring(1);
        if (!Util.isResourceName(sState)) argerror(1, "Specified state was not a resource name");
        TileState state = Registries.TILE_STATE_REGISTRY.get(new ResourceName(sState));
        if (state == null) argerror(1, "Specified state does not exist");
        String prop = varargs.checkjstring(2);
        String value = varargs.checkjstring(3);
        return valueOf(callSet(state, prop, value));
    }

    private Varargs getBooleanValue(Varargs varargs) {
        String sState = varargs.checkjstring(1);
        if (!Util.isResourceName(sState)) argerror(1, "Specified state was not a resource name");
        TileState state = Registries.TILE_STATE_REGISTRY.get(new ResourceName(sState));
        if (state == null) argerror(1, "Specified state does not exist");
        String prop = varargs.checkjstring(2);
        return valueOf(callGet(boolean.class, state, prop));
    }

    private Varargs setBooleanValue(Varargs varargs) {
        String sState = varargs.checkjstring(1);
        if (!Util.isResourceName(sState)) argerror(1, "Specified state was not a resource name");
        TileState state = Registries.TILE_STATE_REGISTRY.get(new ResourceName(sState));
        if (state == null) argerror(1, "Specified state does not exist");
        String prop = varargs.checkjstring(2);
        boolean value = varargs.checkboolean(3);
        return valueOf(callSet(state, prop, value));
    }

    private Varargs getSpecificIntValue(Varargs varargs) {
        String sState = varargs.checkjstring(1);
        if (!Util.isResourceName(sState)) argerror(1, "Specified state was not a resource name");
        TileState state = Registries.TILE_STATE_REGISTRY.get(new ResourceName(sState));
        if (state == null) argerror(1, "Specified state does not exist");
        String prop = varargs.checkjstring(2);
        return valueOf(callGet(int.class, state, prop));
    }

    private Varargs setSpecificIntValue(Varargs varargs) {
        String sState = varargs.checkjstring(1);
        if (!Util.isResourceName(sState)) argerror(1, "Specified state was not a resource name");
        TileState state = Registries.TILE_STATE_REGISTRY.get(new ResourceName(sState));
        if (state == null) argerror(1, "Specified state does not exist");
        String prop = varargs.checkjstring(2);
        int value = varargs.checkint(3);
        return valueOf(callSet(state, prop, value));
    }

    private <T extends Comparable> String callSet(TileState state, String prop, T value) {
        return state.prop(prop, value).getName().toString();
    }

    private <T extends Comparable> T callGet(Class<T> clazz, TileState state, String prop) {
        return state.get(prop);
    }
}
