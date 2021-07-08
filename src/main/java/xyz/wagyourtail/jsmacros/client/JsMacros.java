package xyz.wagyourtail.jsmacros.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.lwjgl.input.Keyboard;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;
import xyz.wagyourtail.jsmacros.client.config.Profile;
import xyz.wagyourtail.jsmacros.client.event.EventRegistry;
import xyz.wagyourtail.jsmacros.client.gui.screens.BaseScreen;
import xyz.wagyourtail.jsmacros.client.gui.screens.KeyMacrosScreen;
import xyz.wagyourtail.jsmacros.client.movement.MovementQueue;
import xyz.wagyourtail.jsmacros.core.Core;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

@Mod(modid = JsMacros.MOD_ID, version = "@VERSION@", guiFactory = "xyz.wagyourtail.jsmacros.client.JsMacrosModConfigFactory")
public class JsMacros {
    public static final KeyBinding keyBinding = new KeyBinding("jsmacros.menu", Keyboard.KEY_K, "jsmacros.title");
    public static final String MOD_ID = "jsmacros";
    public static final Logger LOGGER  = LogManager.getLogger();
    public static BaseScreen prevScreen;
    protected static final File configFolder = new File(Loader.instance().getConfigDir(), "jsMacros");
    
    public static final Core core = Core.createInstance(EventRegistry::new, Profile::new, configFolder, new File(configFolder, "Macros"), LOGGER);
    
    @Mod.EventHandler
    public void onInitializeClient(FMLInitializationEvent event) {
        try {
            core.config.addOptions("client", ClientConfigV2.class);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        
        ClientRegistry.registerKeyBinding(JsMacros.keyBinding);
        prevScreen = new KeyMacrosScreen(null);
        
        Thread t = new Thread(() -> {
            Builder build = Context.newBuilder("js");
            Context con = build.build();
            con.eval("js", "console.log('js pre-loaded.')");
            con.close();
        });
        t.start();

        // Init MovementQueue
        MovementQueue.clear();
        
        FakeFabricLoader.instance.loadEntries();
        MinecraftForge.EVENT_BUS.register(new ForgeEventListener());
    }
    
    static public String getScreenName(GuiScreen s) {
        if (s == null) return null;
        if (s instanceof GuiContainer) {
            //add more ?
            if (s instanceof GuiChest) {
                return String.format("%d Row Chest", ((GuiChest) s).inventorySlots.getInventory().size() / 9);
            } else if (s instanceof GuiDispenser) {
                return "3x3 Container";
            } else if (s instanceof GuiRepair) {
                return "Anvil";
            } else if (s instanceof GuiBeacon) {
                return "Beacon";
            } else if (s instanceof GuiBrewingStand) {
                return "Brewing Stand";
            } else if (s instanceof GuiCrafting) {
                return "Crafting Table";
            } else if (s instanceof GuiEnchantment) {
                return "Enchanting Table";
            } else if (s instanceof GuiFurnace) {
                return "Furnace";
            } else if (s instanceof GuiHopper) {
                return "Hopper";
            } else if (s instanceof GuiMerchant) {
                return "Villager";
            } else if (s instanceof GuiInventory) {
                return "Survival Inventory";
            } else if (s instanceof GuiScreenHorseInventory) {
                return "Horse";
            } else if (s instanceof GuiContainerCreative) {
                return "Creative Inventory";
            } else {
                return s.getClass().getName();
            }
        } else if (s instanceof GuiChat) {
            return "Chat";
        }
        return s.getClass().getTypeName();
    }
    
    static public String getLocalizedName(int keyCode) {
        return GameSettings.getKeyDisplayString(keyCode);
     }
    
    @Deprecated
    static public Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }
    

    public static int[] range(int end) {
        return range(0, end, 1);
    }
    
    public static int[] range(int start, int end) {
        return range(start, end, 1);
    }
    
    public static int[] range(int start, int end, int iter) {
        int[] a = new int[end-start];
        for (int i = start; i < end; i+=iter) {
            a[i-start] = i;
        }
        return a;
    }
    
    public static void openFile(File p_175282_1_)
    {
        try
        {
            Class<?> oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
            oclass.getMethod("open", File.class).invoke(object, p_175282_1_);
        }
        catch (Throwable throwable)
        {
            LOGGER.error("Couldn't open link", throwable);
        }
    }
    
    public static void openURI(URI p_175282_1_) {
        try
        {
            Class<?> oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
            oclass.getMethod("browse", URI.class).invoke(object, p_175282_1_);
        }
        catch (Throwable throwable)
        {
            LOGGER.error("Couldn't open link", throwable);
        }
    }
}