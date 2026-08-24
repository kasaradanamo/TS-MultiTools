package net.kasara.ts_multitools.fabric.mixin;

import net.kasara.ts_multitools.item.ModItemsCommon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 経験値0でSlimeItemを持っている間、採掘速度/ドロップ判定を素手相当にする。
 */
@Mixin(Player.class)
public abstract class PlayerMixin {

    @Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
    private void tokorotenslime$fistModeDestroySpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
        Player player = (Player) (Object) this;
        if (isFistModeSlime(player)) {
            cir.setReturnValue(1.0F); // 素手基準の採掘速度に固定
        }
    }

    @Inject(method = "hasCorrectToolForDrops", at = @At("RETURN"), cancellable = true)
    private void tokorotenslime$fistModeHasCorrectTool(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;
        if (isFistModeSlime(player) && state.requiresCorrectToolForDrops()) {
            cir.setReturnValue(false); // 素手でも掘れるブロックはそのまま、専用ツール必須ブロックのみ不可に
        }
    }

    /**
     * 風エフェクト/追加ダメージを素手扱いの間は無効化する。
     */
    @Inject(method = "isSweepAttack", at = @At("RETURN"), cancellable = true)
    private void tokorotenslime$fistModeNoSweep(boolean fullStrengthAttack, boolean criticalAttack, boolean knockbackAttack, CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;
        if (isFistModeSlime(player)) {
            cir.setReturnValue(false);
        }
    }

    private static boolean isFistModeSlime(Player player) {
        ItemStack stack = player.getMainHandItem();
        return stack.getItem() == ModItemsCommon.SLIME && player.totalExperience < 1;
    }
}
