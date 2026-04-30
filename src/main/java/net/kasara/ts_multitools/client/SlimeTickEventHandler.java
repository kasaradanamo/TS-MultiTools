package net.kasara.ts_multitools.client;

import net.kasara.ts_multitools.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

/**
 * 毎ティックスライムの状態更新をする
 */
public class SlimeTickEventHandler {

    public static void slimeTickEventHandler(Minecraft client) {
        Player player = client.player;
        if (player == null) return;

        Inventory inventory = player.getInventory();

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() != ModItems.SLIME) continue;

            UUID uuid = SlimeUuidClientManager.getOrCreate(stack, i);

            // スライムのstate処理
            SlimeStateClientHandler.updateSlimeState(player, stack, uuid);

            // スライムのエンチャント処理
            SlimeModeClientHandler.updateSlimeEnchantment(stack, uuid);

            // キー入力で SlimeItem のモード切替
            SlimeModeClientHandler.handleModeToggle(client);
        }
    }
}