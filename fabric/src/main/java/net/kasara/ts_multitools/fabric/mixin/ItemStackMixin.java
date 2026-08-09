package net.kasara.ts_multitools.fabric.mixin;

import net.kasara.ts_multitools.item.SlimeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

/**
 * SlimeItemの耐久消費を無効化する。
 */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract Item getItem();

    @Inject(
            method = "hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private <T extends net.minecraft.world.entity.LivingEntity> void ts_multitools$preventSlimeDurability(
            int amount, T entity, Consumer<T> onBroken, CallbackInfo ci) {
        if (getItem() instanceof SlimeItem) {
            ci.cancel();
        }
    }
}
