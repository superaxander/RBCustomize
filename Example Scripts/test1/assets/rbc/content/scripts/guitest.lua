gui.add("rbc/test_gui", function(g)
    function g:init()
        local textComponent = ComponentText(self, 0, 20, 100, 50, .25, false, "Lorem ipsum dolor sit amet or something like that")
        local inputField = ComponentInputField(self, 0, 60, 100, 20, true, true, true, 250, false, function(string)
            textComponent:removeFromComponentList()
            textComponent = ComponentText(self, 0, 20, 100, 50, .25, false, string)
            textComponent:addToComponentList()
        end)
        ComponentButton(self, 0, 0, 100, 20, function()
            inputField:setText("")
            return true
        end, "Clear"):addToComponentList()
        textComponent:addToComponentList()
        inputField:addToComponentList();
        inputField:setText("Lorem ipsum dolor sit amet or something like that")
    end
end)

items.add("rbc/gui_item", nil, 1, nil, nil, function(x, y, layer, mouseX, mouseY, player, instance)
    entities.openGui(player, gui.instantiate("rbc/test_gui", 100, 100))
end)

gui.add("rbc/portable_chest_gui");

local currentInv, currentInstance

containers.add("rbc/portable_chest_container", function (c, player)
    containers.addSlotGrid(c, currentInv, 0, inventory.getSlotAmount(currentInv), 0, 0, 8)
    containers.addPlayerInventory(c, player, 0, 80)
end, nil, function(c, player)
    print("Saving")
    local set = DataSet(data.create())
    inventory.save(currentInv, set.backingData)
    currentInstance:getOrCreateAdditionalData():addDataSet("rbc/inventory", set);
end)

items.add("rbc/portable_chest", nil, 1, nil, nil, function(x, y, layer, mouseX, mouseY, player, instance)
    currentInstance = instance
    currentInv = inventory.instantiate(32)
    local data = instance:getOrCreateAdditionalData()
    if data:hasKey("rbc/inventory") then
        inventory.load(currentInv, data:getDataSet("rbc/inventory").backingData)
    end
    entities.openGuiContainer(player, gui.instantiateContainer("rbc/portable_chest_gui", player, 136, 152), containers.instantiate("rbc/portable_chest_container", player))
end)
