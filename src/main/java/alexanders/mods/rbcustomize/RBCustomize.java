package alexanders.mods.rbcustomize;

import alexanders.mods.rbcustomize.lua.LuaEnvironment;
import de.ellpeck.rockbottom.api.IApiHandler;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.event.IEventHandler;
import de.ellpeck.rockbottom.api.event.impl.WorldLoadEvent;
import de.ellpeck.rockbottom.api.event.impl.WorldTickEvent;
import de.ellpeck.rockbottom.api.event.impl.WorldUnloadEvent;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

public class RBCustomize implements IMod {
    public static RBCustomize instance;
    public static Logger logger;

    public RBCustomize() {
        instance = this;
    }

    public static IResourceName createRes(String resource) {
        return RockBottomAPI.createRes(instance, resource);
    }

    @Override
    public String getDisplayName() {
        return "RBCustomize";
    }

    @Override
    public String getId() {
        return "rbc";
    }

    @Override
    public String getVersion() {
        return "0.0.1";
    }

    @Override
    public String getResourceLocation() {
        return "assets/" + getId();
    }

    @Override
    public String getContentLocation() {
        return "assets/" + getId() + "/content";
    }

    @Override
    public String getDescription() {
        return "Add anything you want to RB using lua scripts!";
    }

    @Override
    public String[] getAuthors() {
        return new String[]{"Alexander Stekelenburg"};
    }

    @Override
    public void init(IGameInstance game, IApiHandler apiHandler, IEventHandler eventHandler) {
        logger = RockBottomAPI.createLogger("RBCustomize");
        LuaEnvironment.init(game);
        RockBottomAPI.CONTENT_LOADER_REGISTRY.register(Script.ID, new ScriptContentLoader());
    }

    @Override
    public void postInit(IGameInstance game, IApiHandler apiHandler, IEventHandler eventHandler) {
        LuaEnvironment.initExecution(game);
        LuaEnvironment.executeScripts(HookType.INIT);

        if (ScriptContentLoader.loadedScripts.values().stream().anyMatch(it -> it.hookType == HookType.WORLD_TICK)) {
            eventHandler.registerListener(WorldTickEvent.class, LuaEnvironment::onWorldTick);
        }

        if (ScriptContentLoader.loadedScripts.values().stream().anyMatch(it -> it.hookType == HookType.WORLD_LOAD)) {
            eventHandler.registerListener(WorldLoadEvent.class, LuaEnvironment::onWorldLoad);
        }

        if (ScriptContentLoader.loadedScripts.values().stream().anyMatch(it -> it.hookType == HookType.WORLD_UNLOAD)) {
            eventHandler.registerListener(WorldUnloadEvent.class, LuaEnvironment::onWorldUnload);
        }
    }
}
