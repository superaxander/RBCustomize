package alexanders.mods.rbcustomize.lua;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JseMathLib;

public class RBJseMathLib extends JseMathLib { //TODO: Probably migrate this to Lua
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue math = super.call(modname, env);
        math.set("min", new min());
        math.set("max", new max());
        return math;
    }

    private static class min extends BinaryOp {
        @Override
        protected double call(double x, double y) {
            return Math.min(x, y);
        }
    }

    private static class max extends BinaryOp {
        @Override
        protected double call(double x, double y) {
            return Math.max(x, y);
        }
    }
}
