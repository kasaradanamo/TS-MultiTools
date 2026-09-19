package net.kasara.ts_multitools.neoforge.item;

import net.kasara.ts_multitools.item.SlimeEnchantmentRules;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * スライムアイテム。
 * commonのSlimeItemを継承し、NeoForge固有のエンチャント可否判定のみ追加する。
 */
public class SlimeItem extends net.kasara.ts_multitools.item.SlimeItem {

    public SlimeItem(ToolMaterial material, Properties pros) {
        super(material, pros);
    }

    /**
     * エンチャント可能かどうかを判定
     * Unbreaking(耐久), Mending(修繕), Infinity(無限) は除外
     */
    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return super.supportsEnchantment(stack, enchantment)
                && !SlimeEnchantmentRules.isBlacklisted(enchantment);
    }
}
