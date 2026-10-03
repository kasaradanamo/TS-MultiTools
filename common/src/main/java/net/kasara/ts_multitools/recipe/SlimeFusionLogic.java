package net.kasara.ts_multitools.recipe;

import net.kasara.ts_multitools.component.MiningEnchantLevelComponent;
import net.kasara.ts_multitools.data.SlimeItemData;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.kasara.ts_multitools.item.SlimeEnchantmentRules;
import net.kasara.ts_multitools.item.SlimeItem;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.UUID;

/**
 * スライム合体の計算
 */
public final class SlimeFusionLogic {

    // 素材ツールの採掘速度を測るブロック
    private static final Map<TagKey<Item>, Block> SPEED_SAMPLE_BLOCKS = Map.of(
            ItemTags.PICKAXES, Blocks.STONE,
            ItemTags.AXES, Blocks.OAK_LOG,
            ItemTags.SHOVELS, Blocks.DIRT,
            ItemTags.HOES, Blocks.HAY_BLOCK);

    /**
     * 合体後のスライムを作る。剣・ツールは性能が素材の方が高い側だけ取り込み、弓はエンチャントだけ入れ替える。
     * statsOnly なら性能だけ取り込み、エンチャントは触らない。何も取り込めない場合は空を返す
     */
    public static ItemStack fuse(ItemStack slime, ItemStack material, boolean statsOnly) {
        if (slime.getItem() != ModItemsCommon.SLIME || material.isEmpty()) return ItemStack.EMPTY;

        ItemStack result = slime.copy();
        boolean changed = false;

        // 剣として：攻撃力・攻撃速度
        if (material.is(ItemTags.SWORDS) && inheritWeaponStats(result, material)) {
            changed = true;
            if (!statsOnly) replaceEnchantments(result, material, new ItemStack(Items.DIAMOND_SWORD));
        }

        // ツールとして：採掘速度
        if (inheritMiningSpeed(result, material)) {
            changed = true;
            if (!statsOnly) {
                updateMiningEnchantLevel(result, material);
                replaceEnchantments(result, material, new ItemStack(Items.DIAMOND_PICKAXE));
            }
        }

        // 弓として：エンチャントだけ入れ替える
        if (!statsOnly && material.getItem() instanceof BowItem) {
            changed |= replaceEnchantments(result, material, new ItemStack(Items.BOW));
        }

        return changed ? result : ItemStack.EMPTY;
    }

    /**
     * 攻撃力か攻撃速度のどちらかが素材の方が高ければ、両方を素材の値にする
     */
    private static boolean inheritWeaponStats(ItemStack result, ItemStack material) {
        OptionalDouble damage = findMainHandValue(material, Attributes.ATTACK_DAMAGE, SlimeItem.ATTACK_DAMAGE_ID);
        OptionalDouble speed = findMainHandValue(material, Attributes.ATTACK_SPEED, SlimeItem.ATTACK_SPEED_ID);
        OptionalDouble currentDamage = findMainHandValue(result, Attributes.ATTACK_DAMAGE, SlimeItem.ATTACK_DAMAGE_ID);
        OptionalDouble currentSpeed = findMainHandValue(result, Attributes.ATTACK_SPEED, SlimeItem.ATTACK_SPEED_ID);

        if (!isHigher(damage, currentDamage) && !isHigher(speed, currentSpeed)) return false;

        // 素材に無い方はスライムの値を残す
        SlimeItemData.setAttackDamage(result, damage.isPresent() ? damage.getAsDouble() : currentDamage.orElse(0.0));
        SlimeItemData.setAttackSpeed(result, speed.isPresent() ? speed.getAsDouble() : currentSpeed.orElse(0.0));
        return true;
    }

    /**
     * 素材ツールの採掘速度の方が高ければ、スライムの採掘速度を書き換える
     */
    private static boolean inheritMiningSpeed(ItemStack result, ItemStack material) {
        float materialSpeed = 0.0F;
        for (Map.Entry<TagKey<Item>, Block> sample : SPEED_SAMPLE_BLOCKS.entrySet()) {
            if (material.is(sample.getKey())) {
                materialSpeed = Math.max(materialSpeed, material.getDestroySpeed(sample.getValue().defaultBlockState()));
            }
        }

        if (materialSpeed <= SlimeItem.getMiningSpeed(result)) return false;

        SlimeItemData.setMiningSpeed(result, materialSpeed);
        return true;
    }

    /**
     * スライムに付いている、その種類（sample に付けられるもの）のエンチャントを、素材のものに丸ごと入れ替える。
     * 幸運・シルクタッチのレベルは素材のものではなく記録の値を使う。エンチャントが変わったかを返す
     */
    private static boolean replaceEnchantments(ItemStack result, ItemStack material, ItemStack sample) {
        Map<Enchantment, Integer> materialEnchants = EnchantmentHelper.getEnchantments(material);
        Map<Enchantment, Integer> current = EnchantmentHelper.getEnchantments(result);
        Map<Enchantment, Integer> replaced = new LinkedHashMap<>(current);
        MiningEnchantLevelComponent record = SlimeItemData.getMiningEnchantLevel(result);

        replaced.keySet().removeIf(enchantment -> belongsTo(enchantment, sample, material));

        for (Map.Entry<Enchantment, Integer> entry : materialEnchants.entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (!belongsTo(enchantment, sample, material)) continue;
            if (!canHold(result, enchantment) || !isCompatibleWithAll(replaced.keySet(), enchantment)) continue;

            int level = entry.getValue();
            if (enchantment == Enchantments.BLOCK_FORTUNE) level = record.fortuneLevel();
            else if (enchantment == Enchantments.SILK_TOUCH) level = record.silkTouchLevel();
            replaced.put(enchantment, level);
        }

        if (replaced.equals(current)) return false;
        EnchantmentHelper.setEnchantments(replaced, result);
        return true;
    }

    /**
     * 幸運・シルクタッチの記録を、素材のレベルの方が高ければ上げる（下げない）
     */
    private static void updateMiningEnchantLevel(ItemStack result, ItemStack material) {
        MiningEnchantLevelComponent record = SlimeItemData.getMiningEnchantLevel(result);
        int fortune = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, material);
        int silkTouch = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, material);

        SlimeItemData.setMiningEnchantLevel(result, record
                .withFortune(Math.max(record.fortuneLevel(), fortune))
                .withSilkTouch(Math.max(record.silkTouchLevel(), silkTouch)));
    }

    /**
     * その種類のエンチャントか。バニラの剣・ツルハシ・弓のどれにも付かない他MODのものは、素材に付けられるかで判定する。
     * 呪いはどの種類にも入れない（合体で移さず、外しもしない）
     */
    private static boolean belongsTo(Enchantment enchantment, ItemStack sample, ItemStack material) {
        if (enchantment.isCurse()) return false;

        if (enchantment.canEnchant(sample)) return true;

        boolean vanillaKind = enchantment.canEnchant(new ItemStack(Items.DIAMOND_SWORD))
                || enchantment.canEnchant(new ItemStack(Items.DIAMOND_PICKAXE))
                || enchantment.canEnchant(new ItemStack(Items.BOW));
        return !vanillaKind && enchantment.canEnchant(material);
    }

    /**
     * スライムに付けられ、除外リストに無いエンチャントならスライムに付けられる
     */
    private static boolean canHold(ItemStack slime, Enchantment enchantment) {
        return !SlimeEnchantmentRules.isBlacklisted(enchantment) && enchantment.canEnchant(slime);
    }

    private static boolean isCompatibleWithAll(Collection<Enchantment> current, Enchantment enchantment) {
        for (Enchantment existing : current) {
            if (existing != enchantment && !existing.isCompatibleWith(enchantment)) return false;
        }
        return true;
    }

    private static boolean isHigher(OptionalDouble material, OptionalDouble current) {
        return material.isPresent() && (current.isEmpty() || material.getAsDouble() > current.getAsDouble());
    }

    private static OptionalDouble findMainHandValue(ItemStack stack, Attribute attribute, UUID id) {
        for (AttributeModifier modifier : stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(attribute)) {
            if (modifier.getId().equals(id) && modifier.getOperation() == AttributeModifier.Operation.ADDITION) {
                return OptionalDouble.of(modifier.getAmount());
            }
        }
        return OptionalDouble.empty();
    }

    private SlimeFusionLogic() {}
}
