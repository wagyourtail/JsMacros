package xyz.wagyourtail.jsmacros.client.gui.settings.settingfields;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.overlays.SelectorDropdownOverlay;
import xyz.wagyourtail.jsmacros.client.gui.screens.BaseScreen;
import xyz.wagyourtail.jsmacros.client.gui.settings.settingcontainer.AbstractSettingContainer;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

public class OptionsField extends AbstractSettingField<Object> {
    
    public OptionsField(int x, int y, int width, FontRenderer textRenderer, AbstractSettingContainer parent, SettingsOverlay.SettingField<Object> field) {
        super(x, y, width, textRenderer.FONT_HEIGHT + 2, textRenderer, parent, field);
    }
    
    @Override
    public void init() {
        super.init();
        try {
            List<Object> values = setting.getOptions();
            List<IChatComponent> textvalues = values.stream().map(e -> new ChatComponentText(e.toString())).collect(Collectors.toList());
            this.addButton(new Button(x + width / 2, y, width / 2, height, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentText(setting.get().toString()), (btn) -> {
                getFirstOverlayParent().openOverlay(new SelectorDropdownOverlay(x + width / 2, y, width / 2, values.size() * (textRenderer.FONT_HEIGHT + 1) + 4, textvalues, textRenderer, getFirstOverlayParent(), (choice) -> {
                    btn.setMessage(textvalues.get(choice));
                    try {
                        setting.set(values.get(choice));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }));
            }));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
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
