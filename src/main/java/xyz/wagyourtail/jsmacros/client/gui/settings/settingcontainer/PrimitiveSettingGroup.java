package xyz.wagyourtail.jsmacros.client.gui.settings.settingcontainer;

import net.minecraft.client.font.TextRenderer;
import xyz.wagyourtail.jsmacros.client.gui.elements.Scrollbar;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;
import xyz.wagyourtail.jsmacros.client.gui.settings.settingfields.*;

import java.util.LinkedList;
import java.util.List;

public class PrimitiveSettingGroup extends AbstractSettingContainer {
    private final List<AbstractSettingField<?>> settings = new LinkedList<>();
    int topScroll = 0;
    int settingHeight = 0;
    
    public PrimitiveSettingGroup(int x, int y, int width, int height, TextRenderer textRenderer, SettingsOverlay parent, String[] group) {
        super(x, y, width, height, textRenderer, parent, group);
    }
    
    @Override
    public void init() {
        scroll = addButton(new Scrollbar(x + width - 10, y, 10, height, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));
    }
    
    public void onScrollbar(double page) {
        topScroll = (int) (page * height);
        int settingHeight = 0;
        int x = this.x + 1;
        int width = this.width - 12;
        for (AbstractSettingField<?> setting : settings) {
            setting.setPos(x, y + settingHeight - topScroll, width, setting.height);
            settingHeight += setting.height;
        }
    }
    
    @Override
    public void render(int mouseX, int mouseY, float delta) {
        for (AbstractSettingField<?> setting : settings) {
            setting.render(mouseX, mouseY, delta);
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void addSetting(SettingsOverlay.SettingField<?> setting) {
        if (!setting.isSimple()) throw new RuntimeException("non-simple settings should get their own group!");
        AbstractSettingField<?> type = null;
        int x = this.x + 1;
        int y = this.y + settingHeight - topScroll;
        int width = this.width - 12;
        if (setting.option.type().value().equals("primitive")) {
            if (!setting.hasOptions()) {
                if (setting.type == String.class) {
                    type = new StringField(x, y, width, textRenderer, this, (SettingsOverlay.SettingField<String>) setting);
                } else if (setting.type == Double.class || setting.type == double.class) {
                    type = new DoubleField(x, y, width, textRenderer, this, (SettingsOverlay.SettingField<Double>) setting);
                } else if (setting.type == Float.class || setting.type == float.class) {
                    type = new FloatField(x, y, width, textRenderer, this, (SettingsOverlay.SettingField<Float>) setting);
                } else if (setting.type == Integer.class || setting.type == int.class) {
                    type = new IntField(x, y, width, textRenderer, this, (SettingsOverlay.SettingField<Integer>) setting);
                } else if (setting.type == Long.class || setting.type == long.class) {
                    type = new LongField(x, y, width, textRenderer, this, (SettingsOverlay.SettingField<Long>) setting);
                } else if (setting.type == Boolean.class || setting.type == boolean.class) {
                    type = new BooleanField(x, y, width, textRenderer, this, (SettingsOverlay.SettingField<Boolean>) setting);
                }
            } else {
                type = new OptionsField(x, y, width, textRenderer, this, (SettingsOverlay.SettingField<Object>) setting);
            }
        } else if (setting.option.type().value().equals("file") && setting.type == String.class) {
            type = new FileField(x, y, width, textRenderer, this, (SettingsOverlay.SettingField<String>) setting);
        }
        
        if (type != null) {
            settingHeight += type.height;
            settings.add(type);
            scroll.setScrollPages(settingHeight / (float)height);
        } else {
            System.out.println(setting.type);
            System.out.println(setting.option.translationKey());
        }
    }
    
}
