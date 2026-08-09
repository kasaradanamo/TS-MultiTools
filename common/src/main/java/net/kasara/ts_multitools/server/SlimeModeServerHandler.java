package net.kasara.ts_multitools.server;

import net.kasara.ts_multitools.component.MiningEnchantLevelComponent;
import net.kasara.ts_multitools.component.SlimeModeComponent;
import net.kasara.ts_multitools.constant.SlimeMode;
import net.kasara.ts_multitools.data.SlimeItemData;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Map;
import java.util.UUID;

/**
 * サーバー側でSlimeItemのモード切替処理をする
 */
public class SlimeModeServerHandler {

    /**
     * SlimeItemの使用モード(弓/ツール)を切り替え
     */
    public static void handleUseMode(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        SlimeModeComponent comp = SlimeItemData.getMode(stack);

        // 現在のモードを反転
        String newUseMode = comp.useMode().equals(SlimeMode.UseMode.BOW) ? SlimeMode.UseMode.TOOL : SlimeMode.UseMode.BOW;

        // モードを更新
        SlimeItemData.setMode(stack, comp.withUseMode(newUseMode));

        // プレイヤーにメッセージを表示
        Component modeText = Component.translatable("mode.tokorotenslime.use." + newUseMode).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.translatable("message.tokorotenslime.use_mode", modeText));
    }

    /**
     * SlimeItemのマイニングモード(fortune/silk_touch)を切り替え
     */
    public static void handleMiningMode(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        SlimeModeComponent comp = SlimeItemData.getMode(stack);

        // 現在のマイニングモードを切替
        String newMiningMode = switch (comp.miningMode()) {
            case SlimeMode.MiningMode.FORTUNE -> SlimeMode.MiningMode.SILK_TOUCH;
            case SlimeMode.MiningMode.SILK_TOUCH -> SlimeMode.MiningMode.FORTUNE;
            default -> SlimeMode.MiningMode.DEFAULT;
        };

        // デフォルトならそのまま
        if (newMiningMode.equals(SlimeMode.MiningMode.DEFAULT)) return;

        // 現在のエンチャントを取得
        Map<Enchantment, Integer> current = EnchantmentHelper.getEnchantments(stack);

        int currentFortune = current.getOrDefault(Enchantments.BLOCK_FORTUNE, 0);
        int currentSilk = current.getOrDefault(Enchantments.SILK_TOUCH, 0);

        MiningEnchantLevelComponent miningComp = SlimeItemData.getMiningEnchantLevel(stack);

        int storedFortune = miningComp.fortuneLevel();
        int storedSilk = miningComp.silkTouchLevel();

        // 保存値より高ければ更新
        if (currentFortune > 0 && currentFortune > storedFortune) {
            miningComp = miningComp.withFortune(currentFortune);
        }
        if (currentSilk > 0 && currentSilk > storedSilk) {
            miningComp = miningComp.withSilkTouch(currentSilk);
        }

        // エンチャントレベルコンポーネントを更新
        SlimeItemData.setMiningEnchantLevel(stack, miningComp);

        // エンチャントを再構築(fortune/silk_touchのみ付与)
        current.remove(Enchantments.BLOCK_FORTUNE);
        current.remove(Enchantments.SILK_TOUCH);
        if (newMiningMode.equals(SlimeMode.MiningMode.FORTUNE) && miningComp.fortuneLevel() > 0) {
            current.put(Enchantments.BLOCK_FORTUNE, miningComp.fortuneLevel());
        } else if (newMiningMode.equals(SlimeMode.MiningMode.SILK_TOUCH) && miningComp.silkTouchLevel() > 0) {
            current.put(Enchantments.SILK_TOUCH, miningComp.silkTouchLevel());
        }
        EnchantmentHelper.setEnchantments(current, stack);

        // モードを更新
        SlimeItemData.setMode(stack, comp.withMiningMode(newMiningMode));

        // プレイヤーに切替結果を通知
        Component miningText = Component.translatable("mode.tokorotenslime.mining." + newMiningMode).setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA));
        player.sendSystemMessage(Component.translatable("message.tokorotenslime.mining_mode", miningText));
    }

    /**
     * UUIDで特定したSlimeItemのマイニングモードをインベントリ内で更新
     *
     * @param stackUuid 対象SlimeItemのUUID
     * @param mode      適用するモード ("fortune"/"silk_touch"/"default")
     * @param player    対象プレイヤー
     */
    public static void updateMiningModeForInventory(UUID stackUuid, String mode, ServerPlayer player) {
        net.minecraft.world.entity.player.Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.isEmpty() || stack.getItem() != ModItemsCommon.SLIME) continue;

            UUID uuidComp = SlimeItemData.getUuid(stack);
            if (uuidComp == null || !uuidComp.equals(stackUuid)) continue;

            SlimeModeComponent modeComp = SlimeItemData.getMode(stack);

            // マイニングモードを更新
            SlimeItemData.setMode(stack, modeComp.withMiningMode(mode));
        }
    }

    /**
     * SlimeItemのモード切替や更新処理をまとめて呼び出す
     * C2Sパケットで受信した情報に基づき、用途モード・マイニングモード・個別モードを適用
     */
    public static void toggleSlimeModeHandle(UUID stackUuid, String mode, ServerPlayer player) {
        switch (mode) {
            case SlimeMode.Type.USE:
                // 使用モード(弓/ツール)切替
                handleUseMode(player);
                break;
            case SlimeMode.Type.MINING:
                // マイニングモード(fortune/silk_touch)切替
                handleMiningMode(player);
                break;
            case SlimeMode.MiningMode.FORTUNE, SlimeMode.MiningMode.SILK_TOUCH, SlimeMode.MiningMode.DEFAULT:
                // 個別スライムのマイニングモード更新
                updateMiningModeForInventory(stackUuid, mode, player);
                break;
        }
    }
}
