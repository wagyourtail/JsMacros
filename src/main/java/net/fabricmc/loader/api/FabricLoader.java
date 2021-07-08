package net.fabricmc.loader.api;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Loader;
import xyz.wagyourtail.jsmacros.client.FakeFabricLoader;

import java.io.File;
import java.nio.file.Path;

public interface FabricLoader {
    
    static FabricLoader getInstance() {
        return FakeFabricLoader.instance;
    }
    
    default File getConfigDirectory() {
        return Loader.instance().getConfigDir();
    }
    
    default File getGameDirectory() {
        return Minecraft.getMinecraft().mcDataDir;
    }
    
    default Path getGameDir() {
        return Minecraft.getMinecraft().mcDataDir.toPath();
    }
    
    default Path getConfigDir() {
        return Loader.instance().getConfigDir().toPath();
    }
    
    boolean isModLoaded(String modid);
}
