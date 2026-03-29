package net.kasara.ts_multitools.server;

import net.kasara.ts_multitools.component.ModComponents;
import net.kasara.ts_multitools.item.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

/**
 * プレイヤーのインベントリに存在するSlimeItemにUUIDを付与する
 */
public class SlimeUuidServerManager {

    public static void setSlimeUuid(ServerPlayerEntity player, UUID uuid, int slot) {
        ItemStack stack = player.getInventory().getStack(slot);
        if (stack.getItem() != ModItems.SLIME) return;

        stack.set(ModComponents.SLIME_UUID, uuid);
    }

    private SlimeUuidServerManager() {}
}
