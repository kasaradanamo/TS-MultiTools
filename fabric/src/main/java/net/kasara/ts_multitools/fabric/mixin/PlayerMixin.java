package net.kasara.ts_multitools.fabric.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.kasara.ts_multitools.item.MultitoolItem;
import net.kasara.ts_multitools.item.SlimeItem;
import net.kasara.ts_multitools.server.ModServerEventsCommon;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * BreakSpeed/HarvestCheck/なぎ払い判定をPlayerへの注入で再現する。
 */
@Mixin(Player.class)
public abstract class PlayerMixin {

    // PlayerEvent.BreakSpeedに相当
    @Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
    private void ts_multitools$onBreakSpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
        Float speed = ModServerEventsCommon.onBreakSpeed((Player) (Object) this);
        if (speed != null) cir.setReturnValue(speed);
    }

    // PlayerEvent.HarvestCheckに相当
    @Inject(method = "hasCorrectToolForDrops", at = @At("RETURN"), cancellable = true)
    private void ts_multitools$onHarvestCheck(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        Boolean canHarvest = ModServerEventsCommon.onHarvestCheck((Player) (Object) this, state);
        if (canHarvest != null) cir.setReturnValue(canHarvest);
    }

    // メインハンドがSlimeItem/MultitoolItemの場合も剣扱いにする
    @Definition(id = "SwordItem", type = SwordItem.class)
    @Expression("? instanceof SwordItem")
    @ModifyExpressionValue(
            method = "attack",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean ts_multitools$treatMultiToolAsSwordForSweep(boolean original, Entity target) {
        if (original) return true;
        Player self = (Player) (Object) this;
        ItemStack stack = self.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.getItem() instanceof SlimeItem) {
            // 経験値0のSlimeは素手相当として扱うため、なぎ払いも不可にする
            return !ModServerEventsCommon.shouldSuppressSweepAttack(self);
        }
        return stack.getItem() instanceof MultitoolItem;
    }
}
