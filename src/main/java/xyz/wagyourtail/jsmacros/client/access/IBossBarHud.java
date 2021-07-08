package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.entity.boss.BossStatus;

import java.util.Map;
import java.util.UUID;

public interface IBossBarHud {
    Map<UUID, BossStatus> jsmacros_GetBossBars();
}
