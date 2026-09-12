package net.kasara.ts_multitools.fabric.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.client.SlimeStateClientHandler;
import net.kasara.ts_multitools.client.SlimeTickEventHandler;
import net.kasara.ts_multitools.fabric.TSMultiTools;
import net.kasara.ts_multitools.fabric.item.ModItems;
import net.minecraft.world.InteractionResult;

/**
 * クライアントのイベントクラス
 */
public class ModClientEvents {

    /**
     * クライアントイベント登録
     */
    public static void register() {

        // ブロック攻撃時に呼ばれる処理
        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
            if (!level.isClientSide() || player.getItemInHand(hand).getItem() != ModItems.SLIME)
                return InteractionResult.PASS;

            // ブロック攻撃に応じてスライムの状態を変更
            SlimeStateClientHandler.onAttackBlock(player, level, hand, pos);

            return InteractionResult.PASS;
        });

        // 毎Tick呼ばれる処理（クライアント専用）
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // tickごとのスライム処理
            SlimeTickEventHandler.slimeTickEventHandler(client);
        });

        // ログ出力
        TSMultiTools.LOGGER.info("Registering addon Mod Client Events for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }
}
