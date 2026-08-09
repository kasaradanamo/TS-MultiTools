package net.kasara.ts_multitools.util;

import net.kasara.ts_multitools.item.SlimeEnchantmentRules;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * マルチツールの設定ユーティリティ(採掘速度・ドロップ可否・エンチャント可否判定)。
 */
public class MultiToolUtil {

    /**
     * 採掘速度を計算する
     * - 対象タグに含まれるブロックは素材本来の速度
     * - クモの巣は常に速度15
     * - それ以外はバニラのデフォルト(1.0)
     */
    public static float getDestroySpeed(Tier tier, TagKey<Block> effectiveBlocks, BlockState state) {
        if (state.is(Blocks.COBWEB)) {
            return 15.0F;
        }
        return state.is(effectiveBlocks) ? tier.getSpeed() : 1.0F;
    }

    /**
     * 適切なツールでの採掘かどうか(ドロップの可否)を判定する。
     */
    public static boolean isCorrectToolForDrops(Tier tier, TagKey<Block> effectiveBlocks, BlockState state) {
        if (!state.is(effectiveBlocks)) return false;
        if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) return tier.getLevel() >= Tiers.DIAMOND.getLevel();
        if (state.is(BlockTags.NEEDS_IRON_TOOL)) return tier.getLevel() >= Tiers.IRON.getLevel();
        if (state.is(BlockTags.NEEDS_STONE_TOOL)) return tier.getLevel() >= Tiers.STONE.getLevel();
        return true;
    }

    /**
     * 耐久値(ツール2.5個分)を適用したPropertiesを返す
     */
    public static Item.Properties applyDurability(Item.Properties properties, Tier tier) {
        return properties.durability((int) (tier.getUses() * 2.5F));
    }

    /**
     * マルチツール用のエンチャント台/金床での可否判定(弓機能は無いのでBOWカテゴリは許可しない)。
     */
    public static boolean canMultitoolApplyAtEnchantingTable(Enchantment enchantment, boolean defaultResult) {
        EnchantmentCategory category = enchantment.category;
        return category == EnchantmentCategory.DIGGER
                || category == EnchantmentCategory.WEAPON
                || category == EnchantmentCategory.VANISHABLE
                || defaultResult;
    }

    /**
     * スライム用のエンチャント台/金床での可否判定。剣・採掘・弓の主要カテゴリを許可しつつ、
     * 耐久力(Unbreaking)・修繕(Mending)・無限(Infinity)はブラックリストで除外する。
     */
    public static boolean canSlimeApplyAtEnchantingTable(Enchantment enchantment, boolean defaultResult) {
        if (SlimeEnchantmentRules.isBlacklisted(enchantment)) return false;
        EnchantmentCategory category = enchantment.category;
        return category == EnchantmentCategory.DIGGER
                || category == EnchantmentCategory.WEAPON
                || category == EnchantmentCategory.BOW
                || category == EnchantmentCategory.VANISHABLE
                || defaultResult;
    }
}
