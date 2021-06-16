package xyz.wagyourtail.jsmacros.client.gui.overlays;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;

import java.util.List;
import java.util.stream.Collectors;

public class AboutOverlay extends OverlayContainer {
    private List<Text> text;
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
        this.text = textRenderer.wrapStringToWidthAsList(message.asFormattedString(), width - 6).stream().map(LiteralText::new).collect(Collectors.toList());
        this.lines = Math.min(Math.max((height - 27) / textRenderer.fontHeight, 1), text.size());
        this.vcenter = ((height - 12) - (lines * textRenderer.fontHeight)) / 2;
    }
    
    protected void renderMessage() {
        for (int i = 0; i < lines; ++i) {
            int w = textRenderer.getStringWidth(text.get(i).asFormattedString());
            textRenderer.draw(text.get(i).asFormattedString(), x + width / 2F - w / 2F, y + 2 + vcenter + (i * textRenderer.fontHeight), 0xFFFFFF);
        }
    }
    
    @Override
    public void render(int mouseX, int mouseY, float delta) {
        renderBackground();
        
        textRenderer.drawTrimmed(new TranslatableText("jsmacros.about").asFormattedString(), x + 3, y + 3, width - 14, 0xFFFFFF);
        renderMessage();
        
        fill(x + 2, y + 12, x + width - 2, y + 13, 0xFFFFFFFF);
        fill(x + 2, y + height - 15, x + width - 2, y + height - 14, 0xFFFFFFFF);
        super.render(mouseX, mouseY, delta);
        
    }
}
