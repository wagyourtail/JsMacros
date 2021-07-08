package xyz.wagyourtail.jsmacros.client.gui.overlays;

import xyz.wagyourtail.jsmacros.client.gui.containers.IContainerParent;

public interface IOverlayParent extends IContainerParent {
    
    void closeOverlay(OverlayContainer overlay);
    
    OverlayContainer getChildOverlay();
}
