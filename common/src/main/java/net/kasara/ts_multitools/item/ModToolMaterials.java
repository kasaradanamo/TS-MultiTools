package net.kasara.ts_multitools.item;

import net.kasara.ts_multitools.util.ModTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * カスタムツール素材を定義する
 */
public class ModToolMaterials {

    // スライム素材のツール素材設定
    public static final Tier SLIME = new Tier() {
        @Override
        public int getUses() {
            return 1; // 耐久値(スライムには実質意味がないため1)
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
        public int getLevel() {
            return 4; // ネザライト相当(採掘レベル)
        }

        @Override
        public int getEnchantmentValue() {
            return 15; // エンチャント適性
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(ModTags.Items.SLIME_MATERIALS); // 修理に使えるアイテム(現状は空タグ)
        }
    };
}
