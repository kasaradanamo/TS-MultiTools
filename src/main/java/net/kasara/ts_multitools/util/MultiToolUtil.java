package net.kasara.ts_multitools.util;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

/**
 * マルチツールの設定ユーティリティ
 */
public class MultiToolUtil {

    private static final HolderGetter<Block> BLOCK_LOOKUP =
            BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);

    /**
     * マルチツールの設定を適応する
     *
     * @param material        ツール素材（耐久値、修理アイテム、エンチャント補正値を使用）
     * @param pros            アイテムの基本設定
     * @param effectiveBlocks 採掘可能ブロックタグ
     * @param attackDamage    攻撃力（マテリアルの攻撃力に加算）
     * @param attackSpeed     攻撃速度
     * @return ToolComponent, WeaponComponent, AttributeModifiers が付与された設定
     */
    public static Item.Properties applyMultiToolProperties(
            ToolMaterial material, Item.Properties pros,
            TagKey<Block> effectiveBlocks,
            float attackDamage,
            float attackSpeed
    ) {
        // 基本設定（耐久値・修理アイテム・エンチャント補正）
        Item.Properties basePros = pros
                .durability((int) (material.durability() * 2.5F)) // 耐久値はツール2.5個分
                .repairable(material.repairItems())
                .enchantable(material.enchantmentValue());

        // 採掘用コンポーネント
        Tool toolComp = new Tool(
                List.of(
                        // 採掘してもドロップしないブロック
                        Tool.Rule.deniesDrops(BLOCK_LOOKUP.getOrThrow(material.incorrectBlocksForDrops())),
                        // 通常の採掘可能ブロック
                        Tool.Rule.minesAndDrops(BLOCK_LOOKUP.getOrThrow(effectiveBlocks), material.speed()),
                        // 蜘蛛の巣破壊できるようにする
                        Tool.Rule.minesAndDrops(HolderSet.direct(Blocks.COBWEB.builtInRegistryHolder()), 15.0F)
                ),
                1.0F, // 範囲倍率（常に1.0）
                1,      // 消費耐久値
                true    // 破壊可能ブロック以外も破壊できるか
        );

        // 武器コンポーネント
        Weapon weaponComp = new Weapon(1);

        // 攻撃力・攻撃速度のAttribute設定
        ItemAttributeModifiers attrComp = ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                        Item.BASE_ATTACK_DAMAGE_ID,
                        attackDamage + material.attackDamageBonus(), // マテリアルの攻撃力を加算
                        AttributeModifier.Operation.ADD_VALUE
                ), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(
                        Item.BASE_ATTACK_DAMAGE_ID,
                        attackSpeed,  // 呼び出し元から渡された攻撃速度
                        AttributeModifier.Operation.ADD_VALUE
                ), EquipmentSlotGroup.MAINHAND)
                .build();

        return basePros
                .component(DataComponents.TOOL, toolComp)
                .component(DataComponents.WEAPON, weaponComp)
                .attributes(attrComp);
    }
}