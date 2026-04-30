package net.kasara.ts_multitools.client.render.block;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.tokorotenslime.api.TokorotenSlimeClientAPI;
import net.kasara.ts_multitools.TSMultiTools;
import net.kasara.ts_multitools.component.ModComponents;
import net.kasara.ts_multitools.constant.SlimeState;
import net.kasara.ts_multitools.item.ModItems;
import net.minecraft.world.item.ItemStack;

/**
 * TokorotenSlimeの台座に置いた際の表示アイテム変更
 */
public class ModPedestalRenderers {

    /**
     *  台座に置かれたアイテムの見た目を変えるハンドラを登録する
     *  onInitializeClient() から呼び出す
     */
    public static void register() {

        TokorotenSlimeClientAPI.registerPedestalTransformer(ModItems.SLIME, ModPedestalRenderers::modifySlimeRender);

        // ログ出力
        TSMultiTools.LOGGER.info("Registering addon Pedestal Renderers for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }

    // SlimeItemの見た目を変更して描画
    private static ItemStack modifySlimeRender(ItemStack original) {
        ItemStack copy = original.copy();
        copy.set(ModComponents.SLIME_STATE, SlimeState.SWORD);
        return copy;
    }
}
