package net.kasara.ts_multitools.server.handler;

import com.google.common.collect.BiMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.block.enums.ChestType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * プレイヤーがツールで右クリックしたときの処理をまとめたクラス
 *
 * シャベル、クワ、斧それぞれの右クリック挙動を管理。
 * 道作り、耕地化、樹皮剥ぎ、錆取り、ワックス剥ぎなどを処理する。
 */
public class ToolRightClickHandler {

    // シャベルで道を作るブロック変換マップ
    private static final Map<Block, BlockState> PATH_STATES = Map.ofEntries(
            Map.entry(Blocks.GRASS_BLOCK, Blocks.DIRT_PATH.getDefaultState()),
            Map.entry(Blocks.DIRT, Blocks.DIRT_PATH.getDefaultState()),
            Map.entry(Blocks.PODZOL, Blocks.DIRT_PATH.getDefaultState()),
            Map.entry(Blocks.COARSE_DIRT, Blocks.DIRT_PATH.getDefaultState()),
            Map.entry(Blocks.MYCELIUM, Blocks.DIRT_PATH.getDefaultState()),
            Map.entry(Blocks.ROOTED_DIRT, Blocks.DIRT_PATH.getDefaultState())
    );

    // クワで耕地化するブロックの変換処理（Predicateで条件判定、Consumerで変換処理）
    private static final Map<Block, Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>>> TILLING_ACTIONS = Map.of(
            Blocks.GRASS_BLOCK, Pair.of(ToolRightClickHandler::canTillFarmland, createTillAction(Blocks.FARMLAND.getDefaultState())),
            Blocks.DIRT_PATH, Pair.of(ToolRightClickHandler::canTillFarmland, createTillAction(Blocks.FARMLAND.getDefaultState())),
            Blocks.DIRT, Pair.of(ToolRightClickHandler::canTillFarmland, createTillAction(Blocks.FARMLAND.getDefaultState())),
            Blocks.COARSE_DIRT, Pair.of(ToolRightClickHandler::canTillFarmland, createTillAction(Blocks.DIRT.getDefaultState())),
            Blocks.ROOTED_DIRT, Pair.of(ctx -> true, createTillAndDropAction(Blocks.DIRT.getDefaultState(), Items.HANGING_ROOTS))
    );

    // 斧で皮剥ぎするブロックの変換マップ
    private static final Map<Block, Block> STRIPPED_BLOCKS = Map.ofEntries(
            Map.entry(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD),
            Map.entry(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG),
            Map.entry(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD),
            Map.entry(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG),
            Map.entry(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD),
            Map.entry(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG),
            Map.entry(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD),
            Map.entry(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG),
            Map.entry(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD),
            Map.entry(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG),
            Map.entry(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD),
            Map.entry(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG),
            Map.entry(Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_WOOD),
            Map.entry(Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG),
            Map.entry(Blocks.PALE_OAK_WOOD, Blocks.STRIPPED_PALE_OAK_WOOD),
            Map.entry(Blocks.PALE_OAK_LOG, Blocks.STRIPPED_PALE_OAK_LOG),
            Map.entry(Blocks.CHERRY_WOOD, Blocks.STRIPPED_CHERRY_WOOD),
            Map.entry(Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG),
            Map.entry(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM),
            Map.entry(Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE),
            Map.entry(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM),
            Map.entry(Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE),
            Map.entry(Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK)
    );

    // 右クリック処理の結果としてのアクション種別
    public enum ToolAction {SHOVEL, HOE, AXE, NONE}

    /**
     * ツールの右クリック処理の統合エントリーポイント
     * プレイヤーの状態とブロックを確認して、適切な処理を実行
     */
    public static ToolAction handleRightClick(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();

        // スニーク状態でクワ処理を優先
        if (player != null && player.isSneaking()) {
            if (tryHoeAction(context)) return ToolAction.HOE;
        }

        if (tryShovelAction(context)) return ToolAction.SHOVEL;
        if (tryAxeAction(context)) return ToolAction.AXE;

        return ToolAction.NONE;
    }

    // シャベルの右クリック処理（道ブロック化や焚き火消火）
    private static boolean tryShovelAction(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (context.getSide() == Direction.DOWN) return false;  // 下向きクリックは無効

        BlockState flattened = PATH_STATES.get(state.getBlock());
        BlockState newState = null;

        if (flattened != null && world.getBlockState(pos.up()).isAir()) {
            // 道ブロック化
            world.playSound(context.getPlayer(), pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
            newState = flattened;
        } else if (state.getBlock() instanceof CampfireBlock && state.get(CampfireBlock.LIT)) {
            // 焚き火消火
            if (!world.isClient()) {
                world.syncWorldEvent(null, WorldEvents.FIRE_EXTINGUISHED, pos, 0);
            }
            CampfireBlock.extinguish(context.getPlayer(), world, pos, state);
            newState = state.with(CampfireBlock.LIT, false);
        }

        // 新しい状態がある場合はブロックを更新
        if (newState != null) {
            if (!world.isClient()) {
                world.setBlockState(pos, newState, Block.NOTIFY_ALL_AND_REDRAW);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(context.getPlayer(), newState));
                damageTool(context);
            }
            return true;
        }
        return false;
    }

    // クワの右クリック処理（耕地化）
    private static boolean tryHoeAction(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        var pair = TILLING_ACTIONS.get(world.getBlockState(pos).getBlock());
        if (pair == null) return false;

        if (pair.getFirst().test(context)) {
            world.playSound(context.getPlayer(), pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!world.isClient()) {
                pair.getSecond().accept(context); // ブロック変換
                damageTool(context);
            }
            return true;
        }
        return false;
    }

    // 斧の右クリック処理（樹皮剥ぎ、錆取り、ワックス剥ぎ）
    private static boolean tryAxeAction(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity playerEntity = context.getPlayer();
        ItemStack itemStack = context.getStack();

        Optional<BlockState> newStateOpt = Optional.empty();

        if (context.getHand() == Hand.MAIN_HAND && Objects.requireNonNull(playerEntity).getOffHandStack().contains(DataComponentTypes.BLOCKS_ATTACKS) && !playerEntity.shouldCancelInteraction()) {
            return false; // アクションをキャンセル
        }

        // 1. 木材の皮剥ぎ
        Block stripped = STRIPPED_BLOCKS.get(state.getBlock());
        if (stripped != null) {
            newStateOpt = Optional.ofNullable(stripped.getDefaultState().with(PillarBlock.AXIS, state.get(PillarBlock.AXIS)));
            world.playSound(context.getPlayer(), pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        // 2. 錆ブロックの酸化度を下げる
        else {
            Optional<BlockState> oxState = Oxidizable.getDecreasedOxidationState(state);
            if (oxState.isPresent()) {
                newStateOpt = oxState;
                world.playSound(context.getPlayer(), pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.syncWorldEvent(context.getPlayer(), WorldEvents.BLOCK_SCRAPED, pos, 0);
            }
            // 3. ワックス付き銅ブロックのワックス剥ぎ
            else {
                Optional<BlockState> waxedState = Optional.ofNullable(
                        (Block)((BiMap<?, ?>)HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get()).get(state.getBlock())
                ).map(block -> block.getStateWithProperties(state));

                if (waxedState.isPresent()) {
                    newStateOpt = waxedState;
                    world.playSound(context.getPlayer(), pos, SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.syncWorldEvent(context.getPlayer(), WorldEvents.WAX_REMOVED, pos, 0);
                }
            }
        }

        // ブロック状態更新
        if (newStateOpt.isPresent()) {
            BlockState newState = newStateOpt.get();
            if (!world.isClient()) {
                if (playerEntity instanceof ServerPlayerEntity) {
                    Criteria.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity)playerEntity, pos, itemStack);
                }
                world.setBlockState(pos, newState, Block.NOTIFY_ALL_AND_REDRAW);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(context.getPlayer(), newState));

                // チェスト隣接処理
                if (state.getBlock() instanceof ChestBlock && state.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
                    BlockPos otherPos = ChestBlock.getPosInFrontOf(pos, state);
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, otherPos, GameEvent.Emitter.of(playerEntity, world.getBlockState(otherPos)));
                    world.syncWorldEvent(playerEntity, WorldEvents.BLOCK_SCRAPED, otherPos, 0);
                }

                damageTool(context);
            }
            return true;
        }
        return false;
    }

    // ツール耐久値を1減らす
    private static void damageTool(ItemUsageContext context) {
        ItemStack stack = context.getStack();
        PlayerEntity player = context.getPlayer();
        if (player != null) {
            stack.damage(1, player, context.getHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }
    }

    // クワ用判定: 上のブロックが空気なら耕せる
    private static boolean canTillFarmland(ItemUsageContext context) {
        return context.getSide() != Direction.DOWN &&
                context.getWorld().getBlockState(context.getBlockPos().up()).isAir();
    }

    // ブロックを指定状態に変換するConsumer生成
    private static Consumer<ItemUsageContext> createTillAction(BlockState result) {
        return context -> {
            context.getWorld().setBlockState(context.getBlockPos(), result, Block.NOTIFY_ALL_AND_REDRAW);
            context.getWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, context.getBlockPos(),
                    GameEvent.Emitter.of(context.getPlayer(), result));
        };
    }

    // ブロックを指定状態に変換し、ドロップを生成するConsumer生成
    private static Consumer<ItemUsageContext> createTillAndDropAction(BlockState result, ItemConvertible drop) {
        return context -> {
            context.getWorld().setBlockState(context.getBlockPos(), result, Block.NOTIFY_ALL_AND_REDRAW);
            context.getWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, context.getBlockPos(),
                    GameEvent.Emitter.of(context.getPlayer(), result));
            Block.dropStack(context.getWorld(), context.getBlockPos(), context.getSide(), new ItemStack(drop));
        };
    }
}
