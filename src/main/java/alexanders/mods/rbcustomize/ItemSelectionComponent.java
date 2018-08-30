package alexanders.mods.rbcustomize;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemSelectionComponent extends GuiComponent {
    private static final ResourceName ITEM_SELECTION_RESOURCE = RBCustomize.createRes("item_selection_component");

    private final ArrayList<Item> options = new ArrayList<>();
    private boolean activated = false;

    public ItemSelectionComponent(Gui gui, int x, int y, int width, int height, boolean onlyItemTiles) {
        super(gui, x, y, width, height);

        if (onlyItemTiles) {
            options.addAll(Registries.TILE_REGISTRY.values().stream().map(Tile::getItem).filter(Objects::nonNull).collect(Collectors.toList()));
        }else {
            options.addAll(Registries.ITEM_REGISTRY.values());
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, int x, int y) {
        super.render(game, manager, g, x, y);
        if(activated) {
            g.addFilledRect(x - width * 2.5f, y+height, width * 5, height * 3, game.getSettings().guiColor);
        }
    }

    @Override
    public boolean onMouseAction(IGameInstance game, int button, float x, float y) {
        if(Settings.KEY_GUI_ACTION_1.isKey(button)) {
            if (activated) {
                int mouseX = (int) game.getRenderer().getMouseInGuiX();
                int mouseY = (int) game.getRenderer().getMouseInGuiY();

                int renderX = this.getRenderX();
                int renderY = this.getRenderY();
                if(mouseX >= renderX - width * 2.5f && mouseX < renderX + width*2.5f && mouseY >= renderY+height && mouseY < renderY + height * 4) {
                    return true;
                }else {
                    activated = false;
                    return true;
                }
            } else if (isMouseOver(game)) {
                activated = true;
                return true;
            }
        }
        return super.onMouseAction(game, button, x, y);
    }

    @Override
    public ResourceName getName() {
        return ITEM_SELECTION_RESOURCE;
    }
}
