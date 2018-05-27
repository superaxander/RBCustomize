package alexanders.mods.rbcustomize.lua;

import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
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
        assetManager.set("localize", new FunctionWrapper(this::localize));
        env.set("assetManager", assetManager);
        return assetManager;
    }

    private Varargs getTexture(Varargs varargs) {
        if (manager == null) error("The AssetManager is not available");
        String sPath = varargs.checkjstring(1);
        if (!Util.isResourceName(sPath)) argerror(1, "Specified path was not a resource name");
        ITexture texture = manager.getTexture(new ResourceName(sPath));
        return userdataOf(texture);
    }

    private Varargs localize(Varargs varargs) {
        if (manager == null) return error("The AssetManager is not available");
        String sName = varargs.checkjstring(1);
        if (!Util.isResourceName(sName)) return argerror(1, "Expected a ResourceName for argument 'unloc'");
        if (varargs.istable(2)) {
            LuaTable args = varargs.checktable(2);
            String[] outArgs = new String[args.length()];
            for (int i = 1; i <= args.length(); i++) {
                outArgs[i - 1] = args.get(i).checkjstring();
            }
            return valueOf(manager.localize(new ResourceName(sName), (Object[]) outArgs));
        } else return valueOf(manager.localize(new ResourceName(sName)));
    }

}
