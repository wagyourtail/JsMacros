package xyz.wagyourtail.jsmacros.client.gui.containers;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import xyz.wagyourtail.jsmacros.client.gui.overlays.IOverlayParent;
import xyz.wagyourtail.jsmacros.client.gui.overlays.OverlayContainer;

public interface IContainerParent {
    
    <T extends AbstractButtonWidget> T  addButton(T button);
    
    void removeButton(AbstractButtonWidget button);
    
    void openOverlay(OverlayContainer overlay);
    
    void openOverlay(OverlayContainer overlay, boolean disableButtons);
    
    IOverlayParent getFirstOverlayParent();
}
