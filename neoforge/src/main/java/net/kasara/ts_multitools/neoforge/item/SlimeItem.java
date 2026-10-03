package net.kasara.ts_multitools.neoforge.item;

import net.kasara.ts_multitools.item.SlimeEnchantmentRules;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.ItemAbility;

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
     * バニラの弓に付けられるものも付ける。Unbreaking(耐久), Mending(修繕), Infinity(無限) は除外
     */
    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return (super.supportsEnchantment(stack, enchantment)
                || Items.BOW.supportsEnchantment(Items.BOW.getDefaultInstance(), enchantment))
                && !SlimeEnchantmentRules.isBlacklisted(enchantment);
    }

    @Override
    public boolean canPerformAction(ItemInstance stack, ItemAbility itemAbility) {
        return ModItemAbilities.MULTITOOL.contains(itemAbility);
    }
}
