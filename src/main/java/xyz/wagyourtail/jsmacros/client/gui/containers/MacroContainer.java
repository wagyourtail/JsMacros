package xyz.wagyourtail.jsmacros.client.gui.containers;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.screens.MacroScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;

import java.io.File;
import java.util.List;

public class MacroContainer extends MultiElementContainer<MacroScreen> {
    private static final Identifier key_down_tex = new Identifier(JsMacros.MOD_ID, "resources/key_down.png");
    private static final Identifier key_up_tex = new Identifier(JsMacros.MOD_ID, "resources/key_up.png");
    private static final Identifier key_both_tex = new Identifier(JsMacros.MOD_ID, "resources/key_both.png");
    @SuppressWarnings("unused")
    private static final Identifier event_tex = new Identifier(JsMacros.MOD_ID, "resources/event.png");
    private final MinecraftClient mc;
    private final ScriptTrigger macro;
    private Button enableBtn;
    private Button keyBtn;
    private Button fileBtn;
    private Button delBtn;
    private Button editBtn;
    private Button keyStateBtn;
    private boolean selectkey = false;

    public MacroContainer(int x, int y, int width, int height, TextRenderer textRenderer, ScriptTrigger macro, MacroScreen parent) {
        super(x, y, width, height, textRenderer, parent);
        this.macro = macro;
        this.mc = MinecraftClient.getInstance();
        init();
    }
    
    public ScriptTrigger getRawMacro() {
        return macro;
    }
    
    @Override
    public void init() {
        super.init();
        int w = width - 12;
        enableBtn = addButton(new Button(x + 1, y + 1, w / 12 - 1, height - 2, textRenderer, macro.enabled ? 0x7000FF00 : 0x70FF0000, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new TranslatableText(macro.enabled ? "jsmacros.enabled" : "jsmacros.disabled"), (btn) -> {
            macro.enabled = !macro.enabled;
            btn.setColor(macro.enabled ? 0x7000FF00 : 0x70FF0000);
            btn.setMessage(new TranslatableText(macro.enabled ? "jsmacros.enabled" : "jsmacros.disabled"));
        }));

        keyBtn = addButton(new Button(x + w / 12 + 1, y + 1, macro.triggerType == ScriptTrigger.TriggerType.EVENT ? (w / 4) - (w / 12) - 1 : (w / 4) - (w / 12) - 1 - height, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, macro.triggerType == ScriptTrigger.TriggerType.EVENT ? new LiteralText(macro.event.replace("Event", "")) : buildKeyName(macro.event), (btn) -> {
            if (macro.triggerType == ScriptTrigger.TriggerType.EVENT) {
                parent.setEvent(this);
            } else {
                selectkey = true;
                btn.setMessage(new TranslatableText("jsmacros.presskey"));
            }
        }));
        if (macro.triggerType != ScriptTrigger.TriggerType.EVENT) keyStateBtn = addButton(new Button(x + w / 4 - height, y + 1, height, height - 2, textRenderer,0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new LiteralText(""), (btn) -> {
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

        fileBtn = addButton(new Button(x + (w / 4) + 1, y + 1, w * 3 / 4 - 3 - 30, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new LiteralText("./"+macro.scriptFile.replaceAll("\\\\", "/")), (btn) -> {
            parent.setFile(this);
        }));
        
        editBtn = addButton(new Button(x + w - 32, y + 1, 30, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new TranslatableText("selectServer.edit"), (btn) -> {
            if (!macro.scriptFile.equals("")) parent.editFile(new File(Core.instance.config.macroFolder, macro.scriptFile));
        }));

        delBtn = addButton(new Button(x + w - 1, y + 1, 12, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new LiteralText("X"), (btn) -> {
            parent.confirmRemoveMacro(this);
        }));
    }
    
    public void setEventType(String type) {
        Core.instance.eventRegistry.removeScriptTrigger(macro);
        macro.event = type;
        Core.instance.eventRegistry.addScriptTrigger(macro);
        keyBtn.setMessage(new LiteralText(macro.event.replace("Event", "")));
    }
    
    public void setFile(File f) {
        String absPath = f.getAbsolutePath();
        macro.scriptFile = absPath.substring(Math.min(absPath.length(), Core.instance.config.macroFolder.getAbsolutePath().length()+1));
        fileBtn.setMessage(new LiteralText("./"+macro.scriptFile.replaceAll("\\\\", "/")));
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
    
    public static Text buildKeyName(String translationKeys) {
        LiteralText text = new LiteralText("");
        boolean notfirst = false;
        for (String s : translationKeys.split("\\+")) {
            if (notfirst) text.append("+");
            text.append(JsMacros.getKeyText(s));
            notfirst = true;
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
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (visible) {
            int w = this.width - 12;
            // separate
            fill(matrices, x + (w / 12), y + 1, x + (w / 12) + 1, y + height - 1, 0xFFFFFFFF);
            fill(matrices, x + (w / 4), y + 1, x + (w / 4) + 1, y + height - 1, 0xFFFFFFFF);
            fill(matrices, x + width - 14, y + 1, x + width - 13, y + height - 1, 0xFFFFFFFF);
            
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
                RenderSystem.enableBlend();
                drawTexture(matrices, x + w / 4 - height + 2, y + 2, height-4, height-4, 0, 0, 32, 32, 32, 32);
                RenderSystem.disableBlend();
            }
            
            // border
            fill(matrices, x, y, x + width, y + 1, 0xFFFFFFFF);
            fill(matrices, x, y + height - 1, x + width, y + height, 0xFFFFFFFF);
            fill(matrices, x, y + 1, x + 1, y + height - 1, 0xFFFFFFFF);
            fill(matrices, x + width - 1, y + 1, x + width, y + height - 1, 0xFFFFFFFF);
            
            // overlay
            if (keyBtn.hovering && keyBtn.cantRenderAllText()) {
                fill(matrices, mouseX-2, mouseY-textRenderer.fontHeight - 3, mouseX+textRenderer.getWidth(keyBtn.getMessage())+2, mouseY, 0xFF000000);
                drawTextWithShadow(matrices, textRenderer, keyBtn.getMessage(), mouseX, mouseY-textRenderer.fontHeight - 1, 0xFFFFFF);
            }
            if (fileBtn.hovering && fileBtn.cantRenderAllText()) {
                List<OrderedText> lines = textRenderer.wrapLines(fileBtn.getMessage(), this.x + this.width - mouseX);
                int top = mouseY-(textRenderer.fontHeight*lines.size())-2;
                int width = lines.stream().map(e -> textRenderer.getWidth(e)).reduce(0, (e, t) -> Math.max(e, t));
                fill(matrices, mouseX-2, top - 1, mouseX+width+2, mouseY, 0xFF000000);
                for (int i = 0; i < lines.size(); ++i) {
                    int wi = textRenderer.getWidth(lines.get(i)) / 2;
                    textRenderer.draw(matrices, lines.get(i), mouseX + width/2 - wi, top+textRenderer.fontHeight*i, 0xFFFFFF);
                }
            }
        }
    }
}
