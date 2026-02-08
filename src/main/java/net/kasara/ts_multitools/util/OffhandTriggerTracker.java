package net.kasara.ts_multitools.util;

import net.minecraft.entity.player.PlayerEntity;

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
     * @param player 対象プレイヤー
     * @param value true = オフハンド操作あり, false = なし
     */
    public static void set(PlayerEntity player, boolean value) {
        TRIGGER_MAP.put(player.getUuid(), value);
    }

    /**
     * 指定プレイヤーのオフハンドトリガー状態を取得する
     * @param player 対象プレイヤー
     * @return オフハンド操作があったかどうか
     */
    public static boolean get(PlayerEntity player) {
        return TRIGGER_MAP.getOrDefault(player.getUuid(), false);
    }

    /**
     * 指定プレイヤーのオフハンドトリガー状態をクリアする
     * @param player 対象プレイヤー
     */
    public static void clear(PlayerEntity player) {
        TRIGGER_MAP.remove(player.getUuid());
    }
}
