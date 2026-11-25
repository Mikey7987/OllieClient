package com.ollie.pvp;

import com.ollie.pvp.gui.Category;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;

public class TriggerBot extends Module {

    private static final double RANGE         = 3.0;
    private static final float  COOLDOWN_MIN  = 0.92f;
    private static final int    MIN_DELAY     = 1;
    private static final int    MAX_DELAY     = 3;

    private int cooldownTicks = 0;

    public TriggerBot() {
        super("Trigger Bot", Category.COMBAT);
    }

    @Override
    public void tick(MinecraftClient client) {
        if (client.player == null || client.crosshairTarget == null) return;

        if (cooldownTicks > 0) { cooldownTicks--; return; }

        if (!(client.crosshairTarget instanceof EntityHitResult hit)) return;
        if (!(hit.getEntity() instanceof LivingEntity target)) return;
        if (target == client.player) return;

        double dist = client.player.getEyePos().distanceTo(target.getEyePos());
        if (dist > RANGE) return;

        if (client.player.getAttackCooldownProgress(0.5f) < COOLDOWN_MIN) return;

        client.interactionManager.attackEntity(client.player, target);
        client.player.swingHand(client.player.getActiveHand());

        cooldownTicks = MIN_DELAY + (int) (Math.random() * (MAX_DELAY - MIN_DELAY + 1));
    }
}
