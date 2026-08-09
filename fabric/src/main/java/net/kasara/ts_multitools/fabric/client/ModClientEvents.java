package net.kasara.ts_multitools.fabric.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.client.SlimeStateClientHandler;
import net.kasara.ts_multitools.client.SlimeTickEventHandler;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.minecraft.client.Minecraft;

/**
 * クライアントのイベントクラス。
 */
public class ModClientEvents {

    public static void register() {
        // ブロック攻撃時のイベント
        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
            if (!level.isClientSide() || player.getItemInHand(hand).getItem() != ModItemsCommon.SLIME) {
                return net.minecraft.world.InteractionResult.PASS;
            }
            SlimeStateClientHandler.onAttackBlock(player, level, hand, pos);
            return net.minecraft.world.InteractionResult.PASS;
        });

        // 毎ティックのイベント
        ClientTickEvents.END_CLIENT_TICK.register(SlimeTickEventHandler::slimeTickEventHandler);

        // ログ出力
        TSMultiToolsCommon.LOGGER.info("Registering addon Mod Client Events for " + TokorotenSlimeAPI.getModId() + " (from " + TSMultiToolsCommon.MOD_ID + ")");
    }
}
