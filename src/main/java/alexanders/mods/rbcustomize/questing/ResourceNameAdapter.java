package alexanders.mods.rbcustomize.questing;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.io.IOException;

public class ResourceNameAdapter extends TypeAdapter<ResourceName> {
    @Override
    public void write(JsonWriter out, ResourceName value) throws IOException {
        out.value(value.toString());
    }

    @Override
    public ResourceName read(JsonReader in) throws IOException {
        return new ResourceName(in.nextString());
    }
}
