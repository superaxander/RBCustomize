package alexanders.mods.rbcustomize.lua;

import alexanders.mods.rbcustomize.HookType;
import alexanders.mods.rbcustomize.RBCustomize;
import alexanders.mods.rbcustomize.ScriptContentLoader;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.font.FontProp;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.WorldLoadEvent;
import de.ellpeck.rockbottom.api.event.impl.WorldTickEvent;
import de.ellpeck.rockbottom.api.event.impl.WorldUnloadEvent;
import de.ellpeck.rockbottom.api.util.Colors;
import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.JseBaseLib;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import static alexanders.mods.rbcustomize.Util.getColorName;

public final class LuaEnvironment {
    public static Globals globals;

    public static void init(IGameInstance game) {
        globals = new Globals();
        globals.load(new JseBaseLib());
        globals.load(new PackageLib());
        globals.load(new DebugLib());
        globals.load(new CoroutineLib());
        globals.load(new PackageLib());
        globals.load(new Bit32Lib());
        globals.load(new TableLib());
        globals.load(new StringLib());
        globals.load(new RBJseMathLib());
        globals.load(new RecipesLib());
        globals.load(new ItemsLib());
        globals.load(new TilesLib());
        globals.load(new WorldLib());
        globals.load(new EntitiesLib());
        globals.load(new InventoryLib());
        globals.load(new GameLib(game));
        globals.load(new InputLib(game.getInput()));
        globals.load(new DataLib());
        globals.load(new SandBoxedIoLib());
        globals.load(new UtilsLib());
        globals.load(new StatesLib());
        globals.load(new RendererLib());
        globals.load(new AssetManagerLib());
        globals.load(new TexturesLib());
        globals.load(new GuiLib());
        globals.load(new NetLib());
        globals.load(new ContainersLib());
        globals.load(new CommandsLib());
        LoadState.install(globals);
        LuaC.install(globals);
        LuaTable codeTable = new LuaTable();
        FormattingCode.getDefaultCodes().forEach((ch, code) -> {
            String name;
            if (code.getColor() == Colors.NO_COLOR) if (code.getProp() == FontProp.NONE) throw new IllegalStateException("FormattingCode without color or font prop found");
            else name = code.getProp().name();
            else name = getColorName(code.getColor());
            if (name.equals("RESET")) name = "RESET_PROPS";
            codeTable.set(name, code.toString());
        });
        globals.set("FormattingCode", codeTable);
    }

    public static void initExecution(IGameInstance game) {
        globals.load(new ToastsLib(game.isDedicatedServer() ? null : game.getToaster()));
        globals.load(new ChatLib(game.getChatLog()));
        RBCustomize.logger.config("Executing initialization script");
        ScriptContentLoader.internalScript.function.initupvalue1(globals);
        ScriptContentLoader.internalScript.function.call();
    }

    public static void executeScripts(HookType type) {
        AtomicInteger i = new AtomicInteger();
        AtomicInteger j = new AtomicInteger();
        RBCustomize.logger.config("Executing scripts for hookType: " + type);
        ScriptContentLoader.loadedScripts.forEach((res, script) -> {
            if (script.hookType == type) {
                RBCustomize.logger.config("Executing script: " + res);
                try {
                    script.function.call();
                    i.getAndIncrement();
                } catch (LuaError e) {
                    j.getAndIncrement();
                    RBCustomize.logger.log(Level.WARNING, "Execution of script with name: " + res + " failed!", e);
                }
            }
        });
        RBCustomize.logger.config("Executed scripts for hookType: " + type + " Successful: " + i + " Failed: " + j);
    }

    public static EventResult onWorldTick(EventResult result, WorldTickEvent event) {
        WorldLib.world = event.world;
        executeScripts(HookType.WORLD_TICK);
        return result;
    }

    public static EventResult onWorldLoad(EventResult result, WorldLoadEvent event) {
        WorldLib.world = event.world;
        executeScripts(HookType.WORLD_LOAD);
        return result;
    }

    public static EventResult onWorldUnload(EventResult result, WorldUnloadEvent event) {
        WorldLib.world = event.world;
        executeScripts(HookType.WORLD_UNLOAD);
        return result;
    }

    static Varargs executeScriptVarargs(@Nonnull LuaValue function, LuaValue... values) {
        try {
            return function.invoke(LuaValue.varargsOf(values));
        } catch (LuaError e) {
            RBCustomize.logger.log(Level.WARNING, "Execution of script failed!", e);
            return LuaValue.varargsOf(new LuaValue[]{LuaValue.FALSE});
        }
    }

    static boolean executeScript(@Nonnull LuaValue function, LuaValue... values) {
        Varargs ret = executeScriptVarargs(function, values);
        return ret.arg1().isboolean() && ret.arg1().toboolean();
    }
}
