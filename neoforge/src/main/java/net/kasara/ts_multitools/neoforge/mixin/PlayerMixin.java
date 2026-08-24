package net.kasara.ts_multitools.neoforge.mixin;

import net.kasara.ts_multitools.item.ModItemsCommon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 風エフェクト/追加ダメージを素手扱いの間は無効化する。
 */
@Mixin(Player.class)
public abstract class PlayerMixin {

    @Inject(method = "isSweepAttack", at = @At("RETURN"), cancellable = true)
    private void tokorotenslime$fistModeNoSweep(boolean fullStrengthAttack, boolean criticalAttack, boolean knockbackAttack, CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() == ModItemsCommon.SLIME && player.totalExperience < 1) {
            cir.setReturnValue(false);
        }
    }
}
