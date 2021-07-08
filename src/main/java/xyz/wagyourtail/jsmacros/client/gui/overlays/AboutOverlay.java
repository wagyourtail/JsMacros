package xyz.wagyourtail.jsmacros.client.gui.overlays;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class AboutOverlay extends OverlayContainer {
    private List<IChatComponent> text;
    private int lines;
    private int vcenter;

    public AboutOverlay(int x, int y, int width, int height, FontRenderer textRenderer, IOverlayParent parent) {
        super(x, y, width, height, textRenderer, parent);
    }
    
    @Override
    public void init() {
        super.init();
        int w = width - 4;
        this.addButton(new Button(x + width - 12, y + 2, 10, 10, textRenderer,0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentText("X"), (btn) -> this.close()));
        
        this.addButton(new Button(x + 2, y + height - 14, w / 3, 12, textRenderer,0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentText("Website"), (btn) -> {
            try {
                JsMacros.openURI(new URL("https://jsmacros.wagyourtail.xyz").toURI());
            } catch (URISyntaxException | MalformedURLException e) {
                e.printStackTrace();
            }
        }));
        
        this.addButton(new Button(x + w / 3 + 2, y + height - 14, w / 3, 12, textRenderer, 0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentText("Discord"), (btn) -> {
            try {
                JsMacros.openURI(new URL("https://discord.gg/P6W58J8").toURI());
            } catch (URISyntaxException | MalformedURLException e) {
                e.printStackTrace();
            }
        }));
        

        this.addButton(new Button(x + w * 2 / 3 + 2, y + height - 14, w / 3, 12, textRenderer, 0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentText("CurseForge"), (btn) -> {
            try {
                JsMacros.openURI(new URL("https://www.curseforge.com/minecraft/mc-mods/jsmacros").toURI());
            } catch (URISyntaxException | MalformedURLException e) {
                e.printStackTrace();
            }
        }));
        
        this.setMessage(new ChatComponentTranslation("jsmacros.aboutinfo"));
    }
    
    public void setMessage(IChatComponent message) {
        this.text = textRenderer.listFormattedStringToWidth(message.getFormattedText(), width - 6).stream().map(ChatComponentText::new).collect(Collectors.toList());
        this.lines = Math.min(Math.max((height - 27) / textRenderer.FONT_HEIGHT, 1), text.size());
        this.vcenter = ((height - 12) - (lines * textRenderer.FONT_HEIGHT)) / 2;
    }
    
    protected void renderMessage() {
        for (int i = 0; i < lines; ++i) {
            int w = textRenderer.getStringWidth(text.get(i).getFormattedText());
            textRenderer.drawString(text.get(i).getFormattedText(), (int) (x + width / 2F - w / 2F), y + 2 + vcenter + (i * textRenderer.FONT_HEIGHT), 0xFFFFFF);
        }
    }
    
    @Override
    public void render(int mouseX, int mouseY, float delta) {
        renderBackground();
        
        textRenderer.drawString(textRenderer.trimStringToWidth(new ChatComponentTranslation("jsmacros.about").getFormattedText(), width - 14),x + 3, y + 3, 0xFFFFFF);
        renderMessage();
        
        drawRect(x + 2, y + 12, x + width - 2, y + 13, 0xFFFFFFFF);
        drawRect(x + 2, y + height - 15, x + width - 2, y + height - 14, 0xFFFFFFFF);
        super.render(mouseX, mouseY, delta);
        
    }
}
