package alexanders.mods.rbcustomize.lua;

import com.google.common.collect.Lists;
import de.ellpeck.rockbottom.api.data.set.AbstractDataSet;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.data.set.ModBasedDataSet;
import de.ellpeck.rockbottom.api.data.set.part.DataPart;
import de.ellpeck.rockbottom.api.data.set.part.num.PartByte;
import de.ellpeck.rockbottom.api.data.set.part.num.PartInt;
import de.ellpeck.rockbottom.api.data.set.part.num.PartShort;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.UUID;

public class DataLib extends TwoArgFunction { //TODO: We should probably give an error when handling unknown DataSet types instead of doing nothing
    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable data = new LuaTable();
        data.set("addString", new FunctionWrapper(this::addString));
        data.set("addBoolean", new FunctionWrapper(this::addBoolean));
        data.set("addByte", new FunctionWrapper(this::addByte));
        data.set("addShort", new FunctionWrapper(this::addShort));
        data.set("addInt", new FunctionWrapper(this::addInt));
        data.set("addLong", new FunctionWrapper(this::addLong));
        data.set("addFloat", new FunctionWrapper(this::addFloat));
        data.set("addDouble", new FunctionWrapper(this::addDouble));
        data.set("addDataSet", new FunctionWrapper(this::addDataSet));
        data.set("addModBasedDataSet", new FunctionWrapper(this::addModBasedDataSet));
        data.set("addByteArray", new FunctionWrapper(this::addByteArray));
        data.set("addIntArray", new FunctionWrapper(this::addIntArray));
        data.set("addShortArray", new FunctionWrapper(this::addShortArray));
        data.set("addUUID", new FunctionWrapper(this::addUUID));
        data.set("getString", new FunctionWrapper(this::getString));
        data.set("getBoolean", new FunctionWrapper(this::getBoolean));
        data.set("getByte", new FunctionWrapper(this::getByte));
        data.set("getShort", new FunctionWrapper(this::getShort));
        data.set("getInt", new FunctionWrapper(this::getInt));
        data.set("getLong", new FunctionWrapper(this::getLong));
        data.set("getFloat", new FunctionWrapper(this::getFloat));
        data.set("getDouble", new FunctionWrapper(this::getDouble));
        data.set("getDataSet", new FunctionWrapper(this::getDataSet));
        data.set("getModBasedDataSet", new FunctionWrapper(this::getModBasedDataSet));
        data.set("getByteArray", new FunctionWrapper(this::getByteArray));
        data.set("getIntArray", new FunctionWrapper(this::getIntArray));
        data.set("getShortArray", new FunctionWrapper(this::getShortArray));
        data.set("getUUID", new FunctionWrapper(this::getUUID));
        data.set("copy", new FunctionWrapper(this::copy));
        data.set("hasKey", new FunctionWrapper(this::hasKey));
        data.set("create", new FunctionWrapper(this::create));
        data.set("createModBased", new FunctionWrapper(this::createModBased));
        data.set("equals", new FunctionWrapper(this::setEquals));
        env.set("data", data);
        return data;
    }

    private Varargs setEquals(Varargs varargs) {
        LuaValue lOp1 = varargs.arg(1);
        LuaValue lOp2 = varargs.arg(2);
        if (!(lOp1.isuserdata(AbstractDataSet.class) && lOp2.isuserdata(AbstractDataSet.class))) return error("Expected both operands to be DataSets");
        return valueOf(lOp1.touserdata().equals(lOp2.touserdata()));
    }

    private AbstractDataSet getDataSet(LuaValue lDataSet, int iArg, String argName) {
        if (!lDataSet.isuserdata(AbstractDataSet.class)) return argerror(iArg, "Expected a DataSet value for argument '" + argName + "'").toboolean() ? null : null; //TODO: lol
        return (AbstractDataSet) lDataSet.touserdata();
    }

    private String getString(LuaValue lString, int iArg, String argName) {
        if (!lString.isstring()) return argerror(iArg, "Expected a string value for argument'" + argName + "'").toboolean() ? null : null;
        return lString.tojstring();
    }

    private Varargs addString(Varargs varargs) { // backingData, key, value --> NIL
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        String value = getString(varargs.arg(3), 3, "value");
        if (dataSet instanceof DataSet) {
            ((DataSet) dataSet).addString(key, value);
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                ((ModBasedDataSet) dataSet).addString(new ResourceName(key), value);
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs addBoolean(Varargs varargs) {// backingData, key, value --> NIL
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        LuaValue lValue = varargs.arg(3);
        if (!lValue.isboolean()) return argerror(3, "Expected a boolean value for argument 'value'");
        boolean value = lValue.toboolean();
        if (dataSet instanceof DataSet) {
            ((DataSet) dataSet).addBoolean(key, value);
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                ((ModBasedDataSet) dataSet).addBoolean(new ResourceName(key), value);
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs addByte(Varargs varargs) {// backingData, key, value --> NIL
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        LuaValue lValue = varargs.arg(3);
        if (!lValue.isint()) return argerror(3, "Expected a byte value for argument 'value'");
        byte value = lValue.tobyte();
        if (dataSet instanceof DataSet) {
            ((DataSet) dataSet).addByte(key, value);
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                ((ModBasedDataSet) dataSet).addByte(new ResourceName(key), value);
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs addShort(Varargs varargs) {// backingData, key, value --> NIL
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        LuaValue lValue = varargs.arg(3);
        if (!lValue.isint()) return argerror(3, "Expected a short value for argument 'value'");
        short value = lValue.toshort();
        if (dataSet instanceof DataSet) {
            ((DataSet) dataSet).addShort(key, value);
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                ((ModBasedDataSet) dataSet).addShort(new ResourceName(key), value);
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs addInt(Varargs varargs) {// backingData, key, value --> NIL
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        LuaValue lValue = varargs.arg(3);
        if (!lValue.isint()) return argerror(3, "Expected an int value for argument 'value'");
        int value = lValue.toint();
        if (dataSet instanceof DataSet) {
            ((DataSet) dataSet).addInt(key, value);
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                ((ModBasedDataSet) dataSet).addInt(new ResourceName(key), value);
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs addLong(Varargs varargs) {// backingData, key, value --> NIL
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        LuaValue lValue = varargs.arg(3);
        if (!lValue.islong()) return argerror(3, "Expected a long value for argument 'value'");
        long value = lValue.tolong();
        if (dataSet instanceof DataSet) {
            ((DataSet) dataSet).addLong(key, value);
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                ((ModBasedDataSet) dataSet).addLong(new ResourceName(key), value);
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs addFloat(Varargs varargs) {// backingData, key, value --> NIL
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        LuaValue lValue = varargs.arg(3);
        if (!lValue.isnumber()) return argerror(3, "Expected a float value for argument 'value'");
        float value = lValue.tofloat();
        if (dataSet instanceof DataSet) {
            ((DataSet) dataSet).addFloat(key, value);
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                ((ModBasedDataSet) dataSet).addFloat(new ResourceName(key), value);
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs addDouble(Varargs varargs) {// backingData, key, value --> NIL
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        LuaValue lValue = varargs.arg(3);
        if (!lValue.isnumber()) return argerror(3, "Expected a double value for argument 'value'");
        double value = lValue.todouble();
        if (dataSet instanceof DataSet) {
            ((DataSet) dataSet).addDouble(key, value);
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                ((ModBasedDataSet) dataSet).addDouble(new ResourceName(key), value);
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs addDataSet(Varargs varargs) {// backingData, key, value --> NIL
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        AbstractDataSet setVal = getDataSet(varargs.arg(3), 3, "value");
        if (!(setVal instanceof DataSet)) return argerror(3, "Expected a DataSet value for argument 'value'");
        DataSet value = (DataSet) setVal;
        if (dataSet instanceof DataSet) {
            ((DataSet) dataSet).addDataSet(key, value);
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                ((ModBasedDataSet) dataSet).addDataSet(new ResourceName(key), value);
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs addModBasedDataSet(Varargs varargs) {// backingData, key, value --> NIL
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        AbstractDataSet setVal = getDataSet(varargs.arg(3), 3, "value");
        if (!(setVal instanceof ModBasedDataSet)) return argerror(3, "Expected a ModBasedDataSet value for argument 'value'");
        ModBasedDataSet value = (ModBasedDataSet) setVal;
        if (dataSet instanceof DataSet) {
            ((DataSet) dataSet).addModBasedDataSet(key, value);
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                ((ModBasedDataSet) dataSet).addModBasedDataSet(new ResourceName(key), value);
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs addByteArray(Varargs varargs) {// backingData, key, value --> NIL
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        LuaValue lValue = varargs.arg(3);
        if (!lValue.istable()) return argerror(3, "Expected a table(array) value for argument 'value'");
        PartByte[] value = new PartByte[lValue.length()];
        for (int i = 1; i <= value.length; i++) {
            value[i - 1] = new PartByte((byte) lValue.checkint(i)); // TODO: this is a bit hacky
        }

        if (dataSet instanceof DataSet) {
            ((DataSet) dataSet).addList(key, Lists.newArrayList(value));
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                ((ModBasedDataSet) dataSet).addList(new ResourceName(key), Lists.newArrayList(value));
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs addIntArray(Varargs varargs) {// backingData, key, value --> NIL
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        LuaValue lValue = varargs.arg(3);
        if (!lValue.istable()) return argerror(3, "Expected a table(array) value for argument 'value'");
        PartInt[] value = new PartInt[lValue.length()];
        for (int i = 1; i <= value.length; i++) {
            value[i - 1] = new PartInt(lValue.checkint(i)); // TODO: this is a bit hacky
        }

        if (dataSet instanceof DataSet) {
            ((DataSet) dataSet).addList(key, Lists.newArrayList(value));
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                ((ModBasedDataSet) dataSet).addList(new ResourceName(key), Lists.newArrayList(value));
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs addShortArray(Varargs varargs) {// backingData, key, value --> NIL
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        LuaValue lValue = varargs.arg(3);
        if (!lValue.istable()) return argerror(3, "Expected a table(array) value for argument 'value'");
        PartShort[] value = new PartShort[lValue.length()];
        for (int i = 1; i <= value.length; i++) {
            value[i - 1] = new PartShort((short) lValue.checkint(i)); // TODO: this is a bit hacky
        }

        if (dataSet instanceof DataSet) {
            ((DataSet) dataSet).addList(key, Lists.newArrayList(value));
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                ((ModBasedDataSet) dataSet).addList(new ResourceName(key), Lists.newArrayList(value));
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs addUUID(Varargs varargs) { // backingData, key, value --> NIL
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        String uuidString = getString(varargs.arg(3), 3, "value");
        try {
            UUID value = UUID.fromString(uuidString);
            if (dataSet instanceof DataSet) {
                ((DataSet) dataSet).addUniqueId(key, value);
            } else if (dataSet instanceof ModBasedDataSet) {
                try {
                    ((ModBasedDataSet) dataSet).addUniqueId(new ResourceName(key), value);
                } catch (IllegalArgumentException e) {
                    return argerror(2, "Key must be a resource name");
                }
            }
        } catch (IllegalArgumentException e) {
            return argerror(3, "Invalid uuid");
        }
        return NIL;
    }

    private Varargs getString(Varargs varargs) { // backingData, key --> value
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        if (dataSet instanceof DataSet) {
            return valueOf(((DataSet) dataSet).getString(key));
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                return valueOf(((ModBasedDataSet) dataSet).getString(new ResourceName(key)));
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs getBoolean(Varargs varargs) { // backingData, key --> value
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        if (dataSet instanceof DataSet) {
            return valueOf(((DataSet) dataSet).getString(key));
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                return valueOf(((ModBasedDataSet) dataSet).getBoolean(new ResourceName(key)));
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs getByte(Varargs varargs) { // backingData, key --> value
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        if (dataSet instanceof DataSet) {
            return valueOf(((DataSet) dataSet).getByte(key));
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                return valueOf(((ModBasedDataSet) dataSet).getByte(new ResourceName(key)));
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs getShort(Varargs varargs) { // backingData, key --> value
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        if (dataSet instanceof DataSet) {
            return valueOf(((DataSet) dataSet).getShort(key));
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                return valueOf(((ModBasedDataSet) dataSet).getShort(new ResourceName(key)));
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs getInt(Varargs varargs) { // backingData, key --> value
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        if (dataSet instanceof DataSet) {
            return valueOf(((DataSet) dataSet).getInt(key));
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                return valueOf(((ModBasedDataSet) dataSet).getInt(new ResourceName(key)));
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs getLong(Varargs varargs) { // backingData, key --> value
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        if (dataSet instanceof DataSet) {
            return valueOf(((DataSet) dataSet).getLong(key));
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                return valueOf(((ModBasedDataSet) dataSet).getLong(new ResourceName(key)));
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs getFloat(Varargs varargs) { // backingData, key --> value
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        if (dataSet instanceof DataSet) {
            return valueOf(((DataSet) dataSet).getFloat(key));
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                return valueOf(((ModBasedDataSet) dataSet).getFloat(new ResourceName(key)));
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs getDouble(Varargs varargs) { // backingData, key --> value
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        if (dataSet instanceof DataSet) {
            return valueOf(((DataSet) dataSet).getDouble(key));
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                return valueOf(((ModBasedDataSet) dataSet).getDouble(new ResourceName(key)));
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs getDataSet(Varargs varargs) { // backingData, key --> value
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        if (dataSet instanceof DataSet) {
            return LuaEnvironment.globals.get("DataSet").call(userdataOf(((DataSet) dataSet).getDataSet(key)));
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                return LuaEnvironment.globals.get("DataSet").call(userdataOf(((ModBasedDataSet) dataSet).getDataSet(new ResourceName(key))));
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs getModBasedDataSet(Varargs varargs) { // backingData, key --> value
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        if (dataSet instanceof DataSet) {
            return LuaEnvironment.globals.get("DataSet").call(userdataOf(((DataSet) dataSet).getModBasedDataSet(key)));
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                return LuaEnvironment.globals.get("DataSet").call(userdataOf(((ModBasedDataSet) dataSet).getModBasedDataSet(new ResourceName(key))));
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs getByteArray(Varargs varargs) { // backingData, key --> value
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        Byte[] array;
        if (dataSet instanceof DataSet) {
            array = ((DataSet) dataSet).<DataPart<Byte>>getList(key).stream().map(DataPart::get).toArray(Byte[]::new);
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                array = ((ModBasedDataSet) dataSet).<DataPart<Byte>>getList(new ResourceName(key)).stream().map(DataPart::get).toArray(Byte[]::new);
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        } else {
            return argerror(2, "Unrecognized DataSet type");
        }
        if (array == null) return NIL;

        LuaTable table = new LuaTable(array.length, 0);
        for (int i = 1; i <= array.length; i++) {
            table.set(i, valueOf(array[i - 1]));
        }
        return table;
    }

    private Varargs getIntArray(Varargs varargs) { // backingData, key --> value
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        Integer[] array;
        if (dataSet instanceof DataSet) {
            array = ((DataSet) dataSet).<DataPart<Integer>>getList(key).stream().map(DataPart::get).toArray(Integer[]::new);
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                array = ((ModBasedDataSet) dataSet).<DataPart<Integer>>getList(new ResourceName(key)).stream().map(DataPart::get).toArray(Integer[]::new);
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        } else {
            return argerror(2, "Unrecognized DataSet type");
        }
        if (array == null) return NIL;

        LuaTable table = new LuaTable(array.length, 0);
        for (int i = 1; i <= array.length; i++) {
            table.set(i, valueOf(array[i - 1]));
        }
        return table;
    }

    private Varargs getShortArray(Varargs varargs) { // backingData, key --> value
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        Short[] array;
        if (dataSet instanceof DataSet) {
            array = ((DataSet) dataSet).<DataPart<Short>>getList(key).stream().map(DataPart::get).toArray(Short[]::new);
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                array = ((ModBasedDataSet) dataSet).<DataPart<Short>>getList(new ResourceName(key)).stream().map(DataPart::get).toArray(Short[]::new);
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        } else {
            return argerror(2, "Unrecognized DataSet type");
        }
        if (array == null) return NIL;

        LuaTable table = new LuaTable(array.length, 0);
        for (int i = 1; i <= array.length; i++) {
            table.set(i, valueOf(array[i - 1]));
        }
        return table;
    }

    private Varargs getUUID(Varargs varargs) { // backingData, key --> value
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        if (dataSet instanceof DataSet) {
            return valueOf(((DataSet) dataSet).getUniqueId(key).toString());
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                return valueOf(((ModBasedDataSet) dataSet).getUniqueId(new ResourceName(key)).toString());
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return NIL;
    }

    private Varargs copy(Varargs varargs) { // backingData --> value
        AbstractDataSet set = getDataSet(varargs.arg(1), 1, "backingData");
        if (set instanceof DataSet) return userdataOf(((DataSet) set).copy());
        else if (set instanceof ModBasedDataSet) return userdataOf(((ModBasedDataSet) set).copy());
        return NIL;
    }

    private Varargs hasKey(Varargs varargs) { // backingData, key --> boolean
        AbstractDataSet dataSet = getDataSet(varargs.arg(1), 1, "backingData");
        String key = getString(varargs.arg(2), 2, "key");
        if (dataSet instanceof DataSet) {
            return valueOf(dataSet.hasKey(key));
        } else if (dataSet instanceof ModBasedDataSet) {
            try {
                return valueOf(dataSet.hasKey(new ResourceName(key).toString()));
            } catch (IllegalArgumentException e) {
                return argerror(2, "Key must be a resource name");
            }
        }
        return FALSE;
    }

    private Varargs create(Varargs varargs) { // --> DataSet
        return userdataOf(new DataSet());
    }

    private Varargs createModBased(Varargs varargs) { // --> DataSet
        return userdataOf(new ModBasedDataSet());
    }
}
