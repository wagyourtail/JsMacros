package xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.scriptimpl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.jetbrains.annotations.NotNull;
import xyz.wagyourtail.Pair;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.AbstractRenderCodeCompiler;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.AutoCompleteSuggestion;
import xyz.wagyourtail.jsmacros.client.gui.overlays.ConfirmOverlay;
import xyz.wagyourtail.jsmacros.client.gui.screens.EditorScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;
import xyz.wagyourtail.jsmacros.core.language.ContextContainer;
import xyz.wagyourtail.jsmacros.core.language.ScriptContext;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Stream;
import java.util.concurrent.Semaphore;

/**
 * @author Wagyourtail
 */
public class ScriptCodeCompiler extends AbstractRenderCodeCompiler {
    private final ScriptTrigger scriptTrigger;
    private IChatComponent[] compiledText = new IChatComponent[] {new ChatComponentText("")};
    private MethodWrapper<Integer, Object, Map<String, MethodWrapper<Object, Object, Object>>> getRClickActions = null;
    private List<AutoCompleteSuggestion> suggestions = new LinkedList<>();
    
    public ScriptCodeCompiler(String language, EditorScreen screen, String scriptFile) {
        super(language, screen);
        scriptTrigger = new ScriptTrigger(ScriptTrigger.TriggerType.EVENT, "CodeCompile", scriptFile, true);
    }
    
    @Override
    public void recompileRenderedText(@NotNull String text) {
        CodeCompileEvent compileEvent = new CodeCompileEvent(text, language, screen);
        ContextContainer<?> t = Core.instance.exec(scriptTrigger, compileEvent, null, (ex) -> {
            FontRenderer renderer = Minecraft.getMinecraft().fontRendererObj;
            StringWriter st = new StringWriter();
            ex.printStackTrace(new PrintWriter(st));
            IChatComponent error = new ChatComponentText(st.toString().replaceAll("\r", "").replaceAll("\t", "    ")).setChatStyle(EditorScreen.defaultStyle);
            screen.openOverlay(new ConfirmOverlay(screen.width / 4, screen.height / 4, screen.width / 2, screen.height / 2, false, renderer, error, screen, (e) -> screen.openParent()));
        });
        if (t != null) {
            try {
                t.awaitLock(null);
            } catch (InterruptedException ignored) {
            }
        }
        getRClickActions = compileEvent.rightClickActions;
        Stream<TextHelper> lines = compileEvent.textLines.stream();
        lines.forEach(e -> e.getRaw().setChatStyle(EditorScreen.defaultStyle));
        compiledText = lines.map(BaseHelper::getRaw).toArray(IChatComponent[]::new);
        suggestions = compileEvent.autoCompleteSuggestions;
    }
    
    @NotNull
    @Override
    public Map<String, Runnable> getRightClickOptions(int index) {
        if (getRClickActions == null) return new HashMap<>();
        Map<String, ? extends Runnable> results = getRClickActions.apply(index);
        if (results == null) return new LinkedHashMap<>();
        return (Map<String, Runnable>) results;
    }
    
    @NotNull
    @Override
    public IChatComponent[] getRenderedText() {
        return compiledText;
    }
    
    @NotNull
    @Override
    public List<AutoCompleteSuggestion> getSuggestions() {
        return suggestions;
    }
    
}
