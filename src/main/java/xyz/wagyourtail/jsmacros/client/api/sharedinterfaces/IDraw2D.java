package xyz.wagyourtail.jsmacros.client.api.sharedinterfaces;

import net.minecraft.client.gui.Drawable;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon.Image;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon.Item;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon.Rect;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon.Text;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public interface IDraw2D<T> {
    
    /**
     * @since 1.2.7
     * @return screen width
     */
    int getWidth();
    
    /**
     * @since 1.2.7
     * @return screen height
     */
    int getHeight();
    
    /**
     * @since 1.2.7
     * @return text elements
     */
     @Deprecated
    List<Text> getTexts();
    
    /**
     * @since 1.2.7
     * @return rect elements
     */
     @Deprecated
    List<Rect> getRects();
    
    /**
     * @since 1.2.7
     * @return item elements
     */
     @Deprecated
    List<Item> getItems();
    
    /**
     * @since 1.2.7
     * @return image elements
     */
     @Deprecated
    List<Image> getImages();
    
    /**
    * @since 1.2.9
     * @return a read only copy of the list of all elements added by scripts.
     */
    List<RenderCommon.RenderElement> getElements();
    
    /**
    * removes any element regardless of type.
    * @since 1.2.9
     * @return self for chaining
     */
    T removeElement(RenderCommon.RenderElement e);
    
    /**
    * re-add an element you removed with {@link #removeElement(xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon.RenderElement)}
    * @since 1.2.9
     * @return self for chaining
     */
    RenderCommon.RenderElement reAddElement(RenderCommon.RenderElement e);
    
    /**
     * @since 1.2.7
     * @param text
     * @param x screen x
     * @param y screen y
     * @param color text color
     * @param shadow include shadow layer
     * @return added text
     */
    Text addText(String text, int x, int y, int color, boolean shadow);
    
    
    /**
     * @since 1.4.0
     * @param text
     * @param x screen x
     * @param y screen y
     * @param color text color
     * @param zIndex z-index
     * @param shadow include shadow layer
     *
     * @return added text
     */
    Text addText(String text, int x, int y, int color, int zIndex, boolean shadow);
    
    /**
     * @since 1.2.7
     * @param text
     * @param x screen x
     * @param y screen y
     * @param color text color
     * @param shadow include shadow layer
     * @param scale text scale (as double)
     * @param rotation text rotation (as degrees)
     * @return added text
     */
    Text addText(String text, int x, int y, int color, boolean shadow, double scale, double rotation);
    
    
    /**
     * @since 1.4.0
     * @param text
     * @param x screen x
     * @param y screen y
     * @param color text color
     * @param zIndex z-index
     * @param shadow include shadow layer
     * @param scale text scale (as double)
     * @param rotation text rotation (as degrees)
     * @return added text
     */
    Text addText(String text, int x, int y, int color, int zIndex, boolean shadow, double scale, double rotation);
    
    /**
     * @since 1.2.7
     * @param text
     * @param x screen x
     * @param y screen y
     * @param color text color
     * @param shadow include shadow layer
     * @return added text
     */
    Text addText(TextHelper text, int x, int y, int color, boolean shadow);
    
    
    /**
     * @since 1.4.0
     * @param text
     * @param x screen x
     * @param y screen y
     * @param color text color
     * @param zIndex z-index
     * @param shadow include shadow layer
     * @return added text
     */
    Text addText(TextHelper text, int x, int y, int color, int zIndex, boolean shadow);
    
    /**
     * @since 1.2.7
     * @param text
     * @param x screen x
     * @param y screen y
     * @param color text color
     * @param shadow include shadow layer
     * @param scale text scale (as double)
     * @param rotation text rotation (as degrees)
     * @return added text
     */
    Text addText(TextHelper text, int x, int y, int color, boolean shadow, double scale, double rotation);
    
    /**
     * @since 1.4.0
     * @param text
     * @param x screen x
     * @param y screen y
     * @param color text color
     * @param zIndex z-index
     * @param shadow include shadow layer
     * @param scale text scale (as double)
     * @param rotation text rotation (as degrees)
     * @return added text
     */
    Text addText(TextHelper text, int x, int y, int color, int zIndex, boolean shadow, double scale, double rotation);
    
    
    /**
     * @since 1.2.7
     * @param t
     * @return self for chaining
     */
     @Deprecated
    T removeText(Text t);
    
    /**
     * @since 1.2.7
     * @param x screen x, top left corner
     * @param y screen y, top left corner
     * @param width width on screen
     * @param height height on screen
     * @param id image id, in the form {@code minecraft:textures} path'd as found in texture packs, ie {@code assets/minecraft/textures/gui/recipe_book.png} becomes {@code minecraft:textures/gui/recipe_book.png}
     * @param imageX the left-most coordinate of the texture region
     * @param imageY the top-most coordinate of the texture region
     * @param regionWidth the width the texture region
     * @param regionHeight the height the texture region
     * @param textureWidth the width of the entire texture
     * @param textureHeight the height of the entire texture
     * @return added image
     */
    Image addImage(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight);
    
    /**
     * @since 1.4.0
     * @param x screen x, top left corner
     * @param y screen y, top left corner
     * @param width width on screen
     * @param height height on screen
     * @param zIndex z-index
     * @param id image id, in the form {@code minecraft:textures} path'd as found in texture packs, ie {@code assets/minecraft/textures/gui/recipe_book.png} becomes {@code minecraft:textures/gui/recipe_book.png}
     * @param imageX the left-most coordinate of the texture region
     * @param imageY the top-most coordinate of the texture region
     * @param regionWidth the width the texture region
     * @param regionHeight the height the texture region
     * @param textureWidth the width of the entire texture
     * @param textureHeight the height of the entire texture
     * @return added image
     */
    Image addImage(int x, int y, int width, int height, int zIndex, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight);
    
    /**
     * @since 1.2.7
     * @param x screen x, top left corner
     * @param y screen y, top left corner
     * @param width width on screen
     * @param height height on screen
     * @param id image id, in the form {@code minecraft:textures} path'd as found in texture packs, ie {@code assets/minecraft/textures/gui/recipe_book.png} becomes {@code minecraft:textures/gui/recipe_book.png}
     * @param imageX the left-most coordinate of the texture region
     * @param imageY the top-most coordinate of the texture region
     * @param regionWidth the width the texture region
     * @param regionHeight the height the texture region
     * @param textureWidth the width of the entire texture
     * @param textureHeight the height of the entire texture
     * @param rotation the rotation of the texture (as degrees)
     * @return added image
     */
    Image addImage(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation);
    
    /**
     * @since 1.4.0
     * @param x screen x, top left corner
     * @param y screen y, top left corner
     * @param width width on screen
     * @param height height on screen
     * @param zIndex z-index
     * @param id image id, in the form {@code minecraft:textures} path'd as found in texture packs, ie {@code assets/minecraft/textures/gui/recipe_book.png} becomes {@code minecraft:textures/gui/recipe_book.png}
     * @param imageX the left-most coordinate of the texture region
     * @param imageY the top-most coordinate of the texture region
     * @param regionWidth the width the texture region
     * @param regionHeight the height the texture region
     * @param textureWidth the width of the entire texture
     * @param textureHeight the height of the entire texture
     * @param rotation the rotation of the texture (as degrees)
     * @return added image
     */
    Image addImage(int x, int y, int width, int height, int zIndex, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation);
    
    /**
     * @since 1.2.7
     * @param i
     * @return self for chaining
     */
     @Deprecated
    T removeImage(Image i);
    
    /**
     * @since 1.2.7
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param color as hex, with alpha channel
     * @return added rect
     */
    Rect addRect(int x1, int y1, int x2, int y2, int color);
    
    /**
     * @since 1.2.7
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param color as hex
     * @param alpha alpha channel 0-255
     * @return added rect
     */
    Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha);
    
    /**
     * @since 1.2.7
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param color as hex
     * @param alpha alpha channel 0-255
     * @param rotation as degrees
     * @return added rect
     */
    Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha, double rotation);
    
    /**
     * @since 1.4.0
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param color as hex
     * @param alpha alpha channel 0-255
     * @param rotation as degrees
     * @param zIndex z-index
     * @return added rect
     */
    Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha, double rotation, int zIndex);
    
    /**
     * @since 1.2.7
     * @param r
     * @return self for chaining
     */
     @Deprecated
    T removeRect(Rect r);
    
    /**
     * @since 1.2.7
     * @param x left most corner
     * @param y top most corner
     * @param id item id
     * @return added item
     */
    Item addItem(int x, int y, String id);
    
    /**
     * @since 1.4.0
     * @param x left most corner
     * @param y top most corner
     * @param zIndex z-index
     * @param id item id
     * @return added item
     */
    Item addItem(int x, int y, int zIndex, String id);
    
    /**
     * @since 1.2.7
     * @param x left most corner
     * @param y top most corner
     * @param id item id
     * @param overlay should include overlay health and count
     * @return added item
     */
    Item addItem(int x, int y, String id, boolean overlay);
    
    /**
     * @since 1.4.0
     * @param x left most corner
     * @param y top most corner
     * @param zIndex z-index
     * @param id item id
     * @param overlay should include overlay health and count
     * @return added item
     */
    Item addItem(int x, int y, int zIndex, String id, boolean overlay);
    
    /**
     * @since 1.2.7
     * @param x left most corner
     * @param y top most corner
     * @param id item id
     * @param overlay should include overlay health and count
     * @param scale scale of item
     * @param rotation rotation of item
     * @return added item
     */
    Item addItem(int x, int y, String id, boolean overlay, double scale, double rotation);
    
    /**
     * @since 1.4.0
     * @param x left most corner
     * @param y top most corner
     * @param zIndex z-index
     * @param id item id
     * @param overlay should include overlay health and count
     * @param scale scale of item
     * @param rotation rotation of item
     * @return added item
     */
    Item addItem(int x, int y, int zIndex, String id, boolean overlay, double scale, double rotation);
    
    /**
     * @since 1.2.7
     * @param x left most corner
     * @param y top most corner
     * @param item from inventory as helper
     * @return added item
     */
    Item addItem(int x, int y, ItemStackHelper item);
    
    /**
     * @since 1.4.0
     * @param x left most corner
     * @param y top most corner
     * @param zIndex z-index
     * @param item from inventory as helper
     * @return added item
     */
    Item addItem(int x, int y, int zIndex, ItemStackHelper item);
    
    /**
     * @since 1.2.7
     * @param x left most corner
     * @param y top most corner
     * @param item from inventory as helper
     * @param overlay should include overlay health and count
     * @return added item
     */
    Item addItem(int x, int y, ItemStackHelper item, boolean overlay);
    
    /**
     * @since 1.4.0
     * @param x left most corner
     * @param y top most corner
     * @param zIndex z-index
     * @param item from inventory as helper
     * @param overlay should include overlay health and count
     * @return added item
     */
    Item addItem(int x, int y, int zIndex, ItemStackHelper item, boolean overlay);
    
    /**
     * @since 1.2.7
     * @param x left most corner
     * @param y top most corner
     * @param item from inventory as helper
     * @param overlay should include overlay health and count
     * @param scale scale of item
     * @param rotation rotation of item
     * @return added item
     */
    Item addItem(int x, int y, ItemStackHelper item, boolean overlay, double scale, double rotation);
    
    /**
     * @since 1.4.0
     * @param x left most corner
     * @param y top most corner
     * @param zIndex z-index
     * @param item from inventory as helper
     * @param overlay should include overlay health and count
     * @param scale scale of item
     * @param rotation rotation of item
     * @return added item
     */
    Item addItem(int x, int y, int zIndex, ItemStackHelper item, boolean overlay, double scale, double rotation);
    
    /**
     * @since 1.2.7
     * @param i
     * @return self for chaining
     */
     @Deprecated
    T removeItem(Item i);

    /**
     * @since 1.2.7
     * @param onInit calls your method as a {@link Consumer}&lt;{@link T}&gt;
     * @return self for chaining
     */
    T setOnInit(MethodWrapper<T, Object, Object> onInit);
    
    /**
     * @since 1.2.7
     * @param catchInit calls your method as a {@link Consumer}&lt;{@link String}&gt;
     * @return self for chaining
     */
    T setOnFailInit(MethodWrapper<String, Object, Object> catchInit);
    
    void render();
}
