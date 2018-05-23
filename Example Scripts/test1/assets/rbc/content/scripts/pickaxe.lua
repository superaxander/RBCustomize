items.add("rbc/test_pick", { "First line", "Second line" }, nil, { ToolLevel("PICKAXE", 2) }, 2.5, function(x, y, layer, mouseX, mouseY, player, instance)
    if layer == "rockbottom/main" then
        world.destroyTile(x, y, "rockbottom/main");
        if not instance.set then
            print("Created set")
            instance.set = DataSet(data.createModBased())
        end
        print("adding int: " .. (instance.set:getInt("rbc/breakCount") + 1))
        instance.set:addInt("rbc/breakCount", instance.set:getInt("rbc/breakCount") + 1)
        inventory.set(entity.getInv(player), entity.getSelectedSlot(player), instance)
    end
    return true;
end)
recipes.add("manual", "rbc/test_pick_recipe", ItemInstance("rbc/test_pick"), ResUseInfo("soil", 4), ItemInstance("rockbottom/brittle_pickaxe"), ItemInstance("rockbottom/brittle_pickaxe"), ItemInstance("rockbottom/brittle_pickaxe"))
