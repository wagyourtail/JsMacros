package xyz.wagyourtail.jsmacros.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import xyz.wagyourtail.jsmacros.client.gui.screens.KeyMacrosScreen;

import java.util.Set;

public class JsMacrosModConfigFactory implements IModGuiFactory {
    
    @Override
    public void initialize(Minecraft minecraftInstance) {
    }
    
    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return ConfigCreator.class;
    }
    
    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
    
    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }
    
    protected static class ConfigCreator extends GuiScreen {
        GuiScreen parent;
        public ConfigCreator(GuiScreen parent) {
            this.parent = parent;
        }
    
        @Override
        public void initGui() {
            mc.displayGuiScreen(JsMacros.prevScreen);
            if (JsMacros.prevScreen instanceof KeyMacrosScreen) {
                JsMacros.prevScreen.parent = parent;
            }
        }
    }
}
