package xyz.wagyourtail.jsmacros.client.gui.screens;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Keyboard;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.access.IMouseScrolled;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen;
import xyz.wagyourtail.jsmacros.client.gui.elements.Scrollbar;
import xyz.wagyourtail.jsmacros.client.gui.elements.TextInput;
import xyz.wagyourtail.jsmacros.client.gui.overlays.IOverlayParent;
import xyz.wagyourtail.jsmacros.client.gui.overlays.OverlayContainer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseScreen extends GuiScreen implements IOverlayParent, IMouseScrolled {
    public GuiScreen parent;
    protected OverlayContainer overlay;
    protected IChatComponent title;
    private final int[] prevMouseX = new int[] {0,0,0,0,0,0};
    private final int[] prevMouseY = new int[] {0,0,0,0,0,0};
    
    protected BaseScreen(IChatComponent title, GuiScreen parent) {
        super();
        this.title = title;
        this.parent = parent;
    }
    
    public static String trimmed(FontRenderer textRenderer, String str, int width) {
        return textRenderer.trimStringToWidth(str, width);
    }
    
    public void reload() {
        initGui();
    }

    @Override
    public void initGui() {
        assert mc != null;
        buttonList.clear();
        super.initGui();
        overlay = null;
        JsMacros.prevScreen = this;
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void onGuiClosed() {
        assert mc != null;
        Keyboard.enableRepeatEvents(false);
    }
    
    @Override
    public void openOverlay(OverlayContainer overlay) {
        openOverlay(overlay, true);
    }

    @Override
    public IOverlayParent getFirstOverlayParent() {
        return this;
    }
    
    @Override
    public OverlayContainer getChildOverlay() {
        if (overlay != null) return overlay.getChildOverlay();
        return null;
    }
    
    @Override
    public void openOverlay(OverlayContainer overlay, boolean disableButtons) {
        if (this.overlay != null) {
            this.overlay.openOverlay(overlay, disableButtons);
            return;
        }
        if (disableButtons) {
            for (GuiButton b : buttonList) {
                overlay.savedBtnStates.put(b, b.enabled);
                b.enabled = false;
            }
        }
        this.overlay = overlay;
        overlay.init();
    }

    @Override
    public void closeOverlay(OverlayContainer overlay) {
        if (overlay == null) return;
        for (GuiButton b : overlay.getButtons()) {
            removeButton(b);
        }
        for (GuiButton b : overlay.savedBtnStates.keySet()) {
            b.enabled = overlay.savedBtnStates.get(b);
        }
        overlay.onClose();
        if (this.overlay == overlay) this.overlay = null;
    }

    @Override
    public void removeButton(GuiButton btn) {
        buttonList.remove(btn);
    }
    
    @Override
    public <T extends GuiButton> T addButton(T button) {
        buttonList.add(button);
        return button;
    }
    
    @Override
    public void handleKeyboardInput() throws IOException {
        if (Keyboard.getEventKeyState()) {
            if (!keyPressed(Keyboard.getEventKey(), 0, createModifiers())) {
                this.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
            }
        } else {
            keyReleased(Keyboard.getEventKey(), 0, createModifiers());
        }
    }
    
    public static int createModifiers() {
        int i = 0;
        if (GuiScreen.isShiftKeyDown()) i |= 1;
        if (GuiScreen.isCtrlKeyDown()) i |= 2;
        if (GuiScreen.isAltKeyDown()) i |= 4;
        return i;
    }
    
    public static List<Integer> unpackModifiers(int mods) {
        List<Integer> l = new ArrayList<>();
        if ((mods & 4) == 4) {
            l.add(Keyboard.KEY_LMETA);
        }
        if ((mods & 2) == 2) {
            l.add(Keyboard.KEY_LCONTROL);
        }
        if ((mods & 1) == 1) {
            l.add(Keyboard.KEY_LSHIFT);
        }
        return l;
    }
    
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (overlay != null) {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                this.overlay.closeOverlay(this.overlay.getChildOverlay());
                return true;
            }
            return this.overlay.keyPressed(keyCode, scanCode, modifiers);
        }
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.onClose();
            return true;
        }
        for (GuiButton b : buttonList) {
            if (b instanceof TextInput && ((TextInput) b).keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }
    
    @Override
    public boolean mouseScrolled(int mouseX, int mouseY, int amount) {
        if (overlay!= null && overlay.scroll != null) overlay.scroll.mouseDragged(mouseX, mouseY, 0, 0, -amount * 2);
        return false;
    }
    
    public boolean mouseDragged(int mouseX, int mouseY, int button, int deltaX, int deltaY) {
        GuiButton focused = ((IScreen) this).getFocused();
        if (focused instanceof Scrollbar) {
            ((Scrollbar) focused).mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            return true;
        }
        for (GuiButton b : buttonList) {
            if (b instanceof TextInput) {
                if (((TextInput) b).selected) return ((TextInput) b).mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            }
        }
        return false;
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (overlay != null) overlay.onClick(mouseX, mouseY, button);
        super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public void mouseReleased(int mosueX, int mouseY, int button) {
        super.mouseReleased(mosueX, mouseY, button);
    }
    
    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int btn, long time) {
        mouseDragged(mouseX, mouseY, btn, mouseX - prevMouseX[btn], mouseY - prevMouseY[btn]);
        prevMouseX[btn] = mouseX;
        prevMouseY[btn] = mouseY;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float delta) {
        if (overlay != null) overlay.render(mouseX, mouseY, delta);
    }
    
    public boolean shouldCloseOnEsc() {
        return this.overlay == null;
    }
    
    public void updateSettings() {}

    public void onClose() {
        assert mc != null;
        if (mc.theWorld == null)
            openParent();
        else {
            mc.displayGuiScreen(null);
        }
    }
    
    public void openParent() {
        assert mc != null;
        mc.displayGuiScreen(parent);
    }
    
}
