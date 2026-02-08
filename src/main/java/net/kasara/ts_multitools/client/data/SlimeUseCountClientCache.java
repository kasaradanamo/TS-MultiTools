package net.kasara.ts_multitools.client.data;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * クライアント側で保持するMod固有のデータ
 * 主にUI表示やクライアントだけで必要な情報を管理する
 */
@Environment(EnvType.CLIENT)
public class SlimeUseCountClientCache {

    // プレイヤーがSlimeItemを使用した回数
    private static int slimeUseCount = 0;

    /**
     * スライムの使用回数を取得
     * @return 現在の使用回数
     */
    public static int getSlimeUseCount() {
        return slimeUseCount;
    }

    /**
     * スライムの使用回数を設定
     * @param count 新しい使用回数
     */
    public static void setSlimeUseCount(int count) {
        slimeUseCount = count;
    }
}
