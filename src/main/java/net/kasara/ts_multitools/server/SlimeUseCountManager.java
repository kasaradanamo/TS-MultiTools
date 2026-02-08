package net.kasara.ts_multitools.server;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultitools;
import net.kasara.ts_multitools.network.packet.s2c.SlimeUseCountS2CPacket;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * スライム使用回数を制御する
 */
public class SlimeUseCountManager {

    /** プレイヤーNBT内で使用回数を保存するキー名 */
    private static final String SLIME_USE_COUNT = "slime_use_count";

    private SlimeUseCountManager() {}

    /**
     * プレイヤーに保存されているスライム使用回数を取得
     * データが存在しない場合は0を返す
     */
    public static int get(PlayerEntity player) {
        NbtCompound nbt = TokorotenSlimeAPI.getAddonData(player, TSMultitools.MOD_ID);
        return nbt.getInt(SLIME_USE_COUNT).orElse(0);
    }

    /**
     * プレイヤーのスライム使用回数を設定する
     */
    public static void set(PlayerEntity player, int count) {
        NbtCompound nbt = TokorotenSlimeAPI.getAddonData(player, TSMultitools.MOD_ID);
        nbt.putInt(SLIME_USE_COUNT, count);

        if(player instanceof ServerPlayerEntity serverPlayer) {
            TokorotenSlimeAPI.writeAddonData(serverPlayer, TSMultitools.MOD_ID, nbt);
            SlimeUseCountS2CPacket.send(serverPlayer, count);   // クライアントに同期
        }
    }

    /**
     * スライム使用回数を1増加させる
     */
    public static void increment(PlayerEntity player) {
        set(player, get(player) + 1);
    }

    /**
     * スライム使用回数を0にリセットする
     */
    public static void reset(PlayerEntity player) {
        set(player, 0);
    }

    /**
     * 別プレイヤーからスライム使用回数をコピーする。
     * 主にリスポーン時（死亡・次元移動）を想定
     *
     * @param oldPlayer コピー元プレイヤー
     * @param newPlayer コピー先プレイヤー
     */
    public static void copyFrom(PlayerEntity oldPlayer, PlayerEntity newPlayer) {
        set(newPlayer, get(oldPlayer));
    }

    /**
     * 旧保存形式（CUSTOM_DATA 内）から現在のアドオンNBT形式へ移行する
     * 既に新形式のデータが存在する場合は何もしない
     */
    public static void migrateIfNeeded(PlayerEntity player) {
        if (has(player)) return;

        NbtComponent oldData = player.get(DataComponentTypes.CUSTOM_DATA);
        if (oldData == null) return;

        NbtCompound oldCompound = oldData.copyNbt();
        if (!oldCompound.contains(SLIME_USE_COUNT)) return;

        int oldCount = oldCompound.getInt(SLIME_USE_COUNT).orElse(0);
        set(player, oldCount);
    }

    // 新形式のアドオンNBTに使用回数が存在するか確認
    private static boolean has(PlayerEntity player) {
        NbtCompound nbt = TokorotenSlimeAPI.getAddonData(player, TSMultitools.MOD_ID);
        return nbt.contains(SLIME_USE_COUNT);
    }
}
