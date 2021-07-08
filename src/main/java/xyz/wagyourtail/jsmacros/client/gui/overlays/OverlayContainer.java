package xyz.wagyourtail.jsmacros.client.gui.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import xyz.wagyourtail.jsmacros.client.gui.containers.MultiElementContainer;
import xyz.wagyourtail.jsmacros.client.gui.elements.Scrollbar;
import xyz.wagyourtail.jsmacros.client.gui.elements.TextInput;

import java.util.HashMap;
import java.util.Map;

public abstract class OverlayContainer extends MultiElementContainer<IOverlayParent> implements IOverlayParent {
    public Map<GuiButton, Boolean> savedBtnStates = new HashMap<>();
    public Scrollbar scroll;
    protected OverlayContainer overlay;
    protected Minecraft mc = Minecraft.getMinecraft();
    
    public OverlayContainer(int x, int y, int width, int height, FontRenderer textRenderer, IOverlayParent parent) {
        super(x, y, width, height, textRenderer, parent);
    }
    
    @Override
    public void removeButton(GuiButton btn) {
        this.buttons.remove(btn);
        parent.removeButton(btn);
    }
    
    @Override
    public void openOverlay(OverlayContainer overlay) {
        openOverlay(overlay, true);
    }
    
    @Override
    public IOverlayParent getFirstOverlayParent() {
        return this;
    }
    
    @Override
    public void openOverlay(OverlayContainer overlay, boolean disableButtons) {
        if (this.overlay != null) {
            this.overlay.openOverlay(overlay, disableButtons);
            return;
        }
        if (disableButtons) {
            for (GuiButton b : buttons) {
                overlay.savedBtnStates.put(b, b.enabled);
                b.enabled = false;
            }
        }
        this.overlay = overlay;
        overlay.init();
    }
    
    @Override
    public OverlayContainer getChildOverlay() {
        if (overlay != null) return overlay.getChildOverlay();
        else return this;
    }
    
    @Override
    public void closeOverlay(OverlayContainer overlay) {
        if (this.overlay != null && this.overlay == overlay) {
            for (GuiButton b : overlay.getButtons()) {
                removeButton(b);
            }
            for (GuiButton b : overlay.savedBtnStates.keySet()) {
                b.enabled = overlay.savedBtnStates.get(b);
            }
            overlay.onClose();
            this.overlay = null;
        }
        else parent.closeOverlay(overlay);
    }
    
    public void onClick(double mouseX, double mouseY, int button) {
        if (overlay != null) overlay.onClick(mouseX, mouseY, button);
    }
    
    /**
     * @return true if should be handled by overlay
     */
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (overlay != null) return overlay.keyPressed(keyCode, scanCode, modifiers);
    
        for (GuiButton b : buttons) {
            if (b instanceof TextInput && ((TextInput) b).keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        
        return false;
    }
    
    public void close() {
        parent.closeOverlay(this);
    }
    
    public void onClose() {}
    
    public void renderBackground() {
        // black bg
        drawRect(x, y, x + width, y + height, 0xFF000000);
        // 2 layer border
        drawRect(x, y, x + width, y + 1, 0x7F7F7F7F);
        drawRect(x, y + height - 1, x + width, y + height, 0x7F7F7F7F);
        drawRect(x, y + 1, x + 1, y + height - 1, 0x7F7F7F7F);
        drawRect(x + width - 1, y + 1, x + width, y + height - 1, 0x7F7F7F7F);
    
        drawRect(x + 1, y + 1, x + width - 1, y + 2, 0xFFFFFFFF);
        drawRect(x + 1, y + height - 2, x + width - 1, y + height - 1, 0xFFFFFFFF);
        drawRect(x + 1, y + 1, x + 2, y + height - 1, 0xFFFFFFFF);
        drawRect(x + width - 2, y + 1, x + width - 1, y + height - 1, 0xFFFFFFFF);

    }
    
    @Override
    public void render(int mouseX, int mouseY, float delta) {
        for (GuiButton btn : buttons) {
            btn.drawButton(mc, mouseX, mouseY);
        }
        if (this.overlay != null) this.overlay.render(mouseX, mouseY, delta);
    }

}
