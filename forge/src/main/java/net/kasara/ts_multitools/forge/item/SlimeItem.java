package net.kasara.ts_multitools.forge.item;

import net.kasara.ts_multitools.util.MultiToolUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class SlimeItem extends net.kasara.ts_multitools.item.SlimeItem {

    public SlimeItem(Tier tier, Properties pros) {
        super(tier, pros);
    }

    /**
     * 耐久消費なし。
     */
    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<T> onBroken) {
        return 0;
    }

    /**
     * 斧/シャベル/クワの右クリック動作(樹皮剥ぎ・道化・耕地化等)を有効化する
     */
    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction action) {
        return canPerformMultiToolAction(action);
    }

    /**
     * エンチャント可能かどうかを判定(採掘・近接・弓系エンチャントすべて許可、
     * Unbreaking(耐久)・Mending(修繕)・Infinity(無限)はブラックリストで除外)
     */
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return MultiToolUtil.canSlimeApplyAtEnchantingTable(enchantment, super.canApplyAtEnchantingTable(stack, enchantment));
    }

    static boolean canPerformMultiToolAction(ToolAction action) {
        return ToolActions.DEFAULT_AXE_ACTIONS.contains(action)
                || ToolActions.DEFAULT_SHOVEL_ACTIONS.contains(action)
                || ToolActions.DEFAULT_HOE_ACTIONS.contains(action)
                // SWORD_SWEEPを許可しないと薙ぎ払いエフェクト・範囲ダメージが発動しない
                || ToolActions.DEFAULT_SWORD_ACTIONS.contains(action);
    }
}
