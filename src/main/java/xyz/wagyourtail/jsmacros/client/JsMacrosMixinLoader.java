package xyz.wagyourtail.jsmacros.client;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraft.launchwrapper.LogWrapper;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class JsMacrosMixinLoader implements IFMLLoadingPlugin {
    
    public JsMacrosMixinLoader() throws Exception {
        new FakeFabricLoader(new File(Launch.minecraftHome, "mods/jsmacros"));
        MixinBootstrap.init();
        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
        FakeFabricLoader.instance.loadMixins();
        LogWrapper.log(Level.INFO, "[JsMacros] Loading Mixins.");
        Mixins.addConfiguration("jsmacros.mixins.json");
        if (hasClass("optifine.OptiFineForgeTweaker")) {
            LogWrapper.log(Level.INFO, "[JsMacros] optifine detected.");
            Mixins.addConfiguration("jsmacros.optifine.mixins.json");
        }
        if (hasClass("club.sk1er.patcher.tweaker.PatcherTweaker")) {
            LogWrapper.log(Level.INFO, "[JsMacros] patcher detected.");
            Mixins.addConfiguration("jsmacros.patcher.mixins.json");
        }
        if (hasClass("bre.smoothfont.asm.CorePlugin")) {
            LogWrapper.log(Level.INFO, "[JsMacros] SmoothFont detected.");
            Mixins.addConfiguration("jsmacros.smoothfont.mixins.json");
        }
        if (hasClass("cubex2.ttfr.BetterFontsCore")) {
            LogWrapper.log(Level.ERROR, "[JsMacros] Editor/Custom Colors are Not Currently Compatible with BetterFonts, try Smooth Font?");
        }
        loadManifestDeps();
    }
    
    public void loadManifestDeps() throws IOException {
        Class<JsMacrosMixinLoader> clazz = JsMacrosMixinLoader.class;
        String className = clazz.getSimpleName() + ".class";
        String classPath = clazz.getResource(className).toString();
        if (!classPath.startsWith("jar")) {
            // Class not from JAR
            return;
        }
        String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) +
            "/META-INF/MANIFEST.MF";
        Manifest manifest = new Manifest(new URL(manifestPath).openStream());
        Attributes attr = manifest.getMainAttributes();
        String[] value = attr.getValue("JsMacrosDeps").split("\\s+");
        extract(value);
    }
    
    public void extract(String[] mods) throws IOException {
        File modFolder = new File(Launch.minecraftHome, "mods/jsmacros/dependencies");
        if (!modFolder.exists() && !modFolder.mkdirs()) throw new RuntimeException("failed to create deps folder dir");
        for (String mod : mods) {
            File modfile = new File(modFolder, mod);
            LogWrapper.log(Level.INFO, "[JsMacros] Extracting Dependency: " + mod);
            java.nio.file.Files.copy(JsMacrosMixinLoader.class.getResourceAsStream("/META-INF/jars/"+mod),
                modfile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);
            ((LaunchClassLoader) JsMacrosMixinLoader.class.getClassLoader()).addURL(modfile.toURI().toURL());
            CoreModManager.getIgnoredMods().add(modfile.getName());
        }
    }
    
    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }
    
    @Override
    public String getModContainerClass() {
        return null;
    }
    
    @Override
    public String getSetupClass() {
        return null;
    }
    
    @Override
    public void injectData(Map<String, Object> data) {
    
    }
    
    @Override
    public String getAccessTransformerClass() {
        return null;
    }
    
    public boolean hasClass(String cname) {
        try {
            Class.forName(cname);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
}
