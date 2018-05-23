package alexanders.mods.rbcustomize.lua;

import alexanders.mods.rbcustomize.RBCustomize;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.function.Supplier;
import java.util.logging.Level;

class LuaSupplier<T> implements Supplier<T> {
    private final LuaValue supplier;
    private final String type;

    LuaSupplier(LuaValue supplier, Class<T> clazz) {
        this.supplier = supplier;
        if (Boolean.class.isAssignableFrom(clazz)) {
            type = "Boolean";
        } else if (Integer.class.isAssignableFrom(clazz)) {
            type = "Integer";
        } else if (Long.class.isAssignableFrom(clazz)) {
            type = "Long";
        } else if (Double.class.isAssignableFrom(clazz)) {
            type = "Double";
        } else if (String.class.isAssignableFrom(clazz)) {
            type = "String";
        } else {
            throw new IllegalArgumentException("Inconvertable type");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        try {
            Varargs out = supplier.invoke();
            switch (type) {
                case "Boolean":
                    return (T) (Boolean) out.checkboolean(1);
                case "Integer":
                    return (T) (Integer) out.checkint(1);
                case "Long":
                    return (T) (Long) out.checklong(1);
                case "Double":
                    return (T) (Double) out.checkdouble(1);
                case "String":
                    return (T) out.checkjstring(1);
                default:
                    throw new IllegalStateException("This is impossible");
            }
        } catch (LuaError e) {
            RBCustomize.logger.log(Level.WARNING, "Execution of script failed!", e);
            return null;
        }
    }
}
