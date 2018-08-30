package alexanders.mods.rbcustomize.questing;

import alexanders.mods.rbcustomize.RBCustomize;
import alexanders.mods.rbcustomize.questing.Quest.TriggerType;
import alexanders.mods.rbcustomize.questing.QuestBook.Chapter;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentFancyButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentFancyToggleButton;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class ChapterGui extends Gui {
    private static final ResourceName CHAPTER_GUI_RESOURCE = RBCustomize.createRes("chapter_gui");
    private static final ResourceName CHAPTER_QUEST_ADD_RESOURCE = RBCustomize.createRes("chapter_quest_add");
    private static final ResourceName CHAPTER_QUEST_DELETE_RESOURCE = RBCustomize.createRes("chapter_quest_delete");

    private final AbstractEntityPlayer player;
    private final Chapter chapter;

    private boolean isMoving = false;
    private Quest selected = null;

    public ChapterGui(QuestBookGui parent, Chapter chapter, AbstractEntityPlayer player) {
        super(200, 150, parent);
        this.player = player;
        this.chapter = chapter;
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);
        components.add(new ComponentFancyButton(this, 2, 120, 10, 10, this::addQuest, CHAPTER_QUEST_ADD_RESOURCE, "Add quest").setHasBackground(false));
        components.add(new ComponentFancyToggleButton(this, 2, 132, 10, 10, false, this::toggleMoving, QuestBookGui.QUEST_BOOK_EDIT_CHAPTER_RESOURCE, "Toggle quest moving").setHasBackground(false));
    }

    private boolean toggleMoving() {
        isMoving = !isMoving;
        return true;
    }

    private boolean addQuest() {
        RBCustomize.questBook.chapters.get(chapter).add(new Quest("Untitled", "Description here", TriggerType.CUSTOM, null));
        return true;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        manager.getTexture(QuestBookGui.QUEST_BOOK_BACKGROUND_RESOURCE).draw(x, y, width, height);
        for (Quest quest : RBCustomize.questBook.chapters.get(chapter)) {
            g.renderItemInGui(game, manager, new ItemInstance(quest.itemIcon == null ? GameContent.ITEM_BRITTLE_PICKAXE : Registries.ITEM_REGISTRY.get(quest.itemIcon)), quest.x,
                              quest.y, 1f, Colors.WHITE);
        }
        super.render(game, manager, g);

        manager.getFont().drawAutoScaledString(x + 100, y + 3, chapter.name, 1f, 100, Colors.BLACK, Colors.NO_COLOR, true, false);
    }

    @Override
    public void renderOverlay(IGameInstance game, IAssetManager manager, IRenderer g) {
        float x = g.getMouseInGuiX();
        float y = g.getMouseInGuiY();
        super.renderOverlay(game, manager, g);
        for (Quest quest : RBCustomize.questBook.chapters.get(chapter)) {
            if (x>= quest.x && x <= quest.x + 12 && y >= quest.y && y <= quest.y + 12) {
                g.drawHoverInfoAtMouse(game, manager, false, 100, quest.name);
            }
        }
    }

    @Override
    public boolean onMouseAction(IGameInstance game, int button, float x, float y) {
        if (super.onMouseAction(game, button, x, y)) return true;
        for (Quest quest : RBCustomize.questBook.chapters.get(chapter)) {
            if (x >= quest.x && x <= quest.x + 12 && y >= quest.y && y <= quest.y + 12) {
                if (isMoving) {
                    if (selected == null) {
                        selected = quest;
                    } else {
                        selected = null;
                    }
                } else {
                    game.getGuiManager().openGui(new QuestGui(this, chapter, quest));
                }
                return true;
            }
        }
        if (isMoving && selected != null) {
            selected.x = x;
            selected.y = y;
        }

        return false;
    }

    @Override
    public ResourceName getName() {
        return CHAPTER_GUI_RESOURCE;
    }
}
