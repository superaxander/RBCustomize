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

ItemInstance = class(function(this, item, amount, meta, set)
    if not item then
        error("Item can not be nil!")
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

ResUseInfo = class(function(this, name, amount)
    if not name then
        error("Name can not be nil!")
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
        error("Invalid argument types expected a string and a number")
    end
    this.tp = tp;
    this.level = level;
end)

DataSet = class(function(this, backingData)
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
        error("Exepected a DataSet value for argument 'value'")
    end
    data.addDataSet(self.backingData, key, value.backingData)
end

function DataSet:addModBasedDataSet(key, value)
    if not type(value) == "table" or value.backingData == nil then
        error("Expected a DataSet value for argument 'value'")
    end
    data.addModBasedDataSet(self.backingData, key, value.backingData)
end

function DataSet:addByteArray(key, value)
    if not type(value) == "table" then
        error("Expected a table(array) value for argument 'value'")
    end
    data.addByteArray(self.backingData, key, value);
end

function DataSet:addIntArray(key, value)
    if not type(value) == "table" then
        error("Expected a table(array) value for argument 'value'")
    end
    data.addIntArray(self.backingData, key, value)
end

function DataSet:addShortArray(key, value)
    if not type(value) == "table" then
        error("Expected a table(array) value for argument 'value'")
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
