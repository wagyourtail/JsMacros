package xyz.wagyourtail.jsmacros.client.gui.settings.settingcontainer;

import net.minecraft.client.gui.FontRenderer;
import xyz.wagyourtail.jsmacros.client.gui.containers.MultiElementContainer;
import xyz.wagyourtail.jsmacros.client.gui.elements.Scrollbar;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;

public abstract class AbstractSettingContainer extends MultiElementContainer<SettingsOverlay> {
    public final String[] group;
    public Scrollbar scroll;
    public AbstractSettingContainer(int x, int y, int width, int height, FontRenderer textRenderer, SettingsOverlay parent, String[] group) {
        super(x, y, width, height, textRenderer, parent);
        this.group = group;
        init();
    }
    
    public abstract void addSetting(SettingsOverlay.SettingField<?> setting);
    
}
