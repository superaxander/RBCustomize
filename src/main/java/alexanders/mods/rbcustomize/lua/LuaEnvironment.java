package alexanders.mods.rbcustomize.lua;

import alexanders.mods.rbcustomize.HookType;
import alexanders.mods.rbcustomize.RBCustomize;
import alexanders.mods.rbcustomize.ScriptContentLoader;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.WorldLoadEvent;
import de.ellpeck.rockbottom.api.event.impl.WorldTickEvent;
import de.ellpeck.rockbottom.api.event.impl.WorldUnloadEvent;
import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class LuaEnvironment {
    public static Globals globals;

    public static void init(IGameInstance game) {
        globals = new Globals();
        globals.load(new JseBaseLib());
        globals.load(new PackageLib());
        globals.load(new Bit32Lib());
        globals.load(new TableLib());
        globals.load(new StringLib());
        globals.load(new JseMathLib());
        globals.load(new RecipesLib());
        globals.load(new ItemsLib());
        //globals.load(new TilesLib());
        globals.load(new WorldLib());
        globals.load(new EntityLib());
        globals.load(new InventoryLib());
        globals.load(new GameLib(game));
        globals.load(new InputLib(game.getInput()));
        globals.load(new DataLib());
        globals.load(new SandBoxedIoLib());
        LoadState.install(globals);
        LuaC.install(globals);
    }
    
    public static void initExecution() {
        RBCustomize.logger.config("Executing initialization script");
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
}
