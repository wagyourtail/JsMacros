package xyz.wagyourtail.jsmacros.client.gui.elements;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class TextInput extends Button {
    public Consumer<String> onChange;
    public String mask = ".*";
    public String content;
    protected int selColor;
    protected int selStart;
    public int selStartIndex;
    protected int selEnd;
    public int selEndIndex;
    protected int arrowCursor;
    
    public TextInput(int x, int y, int width, int height, TextRenderer textRenderer, int color, int borderColor, int hilightColor, int textColor, String message, Consumer<Button> onClick, Consumer<String> onChange) {
        super(x, y, width, height, textRenderer, color, borderColor, color, textColor, new LiteralText(""), onClick);
        this.selColor = hilightColor;
        this.content = message;
        this.onChange = onChange;
        this.updateSelStart(content.length());
        this.updateSelEnd(content.length());
        this.arrowCursor = content.length();
    }

    @Override
    public void setMessage(String message) {
        content = message;
    }

    public void updateSelStart(int startIndex) {
        selStartIndex = startIndex;
        if (startIndex == 0) selStart = x + 1;
        else selStart = x + 2 + textRenderer.getStringWidth(content.substring(0, startIndex));
    }

    public void updateSelEnd(int endIndex) {
        selEndIndex = endIndex;
        if (endIndex == 0) selEnd = x + 2;
        else selEnd = x + 3 + textRenderer.getStringWidth(content.substring(0, endIndex));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.clicked(mouseX, mouseY);
        if (this.isFocused()) {
            int pos = textRenderer.trimToWidth(content, (int) (mouseX - x - 2)).length();
            updateSelStart(pos);
            updateSelEnd(pos);
            arrowCursor = pos;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.isFocused()) {
            int pos = textRenderer.trimToWidth(content, (int) (mouseX - x - 2)).length();
            updateSelEnd(pos);
            arrowCursor = pos;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    public void swapStartEnd() {
        int temp1 = selStartIndex;
        updateSelStart(selEndIndex);
        updateSelEnd(temp1);

    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean ctrl;
        if (this.isFocused()) {
            if (selEndIndex < selStartIndex) swapStartEnd();
            MinecraftClient mc = MinecraftClient.getInstance();
            if (Screen.isSelectAll(keyCode)) {
                this.updateSelStart(0);
                this.updateSelEnd(content.length());
            } else if (Screen.isCopy(keyCode)) {
                mc.keyboard.setClipboard(this.content.substring(selStartIndex, selEndIndex));
            } else if (Screen.isPaste(keyCode)) {
                content = content.substring(0, selStartIndex) + mc.keyboard.getClipboard() + content.substring(selEndIndex);
                if (onChange != null) onChange.accept(content);
                updateSelEnd(selStartIndex + mc.keyboard.getClipboard().length());
                arrowCursor = selStartIndex + mc.keyboard.getClipboard().length();
            } else if (Screen.isCut(keyCode)) {
                mc.keyboard.setClipboard(this.content.substring(selStartIndex, selEndIndex));
                content = content.substring(0, selStartIndex) + content.substring(selEndIndex);
                if (onChange != null) onChange.accept(content);
                updateSelEnd(selStartIndex);
                arrowCursor = selStartIndex;
            }
            switch (keyCode) {
            case GLFW.GLFW_KEY_BACKSPACE:
                if (selStartIndex == selEndIndex && selStartIndex > 0) updateSelStart(selStartIndex - 1);
                content = content.substring(0, selStartIndex) + content.substring(selEndIndex);
                if (onChange != null) onChange.accept(content);
                updateSelEnd(selStartIndex);
                arrowCursor = selStartIndex;
                break;
            case GLFW.GLFW_KEY_DELETE:
                if (selStartIndex == selEndIndex && selStartIndex < content.length()) updateSelEnd(selEndIndex + 1);
                content = content.substring(0, selStartIndex) + content.substring(selEndIndex);
                if (onChange != null) onChange.accept(content);
                updateSelEnd(selStartIndex);
                arrowCursor = selStartIndex;
                break;
            case GLFW.GLFW_KEY_HOME:
                updateSelStart(0);
                updateSelEnd(0);
                break;
            case GLFW.GLFW_KEY_END:
                this.updateSelStart(content.length());
                this.updateSelEnd(content.length());
                break;
            case GLFW.GLFW_KEY_LEFT:
                ctrl = !Screen.hasControlDown();
                if (arrowCursor > 0) if (arrowCursor < selEndIndex) {
                    updateSelStart(--arrowCursor);
                    if (ctrl) updateSelEnd(selStartIndex);
                } else if (arrowCursor >= selEndIndex) {
                    updateSelEnd(--arrowCursor);
                    if (ctrl) updateSelStart(selEndIndex);
                }
                break;
            case GLFW.GLFW_KEY_RIGHT:
                ctrl = !Screen.hasControlDown();
                if (arrowCursor < content.length()) if (arrowCursor < selEndIndex) {
                    updateSelStart(++arrowCursor);
                    if (ctrl) updateSelEnd(selStartIndex);
                } else {
                    updateSelEnd(++arrowCursor);
                    if (ctrl) updateSelStart(selEndIndex);
                }
                break;
            default:
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        if (selEndIndex < selStartIndex) swapStartEnd();
        String newContent = content.substring(0, selStartIndex) + chr + content.substring(selEndIndex);
        if (newContent.matches(mask)) {
            content = newContent;
            if (onChange != null) onChange.accept(content);
            updateSelStart(selStartIndex + 1);
            arrowCursor = selStartIndex;
            updateSelEnd(arrowCursor);
        }
        return false;
    }

    @Override
    public boolean clicked(double mouseX, double mouseY) {
        boolean bl = super.clicked(mouseX, mouseY);
        if (this.isFocused() ^ bl) this.changeFocus(true);
        return bl;
    }
    
    public void setSelected(boolean sel) {
        this.setFocused(sel);
    }

    @Override
    protected void renderMessage() {
        fill(selStart, height > 9 ? y + 2 : y, Math.min(selEnd, x + width - 2), (height > 9 ? y + 2 : y) + textRenderer.fontHeight, selColor);
        drawString(textRenderer, textRenderer.trimToWidth(content, width - 4), x + 2, height > 9 ? y + 2 : y, textColor);
    }
}
