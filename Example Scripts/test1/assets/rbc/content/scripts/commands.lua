commands.add("rbc/tileInfo", "Returns information about the tile. Usage: tileInfo <x> <y>", 1, nil, function(args, sender, playerName)
    if #args < 2 then
        return FormattingCode.RED .. ChatComponentTranslation("rbc/tileInfoUsage")
    end
    local x = tonumber(args[1])
    local y = tonumber(args[2])
    if x == nil or y == nil then
        return FormattingCode.RED .. ChatComponentTranslation("rbc/tileInfoUsage")
    end
    if not math.modf(x) == 0 or math.modf(y) == 0 then
        return FormattingCode.RED .. ChatComponentTranslation("rbc/integersOnly")
    end
    local ret = ChatComponentEmpty()
    local layers = world.getLayers()
    for _, v in pairs(layers) do
        local state = world.getState(x, y, v)
        ret:append(ChatComponentTranslation("rbc/layerInfo", { v, state }) .. "\n")
    end
    return ret
end, function(args, argNumber, sender)
    if argNumber == 0 then
        return { math.floor(entities.getX(chat.getUUID(sender))) }
    elseif argNumber == 1 then
        return { math.floor(entities.getY(chat.getUUID(sender))) }
    else
        return {}
    end
end)

