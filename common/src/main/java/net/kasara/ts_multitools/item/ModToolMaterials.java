package net.kasara.ts_multitools.item;

import net.kasara.ts_multitools.util.ModTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

/**
 * カスタムツール素材を定義するクラス
 */
public class ModToolMaterials {

    // スライム素材のツール素材設定
    public static final Tier SLIME = new Tier() {
        @Override
        public int getUses() {
            return 1; // 耐久値（スライムにはないため1にしてる）
        }

        @Override
        public float getSpeed() {
            return 10.0F; // 採掘速度
        }

        @Override
        public float getAttackDamageBonus() {
            return 5.0F; // 攻撃力ボーナス
        }

        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return ModTags.Blocks.INCORRECT_FOR_SLIME; // 採掘してもドロップしないブロックのタグ
        }

        @Override
        public int getEnchantmentValue() {
            return 15; // エンチャント適性
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(ModTags.Items.SLIME_MATERIALS); // 修理に使えるアイテム
        }
    };
}
