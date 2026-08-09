package net.kasara.ts_multitools.forge.item;

import net.kasara.ts_multitools.util.MultiToolUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.ToolAction;

public class MultitoolItem extends net.kasara.ts_multitools.item.MultitoolItem {

    public MultitoolItem(Tier tier, Properties pros) {
        super(tier, pros);
    }

    /**
     * 斧/シャベル/クワの右クリック動作(樹皮剥ぎ・道化・耕地化等)を有効化する
     */
    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction action) {
        return SlimeItem.canPerformMultiToolAction(action);
    }

    /**
     * エンチャント可能かどうかを判定
     */
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return MultiToolUtil.canMultitoolApplyAtEnchantingTable(enchantment, super.canApplyAtEnchantingTable(stack, enchantment));
    }
}
