package net.kasara.ts_multitools.server;

import net.kasara.ts_multitools.data.SlimeItemData;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

/**
 * プレイヤーのインベントリに存在するSlimeItemにUUIDを付与する
 */
public class SlimeUuidServerManager {

    public static void setSlimeUuid(ServerPlayer player, UUID uuid, int slot) {
        ItemStack stack = player.getInventory().getItem(slot);
        if (stack.getItem() != ModItemsCommon.SLIME) return;

        SlimeItemData.setUuid(stack, uuid);
    }

    private SlimeUuidServerManager() {}
}
