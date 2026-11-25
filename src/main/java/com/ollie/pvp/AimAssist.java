package com.ollie.pvp;

import com.ollie.pvp.gui.Category;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

public class AimAssist extends Module {

    private static final double RANGE     = 4.5;
    private static final float  SPEED     = 0.28f;
    private static final float  MAX_STEP  = 12.0f;

    public AimAssist() {
        super("Aim Assist", Category.COMBAT);
    }

    @Override
    public void tick(MinecraftClient client) {
        LivingEntity target = CombatUtil.bestTarget(client, RANGE);
        if (target == null) return;

        Vec3d eye = client.player.getEyePos();
        float[] want = CombatUtil.anglesTo(eye, target.getEyePos());

        float dYaw = CombatUtil.wrapDegrees(want[0] - client.player.getYaw());
        float dPitch = CombatUtil.wrapDegrees(want[1] - client.player.getPitch());

        float stepYaw = clamp(dYaw * SPEED, -MAX_STEP, MAX_STEP);
        float stepPitch = clamp(dPitch * SPEED, -MAX_STEP, MAX_STEP);

        client.player.setYaw(client.player.getYaw() + stepYaw);
        client.player.setPitch(clamp(client.player.getPitch() + stepPitch, -90.0f, 90.0f));
    }

    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }
}
