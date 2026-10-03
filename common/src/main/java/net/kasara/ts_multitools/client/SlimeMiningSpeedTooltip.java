package net.kasara.ts_multitools.client;

import net.kasara.ts_multitools.item.ModItemsCommon;
import net.kasara.ts_multitools.item.SlimeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;

/**
 * スライムの採掘速度を、攻撃速度の行の下にバニラと同じ書き方で表示する
 */
public final class SlimeMiningSpeedTooltip {

    public static void insertTooltip(ItemStack stack, List<Component> lines) {
        if (stack.getItem() != ModItemsCommon.SLIME) return;

        for (int i = 0; i < lines.size(); i++) {
            if (!isAttackSpeedLine(lines.get(i))) continue;

            lines.add(i + 1, CommonComponents.space().append(Component.translatable(
                            "attribute.modifier.equals." + AttributeModifier.Operation.ADD_VALUE.id(),
                            ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(SlimeItem.getMiningSpeed(stack)),
                            Component.translatable("tooltip.tokorotenslime.mining_speed")))
                    .withStyle(ChatFormatting.DARK_GREEN));
            return;
        }
    }

    private static boolean isAttackSpeedLine(Component line) {
        String attackSpeed = Attributes.ATTACK_SPEED.value().getDescriptionId();
        for (Component part : line.getSiblings()) {
            if (!(part.getContents() instanceof TranslatableContents contents)
                    || !contents.getKey().startsWith("attribute.modifier.equals.")) continue;

            for (Object arg : contents.getArgs()) {
                if (arg instanceof Component name && name.getContents() instanceof TranslatableContents nameContents
                        && nameContents.getKey().equals(attackSpeed)) return true;
            }
        }
        return false;
    }

    private SlimeMiningSpeedTooltip() {}
}
