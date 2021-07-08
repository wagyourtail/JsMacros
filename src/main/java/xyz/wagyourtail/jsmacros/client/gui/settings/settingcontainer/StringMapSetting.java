package xyz.wagyourtail.jsmacros.client.gui.settings.settingcontainer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.overlays.TextPrompt;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;

import java.lang.reflect.InvocationTargetException;

public class StringMapSetting extends AbstractMapSettingContainer<String, StringMapSetting.StringEntry> {
    public StringMapSetting(int x, int y, int width, int height, FontRenderer textRenderer, SettingsOverlay parent, String[] group) {
        super(x, y, width, height, textRenderer, parent, group);
        defaultValue = () -> "";
    }
    
    @Override
    public void addField(String key, String value) {
        if (map.containsKey(key)) return;
        StringEntry entry = new StringEntry(x, y + 12 + totalHeight - topScroll, width - 12, textRenderer, this, key, value);
        map.put(key, entry);
        totalHeight +=  entry.height;
        scroll.setScrollPages(totalHeight / (double) height);
        if (scroll.enabled) {
            scroll.scrollToPercent(0);
        } else {
            onScrollbar(0);
        }
    }
    
    public static class StringEntry extends AbstractMapSettingContainer.MapSettingEntry<String> {
    
        public StringEntry(int x, int y, int width, FontRenderer textRenderer, StringMapSetting parent, String key, String value) {
            super(x, y, width, textRenderer, (AbstractMapSettingContainer) parent, key, value);
        }
    
        @Override
        public void init() {
            super.init();
            int w = width - height;
            addButton(new Button(x + w / 2, y, w / 2, height, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentText(value), (btn) -> {
                int x = parent.x;
                int y = parent.y;
                int width = parent.width;
                int height = parent.height;
                openOverlay(new TextPrompt(x + width / 4, y + height / 4, width / 2, height / 2, textRenderer, new ChatComponentTranslation("jsmacros.setvalue"), value, getFirstOverlayParent(), (str) -> {
                    try {
                        parent.changeValue(key, str);
                        btn.setMessage(new ChatComponentText(str));
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }));
            }));
        }
    }
}
