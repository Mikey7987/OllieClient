package com.ollie.pvp;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;

public final class CombatUtil {

    private CombatUtil() {}

    public static LivingEntity bestTarget(MinecraftClient client, double range) {
        if (client.player == null || client.world == null) return null;

        Vec3d eye = client.player.getEyePos();
        Box search = client.player.getBoundingBox().expand(range);

        List<Entity> candidates = client.world.getOtherEntities(client.player, search);
        return candidates.stream()
                .filter(e -> e instanceof LivingEntity)
                .filter(e -> !(e instanceof PlayerEntity p) || !p.isCreative())
                .map(e -> (LivingEntity) e)
                .filter(e -> eye.distanceTo(e.getEyePos()) <= range)
                .min(Comparator.comparingDouble(e -> eye.distanceToSquared(e.getEyePos())))
                .orElse(null);
    }

    public static float[] anglesTo(Vec3d from, Vec3d to) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, horizontal));
        return new float[]{yaw, pitch};
    }

    public static float wrapDegrees(float deg) {
        deg = deg % 360.0f;
        if (deg >= 180.0f) deg -= 360.0f;
        if (deg < -180.0f) deg += 360.0f;
        return deg;
    }
}
