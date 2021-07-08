package xyz.wagyourtail.jsmacros.client.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import xyz.wagyourtail.jsmacros.client.gui.screens.EditorScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.Option;
import xyz.wagyourtail.jsmacros.core.config.OptionType;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClientConfigV2 {
    @Option(translationKey = "jsmacros.sort", group = "jsmacros.settings.gui")
    public Sorting.MacroSortMethod sortMethod = Sorting.MacroSortMethod.Enabled;
    
    @Option(translationKey = "jsmacros.disablewithscreen", group = "jsmacros.settings.general")
    public boolean disableKeyWhenScreenOpen = true;
    
    @Option(translationKey = "jsmacros.theme", group = {"jsmacros.settings.editor", "jsmacros.settings.editor.color"}, getter = "getThemeData", type = @OptionType("color"))
    public Map<String, short[]> editorTheme = null;
    
    @Option(translationKey = "jsmacros.linteroverrides", group = {"jsmacros.settings.editor", "jsmacros.settings.editor.linter"}, options = "languages", type = @OptionType("file"))
    public Map<String, String> editorLinterOverrides = new HashMap<>();
    
    @Option(translationKey = "jsmacros.history", group = "jsmacros.settings.editor")
    public int editorHistorySize = 20;
    
    @Option(translationKey = "jsmacros.autocomplete", group = "jsmacros.settings.editor")
    public boolean editorSuggestions = true;
    
    public List<String> languages() {
        return EditorScreen.langs;
    }
    
    public Map<String, short[]> getThemeData() {
        if (editorTheme == null) {
            editorTheme = new HashMap<>();
            // JS
            editorTheme.put("keyword", new short[] {0xCC, 0x78, 0x32});
            editorTheme.put("number",  new short[] {0x79, 0xAB, 0xFF});
            editorTheme.put("function-variable", new short[] {0x79, 0xAB, 0xFF});
            editorTheme.put("function", new short[] {0xA2, 0xEA, 0x22});
            editorTheme.put("operator", new short[] {0xD8, 0xD8, 0xD8});
            editorTheme.put("string", new short[] {0x12, 0xD4, 0x89});
            editorTheme.put("comment", new short[] {0xA0, 0xA0, 0xA0});
            editorTheme.put("constant", new short[] {0x21, 0xB4, 0x3E});
            editorTheme.put("class-name", new short[] {0x21, 0xB4, 0x3E});
            editorTheme.put("boolean", new short[] {0xFF, 0xE2, 0x00});
            editorTheme.put("punctuation", new short[] {0xD8, 0xD8, 0xD8});
            editorTheme.put("interpolation-punctuation", new short[] {0xCC, 0x78, 0x32});
            
            //py
            editorTheme.put("builtin", new short[] {0x21, 0xB4, 0x3E});
            editorTheme.put("format-spec", new short[] {0xCC, 0x78, 0x32});
            
            //regex
            editorTheme.put("regex", new short[] {0x12, 0xD4, 0x89});
            editorTheme.put("charset-negation", new short[] {0xCC, 0x78, 0x32});
            editorTheme.put("charset-punctuation", new short[] {0xD8, 0xD8, 0xD8});
            editorTheme.put("escape", new short[]  {0xFF, 0xE2, 0x00});
            editorTheme.put("charclass", new short[] {0xFF, 0xE2, 0x00});
            editorTheme.put("quantifier", new short[] {0x79, 0xAB, 0xFF});
            Core.instance.config.saveConfig();
        }
        return editorTheme;
    }
    
    public Comparator<ScriptTrigger> getSortComparator() {
        if (this.sortMethod == null) this.sortMethod = Sorting.MacroSortMethod.Enabled;
        switch(this.sortMethod) {
            default:
            case Enabled:
                return new Sorting.SortByEnabled();
            case FileName:
                return new Sorting.SortByFileName();
            case TriggerName:
                return new Sorting.SortByTriggerName();
        }
    }
    
    @Deprecated
    public void fromV1(JsonObject v1) {
        sortMethod = Sorting.MacroSortMethod.valueOf(v1.get("sortMethod").getAsString());
        v1.remove("sortMethod");
        disableKeyWhenScreenOpen = v1.get("disableKeyWhenScreenOpen").getAsBoolean();
        v1.remove("disableKeyWhenScreenOpen");
        if (v1.has("editorTheme") && v1.get("editorTheme").isJsonObject()) {
            editorTheme = new HashMap<>();
            for (Map.Entry<String, JsonElement> el : v1.getAsJsonObject("editorTheme").entrySet()) {
                short[] color = new short[3];
                int i = 0;
                for (JsonElement el2 : el.getValue().getAsJsonArray()) {
                    color[i] = el2.getAsShort();
                    ++i;
                }
                editorTheme.put(el.getKey(), color);
            }
        }
        v1.remove("editorTheme");
        editorLinterOverrides = new HashMap<>();
        if (v1.has("editorLinterOverrides") && v1.get("editorLinterOverrides").isJsonObject()) {
            for (Map.Entry<String, JsonElement> el : v1.getAsJsonObject("editorLinterOverrides").entrySet()) {
                editorLinterOverrides.put(el.getKey(), el.getValue().getAsString());
            }
        }
        v1.remove("editorLinterOverrides");
        editorHistorySize = v1.get("editorHistorySize").getAsInt();
        v1.remove("editorHistorySize");
        editorSuggestions = v1.get("editorSuggestions").getAsBoolean();
        v1.remove("editorSuggestions");
        v1.remove("editorFont");
    }
}
