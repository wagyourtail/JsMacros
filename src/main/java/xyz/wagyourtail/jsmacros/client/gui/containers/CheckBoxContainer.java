package xyz.wagyourtail.jsmacros.client.gui.containers;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;

import java.util.function.Consumer;

public class CheckBoxContainer extends MultiElementContainer<IContainerParent> {
    private boolean state;
    private Button checkBox;
    private final Consumer<Boolean> setState;
    public IChatComponent message;
    
    
    public CheckBoxContainer(int x, int y, int width, int height, FontRenderer textRenderer, boolean defaultState, IChatComponent message, IContainerParent parent, Consumer<Boolean> setState) {
        super(x, y, width, height, textRenderer, parent);
        this.state = defaultState;
        this.message = message;
        this.setState = setState;
        this.init();
    }
    
    @Override
    public void init() {
        super.init();
        
        checkBox = this.addButton(new Button(x, y, height, height, textRenderer, 0, 0xFF000000,0x7FFFFFFF, 0xFFFFFF, new ChatComponentText(state ? "\u2713" : ""), btn -> {
            state = !state;
            if (setState != null) setState.accept(state);
            btn.setMessage(new ChatComponentText(state ? "\u2713" : ""));
        }));
    }
    
    @Override
    public void setPos(int x, int y, int width, int height) {
        checkBox.setPos(x+1, y+1, height-2, height-2);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (this.visible) {
            textRenderer.drawString(textRenderer.trimStringToWidth(message.getFormattedText(), width-height-2), x+height, y+2, 0xFFFFFF);
        }
    }

}
