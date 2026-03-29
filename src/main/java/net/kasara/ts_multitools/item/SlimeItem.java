package net.kasara.ts_multitools.item;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.kasara.ts_multitools.client.SlimeStateClientHandler;
import net.kasara.ts_multitools.client.data.SlimeUseCountClientCache;
import net.kasara.ts_multitools.component.MiningEnchantLevelComponent;
import net.kasara.ts_multitools.component.ModComponents;
import net.kasara.ts_multitools.component.SlimeModeComponent;
import net.kasara.ts_multitools.entity.SlimeArrowEntity;
import net.kasara.ts_multitools.server.SlimeUseCountManager;
import net.kasara.ts_multitools.server.ToolRightClickHandler;
import net.kasara.ts_multitools.util.ModTags;
import net.kasara.ts_multitools.util.OffhandTriggerTracker;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static net.kasara.ts_multitools.util.MultiToolUtil.applyMultiToolSettings;

/**
 * スライムアイテム
 */
public class SlimeItem  extends BowItem {

    public SlimeItem(ToolMaterial material, Settings settings) {
        super(applyMultiToolSettings(
                material,
                settings
                        .maxCount(1)            // スタック不可
                        .fireproof()            // 耐火
                        .rarity(Rarity.EPIC)    // レア度：エピック
                        .component(ModComponents.SLIME_STATE, "slime")
                        .component(ModComponents.SLIME_MODE, SlimeModeComponent.DEFAULT)
                        .component(ModComponents.MINING_ENCHANT_LEVEL, MiningEnchantLevelComponent.DEFAULT),
                ModTags.Blocks.SLIME_MINEABLE,  // 採掘できるブロックタグ
                3,                              // 攻撃力 (バニラ剣と同じ)
                -2.4F                           // 攻撃速度（バニラ剣と同じ）
        ));
    }

    /**
     * アイテムスタックができるときにUUIDを付与
     */
    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        if (!stack.contains(ModComponents.SLIME_UUID)) {
            stack.set(ModComponents.SLIME_UUID, UUID.randomUUID());
        }
        return stack;
    }

    /**
     * ブロック右クリック時の挙動。
     * tool → ツールの右クリックを適応して、XP消費/使用回数加算
     * bow → オフハンドの状態（松明やストレージボックス持ち）に応じて処理を制御
     */
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!(context.getPlayer() instanceof PlayerEntity player)
                || player.totalExperience < 1) return ActionResult.PASS; // XP不足なら処理しない

        ItemStack stack = context.getStack();
        SlimeModeComponent mode = stack.get(ModComponents.SLIME_MODE);

        // toolモードなら右クリックで特殊アクションを処理(ここでServerPlayerEntityだと動きがつかない)
        if (mode != null && "tool".equals(mode.useMode())) {
            ToolRightClickHandler.ToolAction action = ToolRightClickHandler.handleRightClick(context);
            if (action != ToolRightClickHandler.ToolAction.NONE) {
                if (player.getEntityWorld().isClient())
                    // クライアント側に状態反映
                    SlimeStateClientHandler.applyStateOnBlockUse(stack, action);

                SlimeUseCountManager.increment(player);    // 使用回数を加算
                shouldConsumeXP(player);                // XP消費判定
            }
            return action != ToolRightClickHandler.ToolAction.NONE ? ActionResult.SUCCESS : ActionResult.PASS;
        }

        // オフハンドの制御（たいまつやストレージ系アイテムなら制限）
        ItemStack offHandStack = player.getOffHandStack();
        String id = Registries.ITEM.getId(offHandStack.getItem()).toString();
        boolean forbiddenOffhand = id.contains("torch") || id.contains("storagebox");

        // ブロックを狙っていて禁止オフハンドならフラグセット
        boolean targetingBlock = !context.getWorld().getBlockState(context.getBlockPos()).isAir();
        if (targetingBlock && forbiddenOffhand) {
            OffhandTriggerTracker.set(player, true);
            return ActionResult.PASS;
        }

        OffhandTriggerTracker.clear(player);
        return ActionResult.PASS;
    }

    /**
     * アイテム使用開始時の処理（右クリック押しっぱなし）
     */
    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (user.totalExperience < 1) return ActionResult.FAIL; // XP不足なら発射不可

        ItemStack stack = user.getStackInHand(hand);
        SlimeModeComponent mode = stack.get(ModComponents.SLIME_MODE);

        // toolモード時は弓動作を無効化
        if (mode != null && "tool".equals(mode.useMode())) return ActionResult.FAIL;

        // オフハンドのフラグが立っていた場合はキャンセル
        if (OffhandTriggerTracker.get(user)) {
            OffhandTriggerTracker.clear(user);
            return ActionResult.FAIL;
        }

        user.setCurrentHand(hand);  // 弓を引く動作開始
        return ActionResult.CONSUME;
    }

    /**
     * 弓の使用終了（離した瞬間）の処理
     */
    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return false;

        // 引いた時間から弓のチャージ進行度を計算
        int useTime = this.getMaxUseTime(stack, user) - remainingUseTicks;
        float pullProgress = getPullProgress(useTime);
        if (pullProgress < 0.1) return false;   // 引き不足なら発射しない

        // ダミー矢を作成（実際の矢アイテム不要）
        ItemStack dummyArrow = new ItemStack(Items.ARROW);
        dummyArrow.set(DataComponentTypes.INTANGIBLE_PROJECTILE, Unit.INSTANCE);
        List<ItemStack> projectiles = List.of(dummyArrow);

        // サーバー側で矢を発射
        if (world instanceof ServerWorld serverWorld) {
            this.shootAll(serverWorld, player, player.getActiveHand(), stack, projectiles,
                    pullProgress * 3.0F, 1.0F, pullProgress == 1.0F, null);
        }

        // 発射音を再生
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS,
                1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + pullProgress * 0.5F);

        SlimeUseCountManager.increment(player);                        // 使用回数加算
        shouldConsumeXP(player);                                       // XP消費判定
        player.incrementStat(Stats.USED.getOrCreateStat(this));   // 使用統計更新
        return true;
    }

    /**
     * カスタム矢エンティティを生成
     */
    @Override
    protected ProjectileEntity createArrowEntity(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
        SlimeArrowEntity arrow = new SlimeArrowEntity(world, shooter, projectileStack, weaponStack);
        arrow.setCritical(critical);    // フルチャージならクリティカル
        return arrow;
    }

    /**
     * ブロック破壊後の処理
     */
    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!(miner instanceof PlayerEntity player)) return false;
        SlimeUseCountManager.increment(player);    // 使用回数加算
        shouldConsumeXP(player);                // XP消費判定
        return super.postMine(stack, world, state, pos, miner);
    }

    /**
     * 攻撃ヒット後の処理
     */
    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player) {
            SlimeUseCountManager.increment(player);    // 使用回数加算
            shouldConsumeXP(player);                   // XP消費判定
            super.postHit(stack, target, attacker);
        }
    }

    /**
     * 採掘速度のカスタマイズ。
     * - ガラス系ブロックは掘るのが少し早い。
     */
    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        Identifier id = Registries.BLOCK.getId(state.getBlock());
        if (id.getPath().contains("glass")) {
            return 1.5F;    // ガラス系は少し早めに
        }
        return super.getMiningSpeed(stack, state);
    }

    /**
     * エンチャント可能かどうかを判定
     * Unbreaking(耐久), Mending(修繕), Infinity(無限) は除外
     */
    @Override
    public boolean canBeEnchantedWith(ItemStack stack, RegistryEntry<Enchantment> enchantment, EnchantingContext context) {
        if (!super.canBeEnchantedWith(stack, enchantment, context)) return false;

        return !enchantment.matchesKey(Enchantments.UNBREAKING) &&
                !enchantment.matchesKey(Enchantments.MENDING) &&
                !enchantment.matchesKey((Enchantments.INFINITY));
    }

    /**
     * ツールチップの追加表示。
     * 使用回数
     * マイニングモード（fortune/silk_touch）
     * 使用モード（bow/tool）
     */
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);

        // 使用回数の表示
        textConsumer.accept(
                Text.translatable("tooltip.tokorotenslime.proficiency", SlimeUseCountClientCache.getSlimeUseCount())
        );

        // モード情報の表示
        SlimeModeComponent mode = stack.get(ModComponents.SLIME_MODE);
        if (mode != null) {
            // マイニングモード表示
            Text miningText = Text.translatable("mode.tokorotenslime.mining." + mode.miningMode());
            textConsumer.accept(
                    Text.translatable("tooltip.tokorotenslime.mining_mode", miningText)
                            .setStyle(Style.EMPTY.withColor(Formatting.AQUA))
            );

            // 使用モード表示
            Text modeText = Text.translatable("mode.tokorotenslime.use." + mode.useMode());
            textConsumer.accept(
                    Text.translatable("tooltip.tokorotenslime.use_mode", modeText)
                            .setStyle(Style.EMPTY.withColor(Formatting.GOLD))
            );
        }
    }

    /**
     * 使用回数に応じて一定確率で XP を消費する。
     * - 使用回数が増えるほど XP 消費確率が減少（最小20%）。
     */
    private void shouldConsumeXP(PlayerEntity player) {
        if (player.getEntityWorld().isClient()) return; // クライアント側なら何もしない

        int count = SlimeUseCountManager.get(player);                       // 使用回数を取得
        double probability = Math.max(1.0 - (count / 50000.0) * 0.8, 0.2);  // 使用回数に応じたXP消費率

        boolean consume = player.getEntityWorld().random.nextDouble() < probability;

        if(consume) player.addExperience(-1);                               // 一定確率で経験値消費
    }
}