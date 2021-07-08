package xyz.wagyourtail.jsmacros.client.mixins.access;

import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.IMerchant;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.village.MerchantRecipeList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IMerchantScreen;

@Mixin(GuiMerchant.class)
public abstract class MixinMerchantScreen extends GuiContainer implements IMerchantScreen {
    
    
    @Shadow private IMerchant merchant;
    
    @Shadow private int selectedMerchantRecipe;
    
    @Override
    public void jsmacros_selectIndex(int index) {
        MerchantRecipeList merchantrecipelist = this.merchant.getRecipes(this.mc.thePlayer);
        if (merchantrecipelist != null && !merchantrecipelist.isEmpty()) {
            if (index < 0 || index >= merchantrecipelist.size()) {
                return;
            }
            selectedMerchantRecipe = index;
            ((ContainerMerchant) this.inventorySlots).setCurrentRecipeIndex(this.selectedMerchantRecipe);
            PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
            packetbuffer.writeInt(this.selectedMerchantRecipe);
            this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|TrSel", packetbuffer));
        }
    }
    
    //ignore
    public MixinMerchantScreen(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }
    
}
