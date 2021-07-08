package xyz.wagyourtail.jsmacros.client.gui.settings.settingfields;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import xyz.wagyourtail.jsmacros.client.gui.elements.TextInput;
import xyz.wagyourtail.jsmacros.client.gui.screens.BaseScreen;
import xyz.wagyourtail.jsmacros.client.gui.settings.settingcontainer.AbstractSettingContainer;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;

import java.lang.reflect.InvocationTargetException;

public class StringField extends AbstractSettingField<String> {
    
    public StringField(int x, int y, int width, FontRenderer textRenderer, AbstractSettingContainer parent, SettingsOverlay.SettingField<String> field) {
        super(x, y, width, textRenderer.FONT_HEIGHT + 2, textRenderer, parent, field);
    }
    
    @Override
    public void init() {
        super.init();
        try {
            addButton(new TextInput(x + width/2, y, width / 2, height, textRenderer, 0xFF101010, 0, 0xFF4040FF, 0xFFFFFF, setting.get(), null, (value) -> {
                try {
                    setting.set(value);
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
        textRenderer.drawString(BaseScreen.trimmed(textRenderer, settingName.getFormattedText(), width / 2), x, y + 1, 0xFFFFFF);
    }
    
}
