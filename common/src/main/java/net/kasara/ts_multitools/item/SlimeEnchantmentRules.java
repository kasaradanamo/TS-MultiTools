package net.kasara.ts_multitools.item;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * SlimeItemのエンチャントルール
 */
public class SlimeEnchantmentRules {

    /**
     * ブラックリスト(耐久力、修繕、無限)
     */
    public static boolean isBlacklisted(Enchantment enchantment) {
        return enchantment == Enchantments.UNBREAKING
                || enchantment == Enchantments.MENDING
                || enchantment == Enchantments.INFINITY_ARROWS;
    }
}
