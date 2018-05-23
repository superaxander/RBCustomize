package alexanders.mods.rbcustomize.lua;

import alexanders.mods.rbcustomize.IdentifierPacket;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

public class NetLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable net = new LuaTable();
        net.set("isClient", new FunctionWrapper(RockBottomAPI.getNet()::isClient));
        net.set("isServer", new FunctionWrapper(RockBottomAPI.getNet()::isServer));
        net.set("isActive", new FunctionWrapper(RockBottomAPI.getNet()::isActive));
        net.set("isConnectedToServer", new FunctionWrapper(RockBottomAPI.getNet()::isConnectedToServer));
        net.set("isWhitelistEnabled", new FunctionWrapper(RockBottomAPI.getNet()::isWhitelistEnabled));
        net.set("addIdentifier", new FunctionWrapper(this::addIdentifier));
        net.set("sendConnectedHasEnabledCheck", new FunctionWrapper(this::connectedHasEnabled));
        net.set("getConfirmedIdentifiers", new FunctionWrapper(this::getConfirmedIdentifiers));
        env.set("net", net);
        return net;
    }

    private Varargs addIdentifier(Varargs varargs) {
        IdentifierPacket.ownIdentifiers.add(varargs.checkjstring(1));
        return NIL;
    }

    private Varargs getConfirmedIdentifiers(Varargs varargs) {
        LuaTable table = new LuaTable();
        IdentifierPacket.confirmedIdentifiers.forEach((key, value) -> table.set(valueOf(key.toString()), valueOf(value)));
        return table;
    }

    private Varargs connectedHasEnabled(Varargs varargs) {
        String identifier = varargs.checkjstring(1);
        if (!RockBottomAPI.getNet().isActive()) return TRUE;
        if (RockBottomAPI.getNet().isClient()) {
            RockBottomAPI.getNet().sendToServer(new IdentifierPacket(RockBottomAPI.getGame().getPlayer().getUniqueId(), true, identifier));
        } else {
            RockBottomAPI.getNet().sendToAllPlayers(RockBottomAPI.getGame().getWorld(), new IdentifierPacket(true, identifier));
        }

        return FALSE;
    }
}
