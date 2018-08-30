package alexanders.mods.rbcustomize.lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public class QuestingLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable questing = new LuaTable();
        
        env.set("questing", questing);
        return questing;
    }
}
