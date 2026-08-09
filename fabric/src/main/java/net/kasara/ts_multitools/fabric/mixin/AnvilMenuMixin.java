package net.kasara.ts_multitools.fabric.mixin;

import net.kasara.ts_multitools.server.ModServerEventsCommon;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 金床でのブラックリストエンチャント判定を{@link AnvilMenu#createResult()}への注入で再現する。
 */
@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {

    @Inject(method = "createResult", at = @At("HEAD"))
    private void ts_multitools$onAnvilUpdate(CallbackInfo ci) {
        ItemCombinerMenuAccessor accessor = (ItemCombinerMenuAccessor) this;
        Player player = accessor.ts_multitools$getPlayer();
        Container inputSlots = accessor.ts_multitools$getInputSlots();
        ItemStack left = inputSlots.getItem(0);
        ItemStack right = inputSlots.getItem(1);

        // 結果には反映しない(port fidelity)
        boolean hasBlacklisted = ModServerEventsCommon.hasBlacklistedAnvilEnchant(player, left, right);
    }
}
