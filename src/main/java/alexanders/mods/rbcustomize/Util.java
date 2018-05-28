package alexanders.mods.rbcustomize;

import de.ellpeck.rockbottom.api.util.Colors;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public final class Util {
    public static LuaValue nilToNull(LuaValue val) {
        return val.isnil() ? null : val;
    }

    public static <T> LuaValue toLuaStringList(Collection<T> collection) {
        LuaTable table = new LuaTable();
        int i = 0;
        for (T t : collection) table.set(++i, LuaValue.valueOf(t.toString()));
        return table;
    }

    public static <T> LuaValue toLuaStringList(T[] array) {
        return toLuaStringList(Arrays.asList(array));
    }

    public static LuaValue toLuaByteList(Collection<Byte> collection) {
        LuaTable table = new LuaTable();
        for (Byte t : collection) table.add(LuaValue.valueOf(t));
        return table;
    }

    public static LuaValue toLuaByteList(Byte[] array) {
        return toLuaByteList(Arrays.asList(array));
    }

    public static LuaValue toLuaCharacterList(Collection<Character> collection) {
        LuaTable table = new LuaTable();
        int i = 0;
        for (Character t : collection) table.set(i, LuaValue.valueOf(t));
        return table;
    }

    public static LuaValue toLuaCharacterList(Character[] array) {
        return toLuaCharacterList(Arrays.asList(array));
    }

    public static LuaValue toLuaShortList(Collection<Short> collection) {
        LuaTable table = new LuaTable();
        int i = 0;
        for (Short t : collection) table.set(++i, LuaValue.valueOf(t));
        return table;
    }

    public static LuaValue toLuaShortList(Short[] array) {
        return toLuaShortList(Arrays.asList(array));
    }

    public static LuaValue toLuaIntegerList(Collection<Integer> collection) {
        LuaTable table = new LuaTable();
        int i = 0;
        for (Integer t : collection) table.set(++i,LuaValue.valueOf(t));
        return table;
    }

    public static LuaValue toLuaIntegerList(Integer[] array) {
        return toLuaIntegerList(Arrays.asList(array));
    }

    public static LuaValue toLuaLongList(Collection<Long> collection) {
        LuaTable table = new LuaTable();
        int i = 0;
        for (Long t : collection) table.set(++i,LuaValue.valueOf(t));
        return table;
    }

    public static LuaValue toLuaLongList(Long[] array) {
        return toLuaLongList(Arrays.asList(array));
    }

    public static LuaValue toLuaFloatList(Collection<Float> collection) {
        LuaTable table = new LuaTable();
        int i = 0;
        for (Float t : collection) table.set(++i,LuaValue.valueOf(t));
        return table;
    }

    public static LuaValue toLuaFloatList(Float[] array) {
        return toLuaFloatList(Arrays.asList(array));
    }

    public static LuaValue toLuaDoubleList(Collection<Double> collection) {
        LuaTable table = new LuaTable();
        int i = 0;
        for (Double t : collection) table.set(++i,LuaValue.valueOf(t));
        return table;
    }

    public static LuaValue toLuaDoubleList(Double[] array) {
        return toLuaDoubleList(Arrays.asList(array));
    }

    @SuppressWarnings("unchecked")
    public static <T> LuaValue toLuaPrimitiveList(Class<T> clazz, Collection<T> collection, Function<T, LuaValue> preTransform, Function<LuaValue, LuaValue> postTransform) {
        if (preTransform == null) {
            LuaValue out;
            if (clazz.isAssignableFrom(Byte.class)) {
                out = toLuaByteList((Collection<Byte>) collection);
            } else if (clazz.isAssignableFrom(Character.class)) {
                out = toLuaCharacterList((Collection<Character>) collection);
            } else if (clazz.isAssignableFrom(Short.class)) {
                out = toLuaShortList((Collection<Short>) collection);
            } else if (clazz.isAssignableFrom(Integer.class)) {
                out = toLuaIntegerList((Collection<Integer>) collection);
            } else if (clazz.isAssignableFrom(Long.class)) {
                out = toLuaLongList((Collection<Long>) collection);
            } else if (clazz.isAssignableFrom(Float.class)) {
                out = toLuaFloatList((Collection<Float>) collection);
            } else if (clazz.isAssignableFrom(Double.class)) {
                out = toLuaDoubleList((Collection<Double>) collection);
            } else {
                throw new IllegalArgumentException("Unsupported primitive");
            }
            if (postTransform == null) {
                return out;
            } else {
                return postTransform.apply(out);
            }
        } else {
            LuaTable table = new LuaTable();
            int i = 0;
            for (T t : collection) table.set(++i,preTransform.apply(t));
            return table;
        }
    }

    public static <T> LuaValue toLuaPrimitiveList(Class<T> clazz, T[] array, Function<T, LuaValue> preTransform, Function<LuaValue, LuaValue> postTransform) {
        return toLuaPrimitiveList(clazz, Arrays.asList(array), preTransform, postTransform);
    }

    public static List<String> luaToStringList(LuaTable table) {
        ArrayList<String> out = new ArrayList<>(table.length());
        for (int i = 1; i <= table.length(); i++) {
            out.add(table.get(i).tojstring());
        }
        return out;
    }

    public static String getColorName(int color) {
        switch (color) {
            case Colors.NO_COLOR:
                return "NO_COLOR";
            case Colors.RESET_COLOR:
                return "RESET_COLOR";
            case Colors.TRANSPARENT:
                return "TRANSPARENT";
            case Colors.WHITE:
                return "WHITE";
            case Colors.BLACK:
                return "BLACK";
            case Colors.DARK_GRAY:
                return "DARK_GRAY";
            case Colors.GRAY:
                return "GRAY";
            case Colors.LIGHT_GRAY:
                return "LIGHT_GRAY";
            case Colors.YELLOW:
                return "YELLOW";
            case Colors.ORANGE:
                return "ORANGE";
            case Colors.RED:
                return "RED";
            case Colors.PINK:
                return "PINK";
            case Colors.MAGENTA:
                return "MAGENTA";
            case Colors.GREEN:
                return "GREEN";
            default:
                throw new IllegalArgumentException("Unrecognized color");
        }
    }
}
