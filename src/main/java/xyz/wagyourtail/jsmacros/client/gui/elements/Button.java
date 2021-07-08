package xyz.wagyourtail.jsmacros.client.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Button extends GuiButton {
    protected final FontRenderer textRenderer;
    protected int color;
    protected int borderColor;
    protected int hilightColor;
    protected int textColor;
    protected List<IChatComponent> textLines;
    protected int visibleLines;
    protected int verticalCenter;
    public boolean horizCenter = true;
    public Consumer<Button> onPress;
    public boolean hovering = false;
    public boolean forceHover = false;
    
    public Button(int x, int y, int width, int height, FontRenderer textRenderer, int color, int borderColor, int hilightColor, int textColor, IChatComponent message, Consumer<Button> onPress) {
        super(1, x, y, width, height, message.getFormattedText());
        this.textRenderer = textRenderer;
        this.color = color;
        this.borderColor = borderColor;
        this.hilightColor = hilightColor;
        this.textColor = textColor;
        this.onPress = onPress;
        this.setMessage(message);
    }
    
    public Button setPos(int x, int y, int width, int height) {
        this.xPosition = x;
        this.yPosition = y;
        this.width = width;
        this.height = height;
        return this;
    }
    
    public boolean cantRenderAllText() {
        return this.textLines.size() > this.visibleLines;
    }
    
    protected void setMessageSuper(IChatComponent message) {
        displayString = message.getFormattedText();
    }
    
    public void setMessage(IChatComponent message) {
        displayString = message.getFormattedText();
        this.textLines = textRenderer.listFormattedStringToWidth(message.getFormattedText(), width - 4).stream().map(ChatComponentText::new).collect(Collectors.toList());
        this.visibleLines = Math.min(Math.max((height - 2) / textRenderer.FONT_HEIGHT, 1), textLines.size());
        this.verticalCenter = ((height - 4) - (visibleLines * textRenderer.FONT_HEIGHT)) / 2;
    }
    
    public void setColor(int color) {
        this.color = color;
    }
    
    public void setHilightColor(int color) {
        this.hilightColor = color;
    }
    
    protected void renderMessage() {
        for (int i = 0; i < visibleLines; ++i) {
            int w = textRenderer.getStringWidth(textLines.get(i).getFormattedText());
            textRenderer.drawString(textLines.get(i).getFormattedText(), horizCenter ? (int) (xPosition + width / 2F - w / 2F) : xPosition + 1, yPosition + 2 + verticalCenter + (i * textRenderer.FONT_HEIGHT), textColor);
        }
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            // fill
            if (mouseX - xPosition >= 0 && mouseX - xPosition - width <= 0 && mouseY - yPosition >= 0 && mouseY - yPosition - height <= 0 && this.enabled || forceHover) {
                hovering = true;
                drawRect(xPosition + 1, yPosition + 1, xPosition + width - 1, yPosition + height - 1, hilightColor);
            } else {
                hovering = false;
                drawRect(xPosition + 1, yPosition + 1, xPosition + width - 1, yPosition + height - 1, color);
            }
            // outline
            drawRect(xPosition, yPosition, xPosition + 1, yPosition + height, borderColor);
            drawRect(xPosition + width - 1, yPosition, xPosition + width, yPosition + height, borderColor);
            drawRect(xPosition + 1, yPosition, xPosition + width - 1, yPosition + 1, borderColor);
            drawRect(xPosition + 1, yPosition + height - 1, xPosition + width - 1, yPosition + height, borderColor);
            this.renderMessage();
        }
    }
    
    
    @Override
    public void mouseReleased(int mouseX, int mouseY)
    {
        if(this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height) {
            onPress();
        }
    }
    
    public void onPress() {
        if (onPress != null) onPress.accept(this);
    }

}
