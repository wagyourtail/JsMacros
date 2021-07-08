package xyz.wagyourtail.jsmacros.client.gui.editor.highlighting;

import net.minecraft.util.IChatComponent;
import org.jetbrains.annotations.NotNull;
import xyz.wagyourtail.jsmacros.client.gui.screens.EditorScreen;

import java.util.List;
import java.util.Map;

public abstract class AbstractRenderCodeCompiler {
    protected final EditorScreen screen;
    protected final String language;
    
    public AbstractRenderCodeCompiler(String language, EditorScreen screen) {
        this.language = language;
        this.screen = screen;
    }
    
    public abstract void recompileRenderedText(@NotNull String text);
    
    @NotNull
    public abstract Map<String, Runnable> getRightClickOptions(int index);
    
    @NotNull
    public abstract IChatComponent[] getRenderedText();
    
    @NotNull
    public abstract List<AutoCompleteSuggestion> getSuggestions();
    
}
