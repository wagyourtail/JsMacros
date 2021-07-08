package xyz.wagyourtail.jsmacros.client.gui.screens;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.*;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;
import xyz.wagyourtail.jsmacros.client.gui.editor.History;
import xyz.wagyourtail.jsmacros.client.gui.editor.SelectCursor;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.AbstractRenderCodeCompiler;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.AutoCompleteSuggestion;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.impl.DefaultCodeCompiler;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.scriptimpl.ScriptCodeCompiler;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.elements.Scrollbar;
import xyz.wagyourtail.jsmacros.client.gui.overlays.ConfirmOverlay;
import xyz.wagyourtail.jsmacros.client.gui.overlays.SelectorDropdownOverlay;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.library.impl.classes.FileHandler;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class EditorScreen extends BaseScreen {
    private final static IChatComponent ellipses = new ChatComponentText("...");
    static {
        ellipses.getChatStyle().setColor(EnumChatFormatting.DARK_GRAY);
    }
    public static final List<String> langs = Lists.newArrayList("javascript", "json", "lua", "python", "ruby", "typescript");
    public static ChatStyle defaultStyle = new ChatStyle();
    protected final File file;
    protected final FileHandler handler;
    public final History history;
    public final SelectCursor cursor;
    private int ellipsesWidth;
    protected String savedString;
    protected IChatComponent fileName = new ChatComponentText("");
    protected String lineCol = "";
    protected Scrollbar scrollbar;
    protected Button saveBtn;
    protected int scroll = 0;
    protected int lineSpread = 0;
    protected int firstLine = 0;
    protected int lastLine;
    public boolean blockFirst = false;
    public long textRenderTime = 0;
    public char prevChar = '\0';
    public String language;
    public AbstractRenderCodeCompiler codeCompiler;
    
    public EditorScreen(GuiScreen parent, @NotNull File file) {
        super(new ChatComponentText("Editor"), parent);
        this.file = file;
        FileHandler handler = new FileHandler(file);
        String content;
        if (file.exists()) {
            try {
                content = handler.read();
            } catch (IOException e) {
                content = I18n.format("jsmacros.erroropening") + e.toString();
                handler = null;
            }
        } else {
            content = "";
        }
        savedString = content;
        
        this.handler = handler;
        defaultStyle = new ChatStyle();
        
        cursor = new SelectCursor(defaultStyle, fontRendererObj);
        
        this.history = new History(content.replaceAll("\r\n", "\n").replaceAll("\t", "    "), cursor);
        
        cursor.updateStartIndex(0, history.current);
        cursor.updateEndIndex(0, history.current);
        cursor.dragStartIndex = 0;
    }
    
    public String getDefaultLanguage() {
        final String[] fname = file.getName().split("\\.", -1);
        String ext = fname[fname.length - 1].toLowerCase();
        
        switch (ext) {
            case "py":
                return "python";
            case "lua":
                return "lua";
            case "json":
                return "json";
            case "rb":
                return "ruby";
            default:
                return "javascript";
        }
    }
    
    public static void openAndScrollToIndex(@NotNull File file, int startIndex, int endIndex) {
        Minecraft mc = Minecraft.getMinecraft();
        int finalEndIndex = endIndex == -1 ? startIndex : endIndex;
        mc.addScheduledTask(() -> {
            EditorScreen screen;
            try {
                if (JsMacros.prevScreen instanceof EditorScreen &&
                    ((EditorScreen) JsMacros.prevScreen).file.getCanonicalPath().equals(file.getCanonicalPath())) {
                    screen = (EditorScreen) JsMacros.prevScreen;
                } else {
                    screen = new EditorScreen(JsMacros.prevScreen, file);
                }
                screen.cursor.updateStartIndex(startIndex, screen.history.current);
                screen.cursor.updateEndIndex(finalEndIndex, screen.history.current);
                mc.displayGuiScreen(screen);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    
    public static void openAndScrollToLine(@NotNull File file, int line, int col, int endCol) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.addScheduledTask(() -> {
            EditorScreen screen;
            try {
                if (JsMacros.prevScreen instanceof EditorScreen &&
                    ((EditorScreen) JsMacros.prevScreen).file.getCanonicalPath().equals(file.getCanonicalPath())) {
                    screen = (EditorScreen) JsMacros.prevScreen;
                } else {
                    screen = new EditorScreen(JsMacros.prevScreen, file);
                }
                String[] lines = screen.history.current.split("\n", -1);
                int lineIndex = 0;
                int max = Math.min(lines.length - 1, line - 1);
                for (int i = 0; i < max; ++i) {
                    lineIndex += lines[i].length() + 1;
                }
                int startIndex;
                if (col == -1) startIndex = lineIndex;
                else startIndex = lineIndex + Math.min(lines[max].length(), col);
                screen.cursor.updateStartIndex(startIndex, screen.history.current);
                if (endCol == -1) startIndex = lineIndex + lines[max].length();
                else startIndex = lineIndex + Math.min(lines[max].length(), endCol);
                screen.cursor.updateEndIndex(startIndex, screen.history.current);
                mc.displayGuiScreen(screen);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    
    public void setScroll(double pages) {
        scroll = (int) ((height - 24) * pages);
        int add = lineSpread - scroll % lineSpread;
        if (add == lineSpread) add = 0;
        firstLine = (int) Math.ceil(scroll / (double) lineSpread);
        lastLine = (int) (firstLine + (height - 24 - add) / (double) lineSpread) - 1;
    }
    
    public synchronized void setLanguage(String language) {
        this.language = language;
        Map<String, String> linterOverrides = Core.instance.config.getOptions(ClientConfigV2.class).editorLinterOverrides;
        if (linterOverrides.containsKey(language)) {
            this.codeCompiler = new ScriptCodeCompiler(language, this, linterOverrides.get(language));
        } else {
            this.codeCompiler = new DefaultCodeCompiler(language, this);
        }
        compileRenderedText();
    }
    
    @Override
    public void initGui() {
        super.initGui();
        assert mc != null;
        //TODO:
        //this.font = new FontRenderer(mc.gameSettings).getFontRenderer(new Identifier("jsmacros", "ubuntumono"));
        cursor.textRenderer = this.fontRendererObj;
        
        ellipsesWidth = fontRendererObj.getStringWidth(ellipses.getFormattedText());
        lineSpread = fontRendererObj.FONT_HEIGHT + 1;
        int width = this.width - 10;
        
        scrollbar = addButton(new Scrollbar(width, 12, 10, height - 24, 0, 0xFF000000, 0xFFFFFFFF, 1, this::setScroll));
        saveBtn = addButton(new Button(width / 2, 0, width / 6, 12, fontRendererObj, needSave() ? 0xFFA0A000 : 0xFF00A000, 0xFF000000, needSave() ? 0xFF707000 : 0xFF007000, 0xFFFFFF, new ChatComponentTranslation("jsmacros.save"), (btn) -> save()));
        addButton(new Button(width * 4 / 6, 0, width / 6, 12, fontRendererObj, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentTranslation("jsmacros.close"), (btn) -> openParent()));
        addButton(new Button(width, 0, 10, 12, fontRendererObj, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentText(mc.theWorld == null ? "X" : "-"), (btn) -> onClose()));
        
        if (language == null)
            setLanguage(getDefaultLanguage());
        else
            compileRenderedText();
        
        history.onChange = (content) -> {
            if (savedString.equals(content)) {
                saveBtn.setColor(0xFF00A000);
                saveBtn.setHilightColor(0xFF007000);
            } else {
                saveBtn.setColor(0xFFA0A000);
                saveBtn.setHilightColor(0xFF707000);
            }
        };
        
        cursor.onChange = (cursor) -> {
            lineCol = (cursor.startIndex != cursor.endIndex ? (cursor.endIndex - cursor.startIndex) + " " : "") +
                (cursor.arrowEnd ? String.format("%d:%d", cursor.endLine + 1, cursor.endLineIndex + 1) : String.format("%d:%d", cursor.startLine + 1, cursor.startLineIndex + 1));
            prevChar = '\0';
            if (overlay instanceof SelectorDropdownOverlay) {
                closeOverlay(overlay);
            }
        };
        
        
        addButton(new Button(this.width - width / 8, height - 12, width / 8, 12, fontRendererObj,0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentText(language), (btn) -> {
            int height = langs.size() * (fontRendererObj.FONT_HEIGHT + 1) + 4;
            openOverlay(new SelectorDropdownOverlay(btn.xPosition, btn.yPosition - height, btn.getButtonWidth(), height, langs.stream().map(ChatComponentText::new).collect(Collectors.toList()), fontRendererObj, this, (i) -> {
                setLanguage(langs.get(i));
                btn.setMessage(new ChatComponentText(langs.get(i)));
            }));
        }));
        
        addButton(new Button(this.width - width / 4, height - 12, width / 8, 12, fontRendererObj, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentTranslation("jsmacros.settings"), (btn) -> {
            openOverlay(new SettingsOverlay(this.width / 4, this.height / 4, this.width / 2, this.height / 2, fontRendererObj, this));
        }));
        
        this.fileName = new ChatComponentText(fontRendererObj.trimStringToWidth(file.getName(), (width - 10) / 2));
        
        setScroll(0);
        scrollToCursor();
    }
    
    public void copyToClipboard() {
        assert mc != null;
        GuiScreen.setClipboardString(history.current.substring(cursor.startIndex, cursor.endIndex));
    }
    
    public void pasteFromClipboard() {
        String pasteContent = GuiScreen.getClipboardString();
        history.replace(cursor.startIndex, cursor.endIndex - cursor.startIndex, pasteContent);
        compileRenderedText();
    }
    
    public void cutToClipboard() {
        assert mc != null;
        GuiScreen.setClipboardString(history.current.substring(cursor.startIndex, cursor.endIndex));
        history.replace(cursor.startIndex, cursor.endIndex - cursor.startIndex, "");
        compileRenderedText();
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        assert mc != null;
        if (overlay == null) {
        } else if (overlay.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (GuiScreen.isKeyComboCtrlA(keyCode)) {
            cursor.updateStartIndex(0, history.current);
            cursor.updateEndIndex(history.current.length(), history.current);
            cursor.arrowEnd = true;
    
            return true;
        } else if (GuiScreen.isKeyComboCtrlC(keyCode)) {
            copyToClipboard();
            return true;
        } else if (GuiScreen.isKeyComboCtrlV(keyCode)) {
            pasteFromClipboard();
            return true;
        } else if (GuiScreen.isKeyComboCtrlX(keyCode)) {
            cutToClipboard();
            return true;
        }
        String startSpaces;
        int index;
        double currentPage;
        switch (keyCode) {
            case Keyboard.KEY_BACK:
                if (cursor.startIndex != cursor.endIndex) {
                    history.bkspacePos(cursor.startIndex, cursor.endIndex - cursor.startIndex);
                    compileRenderedText();
                } else if (cursor.startIndex > 0) {
                    history.bkspacePos(cursor.startIndex - 1, 1);
                    compileRenderedText();
                }
                return true;
            case Keyboard.KEY_DELETE:
                if (cursor.startIndex != cursor.endIndex) {
                    history.deletePos(cursor.startIndex, cursor.endIndex - cursor.startIndex);
                    compileRenderedText();
                } else if (cursor.startIndex < history.current.length()) {
                    history.deletePos(cursor.startIndex, 1);
                    compileRenderedText();
                }
                return true;
            case Keyboard.KEY_HOME:
                startSpaces = history.current.split("\n", -1)[cursor.startLine].split("[^\\s]", -1)[0];
                if (cursor.startLineIndex <= startSpaces.length()) {
                    cursor.updateStartIndex(cursor.startIndex - cursor.startLineIndex, history.current);
                } else {
                    cursor.updateStartIndex(cursor.startIndex - cursor.startLineIndex + startSpaces.length(), history.current);
                }
                if (!GuiScreen.isShiftKeyDown())
                    cursor.updateEndIndex(cursor.startIndex, history.current);
/*
                    //home to start of file impl
                    
                    cursor.updateStartIndex(0, history.current);
                    cursor.updateEndIndex(0, history.current);
                    if (scrollToPercent != null) scrollToPercent.accept(0D);
*/
                return true;
            case Keyboard.KEY_END:
                int endLineLength = history.current.split("\n", -1)[cursor.endLine].length();
                cursor.updateEndIndex(cursor.endIndex + (endLineLength - cursor.endLineIndex), history.current);
                if (!GuiScreen.isShiftKeyDown())
                    cursor.updateStartIndex(cursor.endIndex, history.current);
/*
                    //end to end of file impl

                    cursor.updateStartIndex(history.current.length(), history.current);
                    cursor.updateEndIndex(history.current.length(), history.current);
                    if (scrollToPercent != null) scrollToPercent.accept(1D);
*/
                return true;
            case Keyboard.KEY_LEFT:
                if (GuiScreen.isShiftKeyDown()) {
                    if (cursor.arrowEnd && cursor.startIndex != cursor.endIndex) {
                        cursor.updateEndIndex(cursor.endIndex - 1, history.current);
                        cursor.arrowLineIndex = cursor.endLineIndex;
                    } else {
                        cursor.updateStartIndex(cursor.startIndex - 1, history.current);
                        cursor.arrowLineIndex = cursor.startLineIndex;
                        cursor.arrowEnd = false;
                    }
                } else {
                    if (cursor.startIndex != cursor.endIndex) {
                        cursor.updateEndIndex(cursor.startIndex, history.current);
                    } else {
                        cursor.updateStartIndex(cursor.startIndex - 1, history.current);
                        cursor.updateEndIndex(cursor.startIndex, history.current);
                        cursor.arrowLineIndex = cursor.startLineIndex;
                    }
                }
                scrollToCursor();
                return true;
            case Keyboard.KEY_RIGHT:
                if (GuiScreen.isShiftKeyDown()) {
                    if (cursor.arrowEnd || cursor.endIndex == cursor.startIndex) {
                        cursor.updateEndIndex(cursor.endIndex + 1, history.current);
                        cursor.arrowLineIndex = cursor.endLineIndex;
                        cursor.arrowEnd = true;
                    } else {
                        cursor.updateStartIndex(cursor.startIndex + 1, history.current);
                        cursor.arrowLineIndex = cursor.startLineIndex;
                    }
                } else {
                    if (cursor.startIndex != cursor.endIndex) {
                        cursor.updateStartIndex(cursor.endIndex, history.current);
                    } else {
                        cursor.updateStartIndex(cursor.startIndex + 1, history.current);
                        cursor.updateEndIndex(cursor.startIndex, history.current);
                        cursor.arrowLineIndex = cursor.startLineIndex;
                    }
                }
                scrollToCursor();
                return true;
            case Keyboard.KEY_UP:
                if (GuiScreen.isAltKeyDown()) {
                    history.shiftLine(cursor.startLine, cursor.endLine - cursor.startLine + 1, false);
                    cursor.arrowEnd = false;
                    compileRenderedText();
                } else {
                    index = 0;
                    if (cursor.startLine > 0 || (cursor.arrowEnd && cursor.endLine > 0)) {
                        String[] lines = history.current.split("\n", -1);
                        int line = (cursor.arrowEnd ? cursor.endLine : cursor.startLine) - 1;
                        for (int i = 0; i < line; ++i) {
                            index += lines[i].length() + 1;
                        }
                        index += Math.min(lines[line].length(), cursor.arrowLineIndex);
                    }
                    if (GuiScreen.isShiftKeyDown()) {
                        if (cursor.arrowEnd && index >= cursor.startIndex) {
                            cursor.updateEndIndex(index, history.current);
                            cursor.arrowEnd = true;
                        } else {
                            cursor.updateStartIndex(index, history.current);
                            cursor.arrowEnd = false;
                        }
                    } else {
                        if (cursor.startIndex == cursor.endIndex) {
                            cursor.updateStartIndex(index, history.current);
                        }
                        cursor.updateEndIndex(cursor.startIndex, history.current);
                    }
                }
                scrollToCursor();
                return true;
            case Keyboard.KEY_DOWN:
                if (GuiScreen.isAltKeyDown()) {
                    history.shiftLine(cursor.startLine, cursor.endLine - cursor.startLine + 1, true);
                    cursor.arrowEnd = true;
                    compileRenderedText();
                } else {
                    index = 0;
                    String[] lines = history.current.split("\n", -1);
                    if (cursor.endLine < lines.length - 1 || (!cursor.arrowEnd && cursor.startLine < lines.length - 1)) {
                        int line = (cursor.arrowEnd ? cursor.endLine : cursor.startLine) + 1;
                        for (int i = 0; i < line; ++i) {
                            index += lines[i].length() + 1;
                        }
                        index += Math.min(lines[line].length(), cursor.arrowLineIndex);
                    } else {
                        index = history.current.length();
                    }
                    if (GuiScreen.isShiftKeyDown()) {
                        if (!cursor.arrowEnd && index <= cursor.endIndex) {
                            cursor.updateStartIndex(index, history.current);
                            cursor.arrowEnd = false;
                        } else {
                            cursor.updateEndIndex(index, history.current);
                            cursor.arrowEnd = true;
                        }
                    } else {
                        if (cursor.startIndex != cursor.endIndex) {
                            cursor.updateStartIndex(cursor.endIndex, history.current);
                        } else {
                            cursor.updateStartIndex(index, history.current);
                            cursor.updateEndIndex(cursor.startIndex, history.current);
                        }
                    }
                }
                scrollToCursor();
                return true;
            case Keyboard.KEY_Z:
                if (GuiScreen.isCtrlKeyDown()) {
                    if (GuiScreen.isShiftKeyDown()) {
                        int i = history.redo();
                        
                        if (i != -1) {
                            compileRenderedText();
                        }
                    } else {
                        int i = history.undo();
                        
                        if (i != -1) {
                            compileRenderedText();
                        }
                    }
                    return true;
                }
                break;
            case Keyboard.KEY_Y:
                if (GuiScreen.isCtrlKeyDown()) {
                    int i = history.redo();
    
                    if (i != -1) {
                        compileRenderedText();
                    }
                    return true;
                }
                break;
            case Keyboard.KEY_S:
                if (GuiScreen.isCtrlKeyDown()) {
                    save();
                    return true;
                }
                break;
            case Keyboard.KEY_RETURN:
                startSpaces = history.current.split("\n", -1)[cursor.startLine].split("[^\\s]", -1)[0];
                if (cursor.startIndex != cursor.endIndex) {
                    history.replace(cursor.startIndex, cursor.endIndex - cursor.startIndex, "\n" + startSpaces);
                    cursor.updateStartIndex(cursor.endIndex, history.current);
                } else {
                    history.add(cursor.startIndex, "\n" + startSpaces);
                }
                
                compileRenderedText();
                return true;
            case Keyboard.KEY_TAB:
                if (cursor.startIndex != cursor.endIndex || GuiScreen.isShiftKeyDown()) {
                    history.tabLines(cursor.startLine, cursor.endLine - cursor.startLine + 1, GuiScreen.isShiftKeyDown());
                } else {
                    history.addChar(cursor.startIndex, '\t');
                }
                compileRenderedText();
                return true;
            case Keyboard.KEY_PRIOR:
                currentPage = scroll / (height - 24.0D);
                scrollbar.scrollToPercent(MathHelper.clamp_double((currentPage - 1) / (calcTotalPages() - 1), 0, 1));
                break;
            case Keyboard.KEY_NEXT:
                currentPage = scroll / (height - 24.0D);
                scrollbar.scrollToPercent(MathHelper.clamp_double((currentPage + 1) / (calcTotalPages() - 1), 0, 1));
                return true;
            case Keyboard.KEY_LCONTROL:
            case Keyboard.KEY_RCONTROL:
            case Keyboard.KEY_LMETA:
            case Keyboard.KEY_RMETA:
            case Keyboard.KEY_LMENU:
            case Keyboard.KEY_RMENU:
            case Keyboard.KEY_CAPITAL:
            case Keyboard.KEY_LSHIFT:
            case Keyboard.KEY_RSHIFT:
                return true;
            default:
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    private synchronized void compileRenderedText() {
        long time = System.currentTimeMillis();
        codeCompiler.recompileRenderedText(history.current);
        List<AutoCompleteSuggestion> suggestionList = codeCompiler.getSuggestions();
    
    
        if (overlay instanceof SelectorDropdownOverlay) {
            closeOverlay(overlay);
        }
        
        if (suggestionList.size() > 0 && Core.instance.config.getOptions(ClientConfigV2.class).editorSuggestions) {
            suggestionList.sort(Comparator.comparing(a -> a.suggestion));
            int startIndex = cursor.startIndex;
            int maxWidth = 0;
            List<IChatComponent> displayList = new LinkedList<>();
            for (AutoCompleteSuggestion sug : suggestionList) {
                if (sug.startIndex < startIndex) startIndex = sug.startIndex;
                int width = fontRendererObj.getStringWidth(sug.displayText.getFormattedText());
                if (width > maxWidth) maxWidth = width;
                displayList.add(sug.displayText);
            }
            String[] lines = history.current.substring(0, startIndex).split("\n", -1);
            IChatComponent line = new ChatComponentText(lines[lines.length - 1]);
            line.setChatStyle(defaultStyle);
            int startCol = fontRendererObj.getStringWidth(line.getFormattedText());
            int add = lineSpread - scroll % lineSpread;
            if (add == lineSpread) add = 0;
            int startRow = (lines.length - firstLine + 1) * lineSpread + add;
            
            openOverlay(
                new SelectorDropdownOverlay(startCol + 30, startRow, maxWidth + 8, suggestionList.size() * lineSpread + 4, displayList, fontRendererObj, this, (i) -> {
                    if (i == -1) return;
                    AutoCompleteSuggestion selected = suggestionList.get(i);
                    history.replace(selected.startIndex, cursor.startIndex - selected.startIndex, selected.suggestion);
                    cursor.updateStartIndex(cursor.endIndex, history.current);
                    compileRenderedText();
                })
            );
            ((SelectorDropdownOverlay) overlay).setSelected(0);
        }
        textRenderTime = System.currentTimeMillis() - time;
        this.scrollbar.setScrollPages(calcTotalPages());
        scrollToCursor();
    }
    
    public void scrollToCursor() {
        int cursorLine = cursor.arrowEnd ? cursor.endLine : cursor.startLine;
        if (cursorLine < firstLine || cursorLine > lastLine) {
            int pagelength = lastLine - firstLine;
            double scrollLines = history.current.split("\n", -1).length - pagelength;
            cursorLine -= pagelength / 2;
            scrollbar.scrollToPercent(MathHelper.clamp_double(cursorLine, 0, scrollLines) / scrollLines);
        }
    }
    
    private double calcTotalPages() {
        if (history == null) return 1;
        return (history.current.split("\n", -1).length) / (Math.floor((height - 24) / (double) lineSpread) - 1D);
    }
    
    public void save() {
        if (needSave()) {
            String current = history.current;
            try {
                handler.write(current);
                savedString = current;
                saveBtn.setColor(0xFF00A000);
                saveBtn.setHilightColor(0xFF707000);
            } catch (IOException e) {
                openOverlay(new ConfirmOverlay(this.width / 4, height / 4, this.width / 2, height / 2, fontRendererObj, new ChatComponentTranslation("jsmacros.errorsaving").appendSibling(new ChatComponentText("\n\n" + e.getMessage())), this, null));
            }
        }
    }
    
    public boolean needSave() {
        return !savedString.equals(history.current);
    }
    
    @Override
    public boolean mouseScrolled(int mouseX, int mouseY, int amount) {
        if (overlay == null && scrollbar != null) {
            scrollbar.mouseDragged(mouseX, mouseY, 0, 0, -amount * 2);
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float delta) {
        assert mc != null;
        drawDefaultBackground();
        
        fontRendererObj.drawStringWithShadow(fileName.getFormattedText(), 2, 2, 0xFFFFFF);
        
        fontRendererObj.drawStringWithShadow(String.format("%d ms", (int) textRenderTime), 2, height - 10, 0xFFFFFF);
        fontRendererObj.drawStringWithShadow(lineCol, width - fontRendererObj.getStringWidth(lineCol) - (width - 10) / 8F - 2, height - 10, 0xFFFFFF);
        
        drawRect(0, 12, width - 10, height - 12, 0xFF2B2B2B);
        drawRect(28, 12, 29, height - 12, 0xFF707070);
        drawRect(0, 12, 1, height - 12, 0xFF707070);
        drawRect(width - 11, 12, width - 10, height - 12, 0xFF707070);
        drawRect(1, 12, width - 11, 13, 0xFF707070);
        drawRect(1, height - 13, width - 11, height - 12, 0xFF707070);
        
        int add = lineSpread - scroll % lineSpread;
        if (add == lineSpread) add = 0;
        int y = 13;
        
        final IChatComponent[] renderedText = codeCompiler.getRenderedText();
    
        for (int i = 0, j = firstLine; j <= lastLine && j < renderedText.length; ++i, ++j) {
            if (cursor.startLine == j && cursor.endLine == j) {
                drawRect(30 + cursor.startCol, y + add + i * lineSpread, 30 + cursor.endCol, y + add + (i + 1) * lineSpread, 0xFF33508F);
            } else if (cursor.startLine == j) {
                drawRect(30 + cursor.startCol, y + add + i * lineSpread, width - 10, y + add + (i + 1) * lineSpread, 0xFF33508F);
            } else if (j > cursor.startLine && j < cursor.endLine) {
                drawRect(29, y + add + i * lineSpread, width - 10, y + add + (i + 1) * lineSpread, 0xFF33508F);
            } else if (cursor.endLine == j) {
                drawRect(29, y + add + i * lineSpread, 30 + cursor.endCol, y + add + (i + 1) * lineSpread, 0xFF33508F);
            }
            ChatComponentText lineNum = new ChatComponentText(String.format("%d.", j + 1));
            fontRendererObj.drawString(lineNum.getFormattedText(), 28 - fontRendererObj.getStringWidth(lineNum.getFormattedText()), y + add + i * lineSpread, 0xD8D8D8);
            fontRendererObj.drawString(trim(renderedText[j]), 30, y + add + i * lineSpread, 0xFFFFFF);
        }
        
        for (GuiButton b : ImmutableList.copyOf(this.buttonList)) {
            b.drawButton(mc, mouseX, mouseY);
        }
        
        super.drawScreen(mouseX, mouseY, delta);
    }
    
    private String trim(IChatComponent text) {
        assert mc != null;
        if (fontRendererObj.getStringWidth(text.getFormattedText()) > width - 30) {
            String trimmed = fontRendererObj.trimStringToWidth(text.getFormattedText(), width - 40 - ellipsesWidth);
            return trimmed + ellipses.getFormattedText();
        } else {
            return text.getFormattedText();
        }
    }
    
    @Override
    public void openParent() {
        if (needSave()) {
            openOverlay(new ConfirmOverlay(width / 4, height / 4, width / 2, height / 2, fontRendererObj, new ChatComponentTranslation("jsmacros.nosave"), this, (container) -> super.openParent()));
        } else {
            super.openParent();
        }
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int btn) throws IOException {
        super.mouseClicked(mouseX, mouseY, btn);
        //if (!handled) {
        //if (this.selectedButton == null) {
        for (GuiButton b : buttonList) {
            if (b.mousePressed(this.mc, mouseX, mouseY)) return;
        }
        int index = getIndexPosition(mouseX - 30, mouseY - 12 + 1);
        if (btn != 1 || (cursor.startIndex > index || cursor.endIndex < index)) {
            if (GuiScreen.isShiftKeyDown()) {
                if (index < cursor.dragStartIndex) {
                    cursor.updateEndIndex(cursor.dragStartIndex, history.current);
                    cursor.updateStartIndex(index, history.current);
                    cursor.arrowEnd = false;
                    cursor.arrowLineIndex = cursor.startLineIndex;
                } else {
                    cursor.updateStartIndex(cursor.dragStartIndex, history.current);
                    cursor.updateEndIndex(index, history.current);
                    cursor.arrowEnd = true;
                    cursor.arrowLineIndex = cursor.endLineIndex;
                }
            } else {
                if (cursor.startIndex == index && cursor.endIndex == index) {
                    selectWordAtCursor();
                } else {
                    cursor.updateStartIndex(index, history.current);
                    cursor.updateEndIndex(index, history.current);
                }
                cursor.dragStartIndex = index;
                cursor.arrowEnd = false;
                cursor.arrowLineIndex = cursor.startLineIndex;
            }
        }
        if (btn == 1) {
            openRClickMenu(index, mouseX, mouseY);
        }
    }
    
    private void openRClickMenu(int index, int mouseX, int mouseY) {
        Map<String, Runnable> options = new LinkedHashMap<>();
        if (cursor.startIndex != cursor.endIndex) {
            options.put("cut", this::cutToClipboard);
            options.put("copy", this::copyToClipboard);
        }
        options.put("paste", this::pasteFromClipboard);
        options.putAll(codeCompiler.getRightClickOptions(index));
        openOverlay(new SelectorDropdownOverlay(mouseX, mouseY, 100, (fontRendererObj.FONT_HEIGHT + 1) * options.size() + 4, options.keySet().stream().map(ChatComponentText::new).collect(Collectors.toList()), fontRendererObj, this, i -> options.values().toArray(new Runnable[0])[i].run()));
    }
    
    private int getIndexPosition(double x, double y) {
        assert mc != null;
        int add = lineSpread - scroll % lineSpread;
        if (add == lineSpread) add = 0;
        int line = firstLine + (int) ((y - add) / (double) lineSpread);
        if (line < 0) line = 0;
        String[] lines = history.current.split("\n", -1);
        int col;
        if (line >= lines.length) {
            line = lines.length - 1;
            col = lines[lines.length - 1].length();
        } else {
            col = fontRendererObj.trimStringToWidth(lines[line], (int) x).length();
        }
        int count = 0;
        for (int i = 0; i < line; ++i) {
            count += lines[i].length() + 1;
        }
        count += col;
        return count;
    }
    
    public void selectWordAtCursor() {
        String currentLine = history.current.split("\n", -1)[cursor.startLine];
        String[] startWords = currentLine.substring(0, cursor.startLineIndex).split("\\b");
        int dStart = -startWords[startWords.length - 1].length();
        String[] endWords = currentLine.substring(cursor.startLineIndex).split("\\b");
        int dEnd = endWords[0].length();
        int currentIndex = cursor.startIndex;
        cursor.updateStartIndex(currentIndex + dStart, history.current);
        cursor.updateEndIndex(currentIndex + dEnd, history.current);
    }
    
    @Override
    public boolean mouseDragged(int mouseX, int mouseY, int button, int deltaX, int deltaY) {
        if (!(((IScreen) this).getFocused() instanceof Scrollbar) && button == 0 && overlay == null) {
            int index = getIndexPosition(mouseX - 30, mouseY - 12);
            if (index == cursor.dragStartIndex) {
                cursor.updateStartIndex(index, history.current);
                cursor.updateEndIndex(index, history.current);
            } else if (index < cursor.dragStartIndex) {
                cursor.updateEndIndex(cursor.dragStartIndex, history.current);
                cursor.arrowEnd = false;
                cursor.updateStartIndex(index, history.current);
                cursor.arrowLineIndex = cursor.startLineIndex;
            } else {
                cursor.updateStartIndex(cursor.dragStartIndex, history.current);
                cursor.arrowEnd = true;
                cursor.updateEndIndex(index, history.current);
                cursor.arrowLineIndex = cursor.endLineIndex;
            }
            return true;
        }
        return true;
    }
    
    @Override
    public void updateSettings() {
        assert mc != null;
        cursor.updateStartIndex(cursor.startIndex, history.current);
        cursor.updateEndIndex(cursor.endIndex, history.current);
        setLanguage(language);
    }
    
    @Override
    public synchronized void keyTyped(char chr, int keyCode) {
        if (blockFirst) {
            blockFirst = false;
        } else {
            if (cursor.startIndex != cursor.endIndex) {
                history.replace(cursor.startIndex, cursor.endIndex - cursor.startIndex, String.valueOf(chr));
                cursor.updateStartIndex(cursor.endIndex, history.current);
            } else {
                if (cursor.startIndex < history.current.length() &&
                    ((chr == ']' && history.current.charAt(cursor.startIndex) == ']') ||
                    (chr == '}' && history.current.charAt(cursor.startIndex) == '}')||
                    (chr == ')' &&  history.current.charAt(cursor.startIndex) == ')'))) {
                    cursor.updateEndIndex(cursor.startIndex + 1, history.current);
                    cursor.updateStartIndex(cursor.endIndex, history.current);
                } else {
                    history.addChar(cursor.startIndex, chr);
                }
            }
            switch (chr) {
                case '[':
                    if (countCharBefore('[', cursor.startIndex) > countCharAfter(']', cursor.startIndex)) {
                        history.addChar(cursor.startIndex, ']');
                        cursor.updateStartIndex(cursor.startIndex - 1, history.current);
                        cursor.updateEndIndex(cursor.startIndex, history.current);
                    }
                    break;
                case '{':
                    if (countCharBefore('{', cursor.startIndex) > countCharAfter('}', cursor.startIndex)) {
                        history.addChar(cursor.startIndex, '}');
                        cursor.updateStartIndex(cursor.startIndex - 1, history.current);
                        cursor.updateEndIndex(cursor.startIndex, history.current);
                    }
                    break;
                case '(':
                    if (countCharBefore('(', cursor.startIndex) > countCharAfter(')', cursor.startIndex)) {
                        history.addChar(cursor.startIndex, ')');
                        cursor.updateStartIndex(cursor.startIndex - 1, history.current);
                        cursor.updateEndIndex(cursor.startIndex, history.current);
                    }
                    break;
                default:
            }
            prevChar = chr;
            compileRenderedText();
        }
    }

    private int countCharBefore(char chr, int startIndex) {
        int count = 0;
        for (int i = startIndex - 1; i >= 0; --i) {
            if (history.current.charAt(i) == chr) ++count;
            else break;
        }
        return count;
    }

    private int countCharAfter(char chr, int startIndex) {
        int count = 0;
        for (int i = startIndex; i < history.current.length(); ++i) {
            if (history.current.charAt(i) == chr) ++count;
            else break;
        }
        return count;
    }
    
}
