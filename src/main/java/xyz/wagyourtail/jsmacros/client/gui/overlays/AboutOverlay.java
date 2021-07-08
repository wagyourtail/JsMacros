package xyz.wagyourtail.jsmacros.client.gui.overlays;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;

import java.util.List;

public class AboutOverlay extends OverlayContainer {
    private List<OrderedText> text;
    private int lines;
    private int vcenter;

    public AboutOverlay(int x, int y, int width, int height, TextRenderer textRenderer, IOverlayParent parent) {
        super(x, y, width, height, textRenderer, parent);
    }
    
    @Override
    public void init() {
        super.init();
        int w = width - 4;
        this.addButton(new Button(x + width - 12, y + 2, 10, 10, textRenderer,0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("X"), (btn) -> this.close()));
        
        this.addButton(new Button(x + 2, y + height - 14, w / 3, 12, textRenderer,0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("Website"), (btn) -> Util.getOperatingSystem().open("https://jsmacros.wagyourtail.xyz")));
        
        this.addButton(new Button(x + w / 3 + 2, y + height - 14, w / 3, 12, textRenderer, 0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("Discord"), (btn) -> Util.getOperatingSystem().open("https://discord.gg/P6W58J8")));
        

        this.addButton(new Button(x + w * 2 / 3 + 2, y + height - 14, w / 3, 12, textRenderer, 0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("CurseForge"), (btn) -> Util.getOperatingSystem().open("https://www.curseforge.com/minecraft/mc-mods/jsmacros")));
        
        this.setMessage(new TranslatableText("jsmacros.aboutinfo"));
    }
    
    public void setMessage(Text message) {
        this.text = textRenderer.wrapLines(message, width - 6);
        this.lines = Math.min(Math.max((height - 27) / textRenderer.fontHeight, 1), text.size());
        this.vcenter = ((height - 12) - (lines * textRenderer.fontHeight)) / 2;
    }
    
    protected void renderMessage(MatrixStack matrices) {
        for (int i = 0; i < lines; ++i) {
            int w = textRenderer.getWidth(text.get(i));
            textRenderer.draw(matrices, text.get(i), x + width / 2F - w / 2F, y + 2 + vcenter + (i * textRenderer.fontHeight), 0xFFFFFF);
        }
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        
        textRenderer.drawTrimmed(new TranslatableText("jsmacros.about"), x + 3, y + 3, width - 14, 0xFFFFFF);
        renderMessage(matrices);
        
        fill(matrices, x + 2, y + 12, x + width - 2, y + 13, 0xFFFFFFFF);
        fill(matrices, x + 2, y + height - 15, x + width - 2, y + height - 14, 0xFFFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
        
    }
}
