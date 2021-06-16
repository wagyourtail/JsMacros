package xyz.wagyourtail.jsmacros.client.gui.elements;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Button extends AbstractPressableButtonWidget {
    protected final TextRenderer textRenderer;
    protected int color;
    protected int borderColor;
    protected int hilightColor;
    protected int textColor;
    protected List<Text> textLines;
    protected int visibleLines;
    protected int verticalCenter;
    public boolean horizCenter = true;
    public Consumer<Button> onPress;
    public boolean hovering = false;
    public boolean forceHover = false;
    
    public Button(int x, int y, int width, int height, TextRenderer textRenderer, int color, int borderColor, int hilightColor, int textColor, Text message, Consumer<Button> onPress) {
        super(x, y, width, height, message.asFormattedString());
        this.textRenderer = textRenderer;
        this.color = color;
        this.borderColor = borderColor;
        this.hilightColor = hilightColor;
        this.textColor = textColor;
        this.onPress = onPress;
        this.setMessage(message);
    }
    
    public Button setPos(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        return this;
    }
    
    public boolean cantRenderAllText() {
        return this.textLines.size() > this.visibleLines;
    }
    
    protected void setMessageSuper(Text message) {
        super.setMessage(message.asFormattedString());
    }
    
    public void setMessage(Text message) {
        super.setMessage(message.asFormattedString());
        this.textLines = textRenderer.wrapStringToWidthAsList(message.asFormattedString(), width - 4).stream().map(LiteralText::new).collect(Collectors.toList());
        this.visibleLines = Math.min(Math.max((height - 2) / textRenderer.fontHeight, 1), textLines.size());
        this.verticalCenter = ((height - 4) - (visibleLines * textRenderer.fontHeight)) / 2;
    }
    
    public void setColor(int color) {
        this.color = color;
    }
    
    public void setHilightColor(int color) {
        this.hilightColor = color;
    }
    
    protected void renderMessage() {
        for (int i = 0; i < visibleLines; ++i) {
            int w = textRenderer.getStringWidth(textLines.get(i).asFormattedString());
            textRenderer.draw(textLines.get(i).asFormattedString(), horizCenter ? x + width / 2F - w / 2F : x + 1, y + 2 + verticalCenter + (i * textRenderer.fontHeight), textColor);
        }
    }
    
    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (this.visible) {
            // fill
            if (mouseX - x >= 0 && mouseX - x - width <= 0 && mouseY - y >= 0 && mouseY - y - height <= 0 && this.active || forceHover) {
                hovering = true;
                fill(x + 1, y + 1, x + width - 1, y + height - 1, hilightColor);
            } else {
                hovering = false;
                fill(x + 1, y + 1, x + width - 1, y + height - 1, color);
            }
            // outline
            fill(x, y, x + 1, y + height, borderColor);
            fill(x + width - 1, y, x + width, y + height, borderColor);
            fill(x + 1, y, x + width - 1, y + 1, borderColor);
            fill(x + 1, y + height - 1, x + width - 1, y + height, borderColor);
            this.renderMessage();
        }
    }
    
    @Override
    public void onClick(double mouseX, double mouseY) {
        //super.onClick(mouseX, mouseY);
    }
    
    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
    }
    
    @Override
    public void onPress() {
        if (onPress != null) onPress.accept(this);
    }

}
