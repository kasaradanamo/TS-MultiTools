package net.kasara.ts_multitools.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kasara.ts_multitools.component.ModComponents;
import net.kasara.ts_multitools.item.ModItems;
import net.kasara.ts_multitools.network.packet.c2s.SlimeStateC2SPacket;
import net.kasara.ts_multitools.server.handler.ToolRightClickHandler;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * クライアント側でスライムアイテムの見た目(state)を管理するクラス。
 * ・ブロックを殴ったときにツール種別に応じてstate変更
 * ・tickごとにstateを監視・更新
 * ・右クリックでstateを変更
 */
@Environment(EnvType.CLIENT)
public class SlimeStateClientHandler {

    /** UUIDごとの一時的なstate維持タイマー（tick数） */
    private static final Map<UUID, Integer> slimeTimers = new HashMap<>();

    /** 採掘中のstateを一時保存（ピッケル・斧など） */
    private static final Map<UUID, String> slimeStateDuringMining = new HashMap<>();

    /** 前回送信したstate（メイン・オフハンド別） */
    private static final Map<UUID, String> lastSentMain = new HashMap<>();
    private static final Map<UUID, String> lastSentOff = new HashMap<>();

    /**
     * ブロック攻撃時に呼ばれる
     * - 採掘対象に応じてSlimeItemのstateを更新
     * - 採掘中はタイマーをリセット
     */
    public static void onAttackBlock(PlayerEntity player, World world, Hand hand, BlockPos pos) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() != ModItems.SLIME) return;

        UUID uuid = stack.get(ModComponents.SLIME_UUID);
        if (uuid == null) return;

        // ブロックに応じたツール種別を取得してstate更新
        String state = getStateFromBlock(world.getBlockState(pos));
        slimeStateDuringMining.put(uuid, state);

        // 採掘中はタイマーをリセット
        slimeTimers.remove(uuid);

        stack.set(ModComponents.SLIME_STATE, state);
    }

    /**
     * 毎tick呼ばれ、スライムのstateを更新
     * - 採掘中かどうか、タイマー残り時間、プレイヤーの経験値を考慮
     */
    public static void updateSlimeStates(MinecraftClient client) {
        PlayerEntity player = client.player;
        if (player == null) return;

        ItemStack mainHand = Objects.requireNonNull(player).getMainHandStack();
        ItemStack offHand = player.getOffHandStack();
        ClientPlayerInteractionManager interactionManager = client.interactionManager;

        for (ItemStack stack : player.getInventory()) {
            if (stack.getItem() != ModItems.SLIME) continue;

            UUID uuid = stack.get(ModComponents.SLIME_UUID);
            if (uuid == null) continue;

            boolean inHand = uuid.equals(mainHand.get(ModComponents.SLIME_UUID)) ||
                             uuid.equals(offHand.get(ModComponents.SLIME_UUID));

            String state = "slime";

            if (inHand) {
                // --- 採掘中状態の処理 ---
                if (slimeStateDuringMining.containsKey(uuid)) {
                    state = slimeStateDuringMining.get(uuid);

                    // 採掘が終わったらタイマー開始
                    if (!Objects.requireNonNull(interactionManager).isBreakingBlock()) {
                        slimeTimers.put(uuid, 40);  // 約2秒間維持
                        slimeStateDuringMining.remove(uuid);
                    }
                }
                // --- 採掘直後（タイマー中） ---
                else if (slimeTimers.containsKey(uuid) &&
                        !"slime".equals(stack.get(ModComponents.SLIME_STATE)) &&
                        !"sword".equals(stack.get(ModComponents.SLIME_STATE))) {
                    // タイマー中は前のstateを維持
                    state = player.totalExperience >= 1 ? stack.getOrDefault(ModComponents.SLIME_STATE, "slime") : "slime";
                    int time = slimeTimers.get(uuid) - 1;
                    if (time > 0) slimeTimers.put(uuid, time);
                    else slimeTimers.remove(uuid);
                }
                // --- 通常状態 → 経験値に応じて剣かスライム ---
                else {
                    state = player.totalExperience >= 1 ? "sword" : "slime";
                    slimeTimers.remove(uuid);
                    slimeStateDuringMining.remove(uuid);
                }
            } else {
                // 手にもっていない場合はリセット
                slimeTimers.remove(uuid);
                slimeStateDuringMining.remove(uuid);
                lastSentMain.remove(uuid);
                lastSentOff.remove(uuid);
            }

            // 実際にstateを反映
            stack.set(ModComponents.SLIME_STATE, state);

            // 前回送信したstateと違う場合だけ更新
            if (stack == mainHand && !Objects.equals(lastSentMain.get(uuid), state)) {
                lastSentMain.put(uuid, state);
                SlimeStateC2SPacket.send(uuid, state);
            } else if (stack == offHand && !Objects.equals(lastSentOff.get(uuid), state)) {
                lastSentOff.put(uuid, state);
                SlimeStateC2SPacket.send(uuid, state);
            }
        }
    }

    /**
     * 右クリックによるstate適用。
     * （例：シャベル・斧・クワとして動作）
     */
    public static void applyStateOnBlockUse(ItemStack stack, ToolRightClickHandler.ToolAction action) {
        if (stack == null || action == null) return;

        UUID uuid = stack.get(ModComponents.SLIME_UUID);
        if (uuid == null) return;

        String newState;
        switch (action) {
            case SHOVEL -> newState = "shovel";
            case AXE -> newState = "axe";
            case HOE -> newState = "hoe";
            default -> newState = "sword";
        }

        slimeStateDuringMining.put(uuid, newState);
        slimeTimers.remove(uuid);
        stack.set(ModComponents.SLIME_STATE, newState);
    }

    /** ブロックのタグから対応するツール種別を取得 */
    private  static String getStateFromBlock(BlockState blockState) {
        if (blockState.isIn(BlockTags.PICKAXE_MINEABLE) ||
                Registries.BLOCK.getId(blockState.getBlock()).getPath().contains("glass")) return "pickaxe";
        else if (blockState.isIn(BlockTags.AXE_MINEABLE)) return "axe";
        else if (blockState.isIn(BlockTags.SHOVEL_MINEABLE)) return "shovel";
        else if (blockState.isIn(BlockTags.HOE_MINEABLE)) return "hoe";
        return "sword";
    }
}


