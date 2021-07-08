package xyz.wagyourtail.jsmacros.client.gui.overlays;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.elements.Scrollbar;
import xyz.wagyourtail.jsmacros.core.Core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class EventChooser extends OverlayContainer {
    private String selected;
    private final List<EventObj> events = new ArrayList<>();
    private int topScroll;
    private final Consumer<String> setEvent;
    private final IChatComponent eventText;
    
    public EventChooser(int x, int y, int width, int height, FontRenderer textRenderer, String selected, IOverlayParent parent, Consumer<String> setEvent) {
        super(x, y, width, height, textRenderer, parent);
        this.selected = selected;
        this.setEvent = setEvent;
        this.eventText = new ChatComponentTranslation("jsmacros.events");
    }
    
    public void selectEvent(String event) {
        this.selected = event;
        for (EventObj e : events) {
            if (event.equals(e.event)) {
                e.btn.setColor(0x7FFFFFFF);
            } else {
                e.btn.setColor(0);
            }
        }
    }
    
    @Override
    public void init() {
        super.init();
        int w = width - 4;
        topScroll = y + 13;
        this.addButton(new Button(x + width - 12, y + 2, 10, 10, textRenderer, 0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentText("X"), (btn) -> {
            this.close();
        }));
        scroll = this.addButton(new Scrollbar(x + width - 10, y + 13, 8, height - 28, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));
        this.addButton(new Button(x + 2, y + height - 14, w / 2, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentTranslation("gui.cancel"), (btn) -> {
            this.close();
        }));
        this.addButton(new Button(x + w / 2 + 3, y + height - 14, w / 2, 12, textRenderer,0, 0, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentTranslation("jsmacros.select"), (btn) -> {
            if (this.selected != null && this.setEvent != null) {
                this.setEvent.accept(this.selected);
                this.close();
            }
        }));
        
        List<String> events = new ArrayList<>(Core.instance.eventRegistry.events);
        Collections.sort(events);
        for (String e : events) {
            addEvent(e);
        }
        this.selectEvent(selected);
    }
    
    public void addEvent(String eventName) {
        EventObj e = new EventObj(eventName, new Button(x+3+(events.size() % 5 * (width - 12) / 5), topScroll + (events.size() / 5 * 12), (width - 12) / 5, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentText(eventName.replace("Event", "")), (btn) -> {
            selectEvent(eventName);
        }));
    
        e.btn.visible = topScroll + (events.size() / 5 * 12) >= y + 13 && topScroll + (events.size() / 5 * 12) <= y + height - 27;
        events.add(e);
        this.addButton(e.btn);
        scroll.setScrollPages((Math.ceil(events.size() / 5D) * 12) /(double) Math.max(1, height - 39));
    }
    
    public void updateEventPos() {
        for (int i = 0; i < events.size(); ++i) {
            EventObj e = events.get(i);
            e.btn.visible = topScroll + (i / 5 * 12) >= y + 13 && topScroll + (i / 5 * 12) <= y + height - 27;
            e.btn.setPos(x + 3 + (i % 5 * (width - 12) / 5), topScroll + (i / 5 * 12), (width - 12) / 5, 12);
        }
    }
    
    public void onScrollbar(double page) {
        topScroll = y + 13 - (int) (page * (height - 27));
        int i = 0;
        for (EventObj fi : events) {
            fi.btn.visible = topScroll + (i / 5 * 12) >= y + 13 && topScroll + (i / 5 * 12) <= y + height - 27;
            fi.btn.setPos(x + 3 + (i % 5 * (width - 12) / 5), topScroll + (i / 5 * 12), (width - 12) / 5, 12);
            ++i;
        }
    }
    
    @Override
    public void render(int mouseX, int mouseY, float delta) {
        renderBackground();

        textRenderer.drawString(textRenderer.trimStringToWidth(eventText.getFormattedText(), width - 14), x + 3, y + 3, 0xFFFFFF);

        drawRect(x + 2, y + 12, x + width - 2, y + 13, 0xFFFFFFFF);
        drawRect(x + 2, y + height - 15, x + width - 2, y + height - 14, 0xFFFFFFFF);
//        textRenderer.draw(, mouseX, mouseY, color, shadow, matrix, vertexConsumers, seeThrough, backgroundColor, light)
        super.render(mouseX, mouseY, delta);
        
        for (GuiButton b : ImmutableList.copyOf(this.buttons)) {
            if (b instanceof Button && ((Button) b).hovering && ((Button) b).cantRenderAllText()) {
                // border
                int width = textRenderer.getStringWidth(b.displayString);
                drawRect(mouseX-3, mouseY, mouseX+width+3, mouseY+1, 0x7F7F7F7F);
                drawRect(mouseX+width+2, mouseY-textRenderer.FONT_HEIGHT - 3, mouseX+width+3, mouseY, 0x7F7F7F7F);
                drawRect(mouseX-3, mouseY-textRenderer.FONT_HEIGHT - 3, mouseX-2, mouseY, 0x7F7F7F7F);
                drawRect(mouseX-3, mouseY-textRenderer.FONT_HEIGHT - 4, mouseX+width+3, mouseY-textRenderer.FONT_HEIGHT - 3, 0x7F7F7F7F);
                
                // fill
                drawRect(mouseX-2, mouseY-textRenderer.FONT_HEIGHT - 3, mouseX+width+2, mouseY, 0xFF000000);
                drawString(textRenderer, b.displayString, mouseX, mouseY-textRenderer.FONT_HEIGHT - 1, 0xFFFFFF);
            }
        }
    }
    
    
    public static class EventObj {
        String event;
        Button btn;
        
        public EventObj(String event, Button btn) {
            this.event = event;
            this.btn = btn;
        }
    }
}
