package alexanders.mods.rbcustomize.lua;

import alexanders.mods.rbcustomize.RBCustomize;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.List;
import java.util.logging.Level;

import static alexanders.mods.rbcustomize.Util.*;

public class CommandsLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable commands = new LuaTable();
        commands.set("add", new FunctionWrapper(this::add));
        commands.set("remove", new FunctionWrapper(this::remove));
        env.set("commands", commands);
        return commands;
    }

    private Varargs add(Varargs varargs) { // name, description, level, triggers, execute, getAutoCompleteSuggestions
        String sName = varargs.checkjstring(1);
        if (!Util.isResourceName(sName)) return argerror(1, "Expected a ResourceName for argument 'name'");
        ResourceName name = new ResourceName(sName);
        String description;
        LuaValue lDescription = varargs.arg(2);
        if (lDescription.isstring()) description = lDescription.checkjstring();
        else if (lDescription.isnil()) description = "";
        else return argerror(2, "Expected a string or nil value for argument 'description'");
        int level = varargs.checkint(3);
        String[] triggers;
        LuaValue lTriggers = varargs.arg(4);
        if (lTriggers.istable()) triggers = luaToStringList(lTriggers.checktable()).toArray(new String[0]);
        else if (lTriggers.isstring()) triggers = new String[]{lTriggers.checkjstring()};
        else if (lTriggers.isnil()) triggers = new String[]{name.getResourceName()};
        else return argerror(1, "Expected a table, string or nil value for argument 'triggers'");
        LuaValue execute = nilToNull(varargs.arg(5));
        if (execute != null && !execute.isfunction()) return argerror(5, "Expected a nil or function value for argument 'execute'");
        LuaValue getAutoCompleteSuggestions = nilToNull(varargs.arg(6));
        if (getAutoCompleteSuggestions != null && !getAutoCompleteSuggestions.isfunction())
            return argerror(5, "Expected a nil or function value for argument 'getAutoCompleteSuggestions'");
        new LuaCommand(name, description, level, triggers, execute, getAutoCompleteSuggestions).register();
        return NIL;
    }

    private Varargs remove(Varargs varargs) {
        String sName = varargs.checkjstring(1);
        if (!Util.isResourceName(sName)) return argerror(1, "Expected a ResourceName for argument 'name'");
        Registries.COMMAND_REGISTRY.unregister(new ResourceName(sName));
        return NIL;
    }

    private static final class LuaCommand extends Command {
        private final LuaValue execute;
        private final LuaValue getAutocompleteSuggestions;

        private LuaCommand(ResourceName name, String description, int level, String[] triggers, LuaValue execute, LuaValue getAutoCompleteSuggestions) {
            super(name, description, level, triggers);
            this.execute = execute;
            this.getAutocompleteSuggestions = getAutoCompleteSuggestions;
        }

        @Override
        public ChatComponent execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat) {
            try {
                if (execute != null)
                    return ChatLib.parseChatComponent(LuaEnvironment.executeScriptVarargs(execute, toLuaStringList(args), userdataOf(sender), valueOf(playerName)), 1);
            } catch (LuaError e) {RBCustomize.logger.log(Level.WARNING, "Execution of script failed", e);}
            return null;
        }

        @Override
        public List<String> getAutocompleteSuggestions(String[] args, int argNumber, ICommandSender sender, IGameInstance game, IChatLog chat) {
            if (getAutocompleteSuggestions != null) return luaToStringList(
                    LuaEnvironment.executeScriptVarargs(getAutocompleteSuggestions, toLuaStringList(args), valueOf(argNumber), userdataOf(sender)).checktable(1));
            return super.getAutocompleteSuggestions(args, argNumber, sender, game, chat);
        }
    }
}
