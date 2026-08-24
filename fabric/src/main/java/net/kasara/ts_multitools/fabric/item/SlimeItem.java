package net.kasara.ts_multitools.fabric.item;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.kasara.ts_multitools.item.SlimeEnchantmentRules;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Fabric版のスライムアイテム。
 * commonのSlimeItemを継承し、Fabric固有のエンチャント可否判定のみ追加する。
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
    public boolean canBeEnchantedWith(ItemStack stack, Holder<Enchantment> enchantment, EnchantingContext context) {
        return super.canBeEnchantedWith(stack, enchantment, context)
                && !SlimeEnchantmentRules.isBlacklisted(enchantment);
    }
}
