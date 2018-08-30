package alexanders.mods.rbcustomize.questing;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import org.luaj.vm2.LuaValue;

import java.io.IOException;

public class TriggerObjectAdapter extends TypeAdapter<Object> {
    @Override
    public void write(JsonWriter out, Object value) throws IOException {
        out.beginArray();
        if (value instanceof Item) {
            out.value("item");
            out.value(((Item) value).getName().toString());
        } else if (value instanceof Tile) {
            out.value("tile");
            out.value(((Tile) value).getName().toString());
        } else if(value instanceof LuaValue){
            out.value("lua");
            out.value((String) null);
        }else {
            out.value("other");
            out.value((String) null);
        }
        out.endArray();
    }

    @Override
    public Object read(JsonReader in) throws IOException {
        in.beginArray();
        switch (in.nextString()) {
            case "item":
                Item item = Registries.ITEM_REGISTRY.get(new ResourceName(in.nextString()));
                in.endArray();
                return item;
            case "tile":
                Tile tile = Registries.TILE_REGISTRY.get(new ResourceName(in.nextString()));
                in.endArray();
                return tile;
            case "lua":
                in.nextString();
                in.endArray();
                return null;
            default:
                throw new IllegalArgumentException("Invalid trigger object type");
        }
    }
}
