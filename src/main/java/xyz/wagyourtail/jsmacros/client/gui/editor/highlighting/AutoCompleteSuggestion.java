package xyz.wagyourtail.jsmacros.client.gui.editor.highlighting;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import xyz.wagyourtail.jsmacros.client.gui.screens.EditorScreen;

public class AutoCompleteSuggestion {
    public final int startIndex;
    public final String suggestion;
    public final IChatComponent displayText;
    
    public AutoCompleteSuggestion(int startIndex, String suggestion) {
        this.suggestion = suggestion;
        this.startIndex = startIndex;
        this.displayText = new ChatComponentText(suggestion);
        displayText.setChatStyle(EditorScreen.defaultStyle);
    }
    
    public AutoCompleteSuggestion(int startIndex, String suggestion, IChatComponent displayText) {
        this.suggestion = suggestion;
        this.startIndex = startIndex;
        this.displayText = displayText;
    }
    
}
