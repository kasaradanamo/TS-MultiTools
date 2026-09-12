package net.kasara.ts_multitools.fabric.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.kasara.ts_multitools.item.MultitoolItem;
import net.kasara.ts_multitools.item.SlimeItem;
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
 * 経験値0でSlimeItemを持っている間、採掘速度/ドロップ判定を素手相当にする。
 * あわせて、なぎ払い判定でSlimeItem/MultitoolItemを剣扱いにする。
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
     * メインハンドがSlimeItem/MultitoolItemの場合も剣扱いにする。
     * 経験値0のSlimeは素手相当として扱うため、なぎ払いも不可にする。
     */
    @Definition(id = "SwordItem", type = SwordItem.class)
    @Expression("? instanceof SwordItem")
    @ModifyExpressionValue(
            method = "attack",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean tokorotenslime$treatMultiToolAsSwordForSweep(boolean original, Entity target) {
        if (original) return true;
        Player player = (Player) (Object) this;
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.getItem() instanceof SlimeItem) {
            return !isFistModeSlime(player);
        }
        return stack.getItem() instanceof MultitoolItem;
    }

    private static boolean isFistModeSlime(Player player) {
        ItemStack stack = player.getMainHandItem();
        return stack.getItem() == ModItemsCommon.SLIME && player.totalExperience < 1;
    }
}
