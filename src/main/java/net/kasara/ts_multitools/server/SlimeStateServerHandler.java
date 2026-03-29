package net.kasara.ts_multitools.server;

import com.mojang.datafixers.util.Pair;
import net.kasara.ts_multitools.component.ModComponents;
import net.kasara.ts_multitools.item.ModItems;
import net.kasara.ts_multitools.item.SlimeItem;
import net.kasara.ts_multitools.server.data.SlimeStateServerCache;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

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
    public static void onSlimeStateUpdate(UUID uuid, String state, ServerPlayerEntity player) {
        Inventory inv = player.getInventory();

        // インベントリ内のスライムのSTATEを0に設定（サーバー側のみ）
        for (ItemStack stack : inv) {
            if (stack.isEmpty() || stack.getItem() != ModItems.SLIME) continue;

            UUID invUuid = stack.get(ModComponents.SLIME_UUID);
            if (invUuid == null) continue;

            if (invUuid.equals(uuid)) {
                stack.set(ModComponents.SLIME_STATE, "slime");
            }
        }
        // サーバーキャッシュに保存
        SlimeStateServerCache.setSlimeState(uuid, state);

        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand = player.getOffHandStack();

        //送信情報を格納するやつ
        List<Pair<EquipmentSlot, ItemStack>> equipment = new ArrayList<>();

        if (mainHand.getItem() instanceof SlimeItem) {
            equipment.add(new Pair<>(EquipmentSlot.MAINHAND, mainHand));
        }
        if (offHand.getItem() instanceof SlimeItem) {
            equipment.add(new Pair<>(EquipmentSlot.OFFHAND, offHand));
        }

        if (equipment.isEmpty()) return;

        var packet = new EntityEquipmentUpdateS2CPacket(player.getId(), equipment);

        // 本人以外のサーバープレイヤーに送信
        for (ServerPlayerEntity other : player.getWorld().getServer().getPlayerManager().getPlayerList()) {
            if (player == other) continue;
            other.networkHandler.sendPacket(packet);
        }
    }
}
