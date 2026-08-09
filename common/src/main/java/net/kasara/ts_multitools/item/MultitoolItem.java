package net.kasara.ts_multitools.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.kasara.ts_multitools.server.ToolRightClickHandler;
import net.kasara.ts_multitools.util.ModTags;
import net.kasara.ts_multitools.util.MultiToolUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * マルチツールアイテム(ピッケル/斧/シャベルなど複数のツール機能を持つ)。
 */
public class MultitoolItem extends Item {

    protected final Tier tier;
    private final Multimap<Attribute, AttributeModifier> attributeModifiers;

    public MultitoolItem(Tier tier, Properties pros) {
        super(MultiToolUtil.applyDurability(pros.stacksTo(1), tier));
        this.tier = tier;

        float attackDamage = 3.0F + tier.getAttackDamageBonus(); // 攻撃力(バニラ剣と同じ基準)
        float attackSpeed = -2.4F;                                // 攻撃速度(バニラ剣と同じ)

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", attackSpeed, AttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    /**
     * ブロックに向かって右クリックしたときの処理
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        return ToolRightClickHandler.handleRightClick(context) != ToolRightClickHandler.ToolAction.NONE
                ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return MultiToolUtil.getDestroySpeed(tier, ModTags.Blocks.MULTITOOL_MINEABLE, state);
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState state) {
        return MultiToolUtil.isCorrectToolForDrops(tier, ModTags.Blocks.MULTITOOL_MINEABLE, state);
    }

    @Override
    public int getEnchantmentValue() {
        return tier.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return tier.getRepairIngredient().test(repairCandidate) || super.isValidRepairItem(stack, repairCandidate);
    }

    /**
     * ブロック採掘成功時に耐久を1消費する。
     */
    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0.0F) {
            stack.hurtAndBreak(1, entityLiving, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    /**
     * 攻撃ヒット時に耐久を1消費する。
     */
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? attributeModifiers : super.getDefaultAttributeModifiers(slot);
    }
}
