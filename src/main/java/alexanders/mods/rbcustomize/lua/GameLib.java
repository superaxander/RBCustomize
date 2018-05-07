package alexanders.mods.rbcustomize.lua;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

public class GameLib extends TwoArgFunction {
    public final IGameInstance game;

    public GameLib(IGameInstance game) {
        this.game = game;
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable game = new LuaTable();
        game.set("getWidth", new FunctionWrapper(this::getWidth));
        game.set("getHeight", new FunctionWrapper(this::getHeight));
        game.set("getPlayer", new FunctionWrapper(this::getPlayer));
        game.set("isDedicatedServer", new FunctionWrapper(this::isDedicatedServer));
        env.set("game", game);
        return game;
    }

    private Varargs isDedicatedServer(Varargs varargs) {
        return valueOf(RockBottomAPI.getGame().isDedicatedServer());
    }

    private Varargs getPlayer(Varargs varargs) {
        if (game.isDedicatedServer()) return error("Can't get player on a dedicated server");
        return valueOf(game.getPlayer().getUniqueId().toString());
    }

    private Varargs getWidth(Varargs varargs) {
        return valueOf(game.getWidth());
    }

    private Varargs getHeight(Varargs varargs) {
        return valueOf(game.getHeight());
    }
}
