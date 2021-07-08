package xyz.wagyourtail.jsmacros.client.gui.containers;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;
import xyz.wagyourtail.jsmacros.client.config.Sorting;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.screens.MacroScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;

public class MacroListTopbar extends MultiElementContainer<MacroScreen> {
    public ScriptTrigger.TriggerType deftype;
    private Button type;
    
    public MacroListTopbar(MacroScreen parent, int x, int y, int width, int height, FontRenderer textRenderer, ScriptTrigger.TriggerType deftype) {
        super(x, y, width, height, textRenderer, parent);
        this.deftype = deftype;
        init();
    }

    @Override
    public void init() {
        super.init();
        
        int w = width - 12;
        
        addButton(new Button(x + 1, y + 1, w / 12 - 1, height - 3, textRenderer, Core.instance.config.getOptions(ClientConfigV2.class).sortMethod == Sorting.MacroSortMethod.Enabled ? 0x3FFFFFFF : 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new ChatComponentTranslation("jsmacros.enabled"), (btn) -> {
            Core.instance.config.getOptions(ClientConfigV2.class).sortMethod = Sorting.MacroSortMethod.Enabled;
            parent.reload();
        }));
        
        type = addButton(new Button(x + w / 12 + 1, y + 1, (w / 4) - (w / 12) - 1, height - 3, textRenderer, Core.instance.config.getOptions(ClientConfigV2.class).sortMethod == Sorting.MacroSortMethod.TriggerName ? 0x3FFFFFFF : 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new ChatComponentTranslation(deftype == ScriptTrigger.TriggerType.EVENT ? "jsmacros.events" : "jsmacros.keys"), (btn) -> {
            Core.instance.config.getOptions(ClientConfigV2.class).sortMethod = Sorting.MacroSortMethod.TriggerName;
            parent.reload();
        }));
        
        addButton(new Button(x + (w / 4) + 1, y + 1, w * 3 / 4 - 3 - 30, height - 3, textRenderer, Core.instance.config.getOptions(ClientConfigV2.class).sortMethod == Sorting.MacroSortMethod.FileName ? 0x3FFFFFFF : 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new ChatComponentTranslation("jsmacros.file"), (btn) -> {
            Core.instance.config.getOptions(ClientConfigV2.class).sortMethod = Sorting.MacroSortMethod.FileName;
            parent.reload();
        }));
        
        addButton(new Button(x + w - 32, y + 1, 30, height - 3, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new ChatComponentTranslation("jsmacros.run"), (btn) -> {
            parent.runFile();
        }));
        
        addButton(new Button(x + w - 1, y+1, 11, height - 3, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new ChatComponentText("+"), (btn) -> {
            ScriptTrigger macro = new ScriptTrigger(deftype, "", "", false);
            Core.instance.eventRegistry.addScriptTrigger(macro);
            parent.addMacro(macro);
        }));
    }

    public void updateType(ScriptTrigger.TriggerType type) {
        this.deftype = type;
        this.type.setMessage(new ChatComponentTranslation(deftype == ScriptTrigger.TriggerType.EVENT ? "jsmacros.events" : "jsmacros.keys"));
    }
    
    @Override
    public void render(int mouseX, int mouseY, float delta) {
        drawRect(x, y, x + width, y + 1, 0xFFFFFFFF);
        drawRect(x, y + height - 2, x + width, y + height - 1, 0xFFFFFFFF);
        drawRect(x, y + height - 1, x + width, y + height, 0xFF7F7F7F);
        drawRect(x, y + 1, x + 1, y + height - 1, 0xFFFFFFFF);
        drawRect(x + width - 1, y + 1, x + width, y + height - 1, 0xFFFFFFFF);
        int w = this.width - 12;
    
        drawRect(x + (w / 12), y + 1, x + (w / 12) + 1, y + height - 1, 0xFFFFFFFF);
        drawRect(x + (w / 4), y + 1, x + (w / 4) + 1, y + height - 1, 0xFFFFFFFF);
        drawRect(x + width - 14, y + 1, x + width - 13, y + height - 1, 0xFFFFFFFF);
    }
}
