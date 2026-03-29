package net.kasara.ts_multitools.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultitools;
import net.kasara.ts_multitools.item.ModItems;
import net.minecraft.util.ActionResult;

/**
 * クライアントのイベントクラス
 */
@Environment(EnvType.CLIENT)
public class ModClientEvents {

    /**
     * クライアントイベント登録
     */
    public static void registerEvents() {

        // ブロック攻撃時に呼ばれる処理
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (!world.isClient() || player.getStackInHand(hand).getItem() != ModItems.SLIME)
                return ActionResult.PASS;

            // ブロック攻撃に応じてスライムの状態を変更
            SlimeStateClientHandler.onAttackBlock(player, world, hand, pos);

            return ActionResult.PASS;
        });

        // 毎Tick呼ばれる処理（クライアント専用）
        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            // tickごとのスライム処理
            SlimeTickEventHandler.slimeTickEventHandler(client);
        });

        // ログ出力
        TSMultitools.LOGGER.info("Registering addon Mod Client Events for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultitools.MOD_ID + ")");
    }
}
