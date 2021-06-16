package xyz.wagyourtail.jsmacros.client.gui.editor;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public class SelectCursor {
    public Consumer<SelectCursor> onChange;
    public Style defaultStyle;
    public TextRenderer textRenderer;
    public int startLine = 0;
    public int endLine = 0;
    
    public int startIndex = 0;
    public int endIndex = 0;
    
    public int startLineIndex = 0;
    public int endLineIndex = 0;
    
    public int dragStartIndex = 0;
    
    public int arrowLineIndex = 0;
    public boolean arrowEnd = false;
    
    public int startCol = 0;
    public int endCol = 0;
    
    public SelectCursor(Style defaultStyle, TextRenderer textRenderer) {
        this.defaultStyle = defaultStyle;
        this.textRenderer = textRenderer;
    }
    
    public synchronized void updateStartIndex(int startIndex, String current) {
        this.startIndex = MathHelper.clamp(startIndex, 0, current.length());
        String[] prev = current.substring(0, this.startIndex).split("\n", -1);
        startLine = prev.length - 1;
        if (textRenderer != null) startCol = textRenderer.getStringWidth(new LiteralText(prev[startLine]).setStyle(defaultStyle).asFormattedString()) - 1;
        startLineIndex = prev[startLine].length();
        if (onChange != null) onChange.accept(this);
    }
    
    public synchronized void updateEndIndex(int endIndex, String current) {
        this.endIndex = MathHelper.clamp(endIndex, 0, current.length());
        String[] prev = current.substring(0, this.endIndex).split("\n", -1);
        endLine = prev.length - 1;
        if (textRenderer != null) endCol = textRenderer.getStringWidth(new LiteralText(prev[endLine]).setStyle(defaultStyle).asFormattedString());
        endLineIndex = prev[endLine].length();
        if (onChange != null) onChange.accept(this);
    }
}
