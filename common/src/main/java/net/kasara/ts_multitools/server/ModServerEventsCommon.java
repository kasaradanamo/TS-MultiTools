package net.kasara.ts_multitools.server;

import net.kasara.ts_multitools.component.ModComponentsCommon;
import net.kasara.ts_multitools.constant.SlimeState;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.function.Function;

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
            stack.set(ModComponentsCommon.SLIME_STATE, player.totalExperience >= 1 ? SlimeState.SWORD : SlimeState.SLIME);
        }
    }

    /**
     * プレイヤーが経験値0でSlimeを持っている間、攻撃力/攻撃速度の補正を無効化して素手相当にする
     * (アイテム自体についているエンチャントの効果は無効化しない)。
     * サーバー側の毎ティック処理からのみ呼び出す。補正の取り方はローダーごとに渡す
     */
    public static void onPlayerTick(Player player, Function<ItemStack, ItemAttributeModifiers> modifiersOf) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() != ModItemsCommon.SLIME) return;

        boolean fistMode = player.totalExperience < 1;
        ItemAttributeModifiers modifiers = modifiersOf.apply(stack);
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            if (!entry.slot().test(EquipmentSlot.MAINHAND)) continue;

            AttributeInstance instance = player.getAttribute(entry.attribute());
            if (instance == null) continue;

            boolean present = instance.getModifier(entry.modifier().id()) != null;
            if (fistMode && present) {
                instance.removeModifier(entry.modifier().id());   // 経験値0: 素手扱いにするため補正を除去
            } else if (!fistMode && !present) {
                instance.addOrUpdateTransientModifier(entry.modifier()); // 経験値ある: Slime本来の補正を再付与
            }
        }
    }

    private ModServerEventsCommon() {}
}
