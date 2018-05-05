package alexanders.mods.rbcustomize.lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

public class GuiLib extends TwoArgFunction {

    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable gui = new LuaTable();
        gui.set("add", new FunctionWrapper(this::add));
        return gui;
    }
    
    private Varargs add(Varargs varargs) {
        return NIL;
    }
}
