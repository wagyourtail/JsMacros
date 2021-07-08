package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @author Wagyourtail
 * @since 1.0.8
 */
@SuppressWarnings("unused")
public class TextHelper extends BaseHelper<IChatComponent> {
    
    public TextHelper(String json) {
        super(IChatComponent.Serializer.jsonToComponent(json));
    }
    
    public TextHelper(IChatComponent t) {
        super(t);
    }
    
    /**
     * replace the text in this class with JSON data.
     * @since 1.0.8
     * @param json
     * @return
     */
    public TextHelper replaceFromJson(String json) {
        base = IChatComponent.Serializer.jsonToComponent(json);
        return this;
    }
    
    /**
     * replace the text in this class with {@link java.lang.String String} data.
     * @since 1.0.8
     * @param content
     * @return
     */
    public TextHelper replaceFromString(String content) {
        base = new ChatComponentText(content);
        return this;
    }
    
    /**
     * @since 1.2.7
     * @return JSON data representation.
     */
    public String getJson() {
        return IChatComponent.Serializer.componentToJson(base);
    }

    /**
     * @since 1.2.7
     * @return the text content.
     */
    public String getString() {
        return base.getFormattedText();
    }
    
    
    /**
     * @since 1.0.8
     * @deprecated confusing name.
     * @return
     */
     @Deprecated
    public String toJson() {
        return getJson();
    }

    /**
     * @since 1.0.8, this used to do the same as {@link #getString}
     * @return String representation of text helper.
     */
    public String toString() {
        return String.format("TextHelper:{\"text\": \"%s\"}", base.getUnformattedText());
    }
}
