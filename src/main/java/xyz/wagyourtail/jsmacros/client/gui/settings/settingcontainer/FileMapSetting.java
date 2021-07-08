package xyz.wagyourtail.jsmacros.client.gui.settings.settingcontainer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ChatComponentText;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.overlays.FileChooser;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;
import xyz.wagyourtail.jsmacros.client.gui.settings.settingfields.FileField;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class FileMapSetting extends AbstractMapSettingContainer<String, FileMapSetting.FileEntry> {
    
    public FileMapSetting(int x, int y, int width, int height, FontRenderer textRenderer, SettingsOverlay parent, String[] group) {
        super(x, y, width, height, textRenderer, parent, group);
        defaultValue = () -> "./";
    }
    
    @Override
    public void addField(String key, String value) {
        if (map.containsKey(key)) return;
        FileEntry entry = new FileEntry(x, y + 12 + totalHeight - topScroll, width - 12, textRenderer, this, key, value);
        map.put(key, entry);
        totalHeight +=  entry.height;
        scroll.setScrollPages(totalHeight / (double) height);
        if (scroll.enabled) {
            scroll.scrollToPercent(0);
        } else {
            onScrollbar(0);
        }
    }
    
    public static class FileEntry extends AbstractMapSettingContainer.MapSettingEntry<String> {
    
        public FileEntry(int x, int y, int width, FontRenderer textRenderer, FileMapSetting parent, String key, String value) {
            super(x, y, width, textRenderer, (AbstractMapSettingContainer) parent, key, value);
        }
    
        @Override
        public void init() {
            super.init();
            int w = width - height;
            addButton(new Button(x + w / 2, y, w / 2, height, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentText(value), (btn) -> {
                File current = new File(FileField.getTopLevel(parent.setting), value);
                FileChooser fc = new FileChooser(parent.x, parent.y, parent.width, parent.height, textRenderer, current.getParentFile(), current, getFirstOverlayParent(), (file) -> {
                    String newVal = "." + file.getAbsolutePath().substring(FileField.getTopLevel(parent.setting).getAbsolutePath().length()).replaceAll("\\\\", "/");
                    try {
                        parent.changeValue(key, newVal);
                        btn.setMessage(new ChatComponentText(newVal));
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }, file -> {});
                fc.root = FileField.getTopLevel(parent.setting);
                parent.openOverlay(fc);
            }));
        }
    }
}
