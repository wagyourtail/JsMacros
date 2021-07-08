package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.access.IGuiTextField;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon;
import xyz.wagyourtail.jsmacros.client.gui.elements.Drawable;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;
import xyz.wagyourtail.jsmacros.core.Core;

import java.util.concurrent.Semaphore;

/**F
 * @author Wagyourtail
 * @since 1.0.5
 */
@SuppressWarnings("unused")
public class TextFieldWidgetHelper extends BaseHelper<GuiTextField> implements RenderCommon.RenderElement {
    public int zIndex;
    
    public TextFieldWidgetHelper(GuiTextField t) {
        super(t);
        zIndex = 0;
    }
    
    public TextFieldWidgetHelper(GuiTextField t, int zIndex) {
        super(t);
        zIndex = zIndex;
    }
    
    /**
     * @since 1.0.5
     * @return the {@code x} coordinate of the button.
     */
    public int getX() {
        return base.xPosition;
    }
    
    /**
     * @since 1.0.5
     * @return the {@code y} coordinate of the button.
     */
    public int getY() {
        return base.yPosition;
    }
    
    /**
     * Set the button position.
     *
     * @since 1.0.5
     *
     * @param x
     * @param y
     * @return
     */
    public TextFieldWidgetHelper setPos(int x, int y) {
        base.xPosition = x;
        base.yPosition = y;
        return this;
    }
    
    /**
     * @since 1.0.5
     *
     * @return
     */
    public int getWidth() {
        return base.getWidth();
    }
    
    /**
     * @since 1.0.5
     *
     * @return button clickable state.
     */
    public boolean getActive() {
        return ((IGuiTextField)base).isEnabled();
    }
    
    /**
     * set the button clickable state.
     *
     * @since 1.0.5
     *
     * @param t
     * @return
     */
    public TextFieldWidgetHelper setActive(boolean t) {
        base.setEnabled(t);
        return this;
    }
    
    /**
     * set the button width.
     *
     * @since 1.0.5
     *
     * @param width
     * @return
     */
    public TextFieldWidgetHelper setWidth(int width) {
        base.width = width;
        return this;
    }
    
    
    /**
     * @since 1.0.5
     * @return the currently entered {@link java.lang.String String}.
     */
    public String getText() {
        return base.getText();
    }
    
    /**
     *
     * @since 1.0.5
     * @param text
     * @return
     */
    public TextFieldWidgetHelper setText(String text) throws InterruptedException {
        setText(text, true);
        return this;
    }
    
    /**
     * set the currently entered {@link java.lang.String String}.
     *
     * @param text
     * @param await
     *
     * @return
     * @since 1.3.1
     *
     * @throws InterruptedException
     */
    public TextFieldWidgetHelper setText(String text, boolean await) throws InterruptedException {
        boolean joinedMain = Minecraft.getMinecraft().isCallingFromMinecraftThread() || Core.instance.profile.joinedThreadStack.contains(Thread.currentThread());
        if (joinedMain && await) {
            throw new IllegalThreadStateException("Attempted to wait on a thread that is currently joined!");
        }
        final Semaphore waiter = new Semaphore(await ? 0 : 1);
        Minecraft.getMinecraft().addScheduledTask(() -> {
            base.setText(text);
            base.writeText("");
            waiter.release();
        });
        waiter.acquire();
        return this;
    }
    
    
    
    /**
     * @since 1.0.5
     * @param color
     * @return
     */
    public TextFieldWidgetHelper setEditableColor(int color) {
        base.setTextColor(color);
        return this;
    }
    
    /**
     * @since 1.0.5
     * @param edit
     * @return
     */
    public TextFieldWidgetHelper setEditable(boolean edit) {
        base.setEnabled(edit);
        return this;
    }
    
    /**
     * @since 1.0.5
     * @param color
     * @return
     */
    public TextFieldWidgetHelper setUneditableColor(int color) {
        base.setDisabledTextColour(color);
        return this;
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        base.drawTextBox();
    }
    
    @Override
    public int getZIndex() {
        return zIndex;
    }
    
}
