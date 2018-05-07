package alexanders.mods.rbcustomize;

import org.luaj.vm2.LuaValue;

public class Util {
    public static LuaValue nilToNull(LuaValue val) {
        return val.isnil() ? null : val;
    }
}
