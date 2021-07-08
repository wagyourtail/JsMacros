package xyz.wagyourtail.jsmacros.client.mixins.access;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.access.CustomClickEvent;
import xyz.wagyourtail.jsmacros.client.api.helpers.ButtonWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextFieldWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon.Pos2D;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon.Vec2D;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Mixin(Screen.class)
public abstract class MixinScreen extends AbstractParentElement implements IScreen {
    @Unique private final Set<RenderCommon.RenderElement> elements = new LinkedHashSet<>();
    @Unique private MethodWrapper<PositionCommon.Pos2D, Integer, Object> onMouseDown;
    @Unique private MethodWrapper<PositionCommon.Vec2D, Integer, Object> onMouseDrag;
    @Unique private MethodWrapper<PositionCommon.Pos2D, Integer, Object> onMouseUp;
    @Unique private MethodWrapper<PositionCommon.Pos2D, Double, Object> onScroll;
    @Unique private MethodWrapper<Integer, Integer, Object> onKeyPressed;
    @Unique private MethodWrapper<IScreen, Object, Object> onInit;
    @Unique private MethodWrapper<String, Object, Object> catchInit;
    @Unique private MethodWrapper<IScreen, Object, Object> onClose;
    
    @Shadow public int width;
    @Shadow public int height;
    @Shadow @Final protected Text title;
    @Shadow protected MinecraftClient client;
    @Shadow protected TextRenderer textRenderer;
    @Shadow @Final protected List<Element> children;
    
    @Shadow protected abstract <T extends AbstractButtonWidget> T addButton(T button);
    @Shadow public abstract void onClose();
    @Shadow protected abstract void init();
    
    @Shadow @Final protected List<AbstractButtonWidget> buttons;
    
    @Shadow public abstract void tick();
    
    @Shadow public abstract boolean shouldCloseOnEsc();
    
    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public List<RenderCommon.Text> getTexts() {
        List<RenderCommon.Text> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof RenderCommon.Text) list.add((RenderCommon.Text) e);
            }
        }
        return list;
    }

    @Override
    public List<RenderCommon.Rect> getRects() {
        List<RenderCommon.Rect> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof RenderCommon.Rect) list.add((RenderCommon.Rect) e);
            }
        }
        return list;
    }

    @Override
    public List<RenderCommon.Item> getItems() {
        List<RenderCommon.Item> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof RenderCommon.Item) list.add((RenderCommon.Item) e);
            }
        }
        return list;
    }

    @Override
    public List<RenderCommon.Image> getImages() {
        List<RenderCommon.Image> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof RenderCommon.Image) list.add((RenderCommon.Image) e);
            }
        }
        return list;
    }

    @Override
    public List<TextFieldWidgetHelper> getTextFields() {
        Map<AbstractButtonWidget, TextFieldWidgetHelper> btns = new LinkedHashMap<>();
        for (RenderCommon.RenderElement el : elements) {
            if (el instanceof TextFieldWidgetHelper) {
                btns.put(((TextFieldWidgetHelper) el).getRaw(), (TextFieldWidgetHelper) el);
            }
        }
        synchronized (buttons) {
            for (AbstractButtonWidget e : buttons) {
                if (e instanceof TextFieldWidget && !btns.containsKey(e)) {
                    btns.put(e, new TextFieldWidgetHelper((TextFieldWidget) e));
                }
            }
        }
        return ImmutableList.copyOf(btns.values());
    }
    
    @Override
    public List<ButtonWidgetHelper<?>> getButtonWidgets() {
        Map<AbstractButtonWidget, ButtonWidgetHelper<?>> btns = new LinkedHashMap<>();
        for (RenderCommon.RenderElement el : elements) {
            if (el instanceof ButtonWidgetHelper) {
                btns.put(((ButtonWidgetHelper<?>) el).getRaw(), (ButtonWidgetHelper<?>) el);
            }
        }
        synchronized (buttons) {
            for (AbstractButtonWidget e : buttons) {
                if (!(e instanceof TextFieldWidget) && !btns.containsKey(e)) {
                    btns.put(e, new ButtonWidgetHelper<>(e));
                }
            }
        }
        return ImmutableList.copyOf(btns.values());
    }
    
    @Override
    public List<RenderCommon.RenderElement> getElements() {
        return ImmutableList.copyOf(elements);
    }
    
    @Override
    public IScreen removeElement(RenderCommon.RenderElement e) {
        synchronized (elements) {
            elements.remove(e);
            if (e instanceof ButtonWidgetHelper) children.remove(((ButtonWidgetHelper<?>) e).getRaw());
        }
        return this;
    }
    
    @Override
    public RenderCommon.RenderElement reAddElement(RenderCommon.RenderElement e) {
        synchronized (elements) {
            elements.add(e);
            if (e instanceof ButtonWidgetHelper) children.add(((ButtonWidgetHelper<?>) e).getRaw());
        }
        return e;
    }
    
    @Override
    public RenderCommon.Text addText(String text, int x, int y, int color, boolean shadow) {
        return addText(text, x, y, color, 0, shadow, 1, 0);
    }
    
    @Override
    public RenderCommon.Text addText(String text, int x, int y, int color, int zIndex, boolean shadow) {
        return addText(text, x, y, color, zIndex, shadow, 1, 0);
    }
    
    @Override
    public RenderCommon.Text addText(String text, int x, int y, int color, boolean shadow, double scale, double rotation) {
        return addText(text, x, y, color, 0, shadow, scale, rotation);
    }
    
    @Override
    public RenderCommon.Text addText(String text, int x, int y, int color, int zIndex, boolean shadow, double scale, double rotation) {
        RenderCommon.Text t = new RenderCommon.Text(text, x, y, color, zIndex, shadow, scale, (float) rotation);
        synchronized (elements) {
            elements.add(t);
        }
        return t;
    }
    
    
    @Override
    public RenderCommon.Text addText(TextHelper text, int x, int y, int color, boolean shadow) {
        return addText(text, x, y, color, 0, shadow, 1, 0);
    }
    
    @Override
    public RenderCommon.Text addText(TextHelper text, int x, int y, int color, int zIndex, boolean shadow) {
        return addText(text, x, y, color, zIndex, shadow, 1, 0);
    }
    
    @Override
    public RenderCommon.Text addText(TextHelper text, int x, int y, int color, boolean shadow, double scale, double rotation) {
        return addText(text, x, y, color, 0, shadow, scale, rotation);
    }
    
    @Override
    public RenderCommon.Text addText(TextHelper text, int x, int y, int color, int zIndex, boolean shadow, double scale, double rotation) {
        RenderCommon.Text t = new RenderCommon.Text(text, x, y, color, zIndex, shadow, scale, (float) rotation);
        synchronized (elements) {
            elements.add(t);
        }
        return t;
    }
    
    @Override
    public IScreen removeText(RenderCommon.Text t) {
        synchronized (elements) {
            elements.remove(t);
        }
        return this;
    }

    @Override
    public RenderCommon.Image addImage(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth,
        int regionHeight, int textureWidth, int textureHeight) {
        return addImage(x, y, width, height, 0, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, 0);
    }
    
    @Override
    public RenderCommon.Image addImage(int x, int y, int width, int height, int zIndex, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        return addImage(x, y, width, height, zIndex, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, 0);
    }
    
    @Override
    public RenderCommon.Image addImage(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth,
        int regionHeight, int textureWidth, int textureHeight, double rotation) {
        return addImage(x, y, width, height, 0, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, rotation);
    }
    
    @Override
    public RenderCommon.Image addImage(int x, int y, int width, int height, int zIndex, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation) {
        RenderCommon.Image i = new RenderCommon.Image(x, y, width, height, zIndex, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, (float) rotation);
        synchronized (elements) {
            elements.add(i);
        }
        return i;
    }
    
    @Override
    public IScreen removeImage(RenderCommon.Image i) {
        synchronized (elements) {
            elements.remove(i);
        }
        return this;
    }

    @Override
    public RenderCommon.Rect addRect(int x1, int y1, int x2, int y2, int color) {
        RenderCommon.Rect r = new RenderCommon.Rect(x1, y1, x2, y2, color, 0F, 0);
        synchronized (elements) {
            elements.add(r);
        }
        return r;
    }

    @Override
    public RenderCommon.Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha) {
        return addRect(x1, y1, x2, y2, color, alpha, 0, 0);
    }
    
    
    @Override
    public RenderCommon.Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha, double rotation) {
        return addRect(x1, y1, x2, y2, color, alpha, rotation, 0);
    }
    
    @Override
    public RenderCommon.Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha, double rotation, int zIndex) {
        RenderCommon.Rect r = new RenderCommon.Rect(x1, y1, x2, y2, color, alpha, (float) rotation, zIndex);
        synchronized (elements) {
            elements.add(r);
        }
        return r;
    }

    @Override
    public IScreen removeRect(RenderCommon.Rect r) {
        synchronized (elements) {
            elements.remove(r);
        }
        return this;
    }
    
    @Override
    public RenderCommon.Item addItem(int x, int y, String id) {
        return addItem(x, y, 0, id, true, 1, 0);
    }
    
    @Override
    public RenderCommon.Item addItem(int x, int y, int zIndex, String id) {
        return addItem(x, y, zIndex, id, true, 1, 0);
    }
    
    @Override
    public RenderCommon.Item addItem(int x, int y, String id, boolean overlay) {
        return addItem(x, y, 0, id, overlay, 1, 0);
    }
    
    @Override
    public RenderCommon.Item addItem(int x, int y, int zIndex, String id, boolean overlay) {
        return addItem(x, y, zIndex, id, overlay, 1, 0);
    }
    
    @Override
    public RenderCommon.Item addItem(int x, int y, String id, boolean overlay, double scale, double rotation) {
        return addItem(x, y, 0, id, overlay, scale, rotation);
    }
    
    @Override
    public RenderCommon.Item addItem(int x, int y, int zIndex, String id, boolean overlay, double scale, double rotation) {
        RenderCommon.Item i = new RenderCommon.Item(x, y, zIndex, id, overlay, scale, (float) rotation);
        synchronized (elements) {
            elements.add(i);
        }
        return i;
    }
    
    @Override
    public RenderCommon.Item addItem(int x, int y, ItemStackHelper item) {
        return addItem(x, y, 0, item, true, 1, 0);
    }
    
    @Override
    public RenderCommon.Item addItem(int x, int y, int zIndex, ItemStackHelper item) {
        return addItem(x, y, zIndex, item, true, 1, 0);
    }
    
    @Override
    public RenderCommon.Item addItem(int x, int y, ItemStackHelper item, boolean overlay) {
        return addItem(x, y, 0, item, overlay, 1, 0);
    }
    
    @Override
    public RenderCommon.Item addItem(int x, int y, int zIndex, ItemStackHelper item, boolean overlay) {
        return addItem(x, y, zIndex, item, overlay, 1, 0);
    }
    
    @Override
    public RenderCommon.Item addItem(int x, int y, ItemStackHelper item, boolean overlay, double scale, double rotation) {
        return addItem(x, y, 0, item, overlay, scale, rotation);
    }
    
    @Override
    public RenderCommon.Item addItem(int x, int y, int zIndex, ItemStackHelper item, boolean overlay, double scale, double rotation) {
        RenderCommon.Item i = new RenderCommon.Item(x, y, zIndex, item, overlay, scale, (float) rotation);
        synchronized (elements) {
            elements.add(i);
        }
        return i;
    }

    @Override
    public IScreen removeItem(RenderCommon.Item i) {
        synchronized (elements) {
            elements.remove(i);
        }
        return this;
    }
    
    @Override
    public String getScreenClassName() {
        return IScreen.super.getScreenClassName();
    }
    
    @Override
    public String getTitleText() {
        return title.getString();
    }

    @Override
    public ButtonWidgetHelper<?> addButton(int x, int y, int width, int height, String text,
        MethodWrapper<ButtonWidgetHelper<?>, IScreen, Object> callback) {
        return addButton(x, y, width, height, 0, text, callback);
    }
    
    @Override
    public ButtonWidgetHelper<?> addButton(int x, int y, int width, int height, int zIndex, String text, MethodWrapper<ButtonWidgetHelper<?>, IScreen, Object> callback) {
        AtomicReference<ButtonWidgetHelper<?>> b = new AtomicReference<>(null);
        ButtonWidget button = new ButtonWidget(x, y, width, height, new LiteralText(text), (btn) -> {
            try {
                callback.accept(b.get(), this);
            } catch (Exception e) {
                Core.instance.profile.logError(e);
            }
        });
        b.set(new ButtonWidgetHelper<>(button, zIndex));
        synchronized (elements) {
            elements.add(b.get());
            children.add(button);
        }
        return b.get();
    }
    
    @Override
    public IScreen removeButton(ButtonWidgetHelper<?> btn) {
        synchronized (elements) {
            elements.remove(btn);
            this.children.remove(btn.getRaw());
        }
        return this;
    }

    @Override
    public TextFieldWidgetHelper addTextInput(int x, int y, int width, int height, String message,
        MethodWrapper<String, IScreen, Object> onChange) {
        return addTextInput(x, y, width, height, 0, message, onChange);
    }
    
    @Override
    public TextFieldWidgetHelper addTextInput(int x, int y, int width, int height, int zIndex, String message, MethodWrapper<String, IScreen, Object> onChange) {
        TextFieldWidget field = new TextFieldWidget(this.textRenderer, x, y, width, height, new LiteralText(message));
        if (onChange != null) {
            field.setChangedListener(str -> {
                try {
                    onChange.accept(str, this);
                } catch (Exception e) {
                    Core.instance.profile.logError(e);
                }
            });
        }
        TextFieldWidgetHelper w = new TextFieldWidgetHelper(field, zIndex);
        synchronized (elements) {
            elements.add(w);
            children.add(field);
        }
        return w;
    }
    
    @Override
    public IScreen removeTextInput(TextFieldWidgetHelper inp) {
        synchronized (elements) {
            elements.remove(inp);
            children.remove(inp.getRaw());
        }
        return this;
    }

    @Override
    public void close() {
        onClose();
        
    }

    @Override
    public IScreen setOnMouseDown(MethodWrapper<Pos2D, Integer, Object> onMouseDown) {
        this.onMouseDown = onMouseDown;
        return this;
    }

    @Override
    public IScreen setOnMouseDrag(MethodWrapper<Vec2D, Integer, Object> onMouseDrag) {
        this.onMouseDrag = onMouseDrag;
        return this;
    }

    @Override
    public IScreen setOnMouseUp(MethodWrapper<Pos2D, Integer, Object> onMouseUp) {
        this.onMouseUp = onMouseUp;
        return this;
    }

    @Override
    public IScreen setOnScroll(MethodWrapper<Pos2D, Double, Object> onScroll) {
        this.onScroll = onScroll;
        return this;
    }

    @Override
    public IScreen setOnKeyPressed(MethodWrapper<Integer, Integer, Object> onKeyPressed) {
        this.onKeyPressed = onKeyPressed;
        return this;
    }

    @Override
    public IScreen setOnInit(MethodWrapper<IScreen, Object, Object> onInit) {
        this.onInit = onInit;
        return this;
    }

    @Override
    public IScreen setOnFailInit(MethodWrapper<String, Object, Object> catchInit) {
        this.catchInit = catchInit;
        return this;
    }
    
    @Override
    public IScreen setOnClose(MethodWrapper<IScreen, Object, Object> onClose) {
        this.onClose = onClose;
        return this;
    }
    
    @Override
    public IScreen reloadScreen() {
        client.openScreen((Screen) (Object) this);
        return this;
    }

    @Override
    public void onRenderInternal(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (matrices == null) return;

        synchronized (elements) {
            Iterator<RenderCommon.RenderElement> iter = elements.stream().sorted(Comparator.comparingInt(RenderCommon.RenderElement::getZIndex)).iterator();
            while (iter.hasNext()) {
                iter.next().render(matrices, mouseX, mouseY, delta);
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "render")
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
        onRenderInternal(matrices, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (onMouseDown != null) try {
            onMouseDown.accept(new PositionCommon.Pos2D(mouseX, mouseY), button);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (onMouseDrag != null) try {
            onMouseDrag.accept(new PositionCommon.Vec2D(mouseX, mouseY, deltaX, deltaY), button);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (onMouseUp != null) try {
            onMouseUp.accept(new PositionCommon.Pos2D(mouseX, mouseY), button);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Inject(at = @At("HEAD"), method = "keyPressed")
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> info) {
        if (onKeyPressed != null) try {
            onKeyPressed.accept(keyCode, modifiers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (onScroll != null) try {
            onScroll.accept(new PositionCommon.Pos2D(mouseX, mouseY), amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    
    @Inject(at = @At("RETURN"), method = "init()V")
    protected void init(CallbackInfo info) {
        synchronized (elements) {
            elements.clear();
        }
        client.keyboard.setRepeatEvents(true);
        if (onInit != null) {
            try {
                onInit.accept(this);
            } catch (Exception e) {
                try {
                    if (catchInit != null) catchInit.accept(e.toString());
                    else throw e;
                } catch (Exception f) {
                    Core.instance.profile.logError(f);
                }
            }
        }
    }
    
    @Inject(at = @At("RETURN"), method = "removed")
    public void removed(CallbackInfo info) {
        if (onClose != null) {
            try {
                onClose.accept(this);
            } catch (Exception e) {
                Core.instance.profile.logError(e);
            }
        }
    }
    
    //TODO: switch to enum extention with mixin 9.0 or whenever Mumfrey gets around to it
    @Inject(at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V", remap = false), method = "handleTextClick", cancellable = true)
    public void handleCustomClickEvent(Style style, CallbackInfoReturnable<Boolean> cir) {
        ClickEvent clickEvent = style.getClickEvent();
        if (clickEvent instanceof CustomClickEvent) {
            ((CustomClickEvent) clickEvent).getEvent().run();
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
