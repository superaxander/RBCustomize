package alexanders.mods.rbcustomize.questing;

import alexanders.mods.rbcustomize.questing.QuestBook.Chapter;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class ChapterQuestMapAdapter implements JsonSerializer<LinkedHashMap<Chapter, ArrayList<Quest>>>, JsonDeserializer<LinkedHashMap<Chapter, ArrayList<Quest>>> {
    @Override
    public JsonElement serialize(LinkedHashMap<Chapter, ArrayList<Quest>> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        for (Entry<Chapter, ArrayList<Quest>> chapter : src.entrySet()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", chapter.getKey().name);
            obj.addProperty("description", chapter.getKey().description);
            obj.add("quests", context.serialize(chapter.getValue(), new TypeToken<ArrayList<Quest>>() {}.getType()));
            array.add(obj);
        }
        return array;
    }

    @Override
    public LinkedHashMap<Chapter, ArrayList<Quest>> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        LinkedHashMap<Chapter, ArrayList<Quest>> out = new LinkedHashMap<>();
        JsonArray array = json.getAsJsonArray();
        for (JsonElement chapter : array) {
            JsonObject obj = chapter.getAsJsonObject();
            Chapter c = new Chapter(obj.get("name").getAsString(), obj.get("description").getAsString());
            ArrayList<Quest> quests = context.deserialize(obj.get("quests").getAsJsonArray(), new TypeToken<ArrayList<Quest>>() {}.getType());
            out.put(c, quests);
        }
        return out;
    }
}
