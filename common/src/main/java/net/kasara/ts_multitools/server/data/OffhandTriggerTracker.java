package net.kasara.ts_multitools.server.data;

import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * プレイヤーのオフハンドトリガー状態を管理するユーティリティ
 * オフハンド操作を行ったかどうかを一時的に記録する
 */
public class OffhandTriggerTracker {

    // プレイヤーUUIDごとのオフハンドトリガー状態を保持するマップ
    private static final Map<UUID, Boolean> TRIGGER_MAP = new HashMap<>();

    /**
     * 指定プレイヤーのオフハンドトリガー状態を設定する
     *
     * @param player 対象プレイヤー
     * @param value true = オフハンド操作あり, false = なし
     */
    public static void set(Player player, boolean value) {
        TRIGGER_MAP.put(player.getUUID(), value);
    }

    /**
     * 指定プレイヤーのオフハンドトリガー状態を取得する
     */
    public static boolean get(Player player) {
        return TRIGGER_MAP.getOrDefault(player.getUUID(), false);
    }

    /**
     * 指定プレイヤーのオフハンドトリガー状態をクリアする
     */
    public static void clear(Player player) {
        TRIGGER_MAP.remove(player.getUUID());
    }
}