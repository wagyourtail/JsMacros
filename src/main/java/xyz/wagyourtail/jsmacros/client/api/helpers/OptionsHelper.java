package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Wagyourtail
 * @since 1.1.7
 */
@SuppressWarnings("unused")
public class OptionsHelper extends BaseHelper<GameSettings> {
    private static final Map<String, SoundCategory> SOUND_CATEGORY_MAP = Arrays.stream(SoundCategory.values()).collect(Collectors.toMap(SoundCategory::getCategoryName, Function.identity()));
    private final Minecraft mc = Minecraft.getMinecraft();
    private final ResourcePackRepository rpm = mc.getResourcePackRepository();
    
    public OptionsHelper(GameSettings options) {
        super(options);
    }
    /**
     * @since 1.1.7
     * @return 0: off, 2: fancy
     */
    public int getCloudMode() {
        return base.clouds;
    }
    /**
     * @since 1.1.7
     * @param mode 0: off, 2: fancy 
     * @return
     */
    public OptionsHelper setCloudMode(int mode) {
        base.clouds = mode;
        return this;
    }
    /**
     * @since 1.1.7
     * @return
     */
    public int getGraphicsMode() {
        return base.fancyGraphics ? 1 : 0;
    }
    /**
     * @since 1.1.7
     * @param mode 0: fast, 2: fabulous
     * @return
     */
    public OptionsHelper setGraphicsMode(int mode) {
        base.fancyGraphics = mode == 1;
        return this;
    }
    /**
     * @since 1.1.7
     * @return list of names of resource packs.
     */
    public List<String> getResourcePacks() {
        return rpm.getRepositoryEntriesAll().stream().map(ResourcePackRepository.Entry::getResourcePackName).collect(Collectors.toList());
    }
    
    /**
     * @since 1.2.0
     * @return list of names of enabled resource packs.
     */
    public List<String> getEnabledResourcePacks() {
        return rpm.getRepositoryEntries().stream().map(ResourcePackRepository.Entry::getResourcePackName).collect(Collectors.toList());
    }
    
    /**
     * Set the enabled resource packs to the provided list.
     * 
     * @since 1.2.0
     * @param enabled
     * @return
     */
    public OptionsHelper setEnabledResourcePacks(String[] enabled) {
        mc.addScheduledTask(() -> {
            ResourcePackRepository.Entry[] enabledRP = new ResourcePackRepository.Entry[enabled.length];
            for (ResourcePackRepository.Entry e : rpm.getRepositoryEntriesAll()) {
                for (int i = 0; i < enabled.length; ++i) {
                    if (e.getResourcePackName().equals(enabled[i])) {
                        enabledRP[i] = e;
                    }
                }
            }
            rpm.setRepositories(Arrays.stream(enabledRP).filter(Objects::nonNull).collect(Collectors.toList()));
            base.saveOptions();
            mc.refreshResources();
        });
        return this;
    }
    
    /**
     * @since 1.1.7
     * @return
     */
    public boolean isRightHanded() {
        return true;
        //return base.mainArm == Arm.RIGHT;
    }
    
    /**
     * @since 1.1.7
     * @param val
     */
    public void setRightHanded(boolean val) {
//        if (val) {
//            base.mainArm = Arm.RIGHT;
//        } else {
//            base.mainArm = Arm.LEFT;
//        }
    }
    
    /**
     * @since 1.1.7
     * @return
     */
    public double getFov() {
        return base.fovSetting;
    }
    
    /**
     * @since 1.1.7
     * @param fov
     * @return
     */
    public OptionsHelper setFov(double fov) {
        base.fovSetting = (float) fov;
        return this;
    }
    
    /**
     * @since 1.1.7
     * @return
     */
    public int getRenderDistance() {
        return base.renderDistanceChunks;
    }
    
    /**
     * @since 1.1.7
     * @param d
     */
    public void setRenderDistance(int d) {
        base.renderDistanceChunks = d;
    }
    
    /**
     * @since 1.2.6
     * @return
     */
    public int getWidth() {
        return mc.displayWidth;
    }
    
    /**
     * @since 1.2.6
     * @return
     */
    public int getHeight() {
        return mc.displayHeight;
    }
    
    /**
     * @since 1.2.6
     * @param w
     */
    public void setWidth(int w) throws LWJGLException {
        Display.setDisplayMode(new DisplayMode(w, Display.getHeight()));
    }
    
    /**
     * @since 1.2.6
     * @param h
     */
    public void setHeight(int h) throws LWJGLException {
        Display.setDisplayMode(new DisplayMode(Display.getWidth(), h));
    }
    
    /**
     * @since 1.2.6
     * @param w
     * @param h
     */
    public void setSize(int w, int h) throws LWJGLException {
        Display.setDisplayMode(new DisplayMode(w, h));
    }
    
    /**
     * @since 1.3.0
     * normal values for gamam are between {@code 0} and {@code 1}
     */
    public double getGamma() {
        return base.gammaSetting;
    }
    
    /**
     * @since 1.3.0
     * normal values for gamma are between {@code 0} and {@code 1}
     */
    public void setGamma(double gamma) {
        base.gammaSetting = (float) gamma;
    }
    
    /**
     * @since 1.3.1
     * @param vol
     */
    public void setVolume(double vol) {
        base.setSoundLevel(SoundCategory.MASTER, (float) vol);
    }
    
    /**
     * set volume by category.
     *
     * @since 1.3.1
     * @param category
     * @param volume
     */
    public void setVolume(String category, double volume) {
        base.setSoundLevel(SOUND_CATEGORY_MAP.get(category), (float) volume);
    }
    
    /**
     * @since 1.3.1
     * @return
     */
    public Map<String, Float> getVolumes() {
        Map<String, Float> volumes = new HashMap<>();
        for (SoundCategory category : SoundCategory.values()) {
            volumes.put(category.getCategoryName(), base.getSoundLevel(category));
        }
        return volumes;
    }
    
    /**
     * sets gui scale, {@code 0} for auto.
     *
     * @since 1.3.1
     * @param scale
     */
    public void setGuiScale(int scale) {
        base.guiScale = scale;
        mc.addScheduledTask(() -> {
            ScaledResolution scaledresolution = new ScaledResolution(mc);
            mc.currentScreen.onResize(mc, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
        });
    }
    
    /**
     * @since 1.3.1
     * @return gui scale, {@code 0} for auto.
     */
    public int getGuiScale() {
        return base.guiScale;
    }
    
    /**
     * @param category
     * @since 1.3.1
     * @return
     */
    public float getVolume(String category) {
        return base.getSoundLevel(SOUND_CATEGORY_MAP.get(category));
    }

    /**
     * @since 1.5.0
     * @return
     */
    public boolean getSmoothCamera() {
        return base.smoothCamera;
    }

    /**
     * @param val
     * @since 1.5.0
     */
    public void setSmoothCamera(boolean val) {
        base.smoothCamera = val;
    }

    /**
     * @since 1.5.0
     * @return 0 for 1st person, 2 for in front.
     */
    public int getCameraMode() {
        return base.thirdPersonView;
    }

    /**
     * @param mode 0: first, 2: front
     * @since 1.5.0
     */
    public void setCameraMode(int mode) {
        base.thirdPersonView = mode;
    }
}
