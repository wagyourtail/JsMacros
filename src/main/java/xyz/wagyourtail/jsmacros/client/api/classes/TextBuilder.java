package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.entity.Entity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.wagyourtail.jsmacros.client.access.CustomClickEvent;
import xyz.wagyourtail.jsmacros.client.access.IEntity;
import xyz.wagyourtail.jsmacros.client.access.IStyle;
import xyz.wagyourtail.jsmacros.client.api.helpers.EntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;


/**
 * usage: {@code builder.append("hello,").withColor(0xc).append(" World!").withColor(0x6)}
 * @author Wagyourtail
 * @since 1.3.0
 */
@SuppressWarnings("unused")
public class TextBuilder {
    private final LiteralText head = new LiteralText("");
    private Text self = head;
    
    public TextBuilder() {
    
    }
    
    /**
     * move on to next section and set it's text.
     * @param text a {@link String}, {@link TextHelper} or {@link TextBuilder}
     * @since 1.3.0
     * @return
     */
    public TextBuilder append(Object text) {
        if (text instanceof TextHelper) {
            appendInternal((TextHelper) text);
        } else if (text instanceof TextBuilder) {
            appendInternal(((TextBuilder) text).build());
        } else {
            appendInternal(text.toString());
        }
        return this;
    }
    
    private void appendInternal(String text) {
        head.append(self = new LiteralText(text));
    }
    
    private void appendInternal(TextHelper helper) {
        head.append(self = helper.getRaw());
    }
    
    /**
     * set current section's color by color code as hex, like {@code 0x6} for gold
     * and {@code 0xc} for red.
     * @param color
     * @since 1.3.0
     * @return
     */
    public TextBuilder withColor(int color) {
        self.styled(style -> style.setColor(Formatting.byColorIndex(color)));
        return this;
    }
    
    /**
     * Add text with custom colors.
     * @since 1.3.1
     * @param r red {@code 0-255}
     * @param g green {@code 0-255}
     * @param b blue {@code 0-255}
     *
     * @return
     */
    public TextBuilder withColor(int r, int g, int b) {
        self.styled(style -> ((IStyle)style).setCustomColor((r & 255) << 16 + (g & 255) << 8 + (b & 255)));
        return this;
    }
    
    /**
     * set other formatting options for the current section
     * @param underline
     * @param bold
     * @param italic
     * @param strikethrough
     * @param magic
     * @since 1.3.0
     * @return
     */
    public TextBuilder withFormatting(boolean underline, boolean bold, boolean italic, boolean strikethrough, boolean magic) {
        self.styled(style -> {
            style.setUnderline(underline);
            style.setBold(bold);
            style.setItalic(italic);
            style.setStrikethrough(strikethrough);
            style.setObfuscated(magic);
        });
        return this;
    }
    
    /**
     * set current section's hover event to show text
     * @param text
     * @since 1.3.0
     * @return
     */
    public TextBuilder withShowTextHover(TextHelper text) {
        self.styled(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text.getRaw())));
        return this;
    }
    
    /**
     * set current section's hover event to show an item
     * @param item
     * @since 1.3.0
     * @return
     */
    public TextBuilder withShowItemHover(ItemStackHelper item) {
        self.styled(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, item.getRaw().toHoverableText())));
        return this;
    }
    
    /**
     * set current section's hover event to show an entity
     * @param entity
     * @since 1.3.0
     * @return
     */
    public TextBuilder withShowEntityHover(EntityHelper<Entity> entity) {
        Entity raw = entity.getRaw();
        self.styled(style -> style.setHoverEvent(((IEntity)entity.getRaw()).jsmacros_getHoverEvent()));
        return this;
    }
    
    /**
     * custom click event.
     * @param action
     * @since 1.3.0
     * @return
     */
    public TextBuilder withCustomClickEvent(MethodWrapper<Object, Object, Object> action) {
        self.styled(style -> style.setClickEvent(new CustomClickEvent(() -> new Thread(() -> {
            try {
                action.run();
            } catch (Exception ex) {
                Core.instance.profile.logError(ex);
            }
        }).start())));
        return this;
    }
    
    /**
     * normal click events like: {@code open_url}, {@code open_file}, {@code run_command}, {@code suggest_command}, {@code change_page}, and {@code copy_to_clipboard}
     * @param action
     * @param value
     * @since 1.3.0
     * @return
     */
    public TextBuilder withClickEvent(String action, String value) {
        ClickEvent.Action clickAction = ClickEvent.Action.byName(action);
        assert action != null;
        self.styled(style -> style.setClickEvent(new ClickEvent(clickAction, value)));
        return this;
    }
    
    /**
     * Build to a {@link TextHelper}
     * @since 1.3.0
     * @return
     */
    public TextHelper build() {
        return new TextHelper(head);
    }
}
