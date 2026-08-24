package net.kasara.ts_multitools.fabric.server;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.constant.SlimeState;
import net.kasara.ts_multitools.fabric.TSMultiTools;
import net.kasara.ts_multitools.fabric.component.ModComponents;
import net.kasara.ts_multitools.fabric.item.ModItems;
import net.kasara.ts_multitools.network.packet.s2c.SlimeUseCountS2CPacket;
import net.kasara.ts_multitools.server.SlimeUseCountManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
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
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() != ModItems.SLIME) return InteractionResult.PASS;

            // 経験値0の場合は攻撃をキャンセルせず素手相当(攻撃力/速度)で扱う(onPlayerTickで属性を制御)。
            // 見た目も経験値0の間は素のスライムのまま(剣の見た目にしない)
            if (level.isClientSide()) {
                stack.set(ModComponents.SLIME_STATE, player.totalExperience >= 1 ? SlimeState.SWORD : SlimeState.SLIME);
            }

            return InteractionResult.PASS;
        });

        // プレイヤーが経験値0でSlimeを持っている間、攻撃力/攻撃速度の補正を無効化して素手相当にする
        // (アイテム自体についているエンチャントの効果は無効化しない)。
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() != ModItems.SLIME) continue;

                boolean fistMode = player.totalExperience < 1;
                ItemAttributeModifiers modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
                for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
                    if (!entry.slot().test(EquipmentSlot.MAINHAND)) continue;

                    AttributeInstance instance = player.getAttribute(entry.attribute());
                    if (instance == null) continue;

                    boolean present = instance.getModifier(entry.modifier().id()) != null;
                    if (fistMode && present) {
                        instance.removeModifier(entry.modifier().id());   // 経験値0: 素手扱いにするため補正を除去
                    } else if (!fistMode && !present) {
                        instance.addOrUpdateTransientModifier(entry.modifier()); // 経験値ある: Slime本来の補正を再付与
                    }
                }
            }
        });

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
