local function angle()
    local radians = math.atan2(input.getMouseY() - (game.getHeight() / 2.0), input.getMouseX() - (game.getWidth() / 2.0));
    return math.cos(radians), math.sin(radians)
end

items.add("rbc/slimesling", nil, 1, nil, nil, function(x, y, layer, mouseX, mouseY, player, instance)
    local additionalX, additionalY = angle()
    if entities.isOnGround(player) then
        entities.setMotionX(player, -3 * additionalX);
        entities.setMotionY(player, additionalY);
    end
end)

