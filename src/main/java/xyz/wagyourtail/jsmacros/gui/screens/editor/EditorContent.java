package xyz.wagyourtail.jsmacros.gui.screens.editor;

import io.noties.prism4j.Prism4j;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import xyz.wagyourtail.jsmacros.gui.elements.Button;
import xyz.wagyourtail.jsmacros.gui.screens.editor.hilighting.PrismGrammarLocator;
import xyz.wagyourtail.jsmacros.gui.screens.editor.hilighting.TextStyleCompiler;

import java.util.List;
import java.util.function.Consumer;

public class EditorContent extends Button {
    private final static Prism4j prism4j = new Prism4j(new PrismGrammarLocator());
    
    /*TODO: make py script to convert a copyleft monospaced font to png's for minecraft, or add class for on the fly from a ttf*/
    public final static Style defaultStyle = Style.EMPTY.withFont(new Identifier("jsmacros", "uniform"));
    
    private String language = "javascript";
    
    public Consumer<String> onChange;
    public Consumer<Double> updateScrollPages;
    
    protected LiteralText[] renderedText;
    protected History history;
    
    protected int selColor;
    
    protected int selStartLine;
    protected int selStartCol;
    public int selStartIndex;
    
    protected int selEndLine;
    protected int selEndCol;
    public int selEndIndex;
    
    protected int arrowCursor;
    protected double scroll = 0;
    
    protected int lineSpread = mc.textRenderer.fontHeight + 1;
    protected int firstLine = 0;
    protected int lastLine;
    
    public EditorContent(int x, int y, int width, int height, int color, int borderColor, int hilightColor, int textColor, String message, Consumer<Button> onClick, Consumer<String> onChange, Consumer<Double> updateScrollPages) {
        super(x, y, width, height, color, borderColor, color, textColor, new LiteralText(""), onClick);
        lastLine = (int) (height / (double) lineSpread) - 1;
        this.selColor = hilightColor;
        message = message.replaceAll("\r\n", "\n"); //no, bad windows.
        this.history = new History(message);
        this.onChange = onChange;
        this.updateScrollPages = updateScrollPages;
        this.updateSelStart(message.length());
        this.updateSelEnd(message.length());
        this.arrowCursor = message.length();
        compileRenderedText();
    }
    
    @Override
    public EditorContent setPos(int x, int y, int width, int height) {
        super.setPos(x, y, width, height);
        this.setScroll(scroll);
        return this;
    }
    
    public synchronized void compileRenderedText() {
        final List<Prism4j.Node> nodes = prism4j.tokenize(history.current, prism4j.grammar(language));
        final TextStyleCompiler visitor = new TextStyleCompiler(defaultStyle.withColor(TextColor.fromRgb(textColor)));
        visitor.visit(nodes);
        renderedText = visitor.getResult().toArray(new LiteralText[0]);
    }
    
    public synchronized void setLanguage(String language) {
        this.language = language;
        compileRenderedText();
    }
    
    public void setScroll(double distance) {
        scroll = distance;
        firstLine = (int) Math.ceil(scroll / lineSpread);
        lastLine = (int) (firstLine + height / (double) lineSpread) - 1;
    }
    
    public void updateSelStart(int startIndex) {
        selStartIndex = startIndex;
        String[] prev = history.current.substring(0, startIndex).split("\n");
        selStartLine = prev.length;
        selStartCol = prev[prev.length - 1].length();
    }
    
    public void updateSelEnd(int endIndex) {
        selEndIndex = endIndex;
        String[] prev = history.current.substring(0, endIndex).split("\n");
        selEndLine = prev.length;
        selEndCol = prev[prev.length - 1].length();
    }
    
    public void swapStartEnd() {
        int temp1 = selStartIndex;
        updateSelStart(selEndIndex);
        updateSelEnd(temp1);
        
    }
    
    public boolean clicked(double mouseX, double mouseY) {
        boolean bl = super.clicked(mouseX, mouseY);
        if (this.isFocused() ^ bl) this.changeFocus(true);
        return bl;
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        fill(matrices, x, y, x + width, y + height, color);
        fill(matrices, x + 28, y, x + 29, y + height, borderColor);
        
        Style lineNumStyle = defaultStyle.withColor(TextColor.fromRgb(textColor));
        if (renderedText != null) for (int i = firstLine; i <= lastLine && i < renderedText.length; ++i) {
            LiteralText lineNum = (LiteralText) new LiteralText(String.format("%d.", i + 1)).setStyle(lineNumStyle);
            mc.textRenderer.draw(matrices, lineNum, x + 28 - mc.textRenderer.getWidth(lineNum), y + i * lineSpread, 0xFFFFFF);
            mc.textRenderer.draw(matrices, renderedText[i], x + 30, y + (lineSpread - (int) scroll % lineSpread) + i * lineSpread, 0xFFFFFF);
        }
    }
    
}