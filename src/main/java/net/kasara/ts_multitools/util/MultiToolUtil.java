package net.kasara.ts_multitools.util;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.component.type.WeaponComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

import java.util.List;

/**
 * マルチツールの設定ユーティリティ
 */
public class MultiToolUtil {

    private static final RegistryEntryLookup<Block> BLOCK_LOOKUP =
            Registries.createEntryLookup(Registries.BLOCK);

    /**
     * マルチツールの設定を適応する
     *
     * @param material  ツール素材（耐久値、修理アイテム、エンチャント補正値を使用）
     * @param settings  アイテムの基本設定
     * @param effectiveBlocks   採掘可能ブロックタグ
     * @param attackDamage  攻撃力（マテリアルの攻撃力に加算）
     * @param attackSpeed   攻撃速度
     * @return  ToolComponent, WeaponComponent, AttributeModifiers が付与された設定
     */
    public static Item.Settings applyMultiToolSettings(
            ToolMaterial material, Item.Settings settings,
            TagKey<Block> effectiveBlocks,
            float attackDamage,
            float attackSpeed
    ) {
        // 基本設定（耐久値・修理アイテム・エンチャント補正）
        Item.Settings baseSettings = settings
                .maxDamage((int) (material.durability() * 2.5F)) // 耐久値はツール2.5個分
                .repairable(material.repairItems())
                .enchantable(material.enchantmentValue());

        // 採掘用コンポーネント
        ToolComponent toolComp = new ToolComponent(
                List.of(
                        // 採掘してもドロップしないブロック
                        ToolComponent.Rule.ofNeverDropping(BLOCK_LOOKUP.getOrThrow(material.incorrectBlocksForDrops())),
                        // 通常の採掘可能ブロック
                        ToolComponent.Rule.ofAlwaysDropping(BLOCK_LOOKUP.getOrThrow(effectiveBlocks), material.speed()),
                        // 蜘蛛の巣破壊できるようにする
                        ToolComponent.Rule.ofAlwaysDropping(RegistryEntryList.of(BLOCK_LOOKUP.getOrThrow(
                                RegistryKey.of(RegistryKeys.BLOCK, Registries.BLOCK.getId(Blocks.COBWEB)))), 15.0F)
                ),
                1.0F, // 範囲倍率（常に1.0）
                1,      // 消費耐久値
                true    // 破壊可能ブロック以外も破壊できるか
        );

        // 武器コンポーネント
        WeaponComponent weaponComp = new WeaponComponent(1);

        // 攻撃力・攻撃速度のAttribute設定
        AttributeModifiersComponent attrComp = AttributeModifiersComponent.builder()
                .add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(
                        Item.BASE_ATTACK_DAMAGE_MODIFIER_ID,
                        attackDamage + material.attackDamageBonus(), // マテリアルの攻撃力を加算
                        EntityAttributeModifier.Operation.ADD_VALUE
                ), AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(
                        Item.BASE_ATTACK_SPEED_MODIFIER_ID,
                        attackSpeed,  // 呼び出し元から渡された攻撃速度
                        EntityAttributeModifier.Operation.ADD_VALUE
                ), AttributeModifierSlot.MAINHAND)
                .build();

        return baseSettings
                .component(DataComponentTypes.TOOL, toolComp)
                .component(DataComponentTypes.WEAPON, weaponComp)
                .attributeModifiers(attrComp);
    }
}
