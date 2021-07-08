package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mixin(NetHandlerPlayClient.class)
class MixinClientPlayNetworkHandler {
    
    @Shadow
    private Minecraft gameController;
    @Shadow
    private WorldClient clientWorldController;
    
    @Shadow
    @Final
    private Map<UUID, NetworkPlayerInfo> playerInfoMap;
    
    
    @Shadow @Final private NetworkManager netManager;
    
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/MetadataPlayerDeath;func_152807_a(Ljava/lang/String;)V"), method="handleCombatEvent", cancellable = true)
    private void onDeath(final S42PacketCombatEvent packet, CallbackInfo info) {
        new EventDeath();
    }
    
    @Unique
    private final Set<UUID> newPlayerEntries = new HashSet<>();
    
    @Inject(at = @At("HEAD"), method = "handlePlayerListItem")
    public void onPlayerList(S38PacketPlayerListItem packet, CallbackInfo info) {
        if (this.gameController.isCallingFromMinecraftThread())
            switch (packet.func_179768_b()) {
                case ADD_PLAYER:
                    for (S38PacketPlayerListItem.AddPlayerData e : packet.func_179767_a()) {
                        synchronized (newPlayerEntries) {
                            if (playerInfoMap.get(e.getProfile().getId()) == null) {
                                newPlayerEntries.add(e.getProfile().getId());
                            }
                        }
                    }
                    return;
                case REMOVE_PLAYER:
                    for (S38PacketPlayerListItem.AddPlayerData e : packet.func_179767_a()) {
                      if (playerInfoMap.get(e.getProfile().getId()) != null) {
                            NetworkPlayerInfo p = playerInfoMap.get(e.getProfile().getId());
                            new EventPlayerLeave(e.getProfile().getId(), p);
                      }
                    }
                    return;
                default:
            }
    }
    
    @Inject(at = @At("TAIL"), method = "handlePlayerListItem")
    public void onPlayerListEnd(S38PacketPlayerListItem packet, CallbackInfo info) {
        if (packet.func_179768_b() == S38PacketPlayerListItem.Action.ADD_PLAYER) {
            for (S38PacketPlayerListItem.AddPlayerData e : packet.func_179767_a()) {
                synchronized (newPlayerEntries) {
                    if (newPlayerEntries.contains(e.getProfile().getId())) {
                        new EventPlayerJoin(e.getProfile().getId(), playerInfoMap.get(e.getProfile().getId()));
                        newPlayerEntries.remove(e.getProfile().getId());
                    }
                }
            }
        }
    }
    
    @Inject(at = @At("HEAD"), method = "handleTitle")
    public void onTitle(S45PacketTitle packet, CallbackInfo info) {
        String type = null;
        switch(packet.getType()) {
            case TITLE:
                type = "TITLE";
                break;
            case SUBTITLE:
                type = "SUBTITLE";
                break;
            default:
                break;
        }
        if (type != null && packet.getMessage() != null) {
            new EventTitle(type, packet.getMessage());
        }
    }
    
    @Inject(at = @At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/WorldClient;playSoundAtEntity(Lnet/minecraft/entity/Entity;Ljava/lang/String;FF)V"), method= "handleCollectItem")
    public void onItemPickupAnimation(S0DPacketCollectItem packet, CallbackInfo info) {
        assert clientWorldController != null;
        final Entity e = clientWorldController.getEntityByID(packet.getCollectedItemEntityID());
        EntityLivingBase c = (EntityLivingBase)clientWorldController.getEntityByID(packet.getEntityID());
        if (c == null) c = gameController.thePlayer;
        assert c != null;
        if (c.equals(gameController.thePlayer) && e instanceof EntityItem) {
            ItemStack item = ((EntityItem) e).getEntityItem().copy();
            item.stackSize = 1;
            new EventItemPickup(item);
        }
    }
    
    @Inject(at = @At("TAIL"), method="handleJoinGame")
    public void onGameJoin(S01PacketJoinGame packet, CallbackInfo info) {
        new EventJoinServer(gameController.thePlayer, netManager.getRemoteAddress().toString());
    }
    
    @Inject(at = @At("RETURN"), method="handleChunkData")
    public void onChunkData(S21PacketChunkData packet, CallbackInfo info) {
        if (packet.getExtractedSize() == 0) {
            new EventChunkUnload(packet.getChunkX(), packet.getChunkZ());
        } else {
            new EventChunkLoad(packet.getChunkX(), packet.getChunkZ(), packet.func_149274_i());
        }
    }
    
    @Inject(at = @At("TAIL"), method = "handleMapChunkBulk")
    public void onChunkDatas(S26PacketMapChunkBulk packet, CallbackInfo ci) {
        for (int i = 0; i < packet.getChunkCount(); ++i) {
            new EventChunkLoad(packet.getChunkX(i), packet.getChunkZ(i), true);
        }
    }
    
    @Inject(at = @At("TAIL"), method="handleBlockChange")
    public void onBlockUpdate(S23PacketBlockChange packet, CallbackInfo info) {
        new EventBlockUpdate(packet.blockState, clientWorldController.getTileEntity(packet.getBlockPosition()), packet.getBlockPosition(), "STATE");
    }
    
    @Inject(at = @At("TAIL"), method="handleMultiBlockChange")
    public void onChunkDeltaUpdate(S22PacketMultiBlockChange packet, CallbackInfo info) {
        for (S22PacketMultiBlockChange.BlockUpdateData record : packet.getChangedBlocks()) {
            new EventBlockUpdate(record.getBlockState(), clientWorldController.getTileEntity(record.getPos()), record.getPos(), "STATE");
        }
    }
    
    @Inject(at = @At("TAIL"), method="handleUpdateTileEntity")
    public void onBlockEntityUpdate(S35PacketUpdateTileEntity packet, CallbackInfo info) {
        new EventBlockUpdate(clientWorldController.getBlockState(packet.getPos()), clientWorldController.getTileEntity(packet.getPos()), packet.getPos(), "ENTITY");
    }
}

