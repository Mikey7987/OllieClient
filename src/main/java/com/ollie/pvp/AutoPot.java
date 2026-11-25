package com.ollie.pvp;

import com.ollie.pvp.gui.Category;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.screen.slot.SlotActionType;

public class AutoPot extends Module {

    private static final int DELAY_TICKS = 4;
    private int delay = 0;

    public AutoPot() {
        super("Auto Pot Refill", Category.PLAYER);
    }

    @Override
    public void tick(MinecraftClient client) {
        if (client.player == null) return;
        if (delay > 0) { delay--; return; }

        int sel = client.player.getInventory().selectedSlot;
        ItemStack held = client.player.getInventory().getStack(sel);
        if (held.getItem() instanceof PotionItem && held.getCount() > 0) return;

        int potSlot = findPotion(client);
        if (potSlot == -1) return;

        client.interactionManager.clickSlot(
                client.player.playerScreenHandler.syncId,
                potSlot < 9 ? potSlot + 36 : potSlot,
                sel,
                SlotActionType.SWAP,
                client.player
        );

        delay = DELAY_TICKS;
    }

    private int findPotion(MinecraftClient client) {
        for (int i = 0; i < 36; i++) {
            ItemStack s = client.player.getInventory().getStack(i);
            if (s.getItem() instanceof PotionItem && s.getCount() > 0) return i;
        }
        return -1;
    }
}
