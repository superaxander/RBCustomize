package alexanders.mods.rbcustomize.lua;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.MovableWorldObject;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.tile.TileLiquid;
import de.ellpeck.rockbottom.api.tile.state.*;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TilesLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable tiles = new LuaTable();
        tiles.set("add", new FunctionWrapper(this::addTile));
        tiles.set("remove", new FunctionWrapper(this::remove));
        tiles.set("canBreak", new FunctionWrapper(this::canBreak));
        tiles.set("canPlace", new FunctionWrapper(this::canPlace));
        tiles.set("isFullTile", new FunctionWrapper(this::isFullTile));
        tiles.set("isLiquid", new FunctionWrapper(this::isLiquid));
        tiles.set("getHardness", new FunctionWrapper(this::getHardness));
        tiles.set("doPlace", new FunctionWrapper(this::doPlace));
        tiles.set("doBreak", new FunctionWrapper(this::doBreak));
        tiles.set("getDefaultState", new FunctionWrapper(this::getDefaultState));
        env.set("tiles", tiles);
        return tiles;
    }

    private Varargs remove(Varargs varargs) {
        String lName = varargs.checkjstring(1);
        if (!Util.isResourceName(lName)) return argerror(1, "Expected a ResourceName for argument 'tile'");
        ResourceName name = new ResourceName(lName);
        Tile tile = RockBottomAPI.TILE_REGISTRY.get(name);
        RockBottomAPI.TILE_STATE_REGISTRY.keySet().stream().filter(it -> it.toString().startsWith(lName) && it.equals(name) || it.toString().charAt(lName.length()) == '@')
                .forEach(RockBottomAPI.TILE_STATE_REGISTRY::unregister);
        RockBottomAPI.TILE_REGISTRY.unregister(name);
        return NIL;
    }

    private Varargs getDefaultState(Varargs varargs) {
        LuaValue lTile = varargs.arg(1);
        if (!lTile.isstring()) return argerror(1, "Expected a string value for argument 'tile'");
        String sTile = lTile.tojstring();
        if (!Util.isResourceName(sTile)) return argerror(1, "Specified tile was not a resource name");
        Tile tile = RockBottomAPI.TILE_REGISTRY.get(new ResourceName(sTile));
        if (tile == null) return argerror(1, "Specified tile was not found");

        return valueOf(tile.getDefState().getName().toString());
    }

    private Varargs canBreak(Varargs varargs) {
        if (WorldLib.world == null) return error("The world is not available right now");
        LuaValue lTile = varargs.arg(1);
        if (!lTile.isstring()) return argerror(1, "Expected a string value for argument 'tile'");
        String sTile = lTile.tojstring();
        if (!Util.isResourceName(sTile)) return argerror(1, "Specified tile was not a resource name");
        Tile tile = RockBottomAPI.TILE_REGISTRY.get(new ResourceName(sTile));
        if (tile == null) return argerror(1, "Specified tile was not found");

        LuaValue lX = varargs.arg(2);
        if (!lX.isint()) return argerror(2, "Expected an int value for argument 'y'");
        int x = lX.toint();

        LuaValue lY = varargs.arg(3);
        if (!lY.isint()) return argerror(3, "Expected an int value for argument 'y'");
        int y = lY.toint();

        TileLayer layer;
        LuaValue lLayer = varargs.arg(4);
        if (!lLayer.isstring()) return argerror(4, "Expected a string value for argument 'layer'");
        ResourceName name;
        try {
            name = new ResourceName(lLayer.tojstring());
        } catch (IllegalArgumentException e) {
            return argerror(4, "Specified layer was not a resource name");
        }
        Optional<TileLayer> optionalLayer = TileLayer.getAllLayers().stream().filter(it -> it.getName().equals(name)).findAny();
        if (!optionalLayer.isPresent()) return argerror(4, "Specified layer could not be found");
        layer = optionalLayer.get();

        Entity entity;
        if (varargs.isnil(5)) entity = null;
        else entity = EntityLib.parseUUID(varargs, 5);
        if (entity == null || entity instanceof AbstractEntityPlayer) {
            return valueOf(tile.canPlace(WorldLib.world, x, y, layer, (AbstractEntityPlayer) entity));
        } else {
            return argerror(1, "Specified uuid did not correspond to a player");
        }
    }

    private Varargs canPlace(Varargs varargs) {
        if (WorldLib.world == null) return error("The world is not available right now");
        LuaValue lTile = varargs.arg(1);
        if (!lTile.isstring()) return argerror(1, "Expected a string value for argument 'tile'");
        String sTile = lTile.tojstring();
        if (!Util.isResourceName(sTile)) return argerror(1, "Specified tile was not a resource name");
        Tile tile = RockBottomAPI.TILE_REGISTRY.get(new ResourceName(sTile));
        if (tile == null) return argerror(1, "Specified tile was not found");

        LuaValue lX = varargs.arg(2);
        if (!lX.isint()) return argerror(2, "Expected an int value for argument 'y'");
        int x = lX.toint();

        LuaValue lY = varargs.arg(3);
        if (!lY.isint()) return argerror(3, "Expected an int value for argument 'y'");
        int y = lY.toint();

        TileLayer layer;
        LuaValue lLayer = varargs.arg(4);
        if (!lLayer.isstring()) return argerror(4, "Expected a string value for argument 'layer'");
        ResourceName name;
        try {
            name = new ResourceName(lLayer.tojstring());
        } catch (IllegalArgumentException e) {
            return argerror(4, "Specified layer was not a resource name");
        }
        Optional<TileLayer> optionalLayer = TileLayer.getAllLayers().stream().filter(it -> it.getName().equals(name)).findAny();
        if (!optionalLayer.isPresent()) return argerror(4, "Specified layer could not be found");
        layer = optionalLayer.get();

        boolean isRightTool = !varargs.isnil(6) && varargs.checkboolean(6);

        Entity entity;
        if (varargs.isnil(5)) entity = null;
        else entity = EntityLib.parseUUID(varargs, 5);
        if (entity == null || entity instanceof AbstractEntityPlayer) {
            return valueOf(tile.canBreak(WorldLib.world, x, y, layer, (AbstractEntityPlayer) entity, isRightTool));
        } else {
            return argerror(1, "Specified uuid did not correspond to a player");
        }
    }

    private Varargs isFullTile(Varargs varargs) {
        LuaValue lTile = varargs.arg(1);
        if (!lTile.isstring()) return argerror(1, "Expected a string value for argument 'tile'");
        String sTile = lTile.tojstring();
        if (!Util.isResourceName(sTile)) return argerror(1, "Specified tile was not a resource name");
        Tile tile = RockBottomAPI.TILE_REGISTRY.get(new ResourceName(sTile));
        if (tile == null) return argerror(1, "Specified tile was not found");

        return valueOf(tile.isFullTile());
    }

    private Varargs isLiquid(Varargs varargs) {
        LuaValue lTile = varargs.arg(1);
        if (!lTile.isstring()) return argerror(1, "Expected a string value for argument 'tile'");
        String sTile = lTile.tojstring();
        if (!Util.isResourceName(sTile)) return argerror(1, "Specified tile was not a resource name");
        Tile tile = RockBottomAPI.TILE_REGISTRY.get(new ResourceName(sTile));
        if (tile == null) return argerror(1, "Specified tile was not found");

        return valueOf(tile.isLiquid());
    }

    private Varargs getHardness(Varargs varargs) {
        if (WorldLib.world == null) return error("The world is not available right now");
        LuaValue lTile = varargs.arg(1);
        if (!lTile.isstring()) return argerror(1, "Expected a string value for argument 'tile'");
        String sTile = lTile.tojstring();
        if (!Util.isResourceName(sTile)) return argerror(1, "Specified tile was not a resource name");
        Tile tile = RockBottomAPI.TILE_REGISTRY.get(new ResourceName(sTile));
        if (tile == null) return argerror(1, "Specified tile was not found");

        LuaValue lX = varargs.arg(2);
        if (!lX.isint()) return argerror(2, "Expected an int value for argument 'y'");
        int x = lX.toint();

        LuaValue lY = varargs.arg(3);
        if (!lY.isint()) return argerror(3, "Expected an int value for argument 'y'");
        int y = lY.toint();

        TileLayer layer;
        LuaValue lLayer = varargs.arg(4);
        if (!lLayer.isstring()) return argerror(4, "Expected a string value for argument 'layer'");
        ResourceName name;
        try {
            name = new ResourceName(lLayer.tojstring());
        } catch (IllegalArgumentException e) {
            return argerror(4, "Specified layer was not a resource name");
        }
        Optional<TileLayer> optionalLayer = TileLayer.getAllLayers().stream().filter(it -> it.getName().equals(name)).findAny();
        if (!optionalLayer.isPresent()) return argerror(4, "Specified layer could not be found");
        layer = optionalLayer.get();
        return valueOf(tile.getHardness(WorldLib.world, x, y, layer));
    }

    private Varargs doPlace(Varargs varargs) {
        if (WorldLib.world == null) return error("The world is not available right now");
        LuaValue lTile = varargs.arg(1);
        if (!lTile.isstring()) return argerror(1, "Expected a string value for argument 'tile'");
        String sTile = lTile.tojstring();
        if (!Util.isResourceName(sTile)) return argerror(1, "Specified tile was not a resource name");
        Tile tile = RockBottomAPI.TILE_REGISTRY.get(new ResourceName(sTile));
        if (tile == null) return argerror(1, "Specified tile was not found");

        LuaValue lX = varargs.arg(2);
        if (!lX.isint()) return argerror(2, "Expected an int value for argument 'y'");
        int x = lX.toint();

        LuaValue lY = varargs.arg(3);
        if (!lY.isint()) return argerror(3, "Expected an int value for argument 'y'");
        int y = lY.toint();

        TileLayer layer;
        LuaValue lLayer = varargs.arg(4);
        if (!lLayer.isstring()) return argerror(4, "Expected a string value for argument 'layer'");
        ResourceName name;
        try {
            name = new ResourceName(lLayer.tojstring());
        } catch (IllegalArgumentException e) {
            return argerror(4, "Specified layer was not a resource name");
        }
        Optional<TileLayer> optionalLayer = TileLayer.getAllLayers().stream().filter(it -> it.getName().equals(name)).findAny();
        if (!optionalLayer.isPresent()) return argerror(4, "Specified layer could not be found");
        layer = optionalLayer.get();

        Entity entity;
        if (varargs.isnil(5)) entity = null;
        else entity = EntityLib.parseUUID(varargs, 5);
        if (entity == null || entity instanceof AbstractEntityPlayer) {
            tile.doPlace(WorldLib.world, x, y, layer, ItemsLib.parseItemInstance(5, varargs.checktable(5)), (AbstractEntityPlayer) entity);
        } else {
            return argerror(1, "Specified uuid did not correspond to a player");
        }

        return NIL;
    }

    private Varargs doBreak(Varargs varargs) {
        if (WorldLib.world == null) return error("The world is not available right now");
        LuaValue lTile = varargs.arg(1);
        if (!lTile.isstring()) return argerror(1, "Expected a string value for argument 'tile'");
        String sTile = lTile.tojstring();
        if (!Util.isResourceName(sTile)) return argerror(1, "Specified tile was not a resource name");
        Tile tile = RockBottomAPI.TILE_REGISTRY.get(new ResourceName(sTile));
        if (tile == null) return argerror(1, "Specified tile was not found");

        LuaValue lX = varargs.arg(2);
        if (!lX.isint()) return argerror(2, "Expected an int value for argument 'y'");
        int x = lX.toint();

        LuaValue lY = varargs.arg(3);
        if (!lY.isint()) return argerror(3, "Expected an int value for argument 'y'");
        int y = lY.toint();

        TileLayer layer;
        LuaValue lLayer = varargs.arg(4);
        if (!lLayer.isstring()) return argerror(4, "Expected a string value for argument 'layer'");
        ResourceName name;
        try {
            name = new ResourceName(lLayer.tojstring());
        } catch (IllegalArgumentException e) {
            return argerror(4, "Specified layer was not a resource name");
        }
        Optional<TileLayer> optionalLayer = TileLayer.getAllLayers().stream().filter(it -> it.getName().equals(name)).findAny();
        if (!optionalLayer.isPresent()) return argerror(3, "Specified layer could not be found");
        layer = optionalLayer.get();

        Entity entity;
        if (varargs.isnil(5)) entity = null;
        else entity = EntityLib.parseUUID(varargs, 5);
        if (entity == null || entity instanceof AbstractEntityPlayer) {
            tile.doBreak(WorldLib.world, x, y, layer, (AbstractEntityPlayer) entity, !varargs.isnil(6) && varargs.checkboolean(6), !varargs.isnil(7) && varargs.checkboolean(7));
        } else {
            return argerror(1, "Specified uuid did not correspond to a player");
        }

        return NIL;
    }

    private Varargs addTile(Varargs varargs) { // name, description, init
        LuaValue canBreak = null;
        LuaValue canPlace = null;
        LuaValue canStay = null;
        LuaValue canReplace = null;
        LuaValue onRemoved = null;
        LuaValue onAdded = null;
        LuaValue onDestroyed = null;
        LuaValue onInteractWith = null;
        LuaValue onChangeAround = null;
        LuaValue onScheduledUpdate = null;
        LuaValue onCollideWithEntity = null;
        LuaValue updateRandomly = null;
        LuaValue render = null;
        LuaValue renderItem = null;
        LuaValue getPlacementState = null;
        BoundBox bb = Tile.DEFAULT_BOUNDS;
        List<BoundBox> bbs = null;
        List<TileLayer> placeableLayers = null;
        List<ItemInstance> drops = null;
        boolean hasItem = true;
        boolean canClimb = false;
        boolean canGrassSpreadTo = false;
        boolean canKeepPlants = false;
        boolean canLiquidSpreadInto = false;
        boolean isFullTile = true;
        boolean doesSustainLeaves = false;
        float hardness = 1f;
        int interactionPriority = 0;
        TileProp[] props = new TileProp[0];

        if (varargs.narg() < 1) return argerror("Expected at leas 1 argument");
        LuaValue lName = varargs.arg(1);
        if (!lName.isstring()) return argerror(1, "Expected a string value for argument 'name'");
        ResourceName name;
        try {
            name = new ResourceName(lName.tojstring());
        } catch (IllegalArgumentException e) {
            return argerror(1, "Specified name was not a resource name");
        }

        if (RockBottomAPI.TILE_REGISTRY.get(name) != null) return argerror(1, "Specified name already in use");

        String[] description;
        LuaValue lDescription = varargs.arg(2);
        if (lDescription.istable()) {
            description = new String[lDescription.length()];
            for (int i = 1; i <= description.length; i++) {
                LuaValue lLine = lDescription.get(i);
                if (!lLine.isstring()) return argerror(2, "Specified description table must contain only strings");
                description[i - 1] = lLine.tojstring();
            }
        } else if (lDescription.isstring()) {
            description = new String[]{lDescription.tojstring()};
        } else if (lDescription.isnil()) {
            description = new String[0];
        } else {
            return argerror(2, "Expected a table, string or nil value for argument 'description'");
        }

        LuaValue lInit = varargs.arg(3);
        if (lInit.isfunction()) {
            LuaTable tileTable = new LuaTable();
            tileTable.set("canBreak", NIL);
            tileTable.set("canPlace", NIL);
            tileTable.set("canStay", NIL);
            tileTable.set("canReplace", NIL);
            tileTable.set("onRemoved", NIL);
            tileTable.set("onAdded", NIL);
            tileTable.set("onDestroyed", NIL);
            tileTable.set("onInteractWith", NIL);
            tileTable.set("onChangeAround", NIL);
            tileTable.set("onScheduledUpdate", NIL);
            tileTable.set("onCollideWithEntity", NIL);
            tileTable.set("updateRandomly", NIL);
            tileTable.set("render", NIL);
            tileTable.set("renderItem", NIL);
            tileTable.set("getPlacementState", NIL);
            tileTable.set("bb", BoundBoxesLib.DEFAULT_BOUNDS);
            tileTable.set("bbs", LuaValue.NIL);
            tileTable.set("placeableLayers", NIL);
            tileTable.set("drops", NIL);
            tileTable.set("hasItem", TRUE);
            tileTable.set("canClimb", FALSE);
            tileTable.set("canGrassSpreadTo", FALSE);
            tileTable.set("canKeepPlants", FALSE);
            tileTable.set("canLiquidSpreadInto", FALSE);
            tileTable.set("isFullTile", TRUE);
            tileTable.set("doesSustainLeaves", FALSE);
            tileTable.set("hardness", valueOf(hardness));
            tileTable.set("interactionPriority", valueOf(0));
            tileTable.set("props", NIL);
            lInit.invoke(tileTable);
            canBreak = nilToNull(tileTable.get("canBreak"));
            canPlace = nilToNull(tileTable.get("canPlace"));
            canStay = nilToNull(tileTable.get("canStay"));
            canReplace = nilToNull(tileTable.get("canReplace"));
            onRemoved = nilToNull(tileTable.get("onRemoved"));
            onAdded = nilToNull(tileTable.get("onAdded"));
            onDestroyed = nilToNull(tileTable.get("onDestroyed"));
            onInteractWith = nilToNull(tileTable.get("onInteractWith"));
            onChangeAround = nilToNull(tileTable.get("onChangeAround"));
            onScheduledUpdate = nilToNull(tileTable.get("onScheduledUpdate"));
            onCollideWithEntity = nilToNull(tileTable.get("onCollideWithEntity"));
            updateRandomly = nilToNull(tileTable.get("updateRandomly"));
            render = nilToNull(tileTable.get("render"));
            renderItem = nilToNull(tileTable.get("renderItem"));
            getPlacementState = nilToNull(tileTable.get("getPlacementState"));
            bb = BoundBoxesLib.parseBoundBox(tileTable.get("bb"));
            LuaValue lBBs = tileTable.get("bbs");
            if (lBBs.isnil()) bbs = null;
            else {
                bbs = new ArrayList<>(lBBs.length());
                for (int i = 1; i <= lBBs.length(); i++) {
                    LuaValue lBB = lBBs.get(i);
                    if (!lBB.istable()) return error("All values in the bbs table must be BoundBoxes");
                    bbs.add(BoundBoxesLib.parseBoundBox(lBB));
                }
            }
            LuaValue lPlaceableLayers = tileTable.get("placeableLayers");
            if (lPlaceableLayers.isnil()) placeableLayers = null;
            else {
                placeableLayers = new ArrayList<>(lPlaceableLayers.length());
                for (int i = 1; i <= placeableLayers.size(); i++) {
                    LuaValue lLayer = lPlaceableLayers.get(i);
                    if (!lLayer.isstring()) return error("All values in the placeableLayers table must be strings");
                    String sLayer = lLayer.tojstring();
                    if (!Util.isResourceName(sLayer)) placeableLayers.add(i - 1,
                                                                          TileLayer.getAllLayers().stream().filter((layer) -> layer.getName() == new ResourceName(sLayer)).findAny()
                                                                                  .orElseThrow(() -> new LuaError(valueOf("Invalid layer name"))));
                }
            }
            LuaValue lDrops = tileTable.get("drops");
            if (lDrops.isnil()) drops = null;
            else {
                drops = new ArrayList<>(lDrops.length());
                for (int i = 1; i <= lDrops.length(); i++) {
                    LuaValue lDrop = lDrops.get(i);
                    if (!lDrop.istable()) return error("All values in the drops table must be ItemInstances");
                    drops.add(i - 1, ItemsLib.parseItemInstance(-1, lDrop));
                }
            }
            hasItem = tileTable.get("hasItem").checkboolean();
            canClimb = tileTable.get("canClimb").checkboolean();
            canGrassSpreadTo = tileTable.get("canGrassSpreadTo").checkboolean();
            canKeepPlants = tileTable.get("canKeepPlants").checkboolean();
            canLiquidSpreadInto = tileTable.get("canLiquidSpreadInto").checkboolean();
            isFullTile = tileTable.get("isFullTile").checkboolean();
            doesSustainLeaves = tileTable.get("doesSustainLeaves").checkboolean();
            hardness = (float) tileTable.get("hardness").checkdouble();
            interactionPriority = tileTable.get("interactionPriority").checkint();
            LuaValue lProps = tileTable.get("props");
            if (lProps.istable()) {
                props = new TileProp[lProps.length()];
                for (int i = 1; i <= props.length; i++) {
                    LuaValue lProp = lProps.get(i).checktable();
                    LuaValue index = lProp.get("__index");
                    if (index.raweq(LuaEnvironment.globals.get("IntProp"))) {
                        props[i - 1] = new IntProp(lProp.get("name").checkjstring(), lProp.get("def").checkint(), lProp.get("possibilities").checkint());
                    } else if (index.raweq(LuaEnvironment.globals.get("EnumProp"))) {
                        try {
                            String def = lProp.get("def").checkjstring();
                            Class<?> clazz = Class.forName(lProp.get("enumName").checkjstring());
                            if (!clazz.isEnum()) argerror(1, "The found class was not an enum");
                            Object[] constants = clazz.getEnumConstants();
                            Method method = clazz.getMethod("name");
                            Enum<?> value = null;
                            for (Object constant : constants) {
                                if (method.invoke(constant).equals(def)) {
                                    value = (Enum<?>) constant;
                                    break;
                                }
                            }
                            if (value == null) argerror(4, "That value was not found in this enum");
                            //noinspection unchecked
                            props[i - 1] = new EnumProp(lProp.get("name").checkjstring(), value, clazz);
                        } catch (ClassNotFoundException e) {
                            error("No enum with that name was found");
                        } catch (NoSuchMethodException e) {
                            throw new IllegalStateException("An enum without a name method does not exist");
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            error("There was a problem invoking the enum's name method");
                        }
                    } else if (index.raweq(LuaEnvironment.globals.get("StringProp"))) {
                        LuaTable lAllowedValues = lProp.get("allowedValues").checktable();
                        String[] allowedValues = new String[lAllowedValues.length()];
                        for (int j = 1; j <= allowedValues.length; j++) {
                            allowedValues[j - 1] = lAllowedValues.checkjstring(j);
                        }
                        props[i - 1] = new StringProp(lProp.get("name").checkjstring(), lProp.get("def").checkjstring(), allowedValues);
                    } else if (index.raweq(LuaEnvironment.globals.get("BoolProp"))) {
                        props[i - 1] = new BoolProp(lProp.get("name").checkjstring(), lProp.get("def").checkboolean());
                    } else if (index.raweq(LuaEnvironment.globals.get("SpecificIntProp"))) {
                        LuaTable lAllowedValues = lProp.get("allowedValues").checktable();
                        Integer[] allowedValues = new Integer[lAllowedValues.length()];
                        for (int j = 1; j <= allowedValues.length; j++) {
                            allowedValues[j - 1] = lAllowedValues.checkint(j);
                        }
                        props[i - 1] = new SpecificIntProp(lProp.get("name").checkjstring(), lProp.get("def").checkint(), allowedValues);
                    } else {
                        error("Unrecognized TileProp");
                    }
                }
            }
        } else if (!lInit.isnil()) {
            return argerror(3, "Expected a function or nil value for argument 'init'");
        }
        new LuaTile(name, description, canBreak, canPlace, canStay, canReplace, onRemoved, onAdded, onDestroyed, onInteractWith, onChangeAround, onScheduledUpdate,
                    onCollideWithEntity, updateRandomly, render, renderItem, getPlacementState, bbs, placeableLayers, drops, hasItem, canClimb, canGrassSpreadTo, canKeepPlants,
                    canLiquidSpreadInto, isFullTile, hardness, interactionPriority, props, bb, doesSustainLeaves).register();
        return valueOf(name.toString());
    }

    private LuaValue nilToNull(LuaValue val) {
        return val.isnil() ? null : val;
    }

    private static class LuaTile extends TileBasic {
        private final String[] description;
        private final LuaValue canBreak;
        private final LuaValue canPlace;
        private final LuaValue canStay;
        private final LuaValue canReplace;
        private final LuaValue onRemoved;
        private final LuaValue onAdded;
        private final LuaValue onDestroyed;
        private final LuaValue onInteractWith;
        private final LuaValue onChangeAround;
        private final LuaValue onScheduledUpdate;
        private final LuaValue onCollideWithEntity;
        private final LuaValue updateRandomly;
        private final LuaValue render;
        private final LuaValue renderItem;
        private final LuaValue getPlacementState;
        private final BoundBox bb;
        private final List<BoundBox> bbs;
        private final List<TileLayer> placeableLayers;
        private final List<ItemInstance> drops;
        private final boolean hasItem;
        private final boolean canClimb;
        private final boolean canGrassSpreadTo;
        private final boolean canKeepPlants;
        private final boolean canLiquidSpreadInto;
        private final boolean isFullTile;
        private final int interactionPriority;
        private final boolean doesSustainLeaves;

        private LuaTile(ResourceName name, String[] description, LuaValue canBreak, LuaValue canPlace, LuaValue canStay, LuaValue canReplace, LuaValue onRemoved, LuaValue onAdded, LuaValue onDestroyed, LuaValue onInteractWith, LuaValue onChangeAround, LuaValue onScheduledUpdate, LuaValue onCollideWithEntity, LuaValue updateRandomly, LuaValue render, LuaValue renderItem, LuaValue getPlacementState, List<BoundBox> bbs, List<TileLayer> placeableLayers, List<ItemInstance> drops, boolean hasItem, boolean canClimb, boolean canGrassSpreadTo, boolean canKeepPlants, boolean canLiquidSpreadInto, boolean isFullTile, float hardness, int interactionPriority, TileProp[] props, BoundBox bb, boolean doesSustainLeaves) {
            super(name);
            this.description = description;
            this.canBreak = canBreak;
            this.canPlace = canPlace;
            this.canStay = canStay;
            this.canReplace = canReplace;
            this.onRemoved = onRemoved;
            this.onAdded = onAdded;
            this.onDestroyed = onDestroyed;
            this.onInteractWith = onInteractWith;
            this.onChangeAround = onChangeAround;
            this.onScheduledUpdate = onScheduledUpdate;
            this.onCollideWithEntity = onCollideWithEntity;
            this.updateRandomly = updateRandomly;
            this.render = render;
            this.renderItem = renderItem;
            this.getPlacementState = getPlacementState;
            this.bb = bb;
            this.bbs = bbs;
            this.placeableLayers = placeableLayers;
            this.drops = drops;
            this.hasItem = hasItem;
            this.canClimb = canClimb;
            this.canGrassSpreadTo = canGrassSpreadTo;
            this.canKeepPlants = canKeepPlants;
            this.canLiquidSpreadInto = canLiquidSpreadInto;
            this.isFullTile = isFullTile;
            this.doesSustainLeaves = doesSustainLeaves;
            this.setHardness(hardness);
            this.interactionPriority = interactionPriority;
            this.addProps(props);
        }

        @Override
        public TileState getPlacementState(IWorld world, int x, int y, TileLayer layer, ItemInstance instance, AbstractEntityPlayer placer) {
            if (getPlacementState == null) {
                return super.getPlacementState(world, x, y, layer, instance, placer);
            } else {
                WorldLib.world = world;
                Varargs ret = getPlacementState.invoke(varargsOf(new LuaValue[]{valueOf(x), valueOf(y), valueOf(layer.getName().toString()), ItemsLib.itemInstanceToLua(instance),
                        valueOf(placer.getUniqueId().toString())}));
                String sState = ret.checkjstring(1);
                if (!Util.isResourceName(sState)) error("getPlacementState must return a valid resource name");
                TileState state = RockBottomAPI.TILE_STATE_REGISTRY.get(new ResourceName(sState));
                if (state == null) error("getPlacementState must return a registered state");
                return state;
            }
        }

        @Override
        protected ITileRenderer createRenderer(ResourceName name) {
            return new LuaTileRenderer(name);
        }

        @Override
        public BoundBox getBoundBox(IWorld world, int x, int y, TileLayer layer) {
            return bb;
        }

        @Override
        public List<BoundBox> getBoundBoxes(IWorld world, int x, int y, TileLayer layer, MovableWorldObject object, BoundBox objectBox, BoundBox objectBoxMotion) {
            return bbs == null ? super.getBoundBoxes(world, x, y, layer, object, objectBox, objectBoxMotion) :
                    bbs.stream().map(it -> it.copy().add(x, y)).collect(Collectors.toList());
        }

        @Override
        public boolean canBreak(IWorld world, int x, int y, TileLayer layer, AbstractEntityPlayer player, boolean isRightTool) {
            if (canBreak == null) {
                return super.canBreak(world, x, y, layer, player, isRightTool);
            } else {
                WorldLib.world = world;
                return LuaEnvironment
                        .executeScript(canBreak, valueOf(x), valueOf(y), valueOf(layer.getName().toString()), valueOf(player.getUniqueId().toString()), valueOf(isRightTool));
            }
        }

        @Override
        public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractEntityPlayer player) {
            if (canPlace == null) {
                return super.canPlace(world, x, y, layer, player);
            } else {
                WorldLib.world = world;
                return LuaEnvironment.executeScript(canPlace, valueOf(x), valueOf(y), valueOf(layer.getName().toString()), valueOf(player.getUniqueId().toString()));
            }
        }

        @Override
        public boolean canPlaceInLayer(TileLayer layer) {
            if (placeableLayers == null) return super.canPlaceInLayer(layer);
            else {
                return placeableLayers.contains(layer);
            }
        }

        @Override
        protected boolean hasItem() {
            return hasItem;
        }

        @Override
        public void onRemoved(IWorld world, int x, int y, TileLayer layer) {
            if (onRemoved == null) {
                super.onRemoved(world, x, y, layer);
            } else {
                WorldLib.world = world;
                LuaEnvironment.executeScript(onRemoved, valueOf(x), valueOf(y), valueOf(layer.getName().toString()));
            }
        }

        @Override
        public void onAdded(IWorld world, int x, int y, TileLayer layer) {
            if (onAdded == null) {
                super.onAdded(world, x, y, layer);
            } else {
                WorldLib.world = world;
                LuaEnvironment.executeScript(onAdded, valueOf(x), valueOf(y), valueOf(layer.getName().toString()));
            }
        }

        @Override
        public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player) {
            if (onInteractWith == null) {
                return super.onInteractWith(world, x, y, layer, mouseX, mouseY, player);
            } else {
                WorldLib.world = world;
                return LuaEnvironment.executeScript(onInteractWith, valueOf(x), valueOf(y), valueOf(layer.getName().toString()), valueOf(mouseX), valueOf(mouseY),
                                                    valueOf(player.getUniqueId().toString()));
            }
        }

        @Override
        public int getInteractionPriority(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player) {
            return interactionPriority;
        }

        @Override
        public boolean canReplace(IWorld world, int x, int y, TileLayer layer) {
            if (canReplace == null) {
                return super.canReplace(world, x, y, layer);
            } else {
                WorldLib.world = world;
                return LuaEnvironment.executeScript(canReplace, valueOf(x), valueOf(y), valueOf(layer.getName().toString()));
            }
        }

        @Override
        public boolean doesSustainLeaves(IWorld world, int x, int y, TileLayer layer) {
            return doesSustainLeaves;
        }

        @Override
        public void onDestroyed(IWorld world, int x, int y, Entity destroyer, TileLayer layer, boolean shouldDrop) {
            if (onDestroyed == null) {
                super.onDestroyed(world, x, y, destroyer, layer, shouldDrop);
            } else {
                WorldLib.world = world;
                LuaEnvironment
                        .executeScript(onDestroyed, valueOf(x), valueOf(y), valueOf(destroyer.getUniqueId().toString()), valueOf(layer.getName().toString()), valueOf(shouldDrop));
            }
        }

        @Override
        public List<ItemInstance> getDrops(IWorld world, int x, int y, TileLayer layer, Entity destroyer) {
            return drops == null ? super.getDrops(world, x, y, layer, destroyer) : drops;
        }

        @Override
        public void onChangeAround(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
            if (onChangeAround == null) {
                super.onChangeAround(world, x, y, layer, changedX, changedY, changedLayer);
            } else {
                WorldLib.world = world;
                LuaEnvironment.executeScript(onChangeAround, valueOf(x), valueOf(y), valueOf(layer.getName().toString()), valueOf(changedX), valueOf(changedY),
                                             valueOf(changedLayer.getName().toString()));
            }
        }

        @Override
        public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
            if (canStay == null) {
                return super.canStay(world, x, y, layer, changedX, changedY, changedLayer);
            } else {
                WorldLib.world = world;
                return LuaEnvironment.executeScript(canStay, valueOf(x), valueOf(y), valueOf(layer.getName().toString()), valueOf(changedX), valueOf(changedY),
                                                    valueOf(changedLayer.getName().toString()));
            }
        }

        @Override
        public boolean isFullTile() {
            return isFullTile;
        }

        @Override
        public void updateRandomly(IWorld world, int x, int y, TileLayer layer) {
            if (updateRandomly == null) {
                super.updateRandomly(world, x, y, layer);
            } else {
                WorldLib.world = world;
                LuaEnvironment.executeScript(updateRandomly, valueOf(x), valueOf(y), valueOf(layer.getName().toString()));
            }
        }

        @Override
        public void onScheduledUpdate(IWorld world, int x, int y, TileLayer layer, int scheduledMeta) {
            if (onScheduledUpdate == null) {
                super.onScheduledUpdate(world, x, y, layer, scheduledMeta);
            } else {
                WorldLib.world = world;
                LuaEnvironment.executeScript(onScheduledUpdate, valueOf(x), valueOf(y), valueOf(layer.getName().toString()), valueOf(scheduledMeta));
            }
        }

        @Override
        public boolean canClimb(IWorld world, int x, int y, TileLayer layer, TileState state, BoundBox entityBox, BoundBox entityBoxMotion, List<BoundBox> tileBoxes, Entity entity) {
            return canClimb;
        }

        @Override
        public void onCollideWithEntity(IWorld world, int x, int y, TileLayer layer, TileState state, BoundBox entityBox, BoundBox entityBoxMotion, List<BoundBox> tileBoxes, Entity entity) {
            if (onCollideWithEntity == null) {
                super.onCollideWithEntity(world, x, y, layer, state, entityBox, entityBoxMotion, tileBoxes, entity);
            } else {
                WorldLib.world = world;
                int tileBoxesSize = tileBoxes.size();
                LuaTable table = new LuaTable(tileBoxesSize, 0);
                for (BoundBox tileBox : tileBoxes) {
                    table.add(BoundBoxesLib.boundBoxToLua(tileBox));
                }
                LuaEnvironment.executeScript(onCollideWithEntity, valueOf(x), valueOf(y), valueOf(layer.getName().toString()), valueOf(state.getName().toString()),
                                             BoundBoxesLib.boundBoxToLua(entityBox), BoundBoxesLib.boundBoxToLua(entityBoxMotion), table, valueOf(entity.getUniqueId().toString()));
            }
        }

        @Override
        public void onIntersectWithEntity(IWorld world, int x, int y, TileLayer layer, TileState state, BoundBox entityBox, BoundBox entityBoxMotion, List<BoundBox> tileBoxes, Entity entity) {
            if (onCollideWithEntity == null) {
                super.onCollideWithEntity(world, x, y, layer, state, entityBox, entityBoxMotion, tileBoxes, entity);
            } else {
                WorldLib.world = world;
                int tileBoxesSize = tileBoxes.size();
                LuaTable table = new LuaTable(tileBoxesSize, 0);
                for (BoundBox tileBox : tileBoxes) {
                    table.add(BoundBoxesLib.boundBoxToLua(tileBox));
                }
                LuaEnvironment.executeScript(onCollideWithEntity, valueOf(x), valueOf(y), valueOf(layer.getName().toString()), valueOf(state.getName().toString()),
                                             BoundBoxesLib.boundBoxToLua(entityBox), BoundBoxesLib.boundBoxToLua(entityBoxMotion), table, valueOf(entity.getUniqueId().toString()));
            }
        }

        @Override
        public void describeItem(IAssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced) {
            if (description != null) Collections.addAll(desc, description);
            super.describeItem(manager, instance, desc, isAdvanced);
        }

        @Override
        public boolean canGrassSpreadTo(IWorld world, int x, int y, TileLayer layer) {
            return canGrassSpreadTo;
        }

        @Override
        public boolean canKeepPlants(IWorld world, int x, int y, TileLayer layer) {
            return canKeepPlants;
        }

        @Override
        public boolean canLiquidSpreadInto(IWorld world, int x, int y, TileLiquid liquid) {
            return canLiquidSpreadInto;
        }

        private class LuaTileRenderer extends DefaultTileRenderer<LuaTile> {
            private LuaTileRenderer(ResourceName texture) {
                super(texture);
            }

            @Override
            public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, LuaTile tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light) {
                if (render == null) {
                    super.render(game, manager, g, world, tile, state, x, y, layer, renderX, renderY, scale, light);
                } else {
                    AssetManagerLib.manager = manager;
                    RendererLib.renderer = g;
                    WorldLib.world = world;
                    LuaValue[] lights = new LuaValue[light.length];
                    for (int i = 0; i < lights.length; i++) {
                        lights[i] = valueOf(light[i]);
                    }
                    if (LuaEnvironment.executeScript(render, valueOf(tile.getName().toString()), valueOf(state.getName().toString()), valueOf(x), valueOf(y),
                                                     valueOf(layer.getName().toString()), valueOf(renderX), valueOf(renderY), valueOf(scale),
                                                     listOf(lights))) // if the function returns true execute the super
                        super.render(game, manager, g, world, tile, state, x, y, layer, renderX, renderY, scale, light);

                }
            }

            @Override
            public void renderItem(IGameInstance game, IAssetManager manager, IRenderer g, LuaTile tile, ItemInstance instance, float x, float y, float scale, int filter) {
                if (renderItem == null) {
                    super.renderItem(game, manager, g, tile, instance, x, y, scale, filter);
                } else {
                    AssetManagerLib.manager = manager;
                    RendererLib.renderer = g;
                    if (LuaEnvironment.executeScript(renderItem, valueOf(tile.getName().toString()), ItemsLib.itemInstanceToLua(instance), valueOf(x), valueOf(y), valueOf(scale),
                                                     valueOf(filter))) // if the function returns true execute the super
                        super.renderItem(game, manager, g, tile, instance, x, y, scale, filter);
                }
            }
        }
    }
}
