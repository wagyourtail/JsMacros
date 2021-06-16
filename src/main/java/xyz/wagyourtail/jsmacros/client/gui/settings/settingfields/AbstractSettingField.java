package xyz.wagyourtail.jsmacros.client.gui.settings.settingfields;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.client.gui.containers.MultiElementContainer;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;
import xyz.wagyourtail.jsmacros.client.gui.settings.settingcontainer.AbstractSettingContainer;

public abstract class AbstractSettingField<T> extends MultiElementContainer<AbstractSettingContainer> {
    protected final SettingsOverlay.SettingField<T> setting;
    protected final Text settingName;
    public AbstractSettingField(int x, int y, int width, int height, TextRenderer textRenderer, AbstractSettingContainer parent, SettingsOverlay.SettingField<T> field) {
        super(x, y, width, height, textRenderer, parent);
        setting = field;
        settingName = new TranslatableText(field.option.translationKey());
        init();
    }
    
}
