package xyz.wagyourtail.jsmacros.client.tick;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import xyz.wagyourtail.jsmacros.client.api.event.impl.*;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FClient;

public class TickBasedEvents {
    private static final boolean initialized = false;
    private static ItemStack mainHand = null;
    private static final ItemStack offHand = null;

    private static ItemStack footArmor = null;
    private static ItemStack legArmor = null;
    private static ItemStack chestArmor = null;
    private static ItemStack headArmor = null;
    
    public static boolean areEqualNoDamage(ItemStack a, ItemStack b) {
        return a.getItem() == b.getItem();
    }
    
    public static boolean areNotEqual(ItemStack a, ItemStack b) {
        return (a != null || b != null) && (a == null || b == null || !a.isItemEqual(b) || a.stackSize != b.stackSize || !ItemStack.areItemStackTagsEqual(a, b) || a.getItemDamage() != b.getItemDamage());
    }

    public static boolean areTagsEqualIgnoreDamage(ItemStack a, ItemStack b) {
        if (a == null && b == null) {
            return true;
        } else if (a != null && b != null) {
            if (a.getTagCompound() == null && b.getTagCompound() == null) {
                return true;
            } else {
                NBTTagCompound at;
                NBTTagCompound bt;
                if (a.getTagCompound() != null) at = (NBTTagCompound) a.getTagCompound().copy();
                else at = new NBTTagCompound();
                if (b.getTagCompound() != null) bt = (NBTTagCompound) b.getTagCompound().copy();
                else bt = new NBTTagCompound();
                at.removeTag("Damage");
                bt.removeTag("Damage");
                return at.equals(bt);
            }

        } else {
            return false;
        }
    }

    public static boolean areEqualIgnoreDamage(ItemStack a, ItemStack b) {
        return (a == null && b == null) || (a != null && b != null && a.isItemEqual(b) && a.stackSize == b.stackSize && areTagsEqualIgnoreDamage(a, b));
    }

    public static void onTick(Minecraft mc) {

        FClient.tickSynchronizer.tick();

        new EventTick();
        new EventJoinedTick();

        if (mc.thePlayer != null && mc.thePlayer.inventory != null) {
            InventoryPlayer inv = mc.thePlayer.inventory;

            ItemStack newMainHand = inv.getCurrentItem();
            if (areNotEqual(newMainHand, mainHand)) {
                if (areEqualIgnoreDamage(newMainHand, mainHand)) {
                    new EventItemDamage(newMainHand, newMainHand.getItemDamage());
                }
                new EventHeldItemChange(newMainHand, mainHand, false);
                mainHand = newMainHand != null ? newMainHand.copy() : null;
            }

            ItemStack newHeadArmor = inv.armorInventory[3];
            if (areNotEqual(newHeadArmor, headArmor)) {
                if (areEqualIgnoreDamage(newHeadArmor, headArmor)) {
                    new EventItemDamage(newHeadArmor, newHeadArmor.getItemDamage());
                }
                new EventArmorChange("HEAD", newHeadArmor, headArmor);
                headArmor = newHeadArmor != null ? newHeadArmor.copy() : null;
            }

            ItemStack newChestArmor = inv.armorInventory[2];
            if (areNotEqual(newChestArmor, chestArmor)) {
                if (areEqualIgnoreDamage(newChestArmor, chestArmor)) {
                    new EventItemDamage(newChestArmor, newChestArmor.getItemDamage());
                }
                new EventArmorChange("CHEST", newChestArmor, chestArmor);
                chestArmor = newChestArmor != null ? newChestArmor.copy() : null;

            }

            ItemStack newLegArmor = inv.armorInventory[1];
            if (areNotEqual(newLegArmor, legArmor)) {
                if (areEqualIgnoreDamage(newLegArmor, legArmor)) {
                    new EventItemDamage(newLegArmor, newLegArmor.getItemDamage());
                }
                new EventArmorChange("LEGS", newLegArmor, legArmor);
                legArmor = newLegArmor != null ? newLegArmor.copy() : null;
            }

            ItemStack newFootArmor = inv.armorInventory[0];
            if (areNotEqual(newFootArmor, footArmor)) {
                if (areEqualIgnoreDamage(newFootArmor, footArmor)) {
                    new EventItemDamage(newFootArmor, newFootArmor.getItemDamage());
                }
                new EventArmorChange("FEET", newFootArmor, footArmor);
                footArmor = newFootArmor != null ? newFootArmor.copy() : null;
            }
        }
    }
}
