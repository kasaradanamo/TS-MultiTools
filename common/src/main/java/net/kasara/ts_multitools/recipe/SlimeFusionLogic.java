package net.kasara.ts_multitools.recipe;

import net.kasara.ts_multitools.component.MiningEnchantLevelComponent;
import net.kasara.ts_multitools.component.ModComponentsCommon;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.kasara.ts_multitools.item.SlimeEnchantmentRules;
import net.kasara.ts_multitools.util.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;

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
            if (!statsOnly) replaceEnchantments(result, material, Items.DIAMOND_SWORD.getDefaultInstance());
        }

        // ツールとして：採掘速度
        if (inheritMiningSpeed(result, material)) {
            changed = true;
            if (!statsOnly) {
                updateMiningEnchantLevel(result, material);
                replaceEnchantments(result, material, Items.DIAMOND_PICKAXE.getDefaultInstance());
            }
        }

        // 弓として：エンチャントだけ入れ替える
        if (!statsOnly && material.is(ItemTags.BOW_ENCHANTABLE)) {
            changed |= replaceEnchantments(result, material, Items.BOW.getDefaultInstance());
        }

        return changed ? result : ItemStack.EMPTY;
    }

    /**
     * 攻撃力か攻撃速度のどちらかが素材の方が高ければ、両方を素材の値にする
     */
    private static boolean inheritWeaponStats(ItemStack result, ItemStack material) {
        OptionalDouble damage = findMainHandValue(material, Attributes.ATTACK_DAMAGE, Item.BASE_ATTACK_DAMAGE_ID);
        OptionalDouble speed = findMainHandValue(material, Attributes.ATTACK_SPEED, Item.BASE_ATTACK_SPEED_ID);

        boolean higher = isHigher(damage, findMainHandValue(result, Attributes.ATTACK_DAMAGE, Item.BASE_ATTACK_DAMAGE_ID))
                || isHigher(speed, findMainHandValue(result, Attributes.ATTACK_SPEED, Item.BASE_ATTACK_SPEED_ID));
        if (!higher) return false;

        ItemAttributeModifiers modifiers = result.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        // 素材に無い方はスライムの値を残す
        if (damage.isPresent()) modifiers = withMainHandValue(modifiers, Attributes.ATTACK_DAMAGE, Item.BASE_ATTACK_DAMAGE_ID, damage.getAsDouble());
        if (speed.isPresent()) modifiers = withMainHandValue(modifiers, Attributes.ATTACK_SPEED, Item.BASE_ATTACK_SPEED_ID, speed.getAsDouble());
        result.set(DataComponents.ATTRIBUTE_MODIFIERS, modifiers);
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

        Tool tool = result.get(DataComponents.TOOL);
        if (tool == null) return false;

        List<Tool.Rule> rules = new ArrayList<>(tool.rules());
        for (int i = 0; i < rules.size(); i++) {
            Tool.Rule rule = rules.get(i);
            if (!rule.blocks().unwrapKey().equals(Optional.of(ModTags.Blocks.SLIME_MINEABLE))) continue;
            if (rule.speed().isEmpty() || materialSpeed <= rule.speed().get()) return false;

            rules.set(i, new Tool.Rule(rule.blocks(), Optional.of(materialSpeed), rule.correctForDrops()));
            result.set(DataComponents.TOOL, new Tool(rules, tool.defaultMiningSpeed(), tool.damagePerBlock(), tool.canDestroyBlocksInCreative()));
            return true;
        }
        return false;
    }

    /**
     * スライムに付いている、その種類（sample に付けられるもの）のエンチャントを、素材のものに丸ごと入れ替える。
     * 幸運・シルクタッチのレベルは素材のものではなく記録の値を使う。エンチャントが変わったかを返す
     */
    private static boolean replaceEnchantments(ItemStack result, ItemStack material, ItemStack sample) {
        ItemEnchantments materialEnchants = material.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments current = result.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(current);
        MiningEnchantLevelComponent record = result.get(ModComponentsCommon.MINING_ENCHANT_LEVEL);

        mutable.removeIf(holder -> belongsTo(holder, sample, material));

        for (Holder<Enchantment> holder : materialEnchants.keySet()) {
            if (!belongsTo(holder, sample, material)) continue;
            if (!canHold(result, holder) || !isCompatibleWithAll(mutable, holder)) continue;

            int level = materialEnchants.getLevel(holder);
            if (record != null && holder.is(Enchantments.FORTUNE)) level = record.fortuneLevel();
            else if (record != null && holder.is(Enchantments.SILK_TOUCH)) level = record.silkTouchLevel();
            mutable.set(holder, level);
        }

        ItemEnchantments replaced = mutable.toImmutable();
        result.set(DataComponents.ENCHANTMENTS, replaced);
        return !replaced.equals(current);
    }

    /**
     * 幸運・シルクタッチの記録を、素材のレベルの方が高ければ上げる（下げない）
     */
    private static void updateMiningEnchantLevel(ItemStack result, ItemStack material) {
        MiningEnchantLevelComponent record = result.get(ModComponentsCommon.MINING_ENCHANT_LEVEL);
        if (record == null) return;

        ItemEnchantments materialEnchants = material.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Holder<Enchantment> holder : materialEnchants.keySet()) {
            int level = materialEnchants.getLevel(holder);
            if (holder.is(Enchantments.FORTUNE)) record = record.withFortune(Math.max(record.fortuneLevel(), level));
            else if (holder.is(Enchantments.SILK_TOUCH)) record = record.withSilkTouch(Math.max(record.silkTouchLevel(), level));
        }
        result.set(ModComponentsCommon.MINING_ENCHANT_LEVEL, record);
    }

    /**
     * その種類のエンチャントか。バニラの剣・ツルハシ・弓のどれにも付かない他MODのものは、素材に付けられるかで判定する。
     * 呪いはどの種類にも入れない（合体で移さず、外しもしない）
     */
    private static boolean belongsTo(Holder<Enchantment> holder, ItemStack sample, ItemStack material) {
        if (holder.is(EnchantmentTags.CURSE)) return false;

        Enchantment enchantment = holder.value();
        if (enchantment.isSupportedItem(sample)) return true;

        boolean vanillaKind = enchantment.isSupportedItem(Items.DIAMOND_SWORD.getDefaultInstance())
                || enchantment.isSupportedItem(Items.DIAMOND_PICKAXE.getDefaultInstance())
                || enchantment.isSupportedItem(Items.BOW.getDefaultInstance());
        return !vanillaKind && enchantment.isSupportedItem(material);
    }

    /**
     * スライムかバニラの弓に付けられ、除外リストに無いエンチャントならスライムに付けられる
     */
    private static boolean canHold(ItemStack slime, Holder<Enchantment> holder) {
        if (SlimeEnchantmentRules.isBlacklisted(holder)) return false;

        Enchantment enchantment = holder.value();
        return enchantment.isSupportedItem(slime) || enchantment.isSupportedItem(Items.BOW.getDefaultInstance());
    }

    private static boolean isCompatibleWithAll(ItemEnchantments.Mutable current, Holder<Enchantment> holder) {
        for (Holder<Enchantment> existing : current.keySet()) {
            if (!existing.equals(holder) && !Enchantment.areCompatible(existing, holder)) return false;
        }
        return true;
    }

    private static boolean isHigher(OptionalDouble material, OptionalDouble current) {
        return material.isPresent() && (current.isEmpty() || material.getAsDouble() > current.getAsDouble());
    }

    private static ItemAttributeModifiers withMainHandValue(ItemAttributeModifiers modifiers, Holder<Attribute> attribute, Identifier id, double value) {
        return modifiers.withModifierAdded(attribute,
                new AttributeModifier(id, value, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);
    }

    private static OptionalDouble findMainHandValue(ItemStack stack, Holder<Attribute> attribute, Identifier id) {
        ItemAttributeModifiers modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            if (entry.matches(attribute, id)
                    && entry.modifier().operation() == AttributeModifier.Operation.ADD_VALUE
                    && entry.slot().test(EquipmentSlot.MAINHAND)) {
                return OptionalDouble.of(entry.modifier().amount());
            }
        }
        return OptionalDouble.empty();
    }

    private SlimeFusionLogic() {}
}
