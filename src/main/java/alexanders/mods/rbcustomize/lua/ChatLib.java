package alexanders.mods.rbcustomize.lua;

import alexanders.mods.rbcustomize.RBCustomize;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentEmpty;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentTranslation;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.UUID;

import static alexanders.mods.rbcustomize.Util.*;

public class ChatLib extends TwoArgFunction {
    private final IChatLog chatLog;

    ChatLib(IChatLog chatLog) {this.chatLog = chatLog;}

    static ChatComponent parseChatComponent(Varargs varargs, int arg) {
        LuaValue lComponent = varargs.checktable(arg);
        return parseChatComponent(lComponent);
    }

    private static ChatComponent parseChatComponent(LuaValue lComponent) {
        String type = lComponent.get("type").checkjstring();
        ChatComponent component;
        switch (type) {
            case "ChatComponentEmpty":
                component = new ChatComponentEmpty();
                break;
            case "ChatComponentText":
                component = new ChatComponentText(lComponent.get("text").checkjstring());
                break;
            case "ChatComponentTranslation":
                String sKey = lComponent.get("key").checkjstring();
                if (!Util.isResourceName(sKey)) error("ChatComponentTranslation key must be a resource name");
                component = new ChatComponentTranslation(new ResourceName(sKey), luaToStringList(lComponent.get("formatting").checktable()).toArray(new String[0]));
                break;
            default:
                component = null;
                error("Unrecognized ChatComponent!");
                break;
        }
        LuaValue lChild = lComponent.get("child");
        if (lChild.istable()) {
            return component.append(parseChatComponent(lChild));
        }
        return component;
    }

    private static LuaValue chatComponentToLua(ChatComponent component) {
        LuaValue lComponent;
        if (component instanceof ChatComponentEmpty) {
            lComponent = LuaEnvironment.globals.get("ChatComponentEmpty").call();
        } else if (component instanceof ChatComponentText) {
            lComponent = LuaEnvironment.globals.get("ChatComponentText").call(valueOf(component.getUnformattedString()));
        } else if (component instanceof ChatComponentTranslation) {
            String[] parts = component.getUnformattedString().substring(7).split(",", 2);
            if (Util.isResourceName(parts[0])) throw new IllegalStateException("Expected a resource name, this is a bug");
            String[] formatting = parts[1].substring(2, parts[1].length() - 2).split(",");
            lComponent = LuaEnvironment.globals.get("ChatComponentTranslation").call(valueOf(parts[0]), toLuaStringList(formatting));
        } else {
            RBCustomize.logger.severe("Unrecognized ChatComponent turning into a text component");
            lComponent = LuaEnvironment.globals.get("ChatComponentText")
                    .call(valueOf(component.getDisplayString(RockBottomAPI.getGame(), RockBottomAPI.getGame().getAssetManager())));
        }
        if (component.getAppendage() == null) {
            return lComponent;
        } else {
            return lComponent.get("append").call(lComponent, chatComponentToLua(component.getAppendage()));
        }
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable chat = new LuaTable();

        chat.set("displayMessage", new FunctionWrapper(this::displayMessage));
        chat.set("sendCommandSenderMessage", new FunctionWrapper(this::sendCommandSenderMessage));
        chat.set("getCommand", new FunctionWrapper(this::getCommand));
        chat.set("sendMessageTo", new FunctionWrapper(this::sendMessageTo));
        chat.set("broadcastMessage", new FunctionWrapper(this::broadcastMessage));
        chat.set("getMessages", new FunctionWrapper(this::getMessages));
        chat.set("getLastInputs", new FunctionWrapper(this::getLastInputs));
        chat.set("getPlayerIdFromString", new FunctionWrapper(this::getPlayerIdFromString));
        chat.set("getPlayerSuggestions", new FunctionWrapper(this::getPlayerSuggestions));
        chat.set("getUUID", new FunctionWrapper(this::getUUID));
        env.set("chat", chat);
        return chat;
    }

    private Varargs getUUID(Varargs varargs) {
        return valueOf(parseCommandSender(varargs, 1).getUniqueId().toString());
    }

    private Varargs displayMessage(Varargs varargs) {
        chatLog.displayMessage(parseChatComponent(varargs, 1));
        return NIL;
    }

    private Varargs sendCommandSenderMessage(Varargs varargs) {
        chatLog.sendCommandSenderMessage(varargs.checkjstring(1), parseCommandSender(varargs, 2));
        return NIL;
    }

    private Varargs getCommand(Varargs varargs) {
        Command c = chatLog.getCommand(varargs.checkjstring(1));
        if (c == null) return NIL;
        else return valueOf(c.getName().toString());
    }

    private Varargs sendMessageTo(Varargs varargs) {
        chatLog.sendMessageTo(parseCommandSender(varargs, 1), parseChatComponent(varargs, 2));
        return NIL;
    }

    private Varargs broadcastMessage(Varargs varargs) {
        chatLog.broadcastMessage(parseChatComponent(varargs, 1));
        return NIL;
    }

    private Varargs getMessages(Varargs varargs) {
        return toLuaPrimitiveList(ChatComponent.class, chatLog.getMessages(), ChatLib::chatComponentToLua, null);
    }

    private Varargs getLastInputs(Varargs varargs) {
        return toLuaStringList(chatLog.getLastInputs());
    }

    private Varargs getPlayerIdFromString(Varargs varargs) {
        UUID uuid = chatLog.getPlayerIdFromString(varargs.checkjstring(1));
        return uuid == null ? NIL : valueOf(uuid.toString());
    }

    private Varargs getPlayerSuggestions(Varargs varargs) {
        return toLuaStringList(chatLog.getPlayerSuggestions());
    }

    private ICommandSender parseCommandSender(Varargs varargs, int arg) {
        LuaValue lSender = varargs.arg(arg);
        if (lSender.isuserdata(ICommandSender.class)) return (ICommandSender) lSender.touserdata();
        else if (lSender.isstring()) {
            UUID uuid = chatLog.getPlayerIdFromString(lSender.checkjstring());
            if (uuid == null) error("The specified CommandSender(player) could not be found");
            if (WorldLib.world == null) error("The world is unavailable at this time");
            AbstractEntityPlayer player = WorldLib.world.getPlayer(uuid);
            if (player == null) error("The specified CommandSender(player) could not be found");
            return player;
        } else argerror(arg, "Expected a ICommandSender or string value for argument 'sender'");
        return null;
    }
}
