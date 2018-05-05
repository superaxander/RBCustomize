package alexanders.mods.rbcustomize.lua;

import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.BoundBox;
import org.luaj.vm2.LuaValue;

public final class BoundBoxesLib {
    public static final LuaValue DEFAULT_BOUNDS = boundBoxToLua(Tile.DEFAULT_BOUNDS);

    public static LuaValue boundBoxToLua(BoundBox entityBox) {
        return LuaEnvironment.globals.get("BoundBox").invoke(LuaValue.varargsOf(
                new LuaValue[]{LuaValue.valueOf(entityBox.getMinX()), LuaValue.valueOf(entityBox.getMinY()), LuaValue.valueOf(entityBox.getMaxX()),
                        LuaValue.valueOf(entityBox.getMaxY())})).checkvalue(1);
    }

    public static BoundBox parseBoundBox(LuaValue lBB) {
        if (!lBB.istable()) return (BoundBox) (Object) LuaValue.error("Expected a table value for argument 'bb'"); // lol
        LuaValue lMinX = lBB.get("minX");
        LuaValue lMinY = lBB.get("minY");
        LuaValue lMaxX = lBB.get("maxX");
        LuaValue lMaxY = lBB.get("maxY");
        if (!(lMinX.isnumber() && lMinY.isnumber() && lMaxX.isnumber() && lMaxY.isnumber()))
            return (BoundBox) (Object) LuaValue.error("Invalid BoundBox minX, minY, maxX and maxY must all be numbers");
        return new BoundBox(lMinX.todouble(), lMinY.todouble(), lMaxX.todouble(), lMaxY.todouble());
    }
}
