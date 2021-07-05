package xyz.wagyourtail.jsmacros.client;

import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;
import xyz.wagyourtail.jsmacros.client.gui.screens.BaseScreen;

import java.util.function.Function;

public class ModMenuEntry implements ModMenuApi {
    
    @Override
    public Function<Screen, BaseScreen> getConfigScreenFactory() {
        return (parent) -> {
            return JsMacros.prevScreen;
        };
    }
    
    //deprecated for 1.16
    @Override
    public String getModId() {
        return "jsmacros";
    }
}