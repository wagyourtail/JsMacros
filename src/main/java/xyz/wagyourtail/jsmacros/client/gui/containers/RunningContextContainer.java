package xyz.wagyourtail.jsmacros.client.gui.containers;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.apache.commons.lang3.time.DurationFormatUtils;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.screens.CancelScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.language.ScriptContext;

import java.lang.ref.WeakReference;

public class RunningContextContainer extends MultiElementContainer<CancelScreen> {
    private Button cancelButton;
    public WeakReference<ScriptContext<?>> t;
    
    public RunningContextContainer(int x, int y, int width, int height, TextRenderer textRenderer, CancelScreen parent, ScriptContext<?> t) {
        super(x, y, width, height, textRenderer, parent);
        this.t = new WeakReference<>(t);
        init();
    }
    
    @Override
    public void init() {
        super.init();
        cancelButton = this.addButton(new Button(x+1, y+1, height - 2, height - 2, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("X"), (btn) -> {
                ScriptContext<?> ctx = t.get();
                if (ctx != null && !ctx.isContextClosed())
                    ctx.closeContext();
                parent.removeContainer(this);
        }));
    }
    
    @Override
    public void setPos(int x, int y, int width, int height) {
        super.setPos(x, y, width, height);
        cancelButton.setPos(x+1, y+1, height - 2, height - 2);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        try {
            ScriptContext<?> ctx = t.get();
            if (ctx != null && !ctx.isContextClosed()) {
                if (this.visible) {
                    drawCenteredString(matrices, textRenderer, textRenderer.trimToWidth(Core.instance.contexts.get(t.get()), width - 105 - height), x + (width - 105 - height) / 2 + height + 4, y+2, 0xFFFFFF);
                    drawCenteredString(matrices, textRenderer, textRenderer.trimToWidth(DurationFormatUtils.formatDurationHMS(System.currentTimeMillis() - ctx.startTime), 100), x+width - 50 + height, y+2, 0xFFFFFF);
                    fill(matrices, x+width-101, y, x+width-100, y+height, 0xFFFFFFFF);
                    fill(matrices, x+height, y, x+height+1, y+height, 0xFFFFFFFF);
                    // border
                    fill(matrices, x, y, x + width, y + 1, 0xFFFFFFFF);
                    fill(matrices, x, y + height - 1, x + width, y + height, 0xFFFFFFFF);
                    fill(matrices, x, y + 1, x + 1, y + height - 1, 0xFFFFFFFF);
                    fill(matrices, x + width - 1, y + 1, x + width, y + height - 1, 0xFFFFFFFF);
                }
            } else {
                parent.removeContainer(this);
            }
        } catch(NullPointerException e) {
            parent.removeContainer(this);
        }
    }
}
