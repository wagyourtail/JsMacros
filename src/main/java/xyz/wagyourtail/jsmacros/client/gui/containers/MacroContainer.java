package xyz.wagyourtail.jsmacros.client.gui.containers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.screens.BaseScreen;
import xyz.wagyourtail.jsmacros.client.gui.screens.MacroScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;

import java.io.File;
import java.util.List;

public class MacroContainer extends MultiElementContainer<MacroScreen> {
    private static final ResourceLocation key_down_tex = new ResourceLocation(JsMacros.MOD_ID, "resources/key_down.png");
    private static final ResourceLocation key_up_tex = new ResourceLocation(JsMacros.MOD_ID, "resources/key_up.png");
    private static final ResourceLocation key_both_tex = new ResourceLocation(JsMacros.MOD_ID, "resources/key_both.png");
    @SuppressWarnings("unused")
    private static final ResourceLocation event_tex = new ResourceLocation(JsMacros.MOD_ID, "resources/event.png");
    private final Minecraft mc;
    private final ScriptTrigger macro;
    private Button enableBtn;
    private Button keyBtn;
    private Button fileBtn;
    private Button delBtn;
    private Button editBtn;
    private Button keyStateBtn;
    private boolean selectkey = false;

    public MacroContainer(int x, int y, int width, int height, FontRenderer textRenderer, ScriptTrigger macro, MacroScreen parent) {
        super(x, y, width, height, textRenderer, parent);
        this.macro = macro;
        this.mc = Minecraft.getMinecraft();
        init();
    }
    
    public ScriptTrigger getRawMacro() {
        return macro;
    }
    
    @Override
    public void init() {
        super.init();
        int w = width - 12;
        enableBtn = addButton(new Button(x + 1, y + 1, w / 12 - 1, height - 2, textRenderer, macro.enabled ? 0x7000FF00 : 0x70FF0000, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new ChatComponentTranslation(macro.enabled ? "jsmacros.enabled" : "jsmacros.disabled"), (btn) -> {
            macro.enabled = !macro.enabled;
            btn.setColor(macro.enabled ? 0x7000FF00 : 0x70FF0000);
            btn.setMessage(new ChatComponentTranslation(macro.enabled ? "jsmacros.enabled" : "jsmacros.disabled"));
        }));

        keyBtn = addButton(new Button(x + w / 12 + 1, y + 1, macro.triggerType == ScriptTrigger.TriggerType.EVENT ? (w / 4) - (w / 12) - 1 : (w / 4) - (w / 12) - 1 - height, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, macro.triggerType == ScriptTrigger.TriggerType.EVENT ? new ChatComponentText(macro.event.replace("Event", "")) : buildKeyName(macro.event), (btn) -> {
            if (macro.triggerType == ScriptTrigger.TriggerType.EVENT) {
                parent.setEvent(this);
            } else {
                selectkey = true;
                btn.setMessage(new ChatComponentTranslation("jsmacros.presskey"));
            }
        }));
        if (macro.triggerType != ScriptTrigger.TriggerType.EVENT) keyStateBtn = addButton(new Button(x + w / 4 - height, y + 1, height, height - 2, textRenderer,0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new ChatComponentText(""), (btn) -> {
            switch(macro.triggerType) {
            default:
            case KEY_RISING:
                macro.triggerType = ScriptTrigger.TriggerType.KEY_FALLING;
                break;
            case KEY_FALLING:
                macro.triggerType = ScriptTrigger.TriggerType.KEY_BOTH;
                break;
            case KEY_BOTH:
                macro.triggerType = ScriptTrigger.TriggerType.KEY_RISING;
                break;
            }
        }));

        fileBtn = addButton(new Button(x + (w / 4) + 1, y + 1, w * 3 / 4 - 3 - 30, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new ChatComponentText("./"+macro.scriptFile.replaceAll("\\\\", "/")), (btn) -> {
            parent.setFile(this);
        }));
        
        editBtn = addButton(new Button(x + w - 32, y + 1, 30, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new ChatComponentTranslation("selectServer.edit"), (btn) -> {
            if (!macro.scriptFile.equals("")) parent.editFile(new File(Core.instance.config.macroFolder, macro.scriptFile));
        }));

        delBtn = addButton(new Button(x + w - 1, y + 1, 12, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new ChatComponentText("X"), (btn) -> {
            parent.confirmRemoveMacro(this);
        }));
    }
    
    public void setEventType(String type) {
        Core.instance.eventRegistry.removeScriptTrigger(macro);
        macro.event = type;
        Core.instance.eventRegistry.addScriptTrigger(macro);
        keyBtn.setMessage(new ChatComponentText(macro.event.replace("Event", "")));
    }
    
    public void setFile(File f) {
        String absPath = f.getAbsolutePath();
        macro.scriptFile = absPath.substring(Math.min(absPath.length(), Core.instance.config.macroFolder.getAbsolutePath().length()+1));
        fileBtn.setMessage(new ChatComponentText("./"+macro.scriptFile.replaceAll("\\\\", "/")));
    }

    @Override
    public void setPos(int x, int y, int width, int height) {
        super.setPos(x, y, width, height);
        int w = width - 12;
        enableBtn.setPos(x + 1, y + 1, w / 12 - 1, height - 2);
        keyBtn.setPos(x + w / 12 + 1, y + 1, macro.triggerType == ScriptTrigger.TriggerType.EVENT ? (w / 4) - (w / 12) - 1 : (w / 4) - (w / 12) - 1 - height, height - 2);
        if (macro.triggerType != ScriptTrigger.TriggerType.EVENT) keyStateBtn.setPos(x + w / 4 - height, y + 1, height, height - 2);
        fileBtn.setPos(x + (w / 4) + 1, y + 1, w * 3 / 4 - 3 - 30, height - 2);
        editBtn.setPos(x + w - 32, y + 1, 30, height - 2);
        delBtn.setPos(x + w - 1, y + 1, 12, height - 2);

    }
    
    public boolean onKey(String translationKey) {
        if (selectkey) {
            setKey(translationKey);
            return false;
        }
        return true;
    }
    
    public static ChatComponentText buildKeyName(String translationKeys) {
        ChatComponentText text = new ChatComponentText("");
        boolean notfirst = false;
        String[] s = translationKeys.split("\\+");
        if (s.length == 1) {
            if (s[0].equals("")) s[0] = "0";
            s = new String[] {"0", s[0]};
        }
        try {
            for (int mod : BaseScreen.unpackModifiers(Integer.parseInt(s[0]))) {
                text.appendText(JsMacros.getLocalizedName(mod) + "+");
            }
            text.appendText(JsMacros.getLocalizedName(Integer.parseInt(s[1])));
        } catch(NumberFormatException ex) {
            text.appendText("NONE");
        }
        return text;
    }
    
    public void setKey(String translationKeys) {
        Core.instance.eventRegistry.removeScriptTrigger(macro);
        macro.event = translationKeys;
        Core.instance.eventRegistry.addScriptTrigger(macro);
        keyBtn.setMessage(buildKeyName(translationKeys));
        selectkey = false;
    }
    
    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (visible) {
            int w = this.width - 12;
            // separate
            drawRect(x + (w / 12), y + 1, x + (w / 12) + 1, y + height - 1, 0xFFFFFFFF);
            drawRect(x + (w / 4), y + 1, x + (w / 4) + 1, y + height - 1, 0xFFFFFFFF);
            drawRect(x + width - 14, y + 1, x + width - 13, y + height - 1, 0xFFFFFFFF);
            
            // icon for keystate
            if (macro.triggerType != ScriptTrigger.TriggerType.EVENT) {
                switch (macro.triggerType) {
                default:
                case KEY_FALLING:
                    this.mc.getTextureManager().bindTexture(key_up_tex);
                    break;
                case KEY_RISING:
                    this.mc.getTextureManager().bindTexture(key_down_tex);
                    break;
                case KEY_BOTH:
                    this.mc.getTextureManager().bindTexture(key_both_tex);
                    break;
                }
                GlStateManager.enableBlend();
                drawScaledCustomSizeModalRect(x + w / 4 - height + 2, y + 2, 0, 0, 32, 32, height-4, height-4,32, 32);
                GlStateManager.disableBlend();
            }
            
            // border
            drawRect(x, y, x + width, y + 1, 0xFFFFFFFF);
            drawRect(x, y + height - 1, x + width, y + height, 0xFFFFFFFF);
            drawRect(x, y + 1, x + 1, y + height - 1, 0xFFFFFFFF);
            drawRect(x + width - 1, y + 1, x + width, y + height - 1, 0xFFFFFFFF);
            
            // overlay
            if (keyBtn.hovering && keyBtn.cantRenderAllText()) {
                drawRect(mouseX-2, mouseY-textRenderer.FONT_HEIGHT - 3, mouseX+textRenderer.getStringWidth(keyBtn.displayString)+2, mouseY, 0xFF000000);
                drawString(textRenderer, keyBtn.displayString, mouseX, mouseY-textRenderer.FONT_HEIGHT - 1, 0xFFFFFF);
            }
            if (fileBtn.hovering && fileBtn.cantRenderAllText()) {
                List<String> lines = textRenderer.listFormattedStringToWidth(fileBtn.displayString, this.x + this.width - mouseX);
                int top = mouseY-(textRenderer.FONT_HEIGHT*lines.size())-2;
                int width = lines.stream().map(e -> textRenderer.getStringWidth(e)).reduce(0, (e, t) -> Math.max(e, t));
                drawRect(mouseX-2, top - 1, mouseX+width+2, mouseY, 0xFF000000);
                for (int i = 0; i < lines.size(); ++i) {
                    int wi = textRenderer.getStringWidth(lines.get(i)) / 2;
                    textRenderer.drawString(lines.get(i), mouseX + width/2 - wi, top+textRenderer.FONT_HEIGHT*i, 0xFFFFFF);
                }
            }
        }
    }
}
