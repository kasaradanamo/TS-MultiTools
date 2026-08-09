package net.kasara.ts_multitools.fabric.server;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.network.packet.s2c.SlimeUseCountS2CPacket;
import net.kasara.ts_multitools.server.ModServerEventsCommon;
import net.kasara.ts_multitools.server.SlimeUseCountManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;

/**
 * サーバー側イベントハンドラ。
 */
public final class ModServerEvents {

    public static void register() {
        // プレイヤーがエンティティを攻撃した時のイベント
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            ModServerEventsCommon.onAttackEntity(player); // 内部でisClientSide判定を行う
            return InteractionResult.PASS;
        });

        // プレイヤーが経験値0でSlimeを持っている間、攻撃力/攻撃速度の補正を無効化して素手相当にする
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ModServerEventsCommon.onPlayerTick(player);
            }
        });

        // プレイヤーがワールドに入った時のイベント
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.player;

            // サーバー側で管理しているSlimeItemの使用回数をクライアントに送信
            SlimeUseCountS2CPacket.send(player, SlimeUseCountManager.get(player));
        });

        // 登録完了ログを出力
        TSMultiToolsCommon.LOGGER.info("Registering addon Mod Server Events for " + TokorotenSlimeAPI.getModId() + " (from " + TSMultiToolsCommon.MOD_ID + ")");
    }

    private ModServerEvents() {}
}
