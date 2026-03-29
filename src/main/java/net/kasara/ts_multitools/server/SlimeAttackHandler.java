package net.kasara.ts_multitools.server;

import net.kasara.ts_multitools.item.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SlimeAttackHandler {

    /**
     * 攻撃をキャンセルするかどうかを判定
     * SlimeItemを持っていて、経験値が0の場合は攻撃不可
     */
    public static boolean shouldCancelAttack(Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            ItemStack stack = player.getMainHandItem();
            return stack.getItem() == ModItems.SLIME && player.totalExperience <= 0;
        }
        return false;
    }
}