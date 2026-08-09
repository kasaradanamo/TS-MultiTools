package net.kasara.ts_multitools.server;

import com.google.common.collect.Multimap;
import net.kasara.ts_multitools.constant.SlimeState;
import net.kasara.ts_multitools.data.SlimeItemData;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.kasara.ts_multitools.item.SlimeEnchantmentRules;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

/**
 * ローダー非依存のModServerEventsロジック
 */
public final class ModServerEventsCommon {

    /**
     * SlimeItemで攻撃した時の見た目(state)更新。経験値0の間は素のスライムのまま。
     */
    public static void onAttackEntity(Player player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() != ModItemsCommon.SLIME) return;

        if (player.level().isClientSide()) {
            SlimeItemData.setState(stack, player.totalExperience >= 1 ? SlimeState.SWORD : SlimeState.SLIME);
        }
    }

    /**
     * プレイヤーが経験値0でSlimeを持っている間、攻撃力/攻撃速度の補正を無効化して素手相当にする。
     * サーバー側の毎ティック処理からのみ呼び出す。
     */
    public static void onPlayerTick(Player player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() != ModItemsCommon.SLIME) return;

        boolean fistMode = player.totalExperience < 1;
        Multimap<Attribute, AttributeModifier> modifiers = stack.getAttributeModifiers(EquipmentSlot.MAINHAND);
        for (Map.Entry<Attribute, AttributeModifier> entry : modifiers.entries()) {
            AttributeInstance instance = player.getAttribute(entry.getKey());
            if (instance == null) continue;

            boolean present = instance.getModifier(entry.getValue().getId()) != null;
            if (fistMode && present) {
                instance.removeModifier(entry.getValue());     // 経験値0: 素手扱いにするため補正を除去
            } else if (!fistMode && !present) {
                instance.addTransientModifier(entry.getValue()); // 経験値ある: Slime本来の補正を再付与
            }
        }
    }

    /**
     * プレイヤーが経験値0でSlimeを持っている間、採掘速度を素手相当(1.0)にするかどうか。
     *
     * @return 上書きすべき場合は1.0F、そうでない場合はnull(呼び出し側でバニラ速度を維持)
     */
    public static Float onBreakSpeed(Player player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() != ModItemsCommon.SLIME || player.totalExperience >= 1) return null;

        return 1.0F;
    }

    /**
     * プレイヤーが経験値0でSlimeを持っている間、専用ツールを要求するブロックのドロップを
     * 素手相当(不可)にするかどうか。
     *
     * @return ドロップを禁止すべき場合はfalse、そうでない場合はnull(呼び出し側でバニラ判定を維持)
     */
    public static Boolean onHarvestCheck(Player player, BlockState targetBlock) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() != ModItemsCommon.SLIME || player.totalExperience >= 1) return null;
        if (!targetBlock.requiresCorrectToolForDrops()) return null; // 素手でも掘れるブロックはそのまま

        return false;
    }

    /**
     * プレイヤーが経験値0でSlimeItemを持っている間、なぎ払い攻撃(sweep attack)を
     * 素手相当(不可)にするかどうか。
     *
     * @return なぎ払い攻撃を抑制すべき場合はtrue
     */
    public static boolean shouldSuppressSweepAttack(Player player) {
        ItemStack stack = player.getMainHandItem();
        return stack.getItem() == ModItemsCommon.SLIME && player.totalExperience < 1;
    }

    /**
     * 金床使用時、SlimeItemに対してブラックリストのエンチャントが付いたアイテムを合成しようとしていないか判定。
     *
     * @return ブラックリストのエンチャントが右スロットのアイテムに付いていればtrue
     */
    public static boolean hasBlacklistedAnvilEnchant(Player player, ItemStack left, ItemStack right) {
        if (player.isCreative() || left.getItem() != ModItemsCommon.SLIME) return false;

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(right);
        return enchantments.keySet().stream().anyMatch(SlimeEnchantmentRules::isBlacklisted);
    }

    private ModServerEventsCommon() {}
}
