package xyz.wagyourtail.jsmacros.client.gui.containers;

import net.minecraft.client.gui.GuiButton;
import xyz.wagyourtail.jsmacros.client.gui.overlays.IOverlayParent;
import xyz.wagyourtail.jsmacros.client.gui.overlays.OverlayContainer;

public interface IContainerParent {
    
    <T extends GuiButton> T  addButton(T button);
    
    void removeButton(GuiButton button);
    
    void openOverlay(OverlayContainer overlay);
    
    void openOverlay(OverlayContainer overlay, boolean disableButtons);
    
    IOverlayParent getFirstOverlayParent();
}
