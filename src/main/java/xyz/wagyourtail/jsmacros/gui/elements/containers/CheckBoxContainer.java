package xyz.wagyourtail.jsmacros.gui.elements.containers;

import java.util.function.Consumer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.gui.elements.Button;

public class CheckBoxContainer extends MultiElementContainer {
    private boolean state;
    private Button checkBox;
    private Consumer<Boolean> setState;
    public Text message;
    
    
    public CheckBoxContainer(int x, int y, int width, int height, TextRenderer textRenderer, boolean defaultState, Text message, Consumer<AbstractButtonWidget> addButton, Consumer<Boolean> setState) {
        super(x, y, width, height, textRenderer, addButton);
        this.state = defaultState;
        this.message = message;
        this.setState = setState;
        this.init();
    }
    
    public void init() {
        super.init();
        
        checkBox = (Button) this.addButton(new Button(x, y, height, height, 0, 0xFF000000,0x7FFFFFFF, 0xFFFFFF, new LiteralText(state ? "\u2713" : ""), btn -> {
            state = !state;
            if (setState != null) setState.accept(state);
            btn.setMessage(new LiteralText(state ? "\u2713" : ""));
        }));
    }
    
    public void setPos(int x, int y, int width, int height) {
        checkBox.setPos(x+1, y+1, height-2, height-2);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            textRenderer.drawTrimmed(message, x+height, y+2, width-height-2, 0xFFFFFF);
        }
    }

}