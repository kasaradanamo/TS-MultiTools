package net.kasara.ts_multitools.neoforge.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.neoforged.neoforge.common.ItemAbility;

public class MultitoolItem extends net.kasara.ts_multitools.item.MultitoolItem {

    public MultitoolItem(Tier tier, Properties pros) {
        super(tier, pros);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return ModItemAbilities.MULTITOOL.contains(itemAbility);
    }
}
