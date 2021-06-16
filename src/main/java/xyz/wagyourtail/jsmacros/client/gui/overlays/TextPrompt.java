package xyz.wagyourtail.jsmacros.client.gui.overlays;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.elements.TextInput;

import java.util.function.Consumer;

public class TextPrompt extends OverlayContainer {
    private final Text message;
    private final Consumer<String> accept;
    public TextInput ti;
    private final String defText;

    public TextPrompt(int x, int y, int width, int height, TextRenderer textRenderer, Text message, String defaultText, IOverlayParent parent, Consumer<String> accept) {
        super(x, y, width, height, textRenderer, parent);
        this.message = message;
        this.accept = accept;
        this.defText = defaultText == null ? "" : defaultText;
    }

    @Override
    public void init() {
        super.init();
        int w = width - 4;

        ti = this.addButton(new TextInput(x + 3, y + 25, w - 2, 14, textRenderer, 0xFF101010, 0, 0xFF4040FF, 0xFFFFFF, defText, null, null));
        
        this.addButton(new Button(x + 2, y + height - 14, w / 2, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("gui.cancel"), (btn) -> close()));

        this.addButton(new Button(x + w / 2, y + height - 14, w / 2, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.confirm"), (btn) -> {
            if (this.accept != null) this.accept.accept(ti.content);
            close();
        }));
        
        setFocused(ti);
        ti.setSelected(true);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        drawCenteredString(textRenderer, message.asFormattedString(), x + width / 2, y + 5, 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
    }

}
