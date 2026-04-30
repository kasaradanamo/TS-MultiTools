package net.kasara.ts_multitools.server;

import net.kasara.ts_multitools.component.ModComponents;
import net.kasara.ts_multitools.item.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

/**
 * プレイヤーのインベントリに存在するSlimeItemにUUIDを付与する
 */
public class SlimeUuidServerManager {

    public static void setSlimeUuid(ServerPlayer player, UUID uuid, int slot) {
        ItemStack stack = player.getInventory().getItem(slot);
        if (stack.getItem() != ModItems.SLIME.get()) return;

        stack.set(ModComponents.SLIME_UUID.get(), uuid);
    }

    private SlimeUuidServerManager() {}
}