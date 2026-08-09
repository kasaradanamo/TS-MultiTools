package net.kasara.ts_multitools.server;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.network.packet.s2c.SlimeUseCountS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * スライム使用回数を制御する
 */
public class SlimeUseCountManager {

    // プレイヤーNBT内で使用回数を保存するキー名
    private static final String SLIME_USE_COUNT = "slime_use_count";

    /**
     * プレイヤーに保存されているスライム使用回数を取得
     * データが存在しない場合は0を返す
     */
    public static int get(Player player) {
        CompoundTag nbt = TokorotenSlimeAPI.getAddonData(player, TSMultiToolsCommon.MOD_ID);
        return nbt.getInt(SLIME_USE_COUNT);
    }

    /**
     * プレイヤーのスライム使用回数を設定する
     */
    public static void set(Player player, int count) {
        CompoundTag nbt = TokorotenSlimeAPI.getAddonData(player, TSMultiToolsCommon.MOD_ID);
        nbt.putInt(SLIME_USE_COUNT, count);

        if (player instanceof ServerPlayer serverPlayer) {
            TokorotenSlimeAPI.writeAddonData(serverPlayer, TSMultiToolsCommon.MOD_ID, nbt);
            SlimeUseCountS2CPacket.send(serverPlayer, count);   // クライアントに同期
        }
    }

    /**
     * スライム使用回数を1増加させる
     */
    public static void increment(Player player) {
        set(player, get(player) + 1);
    }

    /**
     * スライム使用回数を0にリセットする
     */
    public static void reset(Player player) {
        set(player, 0);
    }

    /**
     * 別プレイヤーからスライム使用回数をコピーする。
     *
     * @param oldPlayer コピー元プレイヤー
     * @param newPlayer コピー先プレイヤー
     */
    public static void copyFrom(Player oldPlayer, Player newPlayer) {
        set(newPlayer, get(oldPlayer));
    }

    private SlimeUseCountManager() {}
}
