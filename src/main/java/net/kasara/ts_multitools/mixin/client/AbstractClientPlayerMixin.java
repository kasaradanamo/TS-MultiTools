package net.kasara.ts_multitools.mixin.client;

import net.kasara.ts_multitools.item.ModItems;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * SLIMEを弓と同じFovを適応させる
 */
@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {

    @Inject(
            method = "getFieldOfViewModifier",
            at = @At("RETURN"),
            cancellable = true
    )
    private void injectSlimeFov(boolean firstPerson, float effectScale, CallbackInfoReturnable<Float> cir) {
        AbstractClientPlayer player = (AbstractClientPlayer)(Object)this;
        ItemStack stack = player.getUseItem();

        if (stack.is(ModItems.SLIME)) {
            float originalFov = cir.getReturnValue();
            // 弓の補正と同じ式を適用
            float scale = Math.min(player.getTicksUsingItem() / 20.0F, 1.0F);
            float modifiedFov = originalFov * (1.0F - Mth.square(scale) * 0.15F);
            cir.setReturnValue(modifiedFov);
        }
    }
}
