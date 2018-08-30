package alexanders.mods.rbcustomize.questing;

import com.google.gson.annotations.JsonAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class QuestBook {
    @JsonAdapter(ChapterQuestMapAdapter.class)
    public final LinkedHashMap<Chapter, ArrayList<Quest>> chapters = new LinkedHashMap<>();

    public static class Chapter {
        public String name;
        public String description;

        public Chapter(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
}
