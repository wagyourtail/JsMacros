package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ChatComponentText;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Wagyourtail
 * @since 1.0.5
 */
@SuppressWarnings("unused")
public class ButtonWidgetHelper<T extends GuiButton> extends BaseHelper<T> implements RenderCommon.RenderElement {
    public int zIndex;
    
    public ButtonWidgetHelper(T btn) {
        super(btn);
        zIndex = 0;
    }
    
    public ButtonWidgetHelper(T btn, int zIndex) {
        super(btn);
        this.zIndex = zIndex;
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
    public ButtonWidgetHelper<T> setPos(int x, int y) {
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
        return base.getButtonWidth();
    }
    
    
    /**
     * change the text.
     *
     * @since 1.0.5, renamed from {@code setText} in 1.3.1
     * @deprecated only deprecated in buttonWidgetHelper for confusing name.
     *
     * @param label
     * @return
     */
     @Deprecated
    public ButtonWidgetHelper<T> setLabel(String label) {
        base.displayString = label;
        return this;
    }
    
    /**
     * change the text.
     *
     * @since 1.3.1
     *
     * @param helper
     *
     * @return
     */
    public ButtonWidgetHelper<T> setLabel(TextHelper helper) {
        base.displayString = helper.getRaw().getFormattedText();
        return this;
    }
    
    /**
     * @since 1.2.3, renamed fro {@code getText} in 1.3.1
     * 
     * @return current button text.
     */
    public TextHelper getLabel() {
        return new TextHelper(new ChatComponentText(base.displayString));
    }
    
    /**
     * @since 1.0.5
     * 
     * @return button clickable state.
     */
    public boolean getActive() {
        return base.enabled;
    }
    
    /**
     * set the button clickable state.
     * 
     * @since 1.0.5
     * 
     * @param t
     * @return
     */
    public ButtonWidgetHelper<T> setActive(boolean t) {
        base.enabled = t;
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
    public ButtonWidgetHelper<T> setWidth(int width) {
        base.setWidth(width);
        return this;
    }
    
    /**
     * clicks button
     * @since 1.3.1
     */
    public ButtonWidgetHelper<T> click() throws InterruptedException, IOException {
        click(true);
        return this;
    }
    
    /**
     * clicks button
     *
     * @param await should wait for button to finish clicking.
     * @since 1.3.1
     */
    public ButtonWidgetHelper<T> click(boolean await) throws InterruptedException, IOException {
        boolean joinedMain = Minecraft.getMinecraft().isCallingFromMinecraftThread() || Core.instance.profile.joinedThreadStack.contains(Thread.currentThread());
        if (joinedMain && await) {
            throw new IllegalThreadStateException("Attempted to wait on a thread that is currently joined!");
        }
        final Semaphore waiter = new Semaphore(await ? 0 : 1);
        final Minecraft mc =  Minecraft.getMinecraft();
        AtomicReference<IOException> error = new AtomicReference<>(null);
        mc.addScheduledTask(() -> {
            base.mousePressed(mc, base.xPosition, base.yPosition);
            try {
                ((IScreen)mc.currentScreen).clickBtn(base);
            } catch (IOException e) {
                error.set(e);
            }
            base.mouseReleased(base.xPosition, base.yPosition);
            waiter.release();
        });
        waiter.acquire();
        if (error.get() != null) throw error.get();
        return this;
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        base.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
    }
    
    @Override
    public int getZIndex() {
        return zIndex;
    }
    
}
