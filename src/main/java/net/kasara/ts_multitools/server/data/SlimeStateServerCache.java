package net.kasara.ts_multitools.server.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * スライムの描画状態を管理するサーバー側キャッシュ
 * 他の人に適切に描画させる用
 */
public class SlimeStateServerCache {
    // スライムのuuidとstateの保存
    private static final Map<UUID, String> slimeState = new HashMap<>();

    /**
     * スライムのstateを取得
     */
    public static String getSlimeState(UUID uuid) {
        return slimeState.get(uuid);
    }

    /**
     * スライムのstateを設定
     */
    public static void setSlimeState(UUID uuid, String state) {
        slimeState.put(uuid, state);
    }
}