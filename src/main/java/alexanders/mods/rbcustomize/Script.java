package alexanders.mods.rbcustomize;

import de.ellpeck.rockbottom.api.content.IContent;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import org.luaj.vm2.LuaValue;

public class Script implements IContent {
    public static final ResourceName ID = RBCustomize.createRes("script");
    public final LuaValue function;
    public final HookType hookType;

    public Script(LuaValue function, HookType hookType) {
        this.function = function;
        this.hookType = hookType;
    }
}
