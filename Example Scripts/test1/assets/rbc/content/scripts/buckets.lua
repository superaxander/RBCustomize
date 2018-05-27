items.add("rbc/empty_bucket", nil, 1, nil, nil, function(x, y, layer, mouseX, mouseY, player, instance)
    if layer == "rockbottom/liquids" then
        local currentState = world.getState(x, y, layer);
        local selected = entities.getSelectedSlot(player)
        local inv = entities.getInv(player)
        if currentState:sub(1, 16) == "rockbottom/water" then
            local level = tonumber(currentState:sub(24));
            if level - 1 < 0 then
                world.setState(x, y, layer, "rockbottom/air")
            else
                world.setState(x, y, layer, "rockbottom/water;level@" .. (level - 1));
            end
            inventory.set(inv, selected, ItemInstance("rbc/water_bucket", 1))
            return true
        elseif currentState == "rockbottom/remains_goo" then
            world.setState(x, y, layer, "rockbottom/air")
            inventory.set(inv, selected, ItemInstance("rbc/goo_bucket", 1))
            return true
        end
        return false
    else
        return false
    end
end)

items.add("rbc/water_bucket", nil, 1, nil, nil, function(x, y, layer, mouseX, mouseY, player, instance)
    if layer == "rockbottom/liquids" then
        local currentState = world.getState(x, y, layer);
        local currentStateMain = world.getState(x, y, "rockbottom/main");
        local selected = entities.getSelectedSlot(player)
        local inv = entities.getInv(player)
        if currentState == "rockbottom/air" and currentStateMain == "rockbottom/air" then
            inventory.set(inv, selected, ItemInstance("rbc/empty_bucket", 1))
            world.setState(x, y, layer, "rockbottom/water;level@0")
            return true
        end
        return false
    else
        return false
    end
end)

items.add("rbc/goo_bucket", nil, 1, nil, nil, function(x, y, layer, mouseX, mouseY, player, instance)
    if layer == "rockbottom/liquids" then
        local currentState = world.getState(x, y, layer);
        local currentStateMain = world.getState(x, y, "rockbottom/main");
        local selected = entities.getSelectedSlot(player)
        local inv = entities.getInv(player)
        if currentState == "rockbottom/air" and currentStateMain == "rockbottom/air" then
            inventory.set(inv, selected, ItemInstance("rbc/empty_bucket", 1))
            world.setState(x, y, layer, "rockbottom/remains_goo")
            return true
        end
        return false
    else
        return false
    end
end)
