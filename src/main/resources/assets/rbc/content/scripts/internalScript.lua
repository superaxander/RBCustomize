-- From http://lua-users.org/wiki/SimpleLuaClasses
function class(base, init)
    local c = {} -- a new class instance
    if not init and type(base) == 'function' then
        init = base
        base = nil
    elseif type(base) == 'table' then
        -- our new class is a shallow copy of the base class!
        for i, v in pairs(base) do
            c[i] = v
        end
        c._base = base
    end
    -- the class will be the metatable for all its objects,
    -- and they will look up their methods in it.
    c.__index = c

    -- expose a constructor which can be called by <classname>(<args>)
    local mt = {}
    mt.__call = function(class_tbl, ...)
        local obj = {}
        setmetatable(obj, c)
        if init then
            init(obj, ...)
        else
            -- make sure that any stuff from the base class is initialized!
            if base and base.init then
                base.init(obj, ...)
            end
        end
        return obj
    end
    c.init = init
    c.is_a = function(self, klass)
        local m = getmetatable(self)
        while m do
            if m == klass then return true end
            m = m._base
        end
        return false
    end
    setmetatable(c, mt)
    return c
end

DataSet = class(function(this, backingData)
    if not type(backingData) == "userdata" then
        error("Invalid argument type expected a DataSet userdata object", 3)
    end
    this.backingData = backingData;
end)

function DataSet:addString(key, value)
    data.addString(self.backingData, key, value);
end

function DataSet:addBoolean(key, value)
    data.addBoolean(self.backingData, key, value)
end

function DataSet:addByte(key, value)
    data.addByte(self.backingData, key, value)
end

function DataSet:addShort(key, value)
    data.addShort(self.backingData, key, value)
end

function DataSet:addInt(key, value)
    data.addInt(self.backingData, key, value)
end

function DataSet:addLong(key, value)
    data.addLong(self.backingData, key, value)
end

function DataSet:addFloat(key, value)
    data.addFloat(self.backingData, key, value)
end

function DataSet:addDouble(key, value)
    data.addDouble(self.backingData, key, value)
end

function DataSet:addDataSet(key, value)
    if not type(value) == "table" or value.backingData == nil then
        error("Exepected a DataSet value for argument 'value'", 2)
    end
    data.addDataSet(self.backingData, key, value.backingData)
end

function DataSet:addModBasedDataSet(key, value)
    if not type(value) == "table" or value.backingData == nil then
        error("Expected a DataSet value for argument 'value'", 2)
    end
    data.addModBasedDataSet(self.backingData, key, value.backingData)
end

function DataSet:addByteArray(key, value)
    if not type(value) == "table" then
        error("Expected a table(array) value for argument 'value'", 2)
    end
    data.addByteArray(self.backingData, key, value);
end

function DataSet:addIntArray(key, value)
    if not type(value) == "table" then
        error("Expected a table(array) value for argument 'value'", 2)
    end
    data.addIntArray(self.backingData, key, value)
end

function DataSet:addShortArray(key, value)
    if not type(value) == "table" then
        error("Expected a table(array) value for argument 'value'", 2)
    end
    data.addShortArray(self.backingData, key, value)
end

function DataSet:addUUID(key, value)
    data.addUUID(self.backingData, key, value)
end

function DataSet:getString(key)
    return data.getString(self.backingData, key);
end

function DataSet:getBoolean(key)
    return data.getBoolean(self.backingData, key)
end

function DataSet:getByte(key)
    return data.getByte(self.backingData, key)
end

function DataSet:getShort(key)
    return data.getShort(self.backingData, key)
end

function DataSet:getInt(key)
    return data.getInt(self.backingData, key)
end

function DataSet:getLong(key)
    return data.getLong(self.backingData, key)
end

function DataSet:getFloat(key)
    return data.getFloat(self.backingData, key)
end

function DataSet:getDouble(key)
    return data.getDouble(self.backingData, key)
end

function DataSet:getDataSet(key)
    return data.getDataSet(self.backingData, key)
end

function DataSet:getModBasedDataSet(key)
    return data.getModBasedDataSet(self.backingData, key)
end

function DataSet:getByteArray(key)
    return data.getByteArray(self.backingData, key);
end

function DataSet:getIntArray(key)
    return data.getIntArray(self.backingData, key)
end

function DataSet:getShortArray(key)
    return data.getShortArray(self.backingData, key)
end

function DataSet:getUUID(key)
    return data.getUUID(self.backingData, key)
end

function DataSet:copy()
    return DataSet(data.copy(self.backingData))
end

function DataSet:hasKey(key)
    return data.hasKey(self.backingData, key)
end

function DataSet.__eq(op1, op2)
    if not type(op1) == "table" or not type(op2) == "table" then
        error("Expected a DataSet value for arguments 'op1' and 'op2'", 2)
    end
    return data.equals(op1.backingData, op2.backingData)
end

ItemInstance = class(function(this, item, amount, meta, set)
    if not item then
        error("Item can not be nil!", 3)
    end

    this.item = item

    if amount then
        this.amount = amount
    else
        this.amount = 1
    end

    if meta then
        this.meta = meta
    else
        this.meta = 0
    end

    if set then
        this.set = set
    else
        this.set = nil
    end
end)

function ItemInstance.load(set)
    local item = set.getString("item_name")
    local amount = set.getInt("amount")
    local meta = set.getShort("meta")
    local _set;
    if set.hasKey("data") then
        _set = set.getModBasedDataSet("data")
    end
    return ItemInstance(item, amount, meta, _set)
end

function ItemInstance.compare(one, other, item, meta, data)
    if not one and not other then
        return true
    elseif not one or not other then
        return false
    else
        if item then
            if not one.item == other.item then
                return false
            end
        end

        if meta then
            if not one.meta == other.meta then
                return false
            end
        end

        if data then
            if not items.isDataSensitive(one.item) or items.isDataSensitive(other.item) then
                if one.set then
                    if not one.set == other.set then
                        return false
                    end
                elseif not other.set then
                    return false
                end
            end
        end

        return true
    end
end

function ItemInstance:save(set)
    set.addString("item_name", self.item);
    set.addInt("amount", self.amount);
    set.addShort("meta", self.meta);
    if self.set then
        set.addModBasedDataSet("data", self.set);
    end
end

function ItemInstance:getMaxAmount()
    return items.getMaxAmount(self.item)
end

function ItemInstance:fitsAmount(amount)
    return self.amount + amount <= self.getMaxAmount()
end

function ItemInstance:multiplyAmount(modifier)
    self.amount = self.amount * modifier
    return self
end

function ItemInstance:addAmount(amount)
    self.amount = self.amount + amount
end

function ItemInstance:removeAmount(amount)
    self.amount = self.amount - amount
end

function ItemInstance:copy()
    if set then
        return ItemInstance(self.item, self.amount, self.meta, self.set.copy())
    else
        return ItemInstance(self.item, self.amount, self.meta, nil)
    end
end

function ItemInstance:getOrCreateAdditionalData()
    if self.set then
        return self.set
    else
        self.set = DataSet(data.createModBased())
        return self.set
    end
end

function ItemInstance:isEffectivelyEqual(instance)
    ItemInstance.compare(self, instance, true, true, true)
end

function ItemInstance.__eq(op1, op2)
    return op1.item == op2.item and op1.amount == op2.amount and op1.meta == op2.meta and op1.set == op2.set
end

ResUseInfo = class(function(this, name, amount)
    if not name then
        error("Name can not be nil!", 3)
    end

    this.name = name;

    if amount then
        this.amount = amount
    else
        this.amount = 1;
    end
end)

ToolLevel = class(function(this, tp, level)
    if not type(tp) == "string" or not type(level) == "number" then
        error("Invalid argument types expected a string and a number", 3)
    end
    this.tp = tp;
    this.level = level;
end)

BoundBox = class(function(this, minX, minY, maxX, maxY)
    if not minX then
        minX = 0
    else
        this.minX = minX;
    end
    if not minY then
        this.minY = 0
    else
        this.minY = minY
    end
    if not maxX then
        this.maxX = 0
    else
        this.maxX = maxX
    end
    if not maxY then
        this.maxY = 0
    else
        this.maxY = maxY
    end
end)

function BoundBox:set(minX, minY, maxX, maxY)
    if type(minX) == "table" then
        self.minX = minX.minX
        self.minY = minX.minY
        self.maxX = minX.maxX
        self.maxY = minX.maxY
    else
        if not (type(minX) == "number" and type(minY) == "number" and type(maxX) == "number" and type(maxY) == "number") then
            error("minX, minY, maxX and maxY must be numbers", 2)
        end
        self.minX = math.min(minX, maxX);
        self.minY = math.min(minY, maxY);
        self.maxX = math.max(maxX, minX);
        self.maxY = math.max(maxY, minY);
    end
    return self
end

function BoundBox:add(x, y)
    if not (type(x) == "number" and type(y) == "number") then
        error("The arguments x and y must numbers", 2)
    end
    self.minX = self.minX + x;
    self.minY = self.minY + y;
    self.maxX = self.maxX + x;
    self.maxY = self.maxY + y;
    return self
end

function BoundBox:expand(amount)
    if not (type(amount) == "number") then
        error("The argument amount must be a number", 2)
    end
    self.minX = self.minX - amount;
    self.minY = self.minY - amount;
    self.maxX = self.maxX + amount;
    self.maxY = self.maxY + amount;
    return self
end

function BoundBox:intersects(minX, minY, maxX, maxY)
    if type(minX) == "table" then
        return self:intersects(minX.minX, minX.minY, minX.maxX, minX.maxY)
    else
        if not (type(minX) == "number" and type(minY) == "number" and type(maxX) == "number" and type(maxY) == "number") then
            error("minX, minY, maxX and maxY must be numbers", 2)
        end
        return self.minX < maxX and self.maxX > minX and self.minY < maxY and self.maxY > minY
    end
end

function BoundBox:contains(x, y)
    if not (type(x) == "number" and type(y) == "number") then
        error("The arguments x and y must numbers", 2)
    end
    return self.minX <= x and self.minY <= y and self.maxX >= x and self.maxY >= y
end

function BoundBox:isEmpty()
    return self:getWidth() <= 0.0 or self:getHeight() <= 0.0
end

function BoundBox.__eq(op1, op2)
    return op1.minX == op2.minX and op1.minY == op2.minY and op1.maxX == op2.maxY and op1.maxY == op2.maxY
end

function BoundBox:getWidth()
    return self.maxX - self.minX;
end

function BoundBox:getHeight()
    return self.maxY - self.minY;
end

function BoundBox:copy()
    return BoundBox().set(self)
end

TileProp = class(function(this, name)
    this.name = name
end)

function TileProp.__eq(op1, op2)
    return op1.name == op2.name
end

function TileProp.__lt(op1, op2)
    return op1.name < op2.name
end

function TileProp.__le(op1, op2)
    return op1.name <= op2.name
end

function TileProp:__tostring()
    return self.name
end

IntProp = class(TileProp, function(this, name, def, possibilities)
    TileProp.init(this, name)
    this.def = def
    this.possibilities = possibilities
end)

function IntProp:getVariants()
    return self.possibilities
end

function IntProp:getValue(index)
    return index
end

function IntProp:getIndex(value)
    return value
end

EnumProp = class(TileProp, function(this, name, def, enumName)
    TileProp.init(this, name)
    this.def = def;
    if type(enumName) == "string" then
        this.enumName = enumName
        this.values = utils.getEnumValues(enumName)
    else
        error(3, "Expected a string for argument 'enumName'")
    end

    for i = 1, #this.values do
        if this.values[i] then
            return
        end
    end
    error(3, "The default value should be in the enum")
end)

function EnumProp:getVariants()
    return #self.values
end

function EnumProp:getValue(index)
    return self[index]
end

function EnumProp:getIndex(value)
    for k, v in pairs(self.values) do
        if v == value then
            return k
        end
    end
    error(2, "There is no index with that value")
end

StringProp = class(TileProp, function(this, name, def, allowedValues)
    TileProp.init(this, name)
    this.def = def;
    if not type(allowedValues) == "table" then
        error(3, "Allowed values must be a table")
    end
    this.allowedValues = allowedValues
end)

function StringProp:getVariants()
    return #self.allowedValues
end

function StringProp:getValue(index)
    return self.allowedValues[index]
end

function StringProp:getIndex(value)
    for k, v in pairs(self.allowedValues) do
        if v == value then
            return k
        end
    end
    error(2, "There is no index with that value")
end

BoolProp = class(TileProp, function(this, name, def)
    TileProp.init(this, name)
    this.def = def
end)

function BoolProp:getVariants()
    return 2
end

function BoolProp:getValue(index)
    return index == 1
end

function BoolProp:getIndex(value)
    if value then
        return 1
    else
        return 0
    end
end

SpecificIntProp = class(TileProp, function(this, name, def, allowedValues)
    TileProp.init(this, name)
    this.def = def;
    if not type(allowedValues) == "table" then
        error(3, "Allowed values must be a table")
    end
    this.allowedValues = allowedValues
end)

function SpecificIntProp:getVariants()
    return #self.allowedValues
end

function SpecificIntProp:getValue(index)
    return self.allowedValues[index]
end

function SpecificIntProp:getIndex(value)
    for k, v in pairs(self.allowedValues) do
        if v == value then
            return k
        end
    end
    error(2, "There is no index with that value")
end

Texture = class(function(this, backingData)
    this.backingData = backingData
    this.renderWidth = textures.getRenderWidth(backingData)
    this.renderHeight = textures.getRenderHeight(backingData)
end)

function Texture:draw(x, y, x2, y2, x3, y3, x4, y4, srcX, srcY, srcX2, srcY2, light, filter)
    if not y then
        error("Too few arguments")
    end

    if not x2 then -- 2 arguments
        self:draw(x, y, 1.0)
    elseif not y2 then -- 3 arguments
        self:draw(x, y, self.renderWidth * x2, self.renderHeight * x2) -- x2 is scale
    elseif not x3 then -- 4 arguments
        self:draw(x, y, x2, y2, -1) -- -1 is filter
    elseif not y3 then -- 5 arguments
        if type(x3) == "table" then
            self:draw(x, y, x2, y2, x3, -1) -- x3 is light, -1 is filter, x2 = width, y2 = height
        else
            self:draw(x, y, x + x2, y + y2, 0, 0, self.renderWidth, self.renderHeight, {}, x3) -- x2 = width, y2 = height, x3 = filter
        end
    elseif not x4 then -- 6 arguments
        self:draw(x, y, x + x2, y + y2, 0, 0, self.renderWidth, self.renderHeight, x3, y3) -- x2 = width, y2 = height, x3 = light, y3 = filter
    elseif not srcX then -- 8 arguments
        self:draw(x, y, x2, y2, x3, y3, x4, y4, -1) -- -1 = filter
    elseif not srcY then -- 9 arguments
        if type(srcX) == "table" then
            self:draw(x, y, x2, y2, x3, y3, x4, y4, srcX, -1) -- srcX = light, -1 = filter
        else
            self:draw(x, y, x2, y2, x3, y3, x4, y4, {}, srcX) -- srcX = filter
        end
    elseif not srcX2 then -- 10 arguments
        self:draw(x, y, x, y2, x2, y2, x2, y, x3, y3, x4, y4, srcX, srcY) -- srcX = light, srcY = filter
    elseif not filter then
        error("Unsupported amount of arguments")
    else
        textures.draw(self.backingData, x, y, x2, y2, x3, y3, x4, y4, srcX, srcY, srcX2, srcY2, light, filter)
    end
end


function Texture:getPositionalVariation(x, y)
    return Texture(textures.getPositionalVariation(self.backingData, x, y))
end

Direction = class(function(this, x, y)
    this.x = x
    this.y = y
end)

Direction.NONE = Direction(0, 0)
Direction.UP = Direction(0, 1)
Direction.DOWN = Direction(0, -1)
Direction.LEFT = Direction(-1, 0)
Direction.RIGHT = Direction(1, 0)
Direction.LEFT_UP = Direction(-1, 1)
Direction.LEFT_DOWN = Direction(-1, -1)
Direction.RIGHT_UP = Direction(1, 1)
Direction.RIGHT_DOWN = Direction(1, -1)

Direction.DIRECTIONS = { Direction.NONE, Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT, Direction.LEFT_UP, Direction.LEFT_DOWN, Direction.RIGHT_UP, Direction.RIGHT_DOWN }
Direction.ADJACENT = { Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT }
Direction.ADJACENT_INCLUDING_NONE = { Direction.NONE, Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT }
Direction.DIAGONAL = { Direction.LEFT_UP, Direction.RIGHT_UP, Direction.RIGHT_DOWN, Direction.LEFT_DOWN }
Direction.DIAGONAL_INCLUDING_NONE = { Direction.NONE, Direction.LEFT_UP, Direction.RIGHT_UP, Direction.RIGHT_DOWN, Direction.LEFT_DOWN }
Direction.SURROUNDING = { Direction.LEFT_UP, Direction.UP, Direction.RIGHT_UP, Direction.RIGHT, Direction.RIGHT_DOWN, Direction.DOWN, Direction.LEFT_DOWN, Direction.LEFT }
Direction.SURROUNDING_INCLUDING_NONE = { Direction.NONE, Direction.LEFT_UP, Direction.UP, Direction.RIGHT_UP, Direction.RIGHT, Direction.RIGHT_DOWN, Direction.DOWN, Direction.LEFT_DOWN, Direction.LEFT }

-- Privatize constructor
Direction.init = nil
Direction.__call = nil
