package net.kasara.ts_multitools.client.data;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * スライム使用回数をクライアント側で保持する
 */
@Environment(EnvType.CLIENT)
public class SlimeUseCountClientCache {

    // プレイヤーがSlimeItemを使用した回数
    private static int slimeUseCount = 0;

    /**
     * スライムの使用回数を取得
     */
    public static int getSlimeUseCount() {
        return slimeUseCount;
    }

    /**
     * スライムの使用回数を設定
     */
    public static void setSlimeUseCount(int count) {
        slimeUseCount = count;
    }
}
