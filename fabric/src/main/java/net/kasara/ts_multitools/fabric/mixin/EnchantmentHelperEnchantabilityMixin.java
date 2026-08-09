package net.kasara.ts_multitools.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.kasara.ts_multitools.item.MultitoolItem;
import net.kasara.ts_multitools.item.SlimeItem;
import net.kasara.ts_multitools.util.MultiToolUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * エンチャント台での候補判定にSlimeItem/MultitoolItemの特別ルールを適用する。
 */
@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperEnchantabilityMixin {

    @Redirect(
            method = "getAvailableEnchantmentResults(ILnet/minecraft/world/item/ItemStack;Z)Ljava/util/List;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentCategory;canEnchant(Lnet/minecraft/world/item/Item;)Z"
            )
    )
    private static boolean ts_multitools$canEnchant(EnchantmentCategory category, Item item,
            @Local Enchantment enchantment) {
        boolean vanilla = category.canEnchant(item);
        if (item instanceof SlimeItem) {
            return MultiToolUtil.canSlimeApplyAtEnchantingTable(enchantment, vanilla);
        } else if (item instanceof MultitoolItem) {
            return MultiToolUtil.canMultitoolApplyAtEnchantingTable(enchantment, vanilla);
        }
        return vanilla;
    }
}
