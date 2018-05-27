if input.wasKeyPressed(input.keys.APOSTROPHE) then
    local player = game.getPlayer()
    local selectedSlot = entitiesgetSelectedSlot(player)
    local inv = entities.getInv(player)
    local instance = inventory.get(inv, selectedSlot)
    if instance then
        if instance.set then
            if instance.set:hasKey("rbc/breakCount") then
                print(instance.set:getInt("rbc/breakCount"))
            end
        end
    end
end

