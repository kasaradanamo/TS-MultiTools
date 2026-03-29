package net.kasara.ts_multitools.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kasara.ts_multitools.item.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.UUID;

/**
 * 毎ティックスライムの状態更新をする
 */
@Environment(EnvType.CLIENT)
public class SlimeTickEventHandler {

    public static void slimeTickEventHandler(MinecraftClient client) {
        PlayerEntity player = client.player;
        if (player == null) return;

        Inventory inventory = player.getInventory();

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
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
