package net.kasara.ts_multitools.fabric.mixin;

import net.kasara.ts_multitools.item.CampfireDouseRule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * ツールモード以外、または経験値0のスライムでは焚き火を消せないようにする。
 */
@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin {

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void tokorotenslime$restrictSlimeDouse(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (CampfireDouseRule.blocksDouse(player, stack, state)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
