package net.kasara.ts_multitools.mixin;

import net.kasara.ts_multitools.item.MultitoolItem;
import net.kasara.ts_multitools.item.SlimeItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import java.util.stream.Stream;

/**
 * EnchantmentHelperMixin クラス
 * EnchantmentHelper の generateEnchantments メソッドに対して Mixin を適用
 * 特定アイテム（SlimeItem, MultitoolItem）のエンチャント生成閾値を調整する
 */
@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    /**
     * generateEnchantments メソッド内の while ループ閾値を変更
     *
     * バニラでは 50 が閾値になっているが、SlimeItem や MultitoolItem 用に調整
     *
     * @param original 元の閾値（デフォルトは 50）
     * @param random ランダム生成用
     * @param stack 対象のアイテムスタック
     * @param level エンチャントレベル
     * @param possibleEnchantments 生成候補のストリーム
     * @return 修正後の閾値
     */
    @ModifyConstant(
            method = "generateEnchantments(Lnet/minecraft/util/math/random/Random;Lnet/minecraft/item/ItemStack;ILjava/util/stream/Stream;)Ljava/util/List;",
            constant = @Constant(intValue = 50)
    )
    private static int modifyWhileThreshold(int original, Random random, ItemStack stack, int level, Stream<?> possibleEnchantments) {
        // SlimeItem は閾値を低くして簡単にエンチャント生成
        if (stack.getItem() instanceof SlimeItem) {
            return 4;
        }
        // MultitoolItem は SlimeItem より少し高めの閾値
        else if (stack.getItem() instanceof MultitoolItem) {
            return 8;
        }
        // それ以外のアイテムはバニラ設定（50）を使用
        else {
            return original;
        }
    }
}
