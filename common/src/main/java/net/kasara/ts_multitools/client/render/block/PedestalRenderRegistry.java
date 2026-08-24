package net.kasara.ts_multitools.client.render.block;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.tokorotenslime.api.TokorotenSlimeClientAPI;
import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.component.ModComponentsCommon;
import net.kasara.ts_multitools.constant.SlimeState;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.minecraft.world.item.ItemStack;

/**
 * TokorotenSlimeの台座に置いた際の表示アイテム変更
 */
public class PedestalRenderRegistry {

    /**
     *  台座に置かれたアイテムの見た目を変えるハンドラを登録する
     *  onInitializeClient() から呼び出す
     */
    public static void register() {

        TokorotenSlimeClientAPI.registerPedestalTransformer(ModItemsCommon.SLIME, PedestalRenderRegistry::modifySlimeRender);

        // ログ出力
        TSMultiToolsCommon.LOGGER.info("Registering addon Pedestal Renderers for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiToolsCommon.MOD_ID + ")");
    }

    // SlimeItemの見た目を変更して描画
    private static ItemStack modifySlimeRender(ItemStack original) {
        ItemStack copy = original.copy();
        copy.set(ModComponentsCommon.SLIME_STATE, SlimeState.SWORD);
        return copy;
    }
}
