package alexanders.mods.rbcustomize.questing;

import alexanders.mods.rbcustomize.RBCustomize;
import alexanders.mods.rbcustomize.questing.QuestBook.Chapter;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.*;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.ArrayList;

public class QuestBookGui extends Gui {
    public static final ResourceName QUEST_BOOK_BACKGROUND_RESOURCE = RBCustomize.createRes("quest_book_background");
    private static final ResourceName QUEST_BOOK_GUI_RESOURCE = RBCustomize.createRes("quest_book_gui");
    private static final ResourceName QUEST_BOOK_ADD_CHAPTER_RESOURCE = RBCustomize.createRes("quest_book_add_chapter");
    public static final ResourceName QUEST_BOOK_EDIT_CHAPTER_RESOURCE = RBCustomize.createRes("quest_book_edit_chapter");
    private static final ResourceName QUEST_BOOK_DELETE_CHAPTER_RESOURCE = RBCustomize.createRes("quest_book_delete_chapter");

    private final AbstractEntityPlayer player;
    private ComponentMenu menu = null;
    private ArrayList<GuiComponent> chapterComponents = new ArrayList<>();
    private Chapter currentChapter;
    private boolean isEditingChapter = false;

    public QuestBookGui(AbstractEntityPlayer player) {
        super(200, 150);
        this.player = player;
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        menu = new ComponentMenu(this, 13, 23, 10, 123, 1, 7, 0, 5, new BoundBox(x + 13, y + 23, x + 89, y + 145), null);
        components.add(menu);
        components.add(new ComponentFancyButton(this, 2, 132, 10, 10, this::addChapter, QUEST_BOOK_ADD_CHAPTER_RESOURCE, "Add a chapter").setHasBackground(false));
    }

    private boolean addChapter() {
        RBCustomize.questBook.chapters.put(new Chapter("Untitled", "Description here"), new ArrayList<>());
        components.removeAll(chapterComponents);
        chapterComponents.clear();
        isEditingChapter = false;
        return true;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        manager.getTexture(QUEST_BOOK_BACKGROUND_RESOURCE).draw(x, y, width, height);
        menu.clear();
        for (Chapter chapter : RBCustomize.questBook.chapters.keySet()) {
            menu.add(new MenuComponent(77, 15).add(x, y, new ComponentClickableText(null, 0, 0, .35f, false, () -> onClick(chapter), chapter.name)));
        }
        menu.organize();
        super.render(game, manager, g);
        manager.getFont().drawAutoScaledString(x + 13, y + 3, "Chapters", 1f, 78, Colors.BLACK, Colors.NO_COLOR, false, false);
    }

    private boolean onClick(Chapter chapter) {
        components.removeAll(chapterComponents);
        chapterComponents.clear();
        isEditingChapter = false;
        currentChapter = chapter;
        chapterComponents.add(new ComponentText(this, 113, 5, 78, 16, .5f, false, chapter.name));
        chapterComponents.add(new ComponentText(this, 113, 25, 78, 48, .3f, false, chapter.description));
        chapterComponents.add(new ComponentFancyToggleButton(this, 188, 120, 10, 10, false, this::toggleChapterEdit, QUEST_BOOK_EDIT_CHAPTER_RESOURCE, "Edit chapter name and description").setHasBackground(false));
        chapterComponents.add(new ComponentFancyButton(this, 188, 132, 10, 10, this::deleteChapter, QUEST_BOOK_DELETE_CHAPTER_RESOURCE, "Delete chapter").setHasBackground(false));
        chapterComponents.add(new ComponentButton(this, 113, 132, 40, 10, this::openChapter, "Open chapter", "View this chapter's chapters"));
        components.addAll(chapterComponents);
        return true;
    }

    private boolean deleteChapter() {
        components.removeAll(chapterComponents);
        RBCustomize.questBook.chapters.remove(currentChapter);
        return true;
    }

    private boolean openChapter() {
        RockBottomAPI.getGame().getGuiManager().openGui(new ChapterGui(this, currentChapter, player));
        return true;
    }

    private boolean toggleChapterEdit() {
        components.removeAll(chapterComponents);
        if (isEditingChapter) {
            chapterComponents.set(0, new ComponentText(this, 113, 5, 78, 16, .5f, false, currentChapter.name));
            chapterComponents.set(1, new ComponentText(this, 113, 25, 78, 48, .3f, false, currentChapter.description));
        } else {
            ComponentInputField name = new ComponentInputField(this, 113, 3, 78, 16, false, true, true, 100, false, this::chapterNameChange);
            name.setText(currentChapter.name);
            ComponentInputField description = new ComponentInputField(this, 113, 19, 78, 48, false, true, false, 200, false, this::chapterDescriptionChange);
            description.setText(currentChapter.description);
            chapterComponents.set(0, name);
            chapterComponents.set(1, description);
        }
        components.addAll(chapterComponents);

        isEditingChapter = !isEditingChapter;
        return true;
    }

    private void chapterNameChange(String newName) {
        currentChapter.name = newName;
    }

    private void chapterDescriptionChange(String newDescription) {
        currentChapter.description = newDescription;
    }

    @Override
    public ResourceName getName() {
        return QUEST_BOOK_GUI_RESOURCE;
    }
}
