package net.kasara.ts_multitools.server;

import com.mojang.datafixers.util.Pair;
import net.kasara.ts_multitools.component.ModComponentsCommon;
import net.kasara.ts_multitools.constant.SlimeState;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.kasara.ts_multitools.server.data.SlimeStateServerCache;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 指定UUIDを持つSlimeItemの描画状態を更新するサーバー側処理
 */
public class SlimeStateServerHandler {

    /**
     * 指定UUIDのSlimeItemの状態を更新
     * インベントリ内の対象アイテムのSLIME_STATEを "slime" に変更する(サーバー側)
     * サーバーキャッシュにstateを保存し、パケット作成し、サーバープレイヤーに送信
     */
    public static void onSlimeStateUpdate(UUID uuid, String state, ServerPlayer player) {
        Inventory inv = player.getInventory();

        // インベントリ内のスライムのSTATEを0に設定（サーバー側のみ）
        for (ItemStack stack : inv) {
            if (stack.isEmpty() || stack.getItem() != ModItemsCommon.SLIME) continue;

            UUID invUuid = stack.get(ModComponentsCommon.SLIME_UUID);
            if (invUuid == null) continue;

            if (uuid.equals(invUuid)) {
                stack.set(ModComponentsCommon.SLIME_STATE, SlimeState.SLIME);
            }
        }
        // サーバーキャッシュに保存
        SlimeStateServerCache.setSlimeState(uuid, state);

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        //送信情報を格納するやつ
        List<Pair<EquipmentSlot, ItemStack>> equipment = new ArrayList<>();

        if (mainHand.getItem() == ModItemsCommon.SLIME) {
            equipment.add(new Pair<>(EquipmentSlot.MAINHAND, mainHand));
        }
        if (offHand.getItem() == ModItemsCommon.SLIME) {
            equipment.add(new Pair<>(EquipmentSlot.OFFHAND, offHand));
        }

        if (equipment.isEmpty()) return;

        var packet = new ClientboundSetEquipmentPacket(player.getId(), equipment);

        // 本人以外のサーバープレイヤーに送信
        for (ServerPlayer other : player.level().getServer().getPlayerList().getPlayers()) {
            if (player == other) continue;
            other.connection.send(packet);
        }
    }
}