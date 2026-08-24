package net.kasara.ts_multitools.neoforge.item;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ToolMaterial;
import net.neoforged.neoforge.common.ItemAbility;

public class MultitoolItem extends net.kasara.ts_multitools.item.MultitoolItem {

    public MultitoolItem(ToolMaterial material, Properties pros) {
        super(material, pros);
    }

    @Override
    public boolean canPerformAction(ItemInstance stack, ItemAbility itemAbility) {
        return ModItemAbilities.MULTITOOL.contains(itemAbility);
    }
}
