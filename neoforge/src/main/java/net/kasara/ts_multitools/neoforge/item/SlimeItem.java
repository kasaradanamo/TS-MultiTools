package net.kasara.ts_multitools.neoforge.item;

import net.kasara.ts_multitools.item.SlimeEnchantmentRules;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.ItemAbility;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/**
 * NeoForge版のスライムアイテム。
 * commonのSlimeItemを継承し、NeoForge固有の耐久無効化とエンチャント可否判定のみ追加する。
 */
public class SlimeItem extends net.kasara.ts_multitools.item.SlimeItem {

    public SlimeItem(ToolMaterial material, Properties pros) {
        super(material, pros);
    }

    /**
     * 耐久消費なし
     */
    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        return 0;
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

    @Override
    public boolean canPerformAction(ItemInstance stack, ItemAbility itemAbility) {
        return ModItemAbilities.MULTITOOL.contains(itemAbility);
    }
}
