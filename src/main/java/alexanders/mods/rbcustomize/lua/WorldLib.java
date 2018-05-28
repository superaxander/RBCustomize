package alexanders.mods.rbcustomize.lua;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static alexanders.mods.rbcustomize.Util.toLuaStringList;

public class WorldLib extends TwoArgFunction {
    public static IWorld world = null;

    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable world = new LuaTable();
        world.set("destroyTile", new FunctionWrapper(this::destroyTile));
        world.set("getState", new FunctionWrapper(this::getState));
        world.set("setState", new FunctionWrapper(this::setState));
        world.set("getPlayers", new FunctionWrapper(this::getPlayers));
        world.set("getLayers", new FunctionWrapper(this::getLayers));
        env.set("world", world);
        return world;
    }

    private Varargs getLayers(Varargs varargs) {
        return toLuaStringList(TileLayer.getAllLayers().stream().map(TileLayer::toString).collect(Collectors.toList()));
    }

    private Varargs getPlayers(Varargs varargs) {
        if (world == null) return error("The world is not available right now");
        LuaTable table = new LuaTable();
        world.getAllPlayers().forEach(it -> table.add(valueOf(it.getUniqueId().toString())));
        return table;
    }

    private Varargs setState(Varargs varargs) { // x, y, layer, state -> NIL
        if (world == null) return error("The world is not available right now");

        LuaValue lX = varargs.arg(1);
        if (!lX.isint()) return argerror(1, "Expected an int value for argument 'y'");
        int x = lX.toint();

        LuaValue lY = varargs.arg(2);
        if (!lY.isint()) return argerror(2, "Expected an int value for argument 'y'");
        int y = lY.toint();

        TileLayer layer;
        LuaValue lLayer = varargs.arg(3);
        if (!lLayer.isstring()) return argerror(3, "Expected a string value for argument 'layer'");
        ResourceName name;
        try {
            name = new ResourceName(lLayer.tojstring());
        } catch (IllegalArgumentException e) {
            return argerror(3, "Specified layer was not a resource name");
        }
        Optional<TileLayer> optionalLayer = TileLayer.getAllLayers().stream().filter(it -> it.getName().equals(name)).findAny();
        if (!optionalLayer.isPresent()) return argerror(3, "Specified layer could not be found");
        layer = optionalLayer.get();

        TileState state;
        LuaValue lState = varargs.arg(4);
        if (!lState.isstring()) {
            return argerror(4, "Expected a string value for argument 'state'");
        }
        ResourceName stateName;
        try {
            stateName = new ResourceName(lState.tojstring());
        } catch (IllegalArgumentException e) {
            return argerror(4, "Specified state was not a resource name");
        }
        state = RockBottomAPI.TILE_STATE_REGISTRY.get(stateName);
        if (state == null) return argerror(4, "Specified state was not found");

        world.setState(layer, x, y, state);
        return NIL;
    }

    private Varargs getState(Varargs varargs) { // x, y, layer -> state
        if (world == null) return error("The world is not available right now");

        LuaValue lX = varargs.arg(1);
        if (!lX.isint()) return argerror(1, "Expected an int value for argument 'y'");
        int x = lX.toint();

        LuaValue lY = varargs.arg(2);
        if (!lY.isint()) return argerror(2, "Expected an int value for argument 'y'");
        int y = lY.toint();

        TileLayer layer;
        LuaValue lLayer = varargs.arg(3);
        if (!lLayer.isstring()) return argerror(3, "Expected a string value for argument 'layer'");
        ResourceName name;
        try {
            name = new ResourceName(lLayer.tojstring());
        } catch (IllegalArgumentException e) {
            return argerror(3, "Specified layer was not a resource name");
        }
        Optional<TileLayer> optionalLayer = TileLayer.getAllLayers().stream().filter(it -> it.getName().equals(name)).findAny();
        if (!optionalLayer.isPresent()) return argerror(3, "Specified layer could not be found");
        layer = optionalLayer.get();

        return valueOf(world.getState(layer, x, y).getName().toString());
    }

    private Varargs destroyTile(Varargs varargs) { // x, y, layer, destroyer, shouldDrop --> NIL
        if (world == null) return error("The world is not available right now");

        LuaValue lX = varargs.arg(1);
        if (!lX.isint()) return argerror(1, "Expected an int value for argument 'y'");
        int x = lX.toint();

        LuaValue lY = varargs.arg(2);
        if (!lY.isint()) return argerror(2, "Expected an int value for argument 'y'");
        int y = lY.toint();

        TileLayer layer;
        LuaValue lLayer = varargs.arg(3);
        if (!lLayer.isstring()) return argerror(3, "Expected a string value for argument 'layer'");
        ResourceName name;
        try {
            name = new ResourceName(lLayer.tojstring());
        } catch (IllegalArgumentException e) {
            return argerror(3, "Specified layer was not a resource name");
        }
        Optional<TileLayer> optionalLayer = TileLayer.getAllLayers().stream().filter(it -> it.getName().equals(name)).findAny();
        if (!optionalLayer.isPresent()) return argerror(3, "Specified layer could not be found");
        layer = optionalLayer.get();

        Entity destroyer;
        LuaValue lDestroyer = varargs.arg(4);
        if (lDestroyer.isstring()) {
            try {
                destroyer = world.getEntity(UUID.fromString(lDestroyer.tojstring()));
                if (destroyer == null) return argerror(4, "Specified destroyer was not found");
            } catch (IllegalArgumentException e) {
                return argerror(4, "Specified destroyer was not a valid UUID");
            }
        } else if (lDestroyer.isnil()) {
            destroyer = null;
        } else {
            return argerror(4, "Expected a string value for argument 'destroyer'");
        }

        boolean shouldDrop;
        LuaValue lShouldDrop = varargs.arg(5);
        if (lShouldDrop.isboolean()) {
            shouldDrop = lShouldDrop.toboolean();
        } else if (lShouldDrop.isnil()) {
            shouldDrop = false;
        } else {
            return argerror(5, "Expected a boolean value for argument 'shouldDrop'");
        }

        world.destroyTile(x, y, layer, destroyer, shouldDrop);
        return NIL;
    }
}
