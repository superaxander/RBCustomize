package alexanders.mods.rbcustomize.questing;

import alexanders.mods.rbcustomize.DropdownComponent;
import alexanders.mods.rbcustomize.ItemSelectionComponent;
import alexanders.mods.rbcustomize.RBCustomize;
import alexanders.mods.rbcustomize.questing.Quest.TriggerType;
import alexanders.mods.rbcustomize.questing.QuestBook.Chapter;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentFancyButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentFancyToggleButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentInputField;
import de.ellpeck.rockbottom.api.gui.component.ComponentText;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class QuestGui extends Gui {
    private static final ResourceName QUEST_GUI_RESOURCE = RBCustomize.createRes("quest_gui");
    private static final ResourceName CHAPTER_QUEST_DELETE_RESOURCE = RBCustomize.createRes("chapter_quest_delete");
    private final Chapter chapter;
    private final Quest quest;
    private boolean editing = false;

    public QuestGui(Gui parent, Chapter chapter, Quest quest) {
        super(200, 150, parent);
        this.chapter = chapter;
        this.quest = quest;
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);
        components.add(new ComponentText(this, 13, 3, 78, 16, .5f, false, quest.name));
        components.add(new ComponentText(this, 13, -10, 78, 80, .3f, false, quest.description));
        components.add(new ComponentText(this, 13, 50, 78, 16, .3f, false, quest.triggerType.toString()));
        components.add(new ComponentFancyButton(this, 2, 120, 10, 10, this::deleteQuest, CHAPTER_QUEST_DELETE_RESOURCE, "Delete quest"));
        components.add(new ComponentFancyToggleButton(this, 2, 132, 10, 10, false, this::toggleEdit, QuestBookGui.QUEST_BOOK_EDIT_CHAPTER_RESOURCE, "Edit title and description"));
    }

    private boolean deleteQuest() {
        RBCustomize.questBook.chapters.get(chapter).remove(quest);
        RockBottomAPI.getGame().getGuiManager().openGui(parent);
        return true;
    }

    private boolean toggleEdit() {
        if (editing) {
            components.set(0, new ComponentText(this, 13, 3, 78, 16, .5f, false, quest.name));
            components.set(1, new ComponentText(this, 13, 22, 78, 16, .3f, false, quest.description));
            components.set(2, new ComponentText(this, 13, 50, 78, 16, .3f, false, quest.triggerType.toString()));
            components.remove(components.size()-1);
        } else {
            ComponentInputField name = new ComponentInputField(this, 13, 3, 78, 16, false, true, true, 100, false, this::onNameChange);
            name.setText(quest.name);
            ComponentInputField description = new ComponentInputField(this, 13, 22, 78, 16, false, true, false, 100, false, this::onDescriptionChange);
            description.setText(quest.description);
            components.set(0, name);
            components.set(1, description);
            components.set(2, new DropdownComponent(this, 13, 50, 8, 16, this::onTriggerTypeChange, quest.triggerType));
//            components.add()
        }

        editing = !editing;
        return true;
    }

    private void onTriggerTypeChange(Object triggerType) {
        if(!(triggerType instanceof TriggerType)) throw new IllegalStateException("This is a bug!!");
        
        quest.triggerType = (TriggerType) triggerType;
    }

    private void onNameChange(String name) {
        quest.name = name;
    }

    private void onDescriptionChange(String description) {
        quest.description = description;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        manager.getTexture(QuestBookGui.QUEST_BOOK_BACKGROUND_RESOURCE).draw(x, y, width, height);
        super.render(game, manager, g);
    }

    @Override
    public ResourceName getName() {
        return QUEST_GUI_RESOURCE;
    }
}
