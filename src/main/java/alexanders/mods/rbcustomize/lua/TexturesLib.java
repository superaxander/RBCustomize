package alexanders.mods.rbcustomize.lua;

import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

public class TexturesLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable textures = new LuaTable();
        textures.set("draw", new FunctionWrapper(this::draw));
        textures.set("getRenderWidth", new FunctionWrapper(this::getRenderWidth));
        textures.set("getRenderHeight", new FunctionWrapper(this::getRenderHeight));
        textures.set("getPositionalVariation", new FunctionWrapper(this::getPositionalVariation));
        env.set("textures", textures);
        return textures;
    }

    private Varargs draw(Varargs varargs) { // backingData, x, y, x2, y2, x3, y3, x4, y4, srcX, srcY, srcX2, srcY2, light, filter
        ITexture texture = (ITexture) varargs.checkuserdata(1, ITexture.class);
        float x = (float) varargs.checkdouble(2);
        float y = (float) varargs.checkdouble(3);
        float x2 = (float) varargs.checkdouble(4);
        float y2 = (float) varargs.checkdouble(5);
        float x3 = (float) varargs.checkdouble(6);
        float y3 = (float) varargs.checkdouble(7);
        float x4 = (float) varargs.checkdouble(8);
        float y4 = (float) varargs.checkdouble(9);
        float srcX = (float) varargs.checkdouble(10);
        float srcY = (float) varargs.checkdouble(11);
        float srcX2 = (float) varargs.checkdouble(12);
        float srcY2 = (float) varargs.checkdouble(13);
        LuaValue lLight = varargs.arg(14);
        if (!lLight.istable()) argerror("Light must be a table");
        int[] light;
        int length = lLight.length();
        if (length == 0) {
            light = null;
        } else {
            light = new int[length];
            for (int i = 1; i <= length; i++) {
                light[i - 1] = lLight.get(i).checkint();
            }
        }
        int filter = varargs.checkint(15);

        texture.draw(x, y, x2, y2, x3, y3, x4, y4, srcX, srcY, srcX2, srcY2, light, filter);
        return NIL;
    }

    private Varargs getRenderWidth(Varargs varargs) { // backingData --> renderWidth
        ITexture texture = (ITexture) varargs.checkuserdata(1, ITexture.class);
        return valueOf(texture.getRenderWidth());
    }

    private Varargs getRenderHeight(Varargs varargs) { // backingData --> renderHeight
        ITexture texture = (ITexture) varargs.checkuserdata(1, ITexture.class);
        return valueOf(texture.getRenderHeight());
    }

    private Varargs getPositionalVariation(Varargs varargs) { // backingData --> backingData
        ITexture texture = (ITexture) varargs.checkuserdata(1, ITexture.class);
        return userdataOf(texture.getPositionalVariation(varargs.checkint(2), varargs.checkint(3)));
    }
}
