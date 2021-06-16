package xyz.wagyourtail.jsmacros.client.gui.overlays;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ConfirmOverlay extends OverlayContainer {
    private final Consumer<ConfirmOverlay> accept;
    private List<Text> text;
    private int lines;
    public boolean hcenter = true;
    private int vcenter;
    
    public ConfirmOverlay(int x, int y, int width, int height, TextRenderer textRenderer, Text message, IOverlayParent parent, Consumer<ConfirmOverlay>accept) {
        super(x, y, width, height, textRenderer, parent);
        this.setMessage(message);
        this.accept = accept;
    }
    
    public ConfirmOverlay(int x, int y, int width, int height, boolean hcenter, TextRenderer textRenderer, Text message, IOverlayParent parent, Consumer<ConfirmOverlay>accept) {
        this(x, y, width, height, textRenderer, message, parent, accept);
        this.hcenter = hcenter;
    }
    
    public void setMessage(Text message) {
        this.text = textRenderer.wrapStringToWidthAsList(message.asFormattedString(), width - 6).stream().map(LiteralText::new).collect(Collectors.toList());
        this.lines = Math.min(Math.max((height - 15) / textRenderer.fontHeight, 1), text.size());
        this.vcenter = ((height - 15) - (lines * textRenderer.fontHeight)) / 2;
    }
    
    @Override
    public void init() {
        super.init();
        
        this.addButton(new Button(x + 2, y+height-12, (width - 4) / 2, 10, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("gui.cancel"), (btn) -> {
            this.close();
        }));
        
        this.addButton(new Button(x + (width - 4) / 2 + 2, y+height-12, (width - 4) / 2, 10, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.confirm"), (btn) -> {
            if (this.accept != null) this.accept.accept(this);
            this.close();
        }));
        
    }
    
    protected void renderMessage() {
        for (int i = 0; i < lines; ++i) {
            int w = textRenderer.getStringWidth(text.get(i).asFormattedString());
            float centeredX = hcenter ? x + width / 2F - w / 2F : x + 3;
            textRenderer.draw(text.get(i).asFormattedString(), centeredX, y + 2 + vcenter + (i * textRenderer.fontHeight), 0xFFFFFF);
        }
    }
    
    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        fill(x + 1, y + height - 13, x + width - 1, y + height - 12, 0xFFFFFFFF);
        this.renderMessage();
        super.render(mouseX, mouseY, delta);
    }

}
