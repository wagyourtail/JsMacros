package xyz.wagyourtail.jsmacros.client.gui.overlays;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ConfirmOverlay extends OverlayContainer {
    private final Consumer<ConfirmOverlay> accept;
    private List<IChatComponent> text;
    private int lines;
    public boolean hcenter = true;
    private int vcenter;
    
    public ConfirmOverlay(int x, int y, int width, int height, FontRenderer textRenderer, IChatComponent message, IOverlayParent parent, Consumer<ConfirmOverlay>accept) {
        super(x, y, width, height, textRenderer, parent);
        this.setMessage(message);
        this.accept = accept;
    }
    
    public ConfirmOverlay(int x, int y, int width, int height, boolean hcenter, FontRenderer textRenderer, IChatComponent message, IOverlayParent parent, Consumer<ConfirmOverlay>accept) {
        this(x, y, width, height, textRenderer, message, parent, accept);
        this.hcenter = hcenter;
    }
    
    public void setMessage(IChatComponent message) {
        this.text = textRenderer.listFormattedStringToWidth(message.getFormattedText(), width - 6).stream().map(ChatComponentText::new).collect(Collectors.toList());
        this.lines = Math.min(Math.max((height - 15) / textRenderer.FONT_HEIGHT, 1), text.size());
        this.vcenter = ((height - 15) - (lines * textRenderer.FONT_HEIGHT)) / 2;
    }
    
    @Override
    public void init() {
        super.init();
        
        this.addButton(new Button(x + 2, y+height-12, (width - 4) / 2, 10, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentTranslation("gui.cancel"), (btn) -> {
            this.close();
        }));
        
        this.addButton(new Button(x + (width - 4) / 2 + 2, y+height-12, (width - 4) / 2, 10, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentTranslation("jsmacros.confirm"), (btn) -> {
            if (this.accept != null) this.accept.accept(this);
            this.close();
        }));
        
    }
    
    protected void renderMessage() {
        for (int i = 0; i < lines; ++i) {
            int w = textRenderer.getStringWidth(text.get(i).getFormattedText());
            float centeredX = hcenter ? x + width / 2F - w / 2F : x + 3;
            textRenderer.drawString(text.get(i).getFormattedText(), (int) centeredX, y + 2 + vcenter + (i * textRenderer.FONT_HEIGHT), 0xFFFFFF);
        }
    }
    
    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        drawRect(x + 1, y + height - 13, x + width - 1, y + height - 12, 0xFFFFFFFF);
        this.renderMessage();
        super.render(mouseX, mouseY, delta);
    }

}
