package net.kasara.ts_multitools.mixin;

import net.kasara.ts_multitools.item.MultitoolItem;
import net.kasara.ts_multitools.item.SlimeItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.util.stream.Stream;

/**
 * 特定アイテム（SlimeItem, MultitoolItem）のエンチャント生成閾値を調整する
 */
@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    /**
     * generateEnchantments メソッド内の while ループ閾値を変更
     * バニラでは 50 が閾値になっているが、SlimeItem や MultitoolItem 用に調整
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
