package net.kasara.ts_multitools.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * SlimeItemのエンチャントルール
 */
public class SlimeEnchantmentRules {

    /**
     * ブラックリスト(耐久力、修繕、無限)
     */
    public static boolean isBlacklisted(Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.UNBREAKING)
                || enchantment.is(Enchantments.MENDING)
                || enchantment.is(Enchantments.INFINITY);
    }
}
