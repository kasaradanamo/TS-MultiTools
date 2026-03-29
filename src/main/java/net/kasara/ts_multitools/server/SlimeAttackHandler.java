package net.kasara.ts_multitools.server;

import net.kasara.ts_multitools.item.SlimeItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class SlimeAttackHandler {

    /**
     * 攻撃をキャンセルするかどうかを判定
     * SlimeItemを持っていて、経験値が0の場合は攻撃不可
     */
    public static boolean shouldCancelAttack(PlayerEntity player, Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            ItemStack stack = player.getMainHandStack();
            return stack.getItem() instanceof SlimeItem && player.totalExperience <= 0;
        }
        return false;
    }
}
