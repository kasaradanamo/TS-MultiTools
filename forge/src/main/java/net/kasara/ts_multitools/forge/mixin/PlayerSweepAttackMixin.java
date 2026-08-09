package net.kasara.ts_multitools.forge.mixin;

import net.kasara.ts_multitools.item.SlimeItem;
import net.kasara.ts_multitools.server.ModServerEventsCommon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * なぎ払い判定に、SlimeItemが経験値0(素手相当)の間は不可にする条件を追加する。
 */
@Mixin(Player.class)
public abstract class PlayerSweepAttackMixin {

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;canPerformAction(Lnet/minecraftforge/common/ToolAction;)Z"
            )
    )
    private boolean ts_multitools$sweepCheck(ItemStack stack, ToolAction action) {
        boolean result = stack.canPerformAction(action);
        if (result && action == ToolActions.SWORD_SWEEP && stack.getItem() instanceof SlimeItem
                && ModServerEventsCommon.shouldSuppressSweepAttack((Player) (Object) this)) {
            return false;
        }
        return result;
    }
}
