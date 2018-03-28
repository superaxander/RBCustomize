package alexanders.mods.rbcustomize.lua;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.function.Function;

public class FunctionWrapper extends VarArgFunction {
    private final Function<Varargs, Varargs> function;

    public FunctionWrapper(Function<Varargs, Varargs> function) {
        this.function = function;
    }

    @Override
    public Varargs invoke(Varargs args) {
        try {
            return function.apply(args);
        } catch (LuaError e) {
            throw e;
        } catch (Exception e) {
            throw new LuaError(e);
        }
    }
}
