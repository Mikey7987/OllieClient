package com.ollie.pvp;

import com.ollie.pvp.gui.Category;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class AutoTotem extends Module {

    private static final int DELAY_TICKS = 2;
    private int delay = 0;

    public AutoTotem() {
        super("Auto Totem", Category.PLAYER);
    }

    @Override
    public void tick(MinecraftClient client) {
        if (client.player == null) return;
        if (delay > 0) { delay--; return; }

        ItemStack offhand = client.player.getOffHandStack();
        if (offhand.isOf(Items.TOTEM_OF_UNDYING)) return;

        int slot = findTotem(client);
        if (slot == -1) return;

        client.interactionManager.clickSlot(
                client.player.playerScreenHandler.syncId,
                slot < 9 ? slot + 36 : slot,
                40,
                SlotActionType.SWAP,
                client.player
        );

        delay = DELAY_TICKS;
    }

    private int findTotem(MinecraftClient client) {
        for (int i = 0; i < 36; i++) {
            if (client.player.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
                return i;
            }
        }
        return -1;
    }
}
