package net.kasara.ts_multitools.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.component.ModComponents;
import net.kasara.ts_multitools.item.ModItems;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ModPedestalRenderers {

    /**
     *  台座に置かれたアイテムの見た目を変えるハンドラを登録する
     *  onInitializeClient() から呼び出す
     */
    public static void register() {

        TokorotenSlimeAPI.registerPedestalRenderHandler(ModItems.SLIME, ModPedestalRenderers::modifySlimeRender);
    }

    // SlimeItemの見た目を変更して描画
    private static ItemStack modifySlimeRender(ItemStack original) {

        ItemStack copy = original.copy();
        copy.set(ModComponents.SLIME_STATE, "sword");
        return copy;
    }
}
