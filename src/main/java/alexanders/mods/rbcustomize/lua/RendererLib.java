package alexanders.mods.rbcustomize.lua;

import de.ellpeck.rockbottom.api.IRenderer;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

public class RendererLib extends TwoArgFunction {
    static IRenderer renderer;

    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable renderer = new LuaTable();
        renderer.set("mirror", new FunctionWrapper(this::mirror));
        env.set("renderer", renderer);
        return renderer;
    }

    private Varargs mirror(Varargs varargs) {
        if (renderer == null) error("The Renderer is not available");
        renderer.mirror(varargs.checkboolean(1), varargs.checkboolean(2));
        return NIL;
    }
}
