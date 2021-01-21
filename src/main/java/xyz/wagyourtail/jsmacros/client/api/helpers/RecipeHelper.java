package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.recipe.Recipe;

/**
 * @author Wagyourtail
 * @since 1.3.1
 */
@SuppressWarnings("unused")
public class RecipeHelper extends BaseHelper<Recipe<?>> {
    protected int syncId;
    
    public RecipeHelper(Recipe<?> base, int syncId) {
        super(base);
        this.syncId = syncId;
    }
    
    /**
     * @since 1.3.1
     * @return
     */
    public String getId() {
        return base.getId().toString();
    }
    
    /**
     * @since 1.3.1
     * @return
     */
    public ItemStackHelper getOutput() {
        return new ItemStackHelper(base.getOutput());
    }
    
    /**
     * probably not a good idea run this if you were in a crafting table on {@code player.openInventory()} and it's closed now...
     * @since 1.3.1
     * @param craftAll
     */
    public void craft(boolean craftAll) {
        MinecraftClient mc = MinecraftClient.getInstance();
        assert mc.interactionManager != null;
        mc.interactionManager.clickRecipe(syncId, base, craftAll);
    }
    
    public String toString() {
        return String.format("Recipe:{\"id\":\"%s\"}", base.getId().toString());
    }
    
}