package net.kasara.ts_multitools.client.render.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultitools;
import net.kasara.ts_multitools.component.ModComponents;
import net.kasara.ts_multitools.item.ModItems;
import net.minecraft.world.item.ItemStack;

/**
 * TokorotenSlimeの台座に置いた際の表示アイテム変更
 */
@Environment(EnvType.CLIENT)
public class ModPedestalRenderers {

    /**
     *  台座に置かれたアイテムの見た目を変えるハンドラを登録する
     *  onInitializeClient() から呼び出す
     */
    public static void register() {

        TokorotenSlimeAPI.registerPedestalRenderHandler(ModItems.SLIME, ModPedestalRenderers::modifySlimeRender);

        // ログ出力
        TSMultitools.LOGGER.info("Registering addon Pedestal Renderers for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultitools.MOD_ID + ")");
    }

    // SlimeItemの見た目を変更して描画
    private static ItemStack modifySlimeRender(ItemStack original) {
        ItemStack copy = original.copy();
        copy.set(ModComponents.SLIME_STATE, "sword");
        return copy;
    }
}
