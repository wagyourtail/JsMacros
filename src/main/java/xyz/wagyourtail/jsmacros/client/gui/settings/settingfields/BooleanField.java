package xyz.wagyourtail.jsmacros.client.gui.settings.settingfields;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import xyz.wagyourtail.jsmacros.client.gui.elements.AnnotatedCheckBox;
import xyz.wagyourtail.jsmacros.client.gui.settings.settingcontainer.AbstractSettingContainer;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;

import java.lang.reflect.InvocationTargetException;

public class BooleanField  extends AbstractSettingField<Boolean> {
    
    
    public BooleanField(int x, int y, int width, FontRenderer textRenderer, AbstractSettingContainer parent, SettingsOverlay.SettingField<Boolean> field) {
        super(x, y, width, textRenderer.FONT_HEIGHT + 2, textRenderer, parent, field);
    }
    
    @Override
    public void init() {
        super.init();
        try {
            this.addButton(new AnnotatedCheckBox(x, y, width, height, textRenderer, 0xFFFFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, settingName, setting.get(), button -> {
                try {
                    setting.set(((AnnotatedCheckBox)button).value);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void setPos(int x, int y, int width, int height) {
        super.setPos(x, y, width, height);
        for (GuiButton btn : buttons) {
            btn.yPosition = y;
        }
    }
    
    @Override
    public void render(int mouseX, int mouseY, float delta) {
    
    }
    
}
