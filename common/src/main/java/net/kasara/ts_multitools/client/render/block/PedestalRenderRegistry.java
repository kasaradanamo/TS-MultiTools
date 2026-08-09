package net.kasara.ts_multitools.client.render.block;

import net.kasara.tokorotenslime.api.TokorotenSlimeClientAPI;
import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.constant.SlimeState;
import net.kasara.ts_multitools.data.SlimeItemData;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.minecraft.world.item.ItemStack;

/**
 * TokorotenSlimeの台座に置いた際の表示アイテム変更
 */
public class PedestalRenderRegistry {

    /**
     * 台座に置かれたアイテムの見た目を変えるハンドラを登録する
     * クライアントセットアップ時に呼び出す
     */
    public static void register() {

        TokorotenSlimeClientAPI.registerPedestalTransformer(ModItemsCommon.SLIME, PedestalRenderRegistry::transformSlimeRender);

        // ログ出力
        TSMultiToolsCommon.LOGGER.info("Registering addon Pedestal Renderers for " + TSMultiToolsCommon.MOD_ID);
    }

    // SlimeItemの見た目を変更して描画
    private static ItemStack transformSlimeRender(ItemStack original) {
        ItemStack copy = original.copy();
        SlimeItemData.setState(copy, SlimeState.SWORD);
        return copy;
    }
}
