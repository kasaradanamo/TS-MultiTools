package net.kasara.ts_multitools.item;

import net.kasara.ts_multitools.util.ModTags;
import net.kasara.ts_multitools.server.ToolRightClickHandler;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;

import static net.kasara.ts_multitools.util.MultiToolUtil.applyMultiToolProperties;

/**
 * マルチツールアイテム
 * 複数のツール（ピッケル/斧/シャベルなど）の機能を持つ
 */
public class MultitoolItem extends Item {

    private final Tier tier;

    public MultitoolItem(Tier tier, Properties pros) {
        super(applyMultiToolProperties(
                tier,
                pros.stacksTo(1),               // スタック不可
                ModTags.Blocks.MULTITOOL_MINEABLE,  // 採掘できるブロックタグ
                3,                                  // 攻撃力 (バニラ剣と同じ)
                -2.4F                               // 攻撃速度（バニラ剣と同じ）
        ));
        this.tier = tier;
    }

    @Override
    public int getEnchantmentValue() {
        return tier.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return tier.getRepairIngredient().test(repair) || super.isValidRepairItem(toRepair, repair);
    }

    /**
     * 攻撃時に耐久を1消費する
     */
    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public void postHurtEnemy(ItemStack itemStack, LivingEntity target, LivingEntity attacker) {
        itemStack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
    }

    /**
     * ブロックに向かって右クリックしたときの処理
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        return ToolRightClickHandler.handleRightClick(context) != ToolRightClickHandler.ToolAction.NONE
                ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
}
