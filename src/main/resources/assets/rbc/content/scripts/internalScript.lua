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
        error("Invalid argument type expected a DataSet userdata object", 2)
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
        error("Item can not be nil!", 2)
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
        error("Name can not be nil!", 2)
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
        error("Invalid argument types expected a string and a number", 2)
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
