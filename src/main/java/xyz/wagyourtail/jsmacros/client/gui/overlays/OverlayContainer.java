package xyz.wagyourtail.jsmacros.client.gui.overlays;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import xyz.wagyourtail.jsmacros.client.gui.containers.MultiElementContainer;
import xyz.wagyourtail.jsmacros.client.gui.elements.Scrollbar;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class OverlayContainer extends MultiElementContainer<IOverlayParent> implements IOverlayParent {
    public Map<AbstractButtonWidget, Boolean> savedBtnStates = new HashMap<>();
    public Scrollbar scroll;
    protected OverlayContainer overlay;
    
    public OverlayContainer(int x, int y, int width, int height, TextRenderer textRenderer, IOverlayParent parent) {
        super(x, y, width, height, textRenderer, parent);
    }
    
    @Override
    public void removeButton(AbstractButtonWidget btn) {
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
            for (AbstractButtonWidget b : buttons) {
                overlay.savedBtnStates.put(b, b.active);
                b.active = false;
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
            for (AbstractButtonWidget b : overlay.getButtons()) {
                removeButton(b);
            }
            for (AbstractButtonWidget b : overlay.savedBtnStates.keySet()) {
                b.active = overlay.savedBtnStates.get(b);
            }
            overlay.onClose();
            this.overlay = null;
        }
        else parent.closeOverlay(overlay);
    }
    
    @Override
    public void setFocused(@Nullable Element focused) {
        parent.setFocused(focused);
    }
    
    public void onClick(double mouseX, double mouseY, int button) {
        if (overlay != null) overlay.onClick(mouseX, mouseY, button);
    }
    
    /**
     * @return true if should be handled by overlay
     */
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (overlay != null) overlay.keyPressed(keyCode, scanCode, modifiers);
        return false;
    }
    
    public void close() {
        parent.closeOverlay(this);
    }
    
    public void onClose() {}
    
    public void renderBackground() {
        // black bg
        fill(x, y, x + width, y + height, 0xFF000000);
        // 2 layer border
        fill(x, y, x + width, y + 1, 0x7F7F7F7F);
        fill(x, y + height - 1, x + width, y + height, 0x7F7F7F7F);
        fill(x, y + 1, x + 1, y + height - 1, 0x7F7F7F7F);
        fill(x + width - 1, y + 1, x + width, y + height - 1, 0x7F7F7F7F);

        fill(x + 1, y + 1, x + width - 1, y + 2, 0xFFFFFFFF);
        fill(x + 1, y + height - 2, x + width - 1, y + height - 1, 0xFFFFFFFF);
        fill(x + 1, y + 1, x + 2, y + height - 1, 0xFFFFFFFF);
        fill(x + width - 2, y + 1, x + width - 1, y + height - 1, 0xFFFFFFFF);

    }
    
    @Override
    public void render(int mouseX, int mouseY, float delta) {
        for (AbstractButtonWidget btn : buttons) {
            btn.render(mouseX, mouseY, delta);
        }
        if (this.overlay != null) this.overlay.render(mouseX, mouseY, delta);
    }

}
