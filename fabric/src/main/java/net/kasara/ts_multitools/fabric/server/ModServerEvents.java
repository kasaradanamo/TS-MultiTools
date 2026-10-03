package net.kasara.ts_multitools.fabric.server;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.fabric.TSMultiTools;
import net.kasara.ts_multitools.network.packet.s2c.SlimeUseCountS2CPacket;
import net.kasara.ts_multitools.server.ModServerEventsCommon;
import net.kasara.ts_multitools.server.SlimeUseCountManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * サーバー側で発生するイベント
 */
public class ModServerEvents {

    /**
     * サーバー上で発生するイベントを登録するメソッド
     */
    public static void register() {

        // プレイヤーがエンティティを攻撃した時に呼ばれるイベント
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            ModServerEventsCommon.onAttackEntity(player);
            return InteractionResult.PASS;
        });

        // プレイヤーが経験値0でSlimeを持っている間、攻撃力/攻撃速度の補正を無効化して素手相当にする
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ModServerEventsCommon.onPlayerTick(player,
                        stack -> stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY));
            }
        });

        // プレイヤーがワールドに入ったときに呼ばれるイベント
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();

            // サーバー側で管理しているSlimeItemの使用回数をクライアントに送信
            SlimeUseCountS2CPacket.send(player, SlimeUseCountManager.get(player));
        });

        // プレイヤーが別プレイヤーインスタンスにコピーされるときに呼ばれる
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            SlimeUseCountManager.copyFrom(oldPlayer, newPlayer);
        });

        // 登録完了ログを出力
        TSMultiTools.LOGGER.info("Registering addon Mod Server Events for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }
}
