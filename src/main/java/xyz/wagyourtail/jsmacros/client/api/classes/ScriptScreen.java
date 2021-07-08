package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen;
import xyz.wagyourtail.jsmacros.client.gui.screens.BaseScreen;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

/**
 * just go look at {@link xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen IScreen}
 * since all the methods are done through a mixin...
 * 
 * @author Wagyourtail
 * 
 * @since 1.0.5
 * 
 * @see xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen
 */
public class ScriptScreen extends BaseScreen {
    private final int bgStyle;
    private MethodWrapper<PositionCommon.Pos3D, MatrixStack, Object> onRender;
    
    public ScriptScreen(String title, boolean dirt) {
        super(new LiteralText(title), null);
        this.bgStyle = dirt ? 0 : 1;
    }

    @Override
    protected void init() {
        BaseScreen prev = JsMacros.prevScreen;
        super.init();
        JsMacros.prevScreen = prev;
    }

    /**
     * @param parent parent screen to go to when this one exits.
     * @since 1.4.0
     */
    public void setParent(IScreen parent) {
        this.parent = (net.minecraft.client.gui.screen.Screen) parent;
    }

    /**
     * add custom stuff to the render function on the main thread.
     *
     * @param onRender pos3d elements are mousex, mousey, tickDelta
     * @since 1.4.0
     */
    public void setOnRender(MethodWrapper<PositionCommon.Pos3D, MatrixStack, Object> onRender) {
        this.onRender = onRender;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (matrices == null) return;
        if (bgStyle == 0) this.renderBackgroundTexture(0);
        else if (bgStyle == 1) this.renderBackground(matrices, 0);

        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);

        super.render(matrices, mouseX, mouseY, delta);

        for (AbstractButtonWidget button : this.buttons) {
            button.render(matrices, mouseX, mouseY, delta);
        }

        ((IScreen) this).onRenderInternal(matrices, mouseX, mouseY, delta);

        if (onRender != null) onRender.accept(new PositionCommon.Pos3D(mouseX, mouseY, delta), matrices);
    }

    @Override
    public void onClose() {
        openParent();
    }
}
