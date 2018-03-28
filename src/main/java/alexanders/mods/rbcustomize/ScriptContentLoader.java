package alexanders.mods.rbcustomize;

import alexanders.mods.rbcustomize.lua.LuaEnvironment;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.content.IContentLoader;
import de.ellpeck.rockbottom.api.content.pack.ContentPack;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class ScriptContentLoader implements IContentLoader<Script> {
    public static HashMap<IResourceName, Script> loadedScripts = new HashMap<>();
    public static Script internalScript;
    private static ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    @Override
    public IResourceName getContentIdentifier() {
        return Script.ID;
    }

    @Override
    public void loadContent(IGameInstance game, IResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod, ContentPack pack) throws Exception {
        String loc;
        HookType hookType;
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            JsonElement pathElement = obj.get("path");
            if (pathElement != null && pathElement.isJsonPrimitive()) {
                loc = pathElement.getAsString();
            } else {
                RBCustomize.logger
                        .warning("Script with name: " + resourceName + " could not be loaded for content pack: " + pack.getName() + " because it's path could not be interpreted.");
                return;
            }
            JsonElement hookTypeElement = obj.get("hookType");
            if (hookTypeElement != null && hookTypeElement.isJsonPrimitive()) {
                try {
                    hookType = HookType.valueOf(hookTypeElement.getAsString().toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    RBCustomize.logger.warning("Script with name: " + resourceName + " could not be loaded for content pack: " + pack
                            .getName() + " because it's hookType was not recognized. Possible values are: " + Arrays.toString(HookType.values()));
                    return;
                }
            } else {
                RBCustomize.logger.warning(
                        "Script with name: " + resourceName + " could not be loaded for content pack: " + pack.getName() + " because it's hookType could not be interpreted");
                return;
            }
        } else {
            RBCustomize.logger.warning("Script with name: " + resourceName + " could not be loaded for content pack: " + pack
                    .getName() + " because we could not interpret the script's path and hookType");
            return;
        }

        String location = path + loc;
        if (loadedScripts.containsKey(resourceName)) {
            RBCustomize.logger.config("Script with name:" + resourceName + " already exists, not loading script for content pack: " + pack.getName());
        } else {
            if (pack.getId().equals(ContentPack.DEFAULT_PACK_ID)) internalScript = new Script(LuaEnvironment.globals.load(classLoader.getResourceAsStream(location),
                                                                                                                          resourceName.getDomain() + "_" + resourceName
                                                                                                                                  .getResourceName(), "t", LuaEnvironment.globals),
                                                                                              hookType);
            else loadedScripts.put(resourceName, new Script(LuaEnvironment.globals.load(classLoader.getResourceAsStream(location),
                                                                                        resourceName.getDomain() + "_" + resourceName.getResourceName(), "t",
                                                                                        LuaEnvironment.globals), hookType));
            RBCustomize.logger.config("Script with name:" + resourceName + " was loaded for content pack: " + pack.getName());
        }
    }

    @Override
    public void disableContent(IGameInstance game, IResourceName resourceName) {
        throw new UnsupportedOperationException("Disabling scripts is not yet possible");
    }
}
