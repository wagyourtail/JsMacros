package xyz.wagyourtail.jsmacros.client.gui.elements;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AnnotatedCheckBox extends Button {
    public boolean value;
    
    public AnnotatedCheckBox(int x, int y, int width, int height, TextRenderer textRenderer, int color, int borderColor, int hilightColor, int textColor, Text message, boolean initialValue, Consumer<Button> onPress) {
        super(x, y, width, height, textRenderer, color, borderColor, hilightColor, textColor, message, onPress);
        value = initialValue;
        horizCenter = false;
    }
    
    @Override
    public void onPress() {
        value = !value;
        super.onPress();
    }
    
    @Override
    public void setMessage(Text message) {
        setMessageSuper(message);
        int width = this.width - height;
        this.textLines = textRenderer.wrapStringToWidthAsList(message.asFormattedString(), width - 4).stream().map(LiteralText::new).collect(Collectors.toList());
        this.visibleLines = Math.min(Math.max((height - 2) / textRenderer.fontHeight, 1), textLines.size());
        this.verticalCenter = ((height - 4) - (visibleLines * textRenderer.fontHeight)) / 2;
    }
    
    @Override
    protected void renderMessage() {
        int width = this.width - height;
        for (int i = 0; i < visibleLines; ++i) {
            int w = textRenderer.getStringWidth(textLines.get(i).asFormattedString());
            textRenderer.draw(textLines.get(i).asFormattedString(), horizCenter ? x + width / 2F - w / 2F : x + 1, y + 2 + verticalCenter + (i * textRenderer.fontHeight), textColor);
        }
    }
    
    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (this.visible) {
        this.renderMessage();
        
            // fill
            if (mouseX - x >= 0 && mouseX - x - width <= 0 && mouseY - y >= 0 && mouseY - y - height <= 0 && this.active || forceHover) {
                hovering = true;
                fill(x + width - height + 1, y + 1, x + width - 1, y + height - 1, hilightColor);
            } else {
                hovering = false;
                if (value) {
                fill(x + width - height + 1, y + 1, x + width - 1, y + height - 1, color);
                }
            }
            // outline
            fill(x + width - height, y, x + width - height + 1, y + height, borderColor);
            fill(x + width - 1, y, x + width, y + height, borderColor);
            fill(x + width - height + 1, y, x + width - 1, y + 1, borderColor);
            fill(x  + width - height + 1, y + height - 1, x + width - 1, y + height, borderColor);
        }
    }
}
