package xyz.wagyourtail.jsmacros.client.gui.screens;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentTranslation;
import xyz.wagyourtail.jsmacros.client.gui.containers.MacroContainer;
import xyz.wagyourtail.jsmacros.client.gui.containers.MacroListTopbar;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.elements.Scrollbar;
import xyz.wagyourtail.jsmacros.client.gui.overlays.AboutOverlay;
import xyz.wagyourtail.jsmacros.client.gui.overlays.ConfirmOverlay;
import xyz.wagyourtail.jsmacros.client.gui.overlays.EventChooser;
import xyz.wagyourtail.jsmacros.client.gui.overlays.FileChooser;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MacroScreen extends BaseScreen {
    protected MacroListTopbar topbar;
    protected Scrollbar macroScroll;
    protected List<MacroContainer> macros = new ArrayList<>();
    protected int topScroll;
    protected Button keyScreen;
    protected Button eventScreen;
    protected Button runningBtn;
    protected Button aboutBtn;
    
    public MacroScreen(GuiScreen parent) {
        super(new ChatComponentTranslation("jsmacros.title"), parent);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        macros.clear();
        keyScreen = this.addButton(new Button(0, 0, this.width / 6 - 1, 20, fontRendererObj, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentTranslation("jsmacros.keys"), null));

        eventScreen = this.addButton(new Button(this.width / 6 + 1, 0, this.width / 6 - 1, 20, fontRendererObj, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentTranslation("jsmacros.events"), null));

        this.addButton(new Button(this.width * 5 / 6 + 1, 0, this.width / 6 - 1, 20, fontRendererObj, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentTranslation("jsmacros.settings"), (btn) -> {
            openOverlay(new SettingsOverlay(this.width / 4, this.height / 4, this.width / 2, this.height / 2, fontRendererObj, this));
        }));

        topbar = new MacroListTopbar(this, this.width / 12, 25, this.width * 5 / 6, 14, this.fontRendererObj, ScriptTrigger.TriggerType.KEY_RISING);

        topScroll = 40;
        macroScroll = this.addButton(new Scrollbar(this.width * 23 / 24 - 4, 50, 8, this.height - 75, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));
    
        runningBtn = this.addButton(new Button(0, this.height - 12, this.width / 12, 12, fontRendererObj, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentTranslation("jsmacros.running"), (btn) -> {
            assert mc != null;
            mc.displayGuiScreen(new CancelScreen(this));
        }));
        
        aboutBtn = this.addButton(new Button(this.width * 11 / 12, this.height - 12, this.width / 12, 12, fontRendererObj, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentTranslation("jsmacros.about"), (btn) -> this.openOverlay(new AboutOverlay(this.width / 4, this.height / 4, this.width / 2, this.height / 2, fontRendererObj, this))));
    }
    
    @Override
    public boolean mouseScrolled(int mouseX, int mouseY, int amount) {
        if (overlay == null) {
            macroScroll.mouseDragged(mouseX, mouseY, 0, 0, -amount * 2);
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    public void addMacro(ScriptTrigger macro) {
        macros.add(new MacroContainer(this.width / 12, topScroll + macros.size() * 16, this.width * 5 / 6, 14, this.fontRendererObj, macro, this));
        macroScroll.setScrollPages(((macros.size() + 1) * 16) / (double) Math.max(1, this.height - 40));
    }

    public void setFile(MacroContainer macro) {
        File f = new File(Core.instance.config.macroFolder, macro.getRawMacro().scriptFile);
        File dir = Core.instance.config.macroFolder;
        if (!f.equals(Core.instance.config.macroFolder)) dir = f.getParentFile();
        openOverlay(new FileChooser(width / 4, height / 4, width / 2, height / 2, this.fontRendererObj, dir, f, this, macro::setFile, this::editFile));
    }
    
    public void setEvent(MacroContainer macro) {
        openOverlay(new EventChooser(width / 4, height / 4, width / 2, height / 2, this.fontRendererObj, macro.getRawMacro().event, this, macro::setEventType));
    }
    
    public void runFile() {
        openOverlay(new FileChooser(width / 4, height / 4, width / 2, height / 2, this.fontRendererObj, Core.instance.config.macroFolder, null, this, (file) -> {
            try {
                Core.instance.exec(new ScriptTrigger(ScriptTrigger.TriggerType.EVENT, "", file.getCanonicalPath().substring(Core.instance.config.macroFolder.getCanonicalPath().length()), true), null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, this::editFile));
    }

    public void confirmRemoveMacro(MacroContainer macro) {
        openOverlay(new ConfirmOverlay(width / 2 - 100, height / 2 - 50, 200, 100, this.fontRendererObj, new ChatComponentTranslation("jsmacros.confirmdeletemacro"), this, (conf) -> removeMacro(macro)));
    }
    
    public void removeMacro(MacroContainer macro) {
        Core.instance.eventRegistry.removeScriptTrigger(macro.getRawMacro());
        for (GuiButton b : macro.getButtons()) {
            removeButton(b);
        }
        macros.remove(macro);
        setMacroPos();
    }
    
    private void onScrollbar(double page) {
        topScroll = 40 - (int) (page * (height - 40));
        setMacroPos();
    }
    
    public void setMacroPos() {
        int i = 0;
        for (MacroContainer m : macros) {
            m.setVisible(topScroll + i * 16 >= 40);
            m.setPos(this.width / 12, topScroll + (i++) * 16, this.width * 5 / 6, 14);
        }
    }

    public void editFile(File file) {
        if (file != null && file.exists() && file.isFile()) {
            assert mc != null;
            mc.displayGuiScreen(new EditorScreen(this, file));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float delta) {
        this.drawDefaultBackground();
        
        topbar.render(mouseX, mouseY, delta);

        for (GuiButton b : ImmutableList.copyOf(this.buttonList)) {
            b.drawButton(mc, mouseX, mouseY);
        }

        for (MacroContainer macro : ImmutableList.copyOf(this.macros)) {
            macro.render(mouseX, mouseY, delta);
        }
        
        drawCenteredString(this.fontRendererObj, Core.instance.profile.getCurrentProfileName(), this.width * 7 / 12, 5, 0x7F7F7F);

        drawRect(this.width * 5 / 6 - 1, 0, this.width * 5 / 6 + 1, 20, 0xFFFFFFFF);
        drawRect(this.width / 6 - 1, 0, this.width / 6 + 1, 20, 0xFFFFFFFF);
        drawRect(this.width / 6 * 2, 0, this.width / 6 * 2 + 2, 20, 0xFFFFFFFF);
        drawRect(0, 20, width, 22, 0xFFFFFFFF);

        super.drawScreen(mouseX, mouseY, delta);
    }
    
    @Override
    public void updateSettings() {
        reload();
    }
    
    @Override
    public void onClose() {
        Core.instance.profile.saveProfile();
        super.onClose();
    }
}
