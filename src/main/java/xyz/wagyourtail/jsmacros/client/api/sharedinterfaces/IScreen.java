package xyz.wagyourtail.jsmacros.client.api.sharedinterfaces;

import net.minecraft.client.gui.GuiButton;
import xyz.wagyourtail.jsmacros.client.api.helpers.ButtonWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextFieldWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon.Item;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon.Rect;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public interface IScreen extends IDraw2D<IScreen> {
    
    
    /**
     * @since 1.2.7
     * @return
     */
    default String getScreenClassName() {
        return this.getClass().getSimpleName();
    }
    
    /**
     * @since 1.0.5
     * @return
     */
    String getTitleText();
    
    /**
     * in {@code 1.3.1} updated to work with all button widgets not just ones added by scripts.
     * @since 1.0.5
     * @return
     */
    List<ButtonWidgetHelper<?>> getButtonWidgets();
    
    /**
     * in {@code 1.3.1} updated to work with all text fields not just ones added by scripts.
     * @since 1.0.5
     * @return
     */
    List<TextFieldWidgetHelper> getTextFields();
    
    /**
     * @since 1.0.5
     * @param x
     * @param y
     * @param width
     * @param height
     * @param text
     * @param callback calls your method as a {@link Consumer}&lt;{@link ButtonWidgetHelper}&gt;
     * @return
     */
    ButtonWidgetHelper<?> addButton(int x, int y, int width, int height, String text, MethodWrapper<ButtonWidgetHelper<?>, IScreen, Object> callback);
    
    /**
     * @since 1.4.0
     * @param x
     * @param y
     * @param width
     * @param height
     * @param zIndex
     * @param text
     * @param callback calls your method as a {@link Consumer}&lt;{@link ButtonWidgetHelper}&gt;
     * @return
     */
    ButtonWidgetHelper<?> addButton(int x, int y, int width, int height, int zIndex, String text, MethodWrapper<ButtonWidgetHelper<?>, IScreen, Object> callback);
    
    /**
     * @since 1.0.5
     * @param btn
     * @return
     */
     @Deprecated
    IScreen removeButton(ButtonWidgetHelper<?> btn);
    
    /**
     * @since 1.0.5
     * @param x
     * @param y
     * @param width
     * @param height
     * @param message
     * @param onChange calls your method as a {@link Consumer}&lt;{@link String}&gt;
     * @return
     */
    TextFieldWidgetHelper addTextInput(int x, int y, int width, int height, String message, MethodWrapper<String, IScreen, Object> onChange);
    
    /**
     * @since 1.0.5
     * @param x
     * @param y
     * @param width
     * @param height
     * @param zIndex
     * @param message
     * @param onChange calls your method as a {@link Consumer}&lt;{@link String}&gt;
     * @return
     */
    TextFieldWidgetHelper addTextInput(int x, int y, int width, int height, int zIndex, String message, MethodWrapper<String, IScreen, Object> onChange);
    
    /**
     * @since 1.0.5
     * @param inp
     * @return
     */
     @Deprecated
    IScreen removeTextInput(TextFieldWidgetHelper inp);
    
    /**
     * @since 1.2.7
     * @param onMouseDown calls your method as a {@link BiConsumer}&lt;{@link PositionCommon.Pos2D}, {@link Integer}&gt;
     * @return
     */
    IScreen setOnMouseDown(MethodWrapper<PositionCommon.Pos2D, Integer, Object> onMouseDown);
    
    /**
     * @since 1.2.7
     * @param onMouseDrag calls your method as a {@link BiConsumer}&lt;{@link PositionCommon.Vec2D}, {@link Integer}&gt;
     * @return
     */
    IScreen setOnMouseDrag(MethodWrapper<PositionCommon.Pos2D, Integer, Object> onMouseDrag);
    
    /**
     * @since 1.2.7
     * @param onMouseUp calls your method as a {@link BiConsumer}&lt;{@link PositionCommon.Pos2D}, {@link Integer}&gt;
     * @return
     */
    IScreen setOnMouseUp(MethodWrapper<PositionCommon.Pos2D, Integer, Object> onMouseUp);
    
    /**
     * @since 1.2.7
     * @param onScroll calls your method as a {@link BiConsumer}&lt;{@link PositionCommon.Pos2D}, {@link Double}&gt;
     * @return
     */
    IScreen setOnScroll(MethodWrapper<PositionCommon.Pos2D, Double, Object> onScroll);
    
    /**
     * @since 1.2.7
     * @param onKeyPressed calls your method as a {@link BiConsumer}&lt;{@link Integer}, {@link Integer}&gt;
     * @return
     */
    IScreen setOnKeyPressed(MethodWrapper<Integer, Integer, Object> onKeyPressed);
    
    /**
     * @since 1.2.7
     * @param onClose calls your method as a {@link Consumer}&lt;{@link IScreen}&gt;
     * @return
     */
    IScreen setOnClose(MethodWrapper<IScreen, Object, Object> onClose);
    
    /**
     * @since 1.1.9
     */
    void close();
    
    /**
     * @since 1.2.0
     */
    @Override
    Rect addRect(int x1, int y1, int x2, int y2, int color);
    
    /**
     * @since 1.2.0
     */
     @Override
    Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha);
    
    /**
     * @since 1.2.0
     */
    Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha, double rotation);
    
    /**
    * @since 1.2.0
     */
     @Override
     @Deprecated
    IScreen removeRect(Rect r);
    
    /**
     * @since 1.2.0
     */
    @Override
    Item addItem(int x, int y, String id);
    
    /**
     * @since 1.2.0
     */
    @Override
    Item addItem(int x, int y, String id, boolean overlay);
    
    /**
     * @since 1.2.0
     */
    Item addItem(int x, int y, String id, boolean overlay, double scale, double rotation);
    
    /**
     * @since 1.2.0
     */
    @Override
    Item addItem(int x, int y, ItemStackHelper item);
    
    /**
     * @since 1.2.0
     */
    @Override
    Item addItem(int x, int y, ItemStackHelper item, boolean overlay);
    
    /**
     * @since 1.2.0
     */
    Item addItem(int x, int y, ItemStackHelper item, boolean overlay, double scale, double rotation);
    
    /**
     * @since 1.2.0
     */
     @Override
     @Deprecated
    IScreen removeItem(Item i);
    
    /**
     * calls the screen's init function re-loading it.
     * @since 1.2.7
     */
    IScreen reloadScreen();

    /**
     * DON'T TOUCH
     * @since 1.4.1
     */
    void onRenderInternal(int mouseX, int mouseY, float delta);
    
    /**
     * internal
     */
    GuiButton getFocused();
    
    /**
     * internal
     */
     void clickBtn(GuiButton btn) throws IOException;
    
    /**
     * internal
     */
    MethodWrapper<IScreen, Object, Object> getOnClose();
}
