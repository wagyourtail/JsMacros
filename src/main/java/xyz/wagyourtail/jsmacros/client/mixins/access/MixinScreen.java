package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.wagyourtail.jsmacros.client.access.CustomClickEvent;
import xyz.wagyourtail.jsmacros.client.access.IGuiTextField;
import xyz.wagyourtail.jsmacros.client.access.IMouseScrolled;
import xyz.wagyourtail.jsmacros.client.api.helpers.ButtonWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextFieldWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon.Pos2D;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen;
import xyz.wagyourtail.jsmacros.client.gui.elements.Drawable;
import xyz.wagyourtail.jsmacros.client.gui.screens.BaseScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Mixin(GuiScreen.class)
public abstract class MixinScreen extends Gui implements IScreen, IMouseScrolled {
    @Unique private final Set<RenderCommon.RenderElement> elements = new LinkedHashSet<>();
    @Unique private final Map<GuiButton, Consumer<GuiButton>> customButtons = new HashMap<>();
    @Unique private final Set<GuiTextField> customTextFields = new LinkedHashSet<>();
    @Unique private MethodWrapper<PositionCommon.Pos2D, Integer, Object> onMouseDown;
    @Unique private MethodWrapper<PositionCommon.Pos2D, Integer, Object> onMouseDrag;
    @Unique private MethodWrapper<PositionCommon.Pos2D, Integer, Object> onMouseUp;
    @Unique private MethodWrapper<PositionCommon.Pos2D, Double, Object> onScroll;
    @Unique private MethodWrapper<Integer, Integer, Object> onKeyPressed;
    @Unique private MethodWrapper<IScreen, Object, Object> onInit;
    @Unique private MethodWrapper<String, Object, Object> catchInit;
    @Unique private MethodWrapper<IScreen, Object, Object> onClose;

    @Shadow public int width;
    @Shadow public int height;
    @Shadow protected Minecraft mc;
    @Shadow protected FontRenderer fontRendererObj;
    @Shadow protected List<GuiButton> buttonList;
    @Shadow private GuiButton selectedButton;
    
    @Shadow public abstract void onGuiClosed();
    @Shadow protected abstract void initGui();
    @Shadow private static boolean isShiftKeyDown() {
        return false;
    }
    @Shadow private static boolean isCtrlKeyDown() {
        return false;
    }
    @Shadow private static boolean isAltKeyDown() {
        return false;
    }
    
    
    @Shadow protected abstract void actionPerformed(GuiButton button) throws IOException;
    
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
        Map<GuiTextField, TextFieldWidgetHelper> btns = new LinkedHashMap<>();
        for (RenderCommon.RenderElement el : elements) {
            if (el instanceof TextFieldWidgetHelper) {
                btns.put(((TextFieldWidgetHelper) el).getRaw(), (TextFieldWidgetHelper) el);
            }
        }
        Arrays.stream(this.getClass().getDeclaredFields()).filter(e -> e.getType().equals(GuiTextField.class)).map(e -> {
            try {
                e.setAccessible(true);
                return (GuiTextField) e.get(this);
            } catch (IllegalAccessException illegalAccessException) {
                throw new RuntimeException(illegalAccessException);
            }
        }).forEach(e -> btns.put(e, new TextFieldWidgetHelper(e)));
        return new ArrayList<>(btns.values());
    }

    @Override
    public List<ButtonWidgetHelper<?>> getButtonWidgets() {
        Map<GuiButton, ButtonWidgetHelper<?>> btns = new LinkedHashMap<>();
        for (RenderCommon.RenderElement el : elements) {
            if (el instanceof ButtonWidgetHelper) {
                btns.put(((ButtonWidgetHelper<?>) el).getRaw(), (ButtonWidgetHelper<?>) el);
            }
        }
        synchronized (buttonList) {
            for (GuiButton e : buttonList) {
                if (!btns.containsKey(e)) {
                    btns.put(e, new ButtonWidgetHelper<>(e));
                }
            }
            for (GuiButton e : customButtons.keySet()) {
                if (!btns.containsKey(e)) {
                    btns.put(e, new ButtonWidgetHelper<>(e));
                }
            }
        }
        return new ArrayList<>(btns.values());
    }

    @Override
    public List<RenderCommon.RenderElement> getElements() {
        return new ArrayList<>(elements);
    }

    @Override
    public IScreen removeElement(RenderCommon.RenderElement e) {
        synchronized (elements) {
            elements.remove(e);
            if (e instanceof ButtonWidgetHelper) buttonList.remove(((ButtonWidgetHelper<?>) e).getRaw());
        }
        return this;
    }

    @Override
    public RenderCommon.RenderElement reAddElement(RenderCommon.RenderElement e) {
        synchronized (elements) {
            elements.add(e);
            if (e instanceof ButtonWidgetHelper) buttonList.add(((ButtonWidgetHelper<?>) e).getRaw());
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
        return null;
        //return title.getString();
    }

    @Override
    public ButtonWidgetHelper<?> addButton(int x, int y, int width, int height, String text,
        MethodWrapper<ButtonWidgetHelper<?>, IScreen, Object> callback) {
        return addButton(x, y, width, height, 0, text, callback);
    }
    
    @Override
    public ButtonWidgetHelper<?> addButton(int x, int y, int width, int height, int zIndex, String text, MethodWrapper<ButtonWidgetHelper<?>, IScreen, Object> callback) {
        AtomicReference<ButtonWidgetHelper<?>> b = new AtomicReference<>(null);
        GuiButton button = new GuiButton(-999, x, y, width, height, text);
        customButtons.put(button, (btn) -> {
            try {
                callback.accept(b.get(), this);
            } catch (Exception e) {
                Core.instance.profile.logError(e);
            }
        });
        b.set(new ButtonWidgetHelper<>(button, zIndex));
        synchronized (elements) {
            elements.add(b.get());
        }
        return b.get();
    }
    
    @Override
    public IScreen removeButton(ButtonWidgetHelper<?> btn) {
        synchronized (elements) {
            elements.remove(btn);
            customButtons.remove(btn.getRaw());
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
        GuiTextField field = new GuiTextField(-999, this.fontRendererObj, x, y, width, height);
        field.setText(message);
        if (onChange != null) {
            ((IGuiTextField)field).setOnChange(str -> {
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
            customTextFields.add(w.getRaw());
        }
        return w;
    }
    
    @Override
    public IScreen removeTextInput(TextFieldWidgetHelper inp) {
        synchronized (elements) {
            if (customTextFields.contains(inp.getRaw())) {
                elements.remove(inp);
                customTextFields.remove(inp);
            }
        }
        return this;
    }

    @Override
    public void close() {
        if ((Object)this instanceof BaseScreen) {
            ((BaseScreen)(Object) this).onClose();
        }
        onGuiClosed();

    }

    @Override
    public IScreen setOnMouseDown(MethodWrapper<Pos2D, Integer, Object> onMouseDown) {
        this.onMouseDown = onMouseDown;
        return this;
    }

    @Override
    public IScreen setOnMouseDrag(MethodWrapper<Pos2D, Integer, Object> onMouseDrag) {
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
        mc.displayGuiScreen((GuiScreen) (Object) this);
        return this;
    }

    @Override
    public void onRenderInternal(int mouseX, int mouseY, float delta) {
        synchronized (elements) {
            Iterator<RenderCommon.RenderElement> iter = elements.stream().sorted(Comparator.comparingInt(RenderCommon.RenderElement::getZIndex)).iterator();
            while (iter.hasNext()) {
                iter.next().render(mouseX, mouseY, delta);
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "drawScreen")
    public void render(int mouseX, int mouseY, float delta, CallbackInfo info) {
        onRenderInternal(mouseX, mouseY, delta);
    }
    
    @Inject(at = @At("RETURN"), method = "handleMouseInput", locals = LocalCapture.CAPTURE_FAILHARD)
    public void onMouseEvent(CallbackInfo ci, int i, int j) {
        int dw = Mouse.getEventDWheel();
        if (dw != 0) {
            mouseScrolled(i, j, (int) (dw / 60D));
            if (onScroll != null) try {
                onScroll.accept(new PositionCommon.Pos2D(i, j), (double) dw / 60D);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "mouseClicked")
    public void onMouseClicked(int mouseX, int mouseY, int button, CallbackInfo info) {
        if (onMouseDown != null) try {
            onMouseDown.accept(new PositionCommon.Pos2D(mouseX, mouseY), button);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Inject(at = @At("HEAD"), method = "mouseClickMove")
    public void onMouseDragged(int mouseX, int mouseY, int button, long time, CallbackInfo ci) {
        if (onMouseDrag != null) try {
            onMouseDrag.accept(new PositionCommon.Pos2D(mouseX, mouseY), button);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(at = @At("HEAD"), method = "mouseReleased")
    public void onMouseReleased(int mouseX, int mouseY, int button, CallbackInfo ci) {
        if (onMouseUp != null) try {
            onMouseUp.accept(new PositionCommon.Pos2D(mouseX, mouseY), button);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(at = @At("HEAD"), method = "handleKeyboardInput")
    public void onKeyPressed(CallbackInfo info) {
        if (Keyboard.getEventKeyState() && onKeyPressed != null) try {
            onKeyPressed.accept(Keyboard.getEventKey(), createModifiers());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Unique
    private static int createModifiers() {
        int i = 0;
        if (isShiftKeyDown()) i |= 1;
        if (isCtrlKeyDown()) i |= 2;
        if (isAltKeyDown()) i |= 4;
        return i;
    }
    
    @Override
    public boolean mouseScrolled(int mouseX, int mouseY, int amount) {
        return false;
    }

    @Inject(at = @At("RETURN"), method = "initGui()V")
    protected void init(CallbackInfo info) {
        synchronized (elements) {
            elements.clear();
            customButtons.clear();
        }
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
    
    @Inject(at = @At("RETURN"), method = "mouseClicked")
    public void onMouseClick(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) throws IOException {
        if (mouseButton == 0) {
            for (GuiButton btn : customButtons.keySet()) {
                if (btn.mousePressed(this.mc, mouseX, mouseY)) {
                    selectedButton = btn;
                    customButtons.get(btn).accept(btn);
                }
            }
            for (GuiTextField field : customTextFields) {
                field.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public GuiButton getFocused() {
        return selectedButton;
    }

    @Override
    public void clickBtn(GuiButton btn) throws IOException {
        GuiButton prev = selectedButton;
        if (buttonList.contains(btn)) {
            selectedButton = btn;
            btn.playPressSound(this.mc.getSoundHandler());
            actionPerformed(btn);
            selectedButton = prev;
        }
    }
    
    @Override
    public  MethodWrapper<IScreen, Object, Object> getOnClose() {
        return onClose;
    }

    //TODO: switch to enum extention with mixin 9.0 or whenever Mumfrey gets around to it
    @Inject(at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;)V", remap = false), method = "handleComponentClick", cancellable = true)
    public void handleCustomClickEvent(IChatComponent t, CallbackInfoReturnable<Boolean> cir) {
        ClickEvent clickEvent = t.getChatStyle().getChatClickEvent();
        if (clickEvent instanceof CustomClickEvent) {
            ((CustomClickEvent) clickEvent).getEvent().run();
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
