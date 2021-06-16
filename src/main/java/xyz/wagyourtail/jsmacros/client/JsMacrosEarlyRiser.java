package xyz.wagyourtail.jsmacros.client;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.TransformingClassLoader;
import cpw.mods.modlauncher.api.IEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.StandardCopyOption;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class JsMacrosEarlyRiser implements IMixinConnector {
    public static final Logger LOGGER  = LogManager.getLogger("JsMacros EarlyRiser");
    public static final Method addURL;
    public static final URLClassLoader classLoader;
    
    static {
        URLClassLoader classLoader1;
        Method addURL1;
        try {
            Field fd = TransformingClassLoader.class.getDeclaredField("delegatedClassLoader");
            fd.setAccessible(true);
            classLoader1 = (URLClassLoader) fd.get(JsMacrosEarlyRiser.class.getClassLoader());
            addURL1 = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURL1.setAccessible(true);
        } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException e) {
            e.printStackTrace();
            classLoader1 = null;
            addURL1 = null;
        }
        classLoader = classLoader1;
        addURL = addURL1;
    }
    
    @Override
    public void connect() {
        try {
            new FakeFabricLoader(new File(FMLLoader.getGamePath().toFile(), "mods/jsmacros"));
            MixinBootstrap.init();
            MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
            FakeFabricLoader.instance.loadMixins();
            LOGGER.log(Level.INFO, "[JsMacros] Loading Mixins.");
            Mixins.addConfiguration("jsmacros.mixins.json");
            Mixins.addConfiguration("fabric-command-api-v1.mixins.json");
            loadManifestDeps();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void loadManifestDeps() throws IOException, InvocationTargetException, IllegalAccessException {
        Manifest manifest = FMLLoader.getLoadingModList().getModFileById("jsmacros").getManifest().orElseThrow(() -> new RuntimeException("Failed to find manifest, this is normal in the dev environment"));
        Attributes attr = manifest.getMainAttributes();
        String[] value = attr.getValue("JsMacrosDeps").split("\\s+");
        extract(value);
    }
    
    public void extract(String[] mods) throws IOException, InvocationTargetException, IllegalAccessException {
        File modFolder = new File(FMLLoader.getGamePath().toFile(), "mods/jsmacros/dependencies");
        if (!modFolder.exists() && !modFolder.mkdirs()) throw new RuntimeException("failed to create deps folder dir");
        for (String mod : mods) {
            File modfile = new File(modFolder, mod);
            LOGGER.log(Level.INFO, "[JsMacros] Extracting Dependency: " + mod);
            java.nio.file.Files.copy(JsMacrosEarlyRiser.class.getResourceAsStream("/META-INF/jars/"+mod),
                modfile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);
            addURL.invoke(classLoader, modfile.toURI().toURL());
            //CoreModManager.getIgnoredMods().add(modfile.getName());
        }
    }
    
}
