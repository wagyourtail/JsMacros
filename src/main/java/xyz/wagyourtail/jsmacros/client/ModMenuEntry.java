package xyz.wagyourtail.jsmacros.client;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;
import xyz.wagyourtail.jsmacros.client.gui.screens.BaseScreen;

public class ModMenuEntry implements ModMenuApi {
    private final JsMacroScreen jsmacrosscreenfactory = new JsMacroScreen();
    
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return jsmacrosscreenfactory;
    }
    
    public static class JsMacroScreen implements ConfigScreenFactory<BaseScreen> {
        @Override
        public BaseScreen create(Screen parent) {
            return JsMacros.prevScreen;
        }
    }
    
    
    //deprecated for 1.16
    @Override
    public String getModId() {
        return "jsmacros";
    }
}