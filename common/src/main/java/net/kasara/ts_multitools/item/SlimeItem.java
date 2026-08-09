package net.kasara.ts_multitools.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.kasara.ts_multitools.client.SlimeStateClientHandler;
import net.kasara.ts_multitools.component.SlimeModeComponent;
import net.kasara.ts_multitools.constant.SlimeMode;
import net.kasara.ts_multitools.data.SlimeItemData;
import net.kasara.ts_multitools.entity.SlimeArrowEntity;
import net.kasara.ts_multitools.server.SlimeUseCountManager;
import net.kasara.ts_multitools.server.ToolRightClickHandler;
import net.kasara.ts_multitools.server.data.OffhandTriggerTracker;
import net.kasara.ts_multitools.util.ModTags;
import net.kasara.ts_multitools.util.MultiToolUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Consumer;

import static net.kasara.ts_multitools.util.MultiToolUtil.applyDurability;

/**
 * スライムアイテム
 */
public class SlimeItem extends BowItem {

    protected final net.minecraft.world.item.Tier tier;
    private final Multimap<Attribute, AttributeModifier> attributeModifiers;

    public SlimeItem(net.minecraft.world.item.Tier tier, Properties pros) {
        super(applyDurability(pros
                .stacksTo(1)        // スタック不可
                .fireResistant()    // 耐火
                .rarity(Rarity.EPIC), tier)); // レア度:エピック
        this.tier = tier;

        float attackDamage = 3.0F + tier.getAttackDamageBonus(); // 攻撃力(バニラ剣と同じ基準)
        float attackSpeed = -2.4F;                                // 攻撃速度(バニラ剣と同じ)
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", attackSpeed, AttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    /**
     * アイテムスタックができるときにUUIDを付与
     */
    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        if (!SlimeItemData.hasUuid(stack)) {
            SlimeItemData.setUuid(stack, java.util.UUID.randomUUID());
        }
        return stack;
    }

    // 耐久消費なし: 各ローダー側で実現している

    @Override
    public int getEnchantmentValue() {
        return tier.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return tier.getRepairIngredient().test(repairCandidate) || super.isValidRepairItem(stack, repairCandidate);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? attributeModifiers : super.getDefaultAttributeModifiers(slot);
    }

    /**
     * ブロック右クリック時の挙動。
     * tool → ツールの右クリックを適応して、XP消費/使用回数加算
     * bow → オフハンドの状態(松明やストレージボックス持ち)に応じて処理を制御
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || player.totalExperience < 1) return InteractionResult.PASS; // XP不足なら処理しない

        ItemStack stack = context.getItemInHand();
        SlimeModeComponent mode = SlimeItemData.getMode(stack);

        // toolモードなら右クリックで特殊アクションを処理
        if (SlimeMode.UseMode.TOOL.equals(mode.useMode())) {
            ToolRightClickHandler.ToolAction action = ToolRightClickHandler.handleRightClick(context);
            if (action != ToolRightClickHandler.ToolAction.NONE) {
                if (player.level().isClientSide()) {
                    // クライアント側に状態反映
                    SlimeStateClientHandler.applyStateOnBlockUse(stack, action);
                }
                SlimeUseCountManager.increment(player);    // 使用回数を加算
                shouldConsumeXP(player);                // XP消費判定
            }
            return action != ToolRightClickHandler.ToolAction.NONE ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }

        // オフハンドの制御(たいまつやストレージ系アイテムなら制限)
        ItemStack offHandStack = player.getOffhandItem();
        String id = BuiltInRegistries.ITEM.getKey(offHandStack.getItem()).toString();
        boolean forbiddenOffhand = id.contains("torch") || id.contains("storagebox");

        // ブロックを狙っていて禁止オフハンドならフラグセット
        boolean targetingBlock = !context.getLevel().getBlockState(context.getClickedPos()).isAir();
        if (targetingBlock && forbiddenOffhand) {
            OffhandTriggerTracker.set(player, true);
            return InteractionResult.PASS;
        }

        OffhandTriggerTracker.clear(player);
        return InteractionResult.PASS;
    }

    /**
     * アイテム使用開始時の処理(右クリック押しっぱなし)
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.totalExperience < 1) return InteractionResultHolder.fail(stack); // XP不足なら発射不可

        SlimeModeComponent mode = SlimeItemData.getMode(stack);

        // toolモード時は弓動作を無効化
        if (SlimeMode.UseMode.TOOL.equals(mode.useMode())) return InteractionResultHolder.fail(stack);

        // オフハンドのフラグが立っていた場合はキャンセル
        if (OffhandTriggerTracker.get(player)) {
            OffhandTriggerTracker.clear(player);
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);  // 弓を引く動作開始
        return InteractionResultHolder.consume(stack);
    }

    /**
     * 弓の使用終了(離した瞬間)の処理
     */
    @Override
    public void releaseUsing(final ItemStack itemStack, final Level level, final LivingEntity entity, final int remainingTime) {
        if (!(entity instanceof Player player)) return;

        // 引いた時間から弓のチャージ進行度を計算
        int useTime = this.getUseDuration(itemStack) - remainingTime;
        float pullProgress = getPowerForTime(useTime);
        if (pullProgress < 0.1F) return;   // 引き不足なら発射しない

        if (!level.isClientSide()) {
            SlimeArrowEntity arrow = new SlimeArrowEntity(level, player);
            arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, pullProgress * 3.0F, 1.0F);
            if (pullProgress == 1.0F) {
                arrow.setCritArrow(true);
            }

            // 弓エンチャント(射撃ダメージ増加/ノックバック増加/フレイム)を矢に反映
            int power = net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(
                    net.minecraft.world.item.enchantment.Enchantments.POWER_ARROWS, itemStack);
            if (power > 0) {
                arrow.setBaseDamage(arrow.getBaseDamage() + power * 0.5D + 0.5D);
            }
            int punch = net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(
                    net.minecraft.world.item.enchantment.Enchantments.PUNCH_ARROWS, itemStack);
            if (punch > 0) {
                arrow.setKnockback(punch);
            }
            if (net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(
                    net.minecraft.world.item.enchantment.Enchantments.FLAMING_ARROWS, itemStack) > 0) {
                arrow.setSecondsOnFire(100);
            }

            level.addFreshEntity(arrow);
        }

        // 発射音を再生
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                net.minecraft.sounds.SoundEvents.ARROW_SHOOT, net.minecraft.sounds.SoundSource.PLAYERS,
                1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + pullProgress * 0.5F);

        SlimeUseCountManager.increment(player);        // 使用回数加算
        shouldConsumeXP(player);                       // XP消費判定
        player.awardStat(Stats.ITEM_USED.get(this));   // 使用統計更新
    }

    /**
     * ブロック破壊後の処理
     */
    @Override
    public boolean mineBlock(ItemStack itemStack, Level level, BlockState state, net.minecraft.core.BlockPos pos, LivingEntity owner) {
        if (!(owner instanceof Player player)) return false;
        if (player.totalExperience < 1) return true; // XP不足時は素手扱いなので使用回数/XP消費に影響しない
        if (state.getDestroySpeed(level, pos) != 0.0F) { // 松明等の瞬間破壊ブロックは耐久同様XPも消費しない
            SlimeUseCountManager.increment(player); // 使用回数加算
            shouldConsumeXP(player);                // XP消費判定
        }
        return super.mineBlock(itemStack, level, state, pos, owner);
    }

    /**
     * 攻撃ヒット後の処理
     */
    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity mob, LivingEntity attacker) {
        if (attacker instanceof Player player && player.totalExperience >= 1) {
            SlimeUseCountManager.increment(player);    // 使用回数加算
            shouldConsumeXP(player);                   // XP消費判定
        }
        return true;
    }

    /**
     * 採掘速度のカスタマイズ。
     * - マルチツールで採掘可能なブロック + スライム専用ブロックタグは素材本来の速度
     * - ガラス系ブロックは掘るのが少し早い。
     */
    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState state) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (id.getPath().contains("glass")) {
            return 1.5F;    // ガラス系は少し早めに
        }
        return MultiToolUtil.getDestroySpeed(tier, ModTags.Blocks.SLIME_MINEABLE, state);
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState state) {
        return MultiToolUtil.isCorrectToolForDrops(tier, ModTags.Blocks.SLIME_MINEABLE, state);
    }

    /**
     * ツールチップの追加表示。
     * 使用回数
     * マイニングモード(fortune/silk_touch)
     * 使用モード(bow/tool)
     */
    @Override
    public void appendHoverText(ItemStack itemStack, Level level, List<Component> tooltip, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, tooltip, tooltipFlag);

        // 使用回数の表示
        tooltip.add(
                Component.translatable("tooltip.tokorotenslime.proficiency", net.kasara.ts_multitools.client.data.SlimeUseCountClientCache.getSlimeUseCount())
        );

        // モード情報の表示
        SlimeModeComponent mode = SlimeItemData.getMode(itemStack);

        // マイニングモード表示
        Component miningText = Component.translatable("mode.tokorotenslime.mining." + mode.miningMode());
        tooltip.add(
                Component.translatable("tooltip.tokorotenslime.mining_mode", miningText)
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA))
        );

        // 使用モード表示
        Component modeText = Component.translatable("mode.tokorotenslime.use." + mode.useMode());
        tooltip.add(
                Component.translatable("tooltip.tokorotenslime.use_mode", modeText)
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD))
        );
    }

    /**
     * 使用回数に応じて一定確率で XP を消費する
     * 使用回数が増えるほど XP 消費確率が減少(最小20%)
     */
    protected void shouldConsumeXP(Player player) {
        if (player.level().isClientSide()) return; // クライアント側なら何もしない

        int count = SlimeUseCountManager.get(player);                       // 使用回数を取得
        double probability = Math.max(1.0 - (count / 50000.0) * 0.8, 0.2);  // 使用回数に応じたXP消費率

        boolean consume = player.level().getRandom().nextDouble() < probability;

        if (consume) player.giveExperiencePoints(-1);                     // 一定確率で経験値消費
    }
}
