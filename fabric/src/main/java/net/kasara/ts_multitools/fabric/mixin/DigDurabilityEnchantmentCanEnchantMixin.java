package net.kasara.ts_multitools.fabric.mixin;

import net.kasara.ts_multitools.item.MultitoolItem;
import net.kasara.ts_multitools.item.SlimeItem;
import net.kasara.ts_multitools.util.MultiToolUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * DigDurabilityEnchantment(耐久力)はcanEnchantを独自overrideしているため個別対応する。
 */
@Mixin(DigDurabilityEnchantment.class)
public abstract class DigDurabilityEnchantmentCanEnchantMixin {

    @Inject(method = "canEnchant", at = @At("RETURN"), cancellable = true)
    private void ts_multitools$applyMultiToolRules(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Enchantment self = (Enchantment) (Object) this;
        Item item = stack.getItem();
        if (item instanceof SlimeItem) {
            cir.setReturnValue(MultiToolUtil.canSlimeApplyAtEnchantingTable(self, cir.getReturnValueZ()));
        } else if (item instanceof MultitoolItem) {
            cir.setReturnValue(MultiToolUtil.canMultitoolApplyAtEnchantingTable(self, cir.getReturnValueZ()));
        }
    }
}
