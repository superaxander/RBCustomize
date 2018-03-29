package alexanders.mods.rbcustomize.lua;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.util.Util;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

public class AssetManagerLib extends TwoArgFunction {
    static IAssetManager manager = null;
    
    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable assetManager = new LuaTable();
        assetManager.set("getTexture", new FunctionWrapper(this::getTexture));
        env.set("assetManager", assetManager);
        return assetManager;
    }

    private Varargs getTexture(Varargs varargs) {
        if (manager == null) error("The AssetManager is not available");
        String sPath = varargs.checkjstring(1);
        if(!Util.isResourceName(sPath)) argerror(1, "Specified path was not a resource name");
        ITexture texture = manager.getTexture(RockBottomAPI.createRes(sPath));
        return userdataOf(texture);
    }
}
