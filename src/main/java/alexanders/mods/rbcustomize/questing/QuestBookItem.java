package alexanders.mods.rbcustomize.questing;

import alexanders.mods.rbcustomize.RBCustomize;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemBasic;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class QuestBookItem extends ItemBasic {
    public QuestBookItem() {
        super(RBCustomize.createRes("quest_book"));
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player, ItemInstance instance) {
        player.openGui(new QuestBookGui(player));
        return true;
    }
}
