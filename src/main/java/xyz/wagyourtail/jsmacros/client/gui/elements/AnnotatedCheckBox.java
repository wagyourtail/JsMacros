package xyz.wagyourtail.jsmacros.client.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AnnotatedCheckBox extends Button {
    public boolean value;
    
    public AnnotatedCheckBox(int xPosition, int yPosition, int width, int height, FontRenderer textRenderer, int color, int borderColor, int hilightColor, int textColor, IChatComponent message, boolean initialValue, Consumer<Button> onPress) {
        super(xPosition, yPosition, width, height, textRenderer, color, borderColor, hilightColor, textColor, message, onPress);
        value = initialValue;
        horizCenter = false;
    }
    
    @Override
    public void onPress() {
        value = !value;
        super.onPress();
    }
    
    @Override
    public void setMessage(IChatComponent message) {
        setMessageSuper(message);
        int width = this.width - height;
        this.textLines = textRenderer.listFormattedStringToWidth(message.getFormattedText(), width - 4).stream().map(ChatComponentText::new).collect(Collectors.toList());
        this.visibleLines = Math.min(Math.max((height - 2) / textRenderer.FONT_HEIGHT, 1), textLines.size());
        this.verticalCenter = ((height - 4) - (visibleLines * textRenderer.FONT_HEIGHT)) / 2;
    }
    
    @Override
    protected void renderMessage() {
        int width = this.width - height;
        for (int i = 0; i < visibleLines; ++i) {
            int w = textRenderer.getStringWidth(textLines.get(i).getFormattedText());
            textRenderer.drawString(textLines.get(i).getFormattedText(), horizCenter ? (int) (xPosition + width / 2F - w / 2F) : xPosition + 1, yPosition + 2 + verticalCenter + (i * textRenderer.FONT_HEIGHT), textColor);
        }
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
        this.renderMessage();
        
            // fill
            if (mouseX - xPosition >= 0 && mouseX - xPosition - width <= 0 && mouseY - yPosition >= 0 && mouseY - yPosition - height <= 0 && this.enabled || forceHover) {
                hovering = true;
                drawRect(xPosition + width - height + 1, yPosition + 1, xPosition + width - 1, yPosition + height - 1, hilightColor);
            } else {
                hovering = false;
                if (value) {
                drawRect(xPosition + width - height + 1, yPosition + 1, xPosition + width - 1, yPosition + height - 1, color);
                }
            }
            // outline
            drawRect(xPosition + width - height, yPosition, xPosition + width - height + 1, yPosition + height, borderColor);
            drawRect(xPosition + width - 1, yPosition, xPosition + width, yPosition + height, borderColor);
            drawRect(xPosition + width - height + 1, yPosition, xPosition + width - 1, yPosition + 1, borderColor);
            drawRect(xPosition  + width - height + 1, yPosition + height - 1, xPosition + width - 1, yPosition + height, borderColor);
        }
    }
}
