package net.kasara.ts_multitools.item;

import net.kasara.ts_multitools.client.data.SlimeUseCountClientCache;
import net.kasara.ts_multitools.client.SlimeStateClientHandler;
import net.kasara.ts_multitools.component.MiningEnchantLevelComponent;
import net.kasara.ts_multitools.component.ModComponentsCommon;
import net.kasara.ts_multitools.component.SlimeModeComponent;
import net.kasara.ts_multitools.constant.SlimeMode;
import net.kasara.ts_multitools.constant.SlimeState;
import net.kasara.ts_multitools.entity.SlimeArrowEntity;
import net.kasara.ts_multitools.server.SlimeUseCountManager;
import net.kasara.ts_multitools.util.ModTags;
import net.kasara.ts_multitools.server.data.OffhandTriggerTracker;
import net.kasara.ts_multitools.server.ToolRightClickHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.UUID;

import static net.kasara.ts_multitools.util.MultiToolUtil.applyMultiToolProperties;

/**
 * スライムアイテム
 */
public class SlimeItem  extends BowItem {

    private final Tier tier;

    public SlimeItem(Tier tier, Properties pros) {
        super(applyMultiToolProperties(
                tier,
                pros
                        .stacksTo(1)        // スタック不可
                        .fireResistant()         // 耐火
                        .rarity(Rarity.EPIC)     // レア度：エピック
                        .component(ModComponentsCommon.SLIME_STATE, SlimeState.SLIME)
                        .component(ModComponentsCommon.SLIME_MODE, SlimeModeComponent.DEFAULT)
                        .component(ModComponentsCommon.MINING_ENCHANT_LEVEL, MiningEnchantLevelComponent.DEFAULT),
                ModTags.Blocks.SLIME_MINEABLE,    // 採掘できるブロックタグ
                3,                                // 攻撃力 (バニラ剣と同じ)
                -2.4F                             // 攻撃速度（バニラ剣と同じ）
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
     * アイテムスタックができるときにUUIDを付与
     */
    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        if (!stack.has(ModComponentsCommon.SLIME_UUID)) {
            stack.set(ModComponentsCommon.SLIME_UUID, UUID.randomUUID());
        }
        return stack;
    }

    /**
     * ブロック右クリック時の挙動。
     * tool → ツールの右クリックを適応して、XP消費/使用回数加算
     * bow → オフハンドの状態（松明やストレージボックス持ち）に応じて処理を制御
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getPlayer() instanceof Player player)
                || player.totalExperience < 1) return InteractionResult.PASS; // XP不足なら処理しない

        ItemStack stack = context.getItemInHand();
        SlimeModeComponent mode = stack.get(ModComponentsCommon.SLIME_MODE);

        // toolモードなら右クリックで特殊アクションを処理(ここでServerPlayerEntityだと動きがつかない)
        if (mode != null && SlimeMode.UseMode.TOOL.equals(mode.useMode())) {
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

        // オフハンドの制御（たいまつやストレージ系アイテムなら制限）
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
     * アイテム使用開始時の処理（右クリック押しっぱなし）
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.totalExperience < 1) return InteractionResultHolder.fail(stack); // XP不足なら発射不可

        SlimeModeComponent mode = stack.get(ModComponentsCommon.SLIME_MODE);

        // toolモード時は弓動作を無効化
        if (mode != null && SlimeMode.UseMode.TOOL.equals(mode.useMode())) return InteractionResultHolder.fail(stack);

        // オフハンドのフラグが立っていた場合はキャンセル
        if (OffhandTriggerTracker.get(player)) {
            OffhandTriggerTracker.clear(player);
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);  // 弓を引く動作開始
        return InteractionResultHolder.consume(stack);
    }

    /**
     * 弓の使用終了（離した瞬間）の処理
     */
    @Override
    public void releaseUsing(final ItemStack itemStack, final Level level, final LivingEntity entity, final int remainingTime) {
        if (!(entity instanceof Player player)) return;

        // 引いた時間から弓のチャージ進行度を計算
        int useTime = this.getUseDuration(itemStack, player) - remainingTime;
        float pullProgress = getPowerForTime(useTime);
        if (pullProgress < 0.1) return;   // 引き不足なら発射しない

        // ダミー矢を作成（実際の矢アイテム不要）
        ItemStack dummyArrow = new ItemStack(Items.ARROW);
        dummyArrow.set(DataComponents.INTANGIBLE_PROJECTILE, Unit.INSTANCE);
        List<ItemStack> projectiles = List.of(dummyArrow);

        // サーバー側で矢を発射
        if (level instanceof ServerLevel serverLevel) {
            this.shoot(serverLevel, player, player.getUsedItemHand(), itemStack, projectiles,
                    pullProgress * 3.0F, 1.0F, pullProgress == 1.0F, null);
        }

        // 発射音を再生
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
                1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + pullProgress * 0.5F);

        SlimeUseCountManager.increment(player);        // 使用回数加算
        shouldConsumeXP(player);                       // XP消費判定
        player.awardStat(Stats.ITEM_USED.get(this));   // 使用統計更新
    }

    /**
     * カスタム矢エンティティを生成
     */
    @Override
    protected Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack projectile, boolean isCrit) {
        SlimeArrowEntity arrow = new SlimeArrowEntity(level, shooter, projectile, weapon);
        arrow.setCritArrow(isCrit); // フルチャージならクリティカル
        return arrow;
    }

    /**
     * ブロック破壊後の処理
     */
    @Override
    public boolean mineBlock(ItemStack itemStack, Level level, BlockState state, BlockPos pos, LivingEntity owner) {
        if (!(owner instanceof Player player)) return false;
        if (player.totalExperience < 1) return true; // XP不足時は素手扱いなので使用回数/XP消費に影響しない
        if (state.getDestroySpeed(level, pos) != 0.0F) { // 松明等の瞬間破壊ブロックは耐久同様XPも消費しない
            SlimeUseCountManager.increment(player); // 使用回数加算
            shouldConsumeXP(player);                // XP消費判定
        }
        return super.mineBlock(itemStack, level, state, pos, owner);
    }

    /**
     * 攻撃ヒット時の処理
     */
    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity mob, LivingEntity attacker) {
        return true;
    }

    /**
     * 攻撃ヒット後の処理
     */
    @Override
    public void postHurtEnemy(ItemStack itemStack, LivingEntity mob, LivingEntity attacker) {
        if (attacker instanceof Player player && player.totalExperience >= 1) {
            SlimeUseCountManager.increment(player);    // 使用回数加算
            shouldConsumeXP(player);                   // XP消費判定
            itemStack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        }
    }

    /**
     * 採掘速度のカスタマイズ。
     * - ガラス系ブロックは掘るのが少し早い。
     */
    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState state) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (id.getPath().contains("glass")) {
            return 1.5F;    // ガラス系は少し早めに
        }
        return super.getDestroySpeed(itemStack, state);
    }

    /**
     * ツールチップの追加表示。
     * 使用回数
     * マイニングモード（fortune/silk_touch）
     * 使用モード（bow/tool）
     */
    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, List<Component> lines, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, lines, tooltipFlag);

        // 使用回数の表示
        lines.add(
                Component.translatable("tooltip.tokorotenslime.proficiency", SlimeUseCountClientCache.getSlimeUseCount())
        );

        // モード情報の表示
        SlimeModeComponent mode = itemStack.get(ModComponentsCommon.SLIME_MODE);
        if (mode != null) {
            // マイニングモード表示
            Component miningText = Component.translatable("mode.tokorotenslime.mining." + mode.miningMode());
            lines.add(
                    Component.translatable("tooltip.tokorotenslime.mining_mode", miningText)
                            .setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA))
            );

            // 使用モード表示
            Component modeText = Component.translatable("mode.tokorotenslime.use." + mode.useMode());
            lines.add(
                    Component.translatable("tooltip.tokorotenslime.use_mode", modeText)
                            .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD))
            );
        }
    }

    /**
     * 使用回数に応じて一定確率で XP を消費する
     * 使用回数が増えるほど XP 消費確率が減少（最小20%）
     */
    private void shouldConsumeXP(Player player) {
        if (player.level().isClientSide()) return; // クライアント側なら何もしない

        int count = SlimeUseCountManager.get(player);                       // 使用回数を取得
        double probability = Math.max(1.0 - (count / 50000.0) * 0.8, 0.2);  // 使用回数に応じたXP消費率

        boolean consume = player.level().getRandom().nextDouble() < probability;

        if(consume) player.giveExperiencePoints(-1);                     // 一定確率で経験値消費
    }
}
