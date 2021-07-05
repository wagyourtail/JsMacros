package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IRecipeBookWidget;

@Mixin(RecipeBookWidget.class)
public abstract class MixinRecipeBookWidget implements IRecipeBookWidget {
    @Shadow
    @Final
    private RecipeBookResults recipesArea;
    
    @Shadow private boolean field_3087;
    
    @Shadow protected abstract void refreshResults(boolean resetCurrentPage);
    
    @Override
    public RecipeBookResults jsmacros_getResults() {
        return recipesArea;
    }
    
    @Override
    public boolean jsmacros_isSearching() {
        return field_3087;
    }
    
    @Override
    public void jsmacros_refreshResultList() {
        refreshResults(false);
    }
}
