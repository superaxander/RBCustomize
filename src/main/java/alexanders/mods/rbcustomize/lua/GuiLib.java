package alexanders.mods.rbcustomize.lua;

import alexanders.mods.rbcustomize.RBCustomize;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentInputField;
import de.ellpeck.rockbottom.api.gui.component.ComponentText;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.NameRegistry;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

import static alexanders.mods.rbcustomize.Util.nilToNull;

public class GuiLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable gui = new LuaTable();
        gui.set("getX", new FunctionWrapper(this::getX));
        gui.set("getY", new FunctionWrapper(this::getY));
        gui.set("getWidth", new FunctionWrapper(this::getWidth));
        gui.set("getHeight", new FunctionWrapper(this::getHeight));
        gui.set("isMouseOver", new FunctionWrapper(this::isMouseOver));
        gui.set("isMouseOverComponent", new FunctionWrapper(this::isMouseOverComponent));
        gui.set("addComponent", new FunctionWrapper(this::addComponent));
        gui.set("removeComponent", new FunctionWrapper(this::removeComponent));
        gui.set("clearComponents", new FunctionWrapper(this::clearComponents));
        gui.set("add", new FunctionWrapper(this::add));
        gui.set("remove", new FunctionWrapper(this::remove));
        gui.set("instantiate", new FunctionWrapper(this::instantiate));
        gui.set("instantiateContainer", new FunctionWrapper(this::instantiateContainer));
        gui.set("createButton", new FunctionWrapper(this::createButton));
        gui.set("createText", new FunctionWrapper(this::createText));
        gui.set("createInputField", new FunctionWrapper(this::createInputField));
        gui.set("inputFieldIsSelected", new FunctionWrapper(this::inputFieldIsSelected));
        gui.set("inputFieldIsCensored", new FunctionWrapper(this::inputFieldIsCensored));
        gui.set("inputFieldGetText", new FunctionWrapper(this::inputFieldGetText));
        gui.set("inputFieldSetSelected", new FunctionWrapper(this::inputFieldSetSelected));
        gui.set("inputFieldSetCensored", new FunctionWrapper(this::inputFieldSetCensored));
        gui.set("inputFieldSetText", new FunctionWrapper(this::inputFieldSetText));
        gui.set("buttonSetText", new FunctionWrapper(this::buttonSetText));
        gui.set("buttonSetHasBackground", new FunctionWrapper(this::buttonSetHasBackground));
        gui.set("buttonHasBackground", new FunctionWrapper(this::buttonHasBackground));
        gui.set("getOpenGui", new FunctionWrapper(this::getOpenGui));
        env.set("gui", gui);
        return gui;
    }

    private Varargs instantiateContainer(Varargs varargs) { // name, player, sizeX, sizeY
        String sName = varargs.checkjstring(1);
        if (!Util.isResourceName(sName)) return argerror(1, "Expected a resource name for argument 'name'");
        ResourceName name = new ResourceName(sName);
        LuaGuiData data = LuaGui.GUI_REGISTRY.get(name);
        if (data == null) return argerror("The specified gui could not be found");

        Entity e = EntitiesLib.parseUUID(varargs, 2);
        if (e instanceof AbstractEntityPlayer) {
            int sizeX = varargs.checkint(3);
            int sizeY = varargs.checkint(4);
            return userdataOf(new LuaGuiContainer((AbstractEntityPlayer) e, sizeX, sizeY, name));
        } else {
            return argerror(2, "Expected a player's uuid");
        }
    }

    private Varargs buttonSetText(Varargs varargs) {
        ComponentButton button = (ComponentButton) varargs.checkuserdata(1, ComponentButton.class);
        String text = varargs.checkjstring(2);
        button.setText(text);
        return NIL;
    }

    private Varargs buttonSetHasBackground(Varargs varargs) {
        ComponentButton button = (ComponentButton) varargs.checkuserdata(1, ComponentButton.class);
        boolean hasBackground = varargs.checkboolean(2);
        button.setHasBackground(hasBackground);
        return NIL;
    }

    private Varargs buttonHasBackground(Varargs varargs) {
        ComponentButton button = (ComponentButton) varargs.checkuserdata(1, ComponentButton.class);
        return valueOf(button.hasBackground);
    }

    private Varargs createInputField(Varargs varargs) {
        Gui gui = (Gui) varargs.checkuserdata(1, Gui.class);
        int x = varargs.checkint(2);
        int y = varargs.checkint(3);
        int sizeX = varargs.checkint(4);
        int sizeY = varargs.checkint(5);
        boolean renderBox = varargs.checkboolean(6);
        boolean selectable = varargs.checkboolean(7);
        boolean defaultActive = varargs.checkboolean(8);
        int maxLength = varargs.checkint(9);
        boolean displayMaxLength = varargs.checkboolean(10);
        LuaValue consumer = nilToNull(varargs.arg(11));
        if (consumer == null) {
            return userdataOf(new ComponentInputField(gui, x, y, sizeX, sizeY, renderBox, selectable, defaultActive, maxLength, displayMaxLength));
        } else {
            return userdataOf(new ComponentInputField(gui, x, y, sizeX, sizeY, renderBox, selectable, defaultActive, maxLength, displayMaxLength,
                                                      in -> LuaEnvironment.executeScript(consumer, valueOf(in))));
        }
    }

    private Varargs inputFieldIsSelected(Varargs varargs) {
        ComponentInputField field = (ComponentInputField) varargs.checkuserdata(1, ComponentInputField.class);
        return valueOf(field.isSelected());
    }

    private Varargs inputFieldIsCensored(Varargs varargs) {
        ComponentInputField field = (ComponentInputField) varargs.checkuserdata(1, ComponentInputField.class);
        return valueOf(field.isCensored());
    }

    private Varargs inputFieldGetText(Varargs varargs) {
        ComponentInputField field = (ComponentInputField) varargs.checkuserdata(1, ComponentInputField.class);
        return valueOf(field.getText());
    }

    private Varargs inputFieldSetSelected(Varargs varargs) {
        ComponentInputField field = (ComponentInputField) varargs.checkuserdata(1, ComponentInputField.class);
        boolean selected = varargs.checkboolean(2);
        field.setSelected(selected);
        return NIL;
    }

    private Varargs inputFieldSetCensored(Varargs varargs) {
        ComponentInputField field = (ComponentInputField) varargs.checkuserdata(1, ComponentInputField.class);
        boolean censored = varargs.checkboolean(2);
        field.setCensored(censored);
        return NIL;
    }

    private Varargs inputFieldSetText(Varargs varargs) {
        ComponentInputField field = (ComponentInputField) varargs.checkuserdata(1, ComponentInputField.class);
        String text = varargs.checkjstring(2);
        field.setText(text);
        return NIL;
    }

    private Varargs getOpenGui(Varargs varargs) {
        if (RockBottomAPI.getGame().isDedicatedServer()) return error("This function is not supported on a dedicated server");
        return userdataOf(RockBottomAPI.getGame().getGuiManager().getGui());
    }

    private Varargs createText(Varargs varargs) { // gui x, y, width, height, scale, fromRight, text
        Gui gui = (Gui) varargs.checkuserdata(1, Gui.class);
        int x = varargs.checkint(2);
        int y = varargs.checkint(3);
        int width = varargs.checkint(4);
        int height = varargs.checkint(5);
        float scale = (float) varargs.checkdouble(6);
        boolean fromRight = varargs.checkboolean(7);
        String[] text = null;
        LuaValue lText = varargs.arg(8);
        if (lText.isstring()) text = new String[]{lText.tojstring()};
        else if (lText.istable()) {
            int length = lText.length();
            text = new String[length];
            for (int i = 1; i <= length; i++) {
                text[i] = lText.get(i).checkjstring();
            }
        } else if (!lText.isnil()) return argerror(8, "Expected string, table, or nil for argument 'text'");
        return userdataOf(new ComponentText(gui, x, y, width, height, scale, fromRight, text));
    }

    private Varargs isMouseOver(Varargs varargs) {
        Gui gui = (Gui) varargs.checkuserdata(1, Gui.class);
        return valueOf(gui.isMouseOver(RockBottomAPI.getGame()));
    }

    private Varargs isMouseOverComponent(Varargs varargs) {
        GuiComponent component = (GuiComponent) varargs.checkuserdata(1, GuiComponent.class);
        return valueOf(component.isMouseOver(RockBottomAPI.getGame()));
    }

    private Varargs createButton(Varargs varargs) { // gui, x, y, sizeX, sizeY, supplier, text, hover
        Gui gui = (Gui) varargs.checkuserdata(1, Gui.class);
        int x = varargs.checkint(2);
        int y = varargs.checkint(3);
        int sizeX = varargs.checkint(4);
        int sizeY = varargs.checkint(5);
        LuaValue supplier = varargs.checkfunction(6);
        String text = varargs.checkjstring(7);
        String[] hover = null;
        LuaValue lHover = varargs.arg(8);
        if (lHover.isstring()) hover = new String[]{lHover.tojstring()};
        else if (lHover.istable()) {
            int length = lHover.length();
            hover = new String[length];
            for (int i = 1; i <= length; i++) {
                hover[i] = lHover.get(i).checkjstring();
            }
        } else if (!lHover.isnil()) return argerror(8, "Expected string, table, or nil for argument 'hover'");
        return userdataOf(new ComponentButton(gui, x, y, sizeX, sizeY, new LuaSupplier<>(supplier, Boolean.class), text, hover));
    }

    private Varargs addComponent(Varargs varargs) {
        if (varargs.arg1().isuserdata(LuaGui.class)) {
            LuaGui gui = (LuaGui) varargs.checkuserdata(1, LuaGui.class);
            GuiComponent component = (GuiComponent) varargs.checkuserdata(2, GuiComponent.class);
            gui.addComponent(component);
        } else {
            LuaGuiContainer gui = (LuaGuiContainer) varargs.checkuserdata(1, LuaGuiContainer.class);
            GuiComponent component = (GuiComponent) varargs.checkuserdata(2, GuiComponent.class);
            gui.addComponent(component);
        }
        return NIL;
    }

    private Varargs removeComponent(Varargs varargs) {
        if (varargs.arg1().isuserdata(LuaGui.class)) {
            LuaGui gui = (LuaGui) varargs.checkuserdata(1, LuaGui.class);
            GuiComponent component = (GuiComponent) varargs.checkuserdata(2, GuiComponent.class);
            gui.removeComponent(component);
        } else {
            LuaGuiContainer gui = (LuaGuiContainer) varargs.checkuserdata(1, LuaGuiContainer.class);
            GuiComponent component = (GuiComponent) varargs.checkuserdata(2, GuiComponent.class);
            gui.removeComponent(component);
        }
        return NIL;
    }

    private Varargs clearComponents(Varargs varargs) {
        if (varargs.arg1().isuserdata(LuaGui.class)) {
            LuaGui gui = (LuaGui) varargs.checkuserdata(1, LuaGui.class);
            gui.clearComponents();
        } else {
            LuaGuiContainer gui = (LuaGuiContainer) varargs.checkuserdata(1, LuaGuiContainer.class);
            gui.clearComponents();
        }
        return NIL;
    }

    private Varargs getX(Varargs varargs) {
        Gui gui = (Gui) varargs.checkuserdata(1, Gui.class);
        return valueOf(gui.getX());
    }

    private Varargs getY(Varargs varargs) {
        Gui gui = (Gui) varargs.checkuserdata(1, Gui.class);
        return valueOf(gui.getY());
    }

    private Varargs getWidth(Varargs varargs) {
        Gui gui = (Gui) varargs.checkuserdata(1, Gui.class);
        return valueOf(gui.getWidth());
    }

    private Varargs getHeight(Varargs varargs) {
        Gui gui = (Gui) varargs.checkuserdata(1, Gui.class);
        return valueOf(gui.getHeight());
    }

    private Varargs add(Varargs varargs) { // name, init
        String sName = varargs.checkjstring(1);
        if (!Util.isResourceName(sName)) return argerror(1, "Expected a resource name for argument 'name'");

        boolean doesPauseGame = false;
        boolean canCloseWithInvKey = true;
        LuaValue onOpened = null;
        LuaValue onClosed = null;
        LuaValue init = null;
        LuaValue update = null;
        LuaValue onMouseAction = null;
        LuaValue onKeyPressed = null;
        LuaValue onCharInput = null;
        LuaValue render = null;
        LuaValue renderOverlay = null;
        LuaValue tryEscape = null;

        LuaValue lInit = varargs.arg(2);
        if (lInit.isfunction()) {
            LuaTable lGuiData = new LuaTable();
            lGuiData.set("doesPauseGame", FALSE);
            lGuiData.set("canCloseWithInvKey", FALSE);
            lGuiData.set("onOpened", NIL);
            lGuiData.set("onClosed", NIL);
            lGuiData.set("init", NIL);
            lGuiData.set("update", NIL);
            lGuiData.set("onMouseAction", NIL);
            lGuiData.set("onKeyPressed", NIL);
            lGuiData.set("onCharInput", NIL);
            lGuiData.set("render", NIL);
            lGuiData.set("renderOverlay", NIL);
            lGuiData.set("tryEscape", NIL);
            lInit.invoke(lGuiData);
            doesPauseGame = lGuiData.get("doesPauseGame").checkboolean();
            canCloseWithInvKey = lGuiData.get("canCloseWithInvKey").checkboolean();
            onOpened = nilToNull(lGuiData.get("onOpened"));
            onClosed = nilToNull(lGuiData.get("onClosed"));
            init = nilToNull(lGuiData.get("init"));
            update = nilToNull(lGuiData.get("update"));
            onMouseAction = nilToNull(lGuiData.get("onMouseAction"));
            onKeyPressed = nilToNull(lGuiData.get("onKeyPressed"));
            onCharInput = nilToNull(lGuiData.get("onCharInput"));
            render = nilToNull(lGuiData.get("render"));
            renderOverlay = nilToNull(lGuiData.get("renderOverlay"));
            tryEscape = nilToNull(lGuiData.get("tryEscape"));
        } else if (!lInit.isnil()) return argerror(1, "Expected a function or nil for argument 'init'");
        LuaGui.GUI_REGISTRY.register(new ResourceName(sName),
                                     new LuaGuiData(doesPauseGame, canCloseWithInvKey, onOpened, onClosed, init, update, onMouseAction, onKeyPressed, onCharInput, render,
                                                    renderOverlay, tryEscape));
        return NIL;
    }

    private Varargs remove(Varargs varargs) {
        String sName = varargs.checkjstring(1);
        if (!Util.isResourceName(sName)) return argerror(1, "Expected a resource name for argument 'name'");
        LuaGui.GUI_REGISTRY.unregister(new ResourceName(sName));
        return NIL;
    }

    private Varargs instantiate(Varargs varargs) { // name, width, height, parent
        String sName = varargs.checkjstring(1);
        if (!Util.isResourceName(sName)) return argerror(1, "Expected a resource name for argument 'name'");
        ResourceName name = new ResourceName(sName);
        LuaGuiData data = LuaGui.GUI_REGISTRY.get(name);
        if (data == null) return argerror("The specified gui could not be found");

        int width = -1, height = -1;
        Gui parent = null;

        LuaValue lWidth = varargs.arg(2);
        if (lWidth.isint()) width = lWidth.toint();
        else if (!lWidth.isnil()) return argerror(1, "Expected int or nil for argument 'width'");
        LuaValue lHeight = varargs.arg(3);
        if (lHeight.isint()) height = lHeight.toint();
        else if (!lHeight.isnil()) return argerror(2, "Expected int or nil for argument 'height'");
        LuaValue lParent = varargs.arg(4);
        if (lParent.isuserdata(Gui.class)) parent = (Gui) lParent.touserdata();
        else if (!lParent.isnil()) return argerror(3, "Expected Gui or nil for argument 'parent'");

        return userdataOf(new LuaGui(name, width, height, parent));
    }

    private static final class LuaGuiContainer extends GuiContainer {
        private final ResourceName name;
        private final LuaGuiData data;

        private LuaGuiContainer(AbstractEntityPlayer player, int sizeX, int sizeY, ResourceName name) {
            super(player, sizeX, sizeY);
            this.name = name;
            this.data = LuaGui.GUI_REGISTRY.get(name);
        }

        private void addComponent(GuiComponent component) {
            components.add(component);
        }

        private void removeComponent(GuiComponent component) {
            components.remove(component);
        }

        private void clearComponents() {
            components.clear();
        }

        @Override
        public void onOpened(IGameInstance game) {
            if (data.onOpened != null) {
                LuaEnvironment.executeScript(data.onOpened, userdataOf(this));
            } else {
                super.onOpened(game);
            }
        }

        @Override
        public void onClosed(IGameInstance game) {
            if (data.onClosed != null) {
                LuaEnvironment.executeScript(data.onClosed, userdataOf(this));
            } else {
                super.onClosed(game);
            }
        }

        @Override
        public void init(IGameInstance game) {
            super.init(game);
            if (data.init != null) {
                LuaEnvironment.executeScript(data.init, userdataOf(this));
            }
        }

        @Override
        public void update(IGameInstance game) {
            if (data.update != null) {
                LuaEnvironment.executeScript(data.update, userdataOf(this));
            } else {
                super.update(game);
            }
        }

        @Override
        public boolean onMouseAction(IGameInstance game, int button, float x, float y) {
            if (data.onMouseAction != null) {
                return LuaEnvironment.executeScript(data.onMouseAction, userdataOf(this), valueOf(button), valueOf(x), valueOf(y));
            } else {
                return super.onMouseAction(game, button, x, y);
            }
        }

        @Override
        public boolean onKeyPressed(IGameInstance game, int button) {
            if (data.onKeyPressed != null) {
                return LuaEnvironment.executeScript(data.onKeyPressed, userdataOf(this), valueOf(button));
            } else {
                return super.onKeyPressed(game, button);
            }
        }

        @Override
        public boolean onCharInput(IGameInstance game, int codePoint, char[] characters) {
            if (data.onCharInput != null) {
                return LuaEnvironment.executeScript(data.onCharInput, userdataOf(this), valueOf(codePoint), valueOf(new String(characters)));
            } else {
                return super.onCharInput(game, codePoint, characters);
            }
        }

        @Override
        public boolean canCloseWithInvKey() {
            return data.canCloseWithInvKey;
        }

        @Override
        public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
            if (data.render != null) {
                AssetManagerLib.manager = manager;
                RendererLib.renderer = g;
                if (LuaEnvironment.executeScript(data.render, userdataOf(this))) super.render(game, manager, g);
            } else {
                super.render(game, manager, g);
            }
        }

        @Override
        public void renderOverlay(IGameInstance game, IAssetManager manager, IRenderer g) {
            if (data.renderOverlay != null) {
                AssetManagerLib.manager = manager;
                RendererLib.renderer = g;
                if (LuaEnvironment.executeScript(data.renderOverlay, userdataOf(this))) super.renderOverlay(game, manager, g);
            } else {
                super.renderOverlay(game, manager, g);
            }
        }


        @Override
        protected boolean tryEscape(IGameInstance game) {
            if (data.tryEscape != null) {
                return LuaEnvironment.executeScript(data.tryEscape, userdataOf(this));
            } else {
                return super.tryEscape(game);
            }
        }

        @Override
        public ResourceName getName() {
            return name;
        }
    }

    private static final class LuaGui extends Gui {
        private static final NameRegistry<LuaGuiData> GUI_REGISTRY = new NameRegistry<>(RBCustomize.createRes("LuaGuiRegistry"), true);

        private final ResourceName name;
        private final LuaGuiData data;

        private LuaGui(ResourceName name, int width, int height, Gui parent) {
            super(width, height, parent);
            this.name = name;
            data = GUI_REGISTRY.get(name);
            if (data == null) throw new IllegalArgumentException("Can't instantiate an unregistered gui with the name: " + name);
        }

        private void addComponent(GuiComponent component) {
            components.add(component);
        }

        private void removeComponent(GuiComponent component) {
            components.remove(component);
        }

        private void clearComponents() {
            components.clear();
        }

        @Override
        public void onOpened(IGameInstance game) {
            if (data.onOpened != null) {
                LuaEnvironment.executeScript(data.onOpened, userdataOf(this));
            } else {
                super.onOpened(game);
            }
        }

        @Override
        public void onClosed(IGameInstance game) {
            if (data.onClosed != null) {
                LuaEnvironment.executeScript(data.onClosed, userdataOf(this));
            } else {
                super.onClosed(game);
            }
        }

        @Override
        public void init(IGameInstance game) {
            super.init(game);
            if (data.init != null) {
                LuaEnvironment.executeScript(data.init, userdataOf(this));
            }
        }

        @Override
        public void update(IGameInstance game) {
            if (data.update != null) {
                LuaEnvironment.executeScript(data.update, userdataOf(this));
            } else {
                super.update(game);
            }
        }

        @Override
        public boolean onMouseAction(IGameInstance game, int button, float x, float y) {
            if (data.onMouseAction != null) {
                return LuaEnvironment.executeScript(data.onMouseAction, userdataOf(this), valueOf(button), valueOf(x), valueOf(y));
            } else {
                return super.onMouseAction(game, button, x, y);
            }
        }

        @Override
        public boolean onKeyPressed(IGameInstance game, int button) {
            if (data.onKeyPressed != null) {
                return LuaEnvironment.executeScript(data.onKeyPressed, userdataOf(this), valueOf(button));
            } else {
                return super.onKeyPressed(game, button);
            }
        }

        @Override
        public boolean onCharInput(IGameInstance game, int codePoint, char[] characters) {
            if (data.onCharInput != null) {
                return LuaEnvironment.executeScript(data.onCharInput, userdataOf(this), valueOf(codePoint), valueOf(new String(characters)));
            } else {
                return super.onCharInput(game, codePoint, characters);
            }
        }

        @Override
        public boolean canCloseWithInvKey() {
            return data.canCloseWithInvKey;
        }

        @Override
        public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
            if (data.render != null) {
                AssetManagerLib.manager = manager;
                RendererLib.renderer = g;
                if (LuaEnvironment.executeScript(data.render, userdataOf(this))) super.render(game, manager, g);
            } else {
                super.render(game, manager, g);
            }
        }

        @Override
        public void renderOverlay(IGameInstance game, IAssetManager manager, IRenderer g) {
            if (data.renderOverlay != null) {
                AssetManagerLib.manager = manager;
                RendererLib.renderer = g;
                if (LuaEnvironment.executeScript(data.renderOverlay, userdataOf(this))) super.renderOverlay(game, manager, g);
            } else {
                super.renderOverlay(game, manager, g);
            }
        }

        @Override
        protected boolean tryEscape(IGameInstance game) {
            if (data.tryEscape != null) {
                return LuaEnvironment.executeScript(data.tryEscape, userdataOf(this));
            } else {
                return super.tryEscape(game);
            }
        }

        @Override
        public boolean doesPauseGame() {
            return data.doesPauseGame;
        }

        @Override
        public ResourceName getName() {
            return name;
        }
    }

    private static final class LuaGuiData {
        private final boolean doesPauseGame;
        private final boolean canCloseWithInvKey;
        private final LuaValue onOpened;
        private final LuaValue onClosed;
        private final LuaValue init;
        private final LuaValue update;
        private final LuaValue onMouseAction;
        private final LuaValue onKeyPressed;
        private final LuaValue onCharInput;
        private final LuaValue render;
        private final LuaValue renderOverlay;
        private final LuaValue tryEscape;

        private LuaGuiData(boolean doesPauseGame, boolean canCloseWithInvKey, LuaValue onOpened, LuaValue onClosed, LuaValue init, LuaValue update, LuaValue onMouseAction, LuaValue onKeyPressed, LuaValue onCharInput, LuaValue render, LuaValue renderOverlay, LuaValue tryEscape) {
            this.doesPauseGame = doesPauseGame;
            this.canCloseWithInvKey = canCloseWithInvKey;
            this.onOpened = onOpened;
            this.onClosed = onClosed;
            this.init = init;
            this.update = update;
            this.onMouseAction = onMouseAction;
            this.onKeyPressed = onKeyPressed;
            this.onCharInput = onCharInput;
            this.render = render;
            this.renderOverlay = renderOverlay;
            this.tryEscape = tryEscape;
        }

    }
}
