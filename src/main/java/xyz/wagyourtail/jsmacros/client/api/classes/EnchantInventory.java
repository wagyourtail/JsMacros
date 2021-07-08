package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.util.ResourceLocation;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;

/**
 * @since 1.3.1
 */
public class EnchantInventory extends Inventory<GuiEnchantment> {
    
    protected EnchantInventory(GuiEnchantment inventory) {
        super(inventory);
    }
    
    /**
     * @return xp level required to do enchantments
     * @since 1.3.1
     */
    public int[] getRequiredLevels() {
        return ((ContainerEnchantment)inventory.inventorySlots).enchantLevels;
    }
    
    /**
     * @return list of enchantments text.
     * @since 1.3.1
     */
    public TextHelper[] getEnchantments() {
        TextHelper[] enchants = new TextHelper[3];
        for (int j = 0; j < 3; ++j) {
            Enchantment enchantment = Enchantment.getEnchantmentById(((ContainerEnchantment)inventory.inventorySlots).field_178151_h[j] & 255);
            if (((ContainerEnchantment)inventory.inventorySlots).field_178151_h[j] > 0 && (enchantment) != null) {
                enchants[j] = new TextHelper(enchantment.getTranslatedName((((ContainerEnchantment)inventory.inventorySlots).field_178151_h[j] & 65280) >> 8));
            }
        }
        return enchants;
    }
    
    private static String getEnchantId(Enchantment enchantment) {
        for (ResourceLocation id : Enchantment.func_181077_c()) {
            if (Enchantment.getEnchantmentByLocation(id.toString()) == enchantment) {
                return id.toString();
            }
        }
        return null;
    }
    
    /**
     * @return id for enchantments
     * @since 1.3.1
     */
    public String[] getEnchantmentIds() {
        String[] enchants = new String[3];
        for (int j = 0; j < 3; ++j) {
            Enchantment enchantment =  Enchantment.getEnchantmentById(((ContainerEnchantment)inventory.inventorySlots).field_178151_h[j] & 255);
            if (((ContainerEnchantment)inventory.inventorySlots).field_178151_h[j] >= 0 && (enchantment) != null) {
                enchants[j] = getEnchantId(enchantment);
            }
        }
        return enchants;
    }
    
    /**
     * @return level of enchantments
     * @since 1.3.1
     */
    public int[] getEnchantmentLevels() {
        int[] list = new int[3];
        for (int i = 0; i < 3; ++i) {
            list[i] = (((ContainerEnchantment)inventory.inventorySlots).field_178151_h[i] & 65280) >> 8;
        }
        return list;
    }
    
    /**
    *  clicks the button to enchant.
    *
     * @param index
     * @return success
     * @since 1.3.1
     */
    public boolean doEnchant(int index) {
        assert mc.playerController != null;
        if (inventory.inventorySlots.enchantItem(mc.thePlayer, index)) {
            mc.playerController.sendEnchantPacket(inventory.inventorySlots.windowId, index);
            return true;
        }
        return false;
    }
    
}
