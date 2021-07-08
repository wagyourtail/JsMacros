package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.access.TPSData;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FWorld;

import java.util.LinkedList;
import java.util.List;

@Mixin(NetHandlerPlayClient.class)
public class MixinClientPlayNetworkHandler {

    @Unique
    long lastServerTimeRecvTime = 0;
    
    @Unique
    long lastServerTimeRecvTick = 0;
    
    @Unique
    List<TPSData> tpsData1M = new LinkedList<>();
    @Unique
    List<TPSData> tpsData5M = new LinkedList<>();
    @Unique
    List<TPSData> tpsData15M = new LinkedList<>();
    
    @Unique
    private final Object timeSync = new Object();
    
    
    @Inject(at = @At("HEAD"), method="handleTimeUpdate")
    public void onServerTime(S03PacketTimeUpdate packet, CallbackInfo info) {
        synchronized (timeSync) {
            final long tick = packet.getTotalWorldTime();
            final long time = System.currentTimeMillis();
            if (tick != lastServerTimeRecvTick) {
                final double mspt = (double)(time - lastServerTimeRecvTime) / (double)(tick - lastServerTimeRecvTick);
                if (lastServerTimeRecvTick == 0) {
                    lastServerTimeRecvTime = time;
                    lastServerTimeRecvTick = tick;
                    return;
                }
                lastServerTimeRecvTime = time;
                lastServerTimeRecvTick = tick;
                
                FWorld.serverInstantTPS = 1000 / mspt;
                tpsData1M.add(new TPSData(time, FWorld.serverInstantTPS));
                if (time - tpsData1M.get(0).recvTime > 60000)
                    tpsData1M.remove(0);
                FWorld.server1MAverageTPS = tpsData1M.stream().reduce(0D, (res, data) -> res + data.tps, Double::sum) / (double)tpsData1M.size();
                if (tpsData5M.size() == 0 || time - tpsData5M.get(tpsData5M.size() - 1).recvTime > 60000)
                    tpsData5M.add(new TPSData(time, FWorld.server1MAverageTPS));
                if (time - tpsData5M.get(0).recvTime > 60000 * 5)
                    tpsData5M.remove(0);
                FWorld.server5MAverageTPS = tpsData5M.stream().reduce(0D, (res, data) -> res + data.tps, Double::sum) / (double)tpsData5M.size();
                if (tpsData15M.size() == 0 || time - tpsData15M.get(tpsData15M.size() - 1).recvTime > 60000 * 5)
                    tpsData15M.add(new TPSData(time, FWorld.server5MAverageTPS));
                if (time - tpsData15M.get(0).recvTime > 60000 * 15)
                    tpsData15M.remove(0);
                FWorld.server15MAverageTPS = tpsData15M.stream().reduce(0D, (res, data) -> res + data.tps, Double::sum) / (double)tpsData15M.size();
            }
        }
    }
    
    @Inject(at = @At("TAIL"), method="handleJoinGame")
    public void onGameJoin(S01PacketJoinGame packetIn, CallbackInfo info) {
        synchronized (timeSync) {
            lastServerTimeRecvTime = 0;
            lastServerTimeRecvTick = 0;
    
            tpsData1M.clear();
            tpsData5M.clear();
            tpsData15M.clear();
        }
    }
}
