package alexanders.mods.rbcustomize.lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

public class TilesLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable tiles = new LuaTable();
        tiles.set("add", new FunctionWrapper(this::addTile));
        return null;
    }

    private Varargs addTile(Varargs varargs) { // name, 
        
        return null;
    }
}
