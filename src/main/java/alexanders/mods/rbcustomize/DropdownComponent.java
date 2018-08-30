package alexanders.mods.rbcustomize;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.Objects;
import java.util.function.Consumer;

public class DropdownComponent extends GuiComponent {
    private static final ResourceName DROPDOWN_COMPONENT_RESOURCE = RBCustomize.createRes("dropdown_component");
    private static final ResourceName DROPDOWN_ICON_RESOURCE = RBCustomize.createRes("dropdown_icon");
    private final Object[] options;
    private final Consumer consumer;
    private Object currentOption;
    private boolean isActived = false;

    public <E extends Enum<E>> DropdownComponent(Gui gui, int x, int y, int width, int height, Consumer consumer, Enum<E> defaultOption, Class<Enum<E>> clazz, boolean allowNull) {
        this(gui, x, y, width, height, consumer, defaultOption, clazz.getEnumConstants(), null);
    }

    public <E extends Enum<E>> DropdownComponent(Gui gui, int x, int y, int width, int height, Consumer consumer, Enum<E> defaultOption) {
        this(gui, x, y, width, height, consumer, defaultOption, defaultOption.getDeclaringClass().getEnumConstants());
    }

    @SafeVarargs
    public <T> DropdownComponent(Gui gui, int x, int y, int width, int height, Consumer consumer, T defaultOption, T... options) {
        super(gui, x, y, width, height);
        this.options = options;
        this.consumer = consumer;
        currentOption = defaultOption;
        for (T option : options) if (option == defaultOption) return;
        throw new IllegalArgumentException("The default option must appear in the list of options");
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, int x, int y) {
        manager.getFont().drawAutoScaledString(x, y + height / 2f, Objects.toString(currentOption), 1f, width - 10, Colors.WHITE, Colors.BLACK, false, true);
        manager.getTexture(DROPDOWN_ICON_RESOURCE).draw(x + width - 10, y + height / 2f - 5, 10, 10);
    }

    @Override
    public void renderOverlay(IGameInstance game, IAssetManager manager, IRenderer g, int x, int y) {
        super.renderOverlay(game, manager, g, x, y);
        if (isActived) {
            g.addFilledRect(x, y + height, width, options.length * height, game.getSettings().guiColor);
            for (int i = 0; i < options.length; i++) {
                Object option = options[i];
                manager.getFont().drawAutoScaledString(x, y + height * (i + 1) + height / 2f, option.toString(), 1f, width - 10, Colors.WHITE, Colors.BLACK, false, true);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onMouseAction(IGameInstance game, int button, float x, float y) {
        if (Settings.KEY_GUI_ACTION_1.isKey(button)) {
            if (isActived) {
                int mouseX = (int) game.getRenderer().getMouseInGuiX();
                int mouseY = (int) game.getRenderer().getMouseInGuiY();

                int renderX = this.getRenderX();
                int renderY = this.getRenderY();
                if (mouseX >= renderX && mouseX < renderX + this.width && mouseY >= renderY && mouseY < renderY + this.height * (options.length+1)) {
                    float percentage = (renderY + this.height * options.length - mouseY) / (float) (this.height * options.length);
                    int option = de.ellpeck.rockbottom.api.util.Util.floor((1 - percentage) * (options.length));
                    if (option == 0) {
                        isActived = false;
                    } else {
                        currentOption = options[option - 1];
                        consumer.accept(currentOption); // Unsafe
                    }
                    isActived = false;
                }
            } else return isActived = this.isMouseOver(game);
        }
        return false;
    }

    @Override
    public ResourceName getName() {
        return DROPDOWN_COMPONENT_RESOURCE;
    }
}
