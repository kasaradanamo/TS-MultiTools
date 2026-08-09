package net.kasara.ts_multitools.client;

import net.kasara.ts_multitools.constant.SlimeState;
import net.kasara.ts_multitools.data.SlimeItemData;
import net.kasara.ts_multitools.network.packet.c2s.SlimeStateC2SPacket;
import net.kasara.ts_multitools.server.ToolRightClickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * クライアント側でスライムアイテムの見た目(state)を管理するクラス
 */
public class SlimeStateClientHandler {

    // UUIDごとの一時的なstate維持タイマー(tick数)
    private static final Map<UUID, Integer> slimeTimers = new HashMap<>();

    // 採掘中のstateを一時保存(ピッケル・斧など)
    private static final Map<UUID, String> slimeStateDuringMining = new HashMap<>();

    // 前回送信したstate(メイン・オフハンド別)
    private static final Map<UUID, String> lastSentMain = new HashMap<>();
    private static final Map<UUID, String> lastSentOff = new HashMap<>();

    /**
     * ブロック攻撃時に呼ばれる
     */
    public static void onAttackBlock(Player player, Level level, InteractionHand hand, BlockPos pos) {
        ItemStack stack = player.getItemInHand(hand);

        UUID uuid = SlimeItemData.getUuid(stack);
        if (uuid == null) return;

        // ブロックに応じたツール種別を取得してstate更新(経験値0の間は素手扱いなので見た目も変えない)
        String state = player.totalExperience >= 1 ? getStateFromBlock(level.getBlockState(pos)) : SlimeState.SLIME;
        slimeStateDuringMining.put(uuid, state);

        // 採掘中はタイマーをリセット
        slimeTimers.remove(uuid);

        SlimeItemData.setState(stack, state);
    }

    /**
     * 毎tick呼ばれ、スライムのstateを更新
     * 採掘中かどうか、タイマー残り時間、プレイヤーの経験値を考慮
     */
    public static void updateSlimeState(Player player, ItemStack stack, UUID uuid) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        boolean inHand = uuid.equals(SlimeItemData.getUuid(mainHand)) ||
                uuid.equals(SlimeItemData.getUuid(offHand));

        String state = SlimeState.SLIME;

        if (inHand) {
            // --- 採掘中状態の処理 ---
            if (slimeStateDuringMining.containsKey(uuid)) {
                state = slimeStateDuringMining.get(uuid);

                // 採掘が終わったらタイマー開始
                if (!Minecraft.getInstance().gameMode.isDestroying()) {
                    slimeTimers.put(uuid, 40);  // 約2秒間維持
                    slimeStateDuringMining.remove(uuid);
                }
            }
            // --- 採掘直後(タイマー中) ---
            else if (slimeTimers.containsKey(uuid) &&
                    !SlimeState.SLIME.equals(SlimeItemData.getState(stack)) &&
                    !SlimeState.SWORD.equals(SlimeItemData.getState(stack))) {
                // タイマー中は前のstateを維持
                state = player.totalExperience >= 1 ? SlimeItemData.getState(stack) : SlimeState.SLIME;
                int time = slimeTimers.get(uuid) - 1;
                if (time > 0) slimeTimers.put(uuid, time);
                else slimeTimers.remove(uuid);
            }
            // --- 通常状態 → 経験値に応じて剣かスライム ---
            else {
                state = player.totalExperience >= 1 ? SlimeState.SWORD : SlimeState.SLIME;
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
        SlimeItemData.setState(stack, state);

        // 前回送信したstateと違う場合だけ更新
        if (stack == mainHand && !state.equals(lastSentMain.get(uuid))) {
            lastSentMain.put(uuid, state);
            SlimeStateC2SPacket.send(uuid, state);
        } else if (stack == offHand && !state.equals(lastSentOff.get(uuid))) {
            lastSentOff.put(uuid, state);
            SlimeStateC2SPacket.send(uuid, state);
        }
    }

    /**
     * ブロックのタグから対応するツール種別を取得
     */
    private static String getStateFromBlock(BlockState blockState) {
        if (blockState.is(BlockTags.MINEABLE_WITH_PICKAXE) ||
                BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).getPath().contains("glass")) return SlimeState.PICKAXE;
        else if (blockState.is(BlockTags.MINEABLE_WITH_AXE)) return SlimeState.AXE;
        else if (blockState.is(BlockTags.MINEABLE_WITH_SHOVEL)) return SlimeState.SHOVEL;
        else if (blockState.is(BlockTags.MINEABLE_WITH_HOE)) return SlimeState.HOE;
        return SlimeState.SWORD;
    }

    /**
     * 右クリックによるstate適用。
     * (例:シャベル・斧・クワとして動作)
     */
    public static void applyStateOnBlockUse(ItemStack stack, ToolRightClickHandler.ToolAction action) {
        if (stack == null || action == null) return;

        UUID uuid = SlimeItemData.getUuid(stack);
        if (uuid == null) return;

        String newState = switch (action) {
            case SHOVEL -> SlimeState.SHOVEL;
            case AXE -> SlimeState.AXE;
            case HOE -> SlimeState.HOE;
            default -> SlimeState.SWORD;
        };

        slimeStateDuringMining.put(uuid, newState);
        slimeTimers.remove(uuid);
        SlimeItemData.setState(stack, newState);
    }
}
