package alexanders.mods.rbcustomize.lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UtilsLib extends TwoArgFunction {

    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable utils = new LuaTable();
        utils.set("getEnumValues", new FunctionWrapper(this::getEnumValues));
        env.set("utils", utils);
        return utils;
    }


    private Varargs getEnumValues(Varargs varargs) {
        String enumName = varargs.checkjstring(1);
        try {
            Class<?> clazz = Class.forName(enumName);
            if (!clazz.isEnum()) argerror(1, "The found class was not an enum");
            Object[] constants = clazz.getEnumConstants();
            Method method = clazz.getMethod("name");
            LuaValue[] values = new LuaValue[constants.length];
            for (int i = 0; i < constants.length; i++) {
                values[i] = valueOf((String) method.invoke(constants[i]));
            }
            return listOf(values);
        } catch (ClassNotFoundException e) {
            argerror(1, "No enum with that name was found");
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("An enum without a name method does not exist");
        } catch (IllegalAccessException | InvocationTargetException e) {
            argerror(1, "There was a problem invoking the enum's name method");
        }
        return NIL;
    }
}
