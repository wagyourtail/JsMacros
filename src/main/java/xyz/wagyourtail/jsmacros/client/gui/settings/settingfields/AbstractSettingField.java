package xyz.wagyourtail.jsmacros.client.gui.settings.settingfields;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import xyz.wagyourtail.jsmacros.client.gui.containers.MultiElementContainer;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;
import xyz.wagyourtail.jsmacros.client.gui.settings.settingcontainer.AbstractSettingContainer;

public abstract class AbstractSettingField<T> extends MultiElementContainer<AbstractSettingContainer> {
    protected final SettingsOverlay.SettingField<T> setting;
    protected final IChatComponent settingName;
    public AbstractSettingField(int x, int y, int width, int height, FontRenderer textRenderer, AbstractSettingContainer parent, SettingsOverlay.SettingField<T> field) {
        super(x, y, width, height, textRenderer, parent);
        setting = field;
        settingName = new ChatComponentTranslation(field.option.translationKey());
        init();
    }
    
}
