package net.kasara.ts_multitools.mixin;

import net.kasara.ts_multitools.item.SlimeItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

/**
 * SlimeItem専用の耐久無効化
 */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract Item getItem();

    /**
     * SlimeItemの耐久消費を無効化
     * hurtAndBreakメソッド実行前にチェックし、SlimeItemならキャンセルする
     */
    @Inject(
            method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Consumer;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventDurabilityForSlime(int amount, ServerLevel level, ServerPlayer player, Consumer<Item> onBreak, CallbackInfo ci) {
        if (getItem() instanceof SlimeItem) {
            ci.cancel(); // SlimeItem の耐久消費を完全に無効化
        }
    }
}



