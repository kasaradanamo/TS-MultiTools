package net.kasara.ts_multitools.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultitools;
import net.minecraft.util.ActionResult;

/**
 * Mod 全体のクライアントイベントとパケット受信処理をまとめるクラス。
 *
 * 主な責務：
 * - ClientTickEvents など、クライアント専用イベントの登録と管理
 * - サーバーから送られるパケットを受信し、クライアントの状態を更新
 * - 実際の処理ロジックは必要に応じて別クラス（例: SlimeModeClientHandler）に委譲する
 */
@Environment(EnvType.CLIENT)
public class ModClientEvents {

    /** インベントリ更新処理の実行タイミングを調整するためのカウンタ */
    private static int tickCounter = 0;
    private static final int CHECK_INTERVAL = 5;    // 5tickごとに更新チェック

    /** クライアントイベント登録 */
    public static void registerEvents() {
        // ブロック攻撃時に呼ばれる処理
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (player == null || !world.isClient()) return ActionResult.PASS;

            // ブロック攻撃に応じてスライムの状態を変更
            SlimeStateClientHandler.onAttackBlock(player, world, hand, pos);

            return ActionResult.PASS;
        });

        // 毎Tick呼ばれる処理（クライアント専用）
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // スライムの見た目状態更新（スライムの状態管理
            SlimeStateClientHandler.updateSlimeStates(client);

            // キー入力で SlimeItem のモード切替
            SlimeModeClientHandler.handleModeToggle(client);

            // 一定間隔でインベントリ内の SlimeItem のエンチャントに応じたモード自動更新
            if (tickCounter++ >= CHECK_INTERVAL) {
                tickCounter = 0;
                SlimeModeClientHandler.updateInventoryEnchantments(client);
            }
        });

        TSMultitools.LOGGER.info("Registering addon Mod Client Events for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultitools.MOD_ID + ")");
    }
}
