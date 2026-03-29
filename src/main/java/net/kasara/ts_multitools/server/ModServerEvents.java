package net.kasara.ts_multitools.server;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultitools;
import net.kasara.ts_multitools.component.ModComponents;
import net.kasara.ts_multitools.item.ModItems;
import net.kasara.ts_multitools.network.packet.s2c.SlimeUseCountS2CPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

/**
 * サーバー側で発生するイベント
 */
public class ModServerEvents {

    /**
     * サーバー上で発生するイベントを登録するメソッド
     */
    public static void registerEvents() {

        // プレイヤーがエンティティを攻撃した時に呼ばれるイベント
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            ItemStack stack = player.getStackInHand(hand);
            if (stack.getItem() != ModItems.SLIME) return ActionResult.PASS;

            // 経験値がない場合何もできないようにする
            if (player.totalExperience < 1) return ActionResult.FAIL;

            // 攻撃した際常にswordになるように
            if (world.isClient()) {
                stack.set(ModComponents.SLIME_STATE, "sword");
            }

            return ActionResult.PASS;
        });

        // プレイヤーがワールドに入ったときに呼ばれるイベント
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            //旧データがあって、新データがなかった場合コピー
            SlimeUseCountManager.migrateIfNeeded(player);

            // サーバー側で管理しているSlimeItemの使用回数をクライアントに送信
            SlimeUseCountS2CPacket.send(player, SlimeUseCountManager.get(handler.getPlayer()));
        });

        // プレイヤーがブロックを攻撃したときに呼ばれるイベント
        AttackBlockCallback.EVENT.register(((playerEntity, world, hand, blockPos, direction) -> {
            // 条件に応じて攻撃をキャンセルするか判定
            if (SlimeAttackHandler.shouldCancelAttack(playerEntity, hand)) {
                return ActionResult.FAIL;   // 攻撃をキャンセル
            }
            return ActionResult.PASS;   // 通常通り処理
        }));

        // プレイヤーが別プレイヤーインスタンスにコピーされるときに呼ばれる
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            SlimeUseCountManager.copyFrom(oldPlayer, newPlayer);
        });

        // 登録完了ログを出力
        TSMultitools.LOGGER.info("Registering addon Mod Server Events for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultitools.MOD_ID + ")");
    }
}
