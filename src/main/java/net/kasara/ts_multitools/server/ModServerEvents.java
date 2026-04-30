package net.kasara.ts_multitools.server;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultiTools;
import net.kasara.ts_multitools.component.ModComponents;
import net.kasara.ts_multitools.constant.SlimeState;
import net.kasara.ts_multitools.item.ModItems;
import net.kasara.ts_multitools.network.packet.s2c.SlimeUseCountS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

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
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() != ModItems.SLIME) return InteractionResult.PASS;

            // 経験値がない場合何もできないようにする
            if (player.totalExperience < 1) return InteractionResult.FAIL;

            // 攻撃した際常にswordになるように
            if (level.isClientSide()) {
                stack.set(ModComponents.SLIME_STATE, SlimeState.SWORD);
            }

            return InteractionResult.PASS;
        });

        // プレイヤーがブロックを攻撃したときに呼ばれるイベント
        AttackBlockCallback.EVENT.register(((playerEntity, world, hand, blockPos, direction) -> {
            // 条件に応じて攻撃をキャンセルするか判定
            if (SlimeAttackHandler.shouldCancelAttack(playerEntity, hand)) {
                return InteractionResult.FAIL;   // 攻撃をキャンセル
            }
            return InteractionResult.PASS;   // 通常通り処理
        }));

        // プレイヤーがワールドに入ったときに呼ばれるイベント
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            //旧データがあって、新データがなかった場合コピー
            SlimeUseCountManager.migrateIfNeeded(player);

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