items.add("rbc/test_pick", { "First line", "Second line" }, nil, { ToolLevel("rockbottom/pickaxe", 2) }, 2.5, function(x, y, layer, mouseX, mouseY, player, instance)
    if layer == "rockbottom/main" then
        world.destroyTile(x, y, "rockbottom/main");
        if not instance.set then
            print("Created set")
            instance.set = DataSet(data.createModBased())
        end
        chat.sendMessageTo(player, ChatComponentText(FormattingCode.GREEN..FormattingCode.BOLD) .. ChatComponentTranslation("rbc/pickaxe_break", tostring(instance.set:getInt("rbc/breakCount") + 1)) .. ChatComponentText(FormattingCode.RESET_COLOR..FormattingCode.RESET_PROPS.." piece of unformatted text " .. FormattingCode.PINK .. FormattingCode.UNDERLINED .. "this" .. FormattingCode.RESET_PROPS .. " is a piece of fixed text"))
        instance.set:addInt("rbc/breakCount", instance.set:getInt("rbc/breakCount") + 1)
        inventory.set(entities.getInv(player), entities.getSelectedSlot(player), instance)
    end
    return true;
end)
recipes.add("manual", "rbc/test_pick_recipe", ItemInstance("rbc/test_pick"), 0, ResUseInfo("soil", 4), ItemInstance("rockbottom/brittle_pickaxe"), ItemInstance("rockbottom/brittle_pickaxe"), ItemInstance("rockbottom/brittle_pickaxe"))
