tiles.add("rbc/stone_steps", "This is a stone what more do you want to know", function(tile)
    tile.props = {
        BoolProp("flipped", false)
    }
    tile.bbs = {
        BoundBox(0, 0, 0.5, 1), BoundBox(0.5, 0, 1, 0.5)
    }
    tile.canClimb = true
    function tile.getPlacementState(x, y, layer, instance, placer)
        if entity.getFacing(placer).x > 0 then
            return states.setBooleanValue(tiles.getDefaultState("rbc/stone_steps"), "flipped", true);
        else
            return tiles.getDefaultState("rbc/stone_steps");
        end
    end

    function tile.render(tile, state, x, y, layer, renderX, renderY, scale, light)
        local mirror = states.getBooleanValue(state, "flipped")
        if mirror then
            renderer.mirror(true, false)
        end
        
        Texture(assetManager.getTexture("rbc/tiles.stone_steps")):draw(renderX, renderY, scale, scale, light)
        
        if mirror then
            renderer.mirror(true, false)
        end
        return false
    end
end)
