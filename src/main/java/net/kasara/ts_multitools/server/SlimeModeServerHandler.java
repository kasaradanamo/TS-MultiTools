package net.kasara.ts_multitools.server;

import net.kasara.ts_multitools.component.ModComponents;
import net.kasara.ts_multitools.component.SlimeModeComponent;
import net.kasara.ts_multitools.constant.SlimeMode;
import net.kasara.ts_multitools.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.UUID;

/**
 * サーバー側でSlimeItemのモード切替処理をする
 */
public class SlimeModeServerHandler {

    /**
     * SlimeItemの使用モード（弓/ツール）を切り替え
     */
    public static void handleUseMode(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        SlimeModeComponent comp = stack.get(ModComponents.SLIME_MODE.get());
        if (comp == null) return;

        // 現在のモードを反転
        String newUseMode = comp.useMode().equals(SlimeMode.UseMode.BOW) ? SlimeMode.UseMode.TOOL : SlimeMode.UseMode.BOW;

        // モードを更新
        stack.set(ModComponents.SLIME_MODE.get(), comp.withUseMode(newUseMode));

        // プレイヤーにメッセージを表示
        Component modeText = Component.translatable("mode.tokorotenslime.use." + newUseMode).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.translatable("message.tokorotenslime.use_mode", modeText), false);
    }

    /**
     * SlimeItemのマイニングモード（fortune/silk_touch）を切り替え
     */
    public static void handleMiningMode(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        var comp = stack.get(ModComponents.SLIME_MODE.get());
        if (comp == null) return;

        // 現在のマイニングモードを切替
        String newMiningMode = switch (comp.miningMode()) {
            case SlimeMode.MiningMode.FORTUNE -> SlimeMode.MiningMode.SILK_TOUCH;
            case SlimeMode.MiningMode.SILK_TOUCH -> SlimeMode.MiningMode.FORTUNE;
            default -> SlimeMode.MiningMode.DEFAULT;
        };

        // デフォルトならそのまま
        if (newMiningMode.equals(SlimeMode.MiningMode.DEFAULT)) return;

        Holder<Enchantment> fortuneHolder = player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);
        Holder<Enchantment> silkTouchHolder = player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH);

        // 現在のエンチャントを取得
        ItemEnchantments current = stack.getEnchantments();

        int currentFortune = current.getLevel(fortuneHolder);
        int currentSilk = current.getLevel(silkTouchHolder);

        var miningComp = stack.get(ModComponents.MINING_ENCHANT_LEVEL.get());
        if (miningComp == null) return;

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
        stack.set(ModComponents.MINING_ENCHANT_LEVEL.get(), miningComp);

        // エンチャントを再構築（fortune/silk_touchのみ付与）
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(current);
        mutable.removeIf(entry -> entry.equals(fortuneHolder) || entry.equals(silkTouchHolder));
        if (newMiningMode.equals(SlimeMode.MiningMode.FORTUNE) && miningComp.fortuneLevel() > 0) {
            mutable.set(fortuneHolder, miningComp.fortuneLevel());
        } else if (newMiningMode.equals(SlimeMode.MiningMode.SILK_TOUCH) && miningComp.silkTouchLevel() > 0) {
            mutable.set(silkTouchHolder, miningComp.silkTouchLevel());
        }
        stack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());

        // モードを更新
        stack.set(ModComponents.SLIME_MODE.get(), comp.withMiningMode(newMiningMode));

        // プレイヤーに切替結果を通知
        Component miningText = Component.translatable("mode.tokorotenslime.mining." + newMiningMode).setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA));
        player.sendSystemMessage(Component.translatable("message.tokorotenslime.mining_mode", miningText), false);
    }

    /**
     * UUIDで特定したSlimeItemのマイニングモードをインベントリ内で更新
     *
     * @param stackUuid 対象SlimeItemのUUID
     * @param mode 適用するモード ("fortune"/"silk_touch"/"default")
     * @param player 対象プレイヤー
     */
    public static void updateMiningModeForInventory(UUID stackUuid, String mode, ServerPlayer player) {
        for (ItemStack stack : player.getInventory()) {
            if (stack.isEmpty() || stack.getItem() != ModItems.SLIME.get()) continue;

            UUID uuidComp = stack.get(ModComponents.SLIME_UUID.get());
            if (uuidComp == null || !uuidComp.equals(stackUuid)) continue;

            SlimeModeComponent modeComp = stack.get(ModComponents.SLIME_MODE.get());
            if (modeComp == null) continue;

            // マイニングモードを更新
            SlimeModeComponent newComp = modeComp.withMiningMode(mode);
            stack.set(ModComponents.SLIME_MODE.get(), newComp);
        }
    }

    /**
     * SlimeItemのモード切替や更新処理をまとめて呼び出す
     * C2Sパケットで受信した情報に基づき、用途モード・マイニングモード・個別モードを適用
     */
    public static void toggleSlimeModeHandle(UUID stackUuid, String mode, ServerPlayer player) {
        switch (mode) {
            case SlimeMode.Type.USE:
                // 使用モード（弓/ツール）切替
                handleUseMode(player);
                break;
            case SlimeMode.Type.MINING:
                // マイニングモード（fortune/silk_touch）切替
                handleMiningMode(player);
                break;
            case SlimeMode.MiningMode.FORTUNE: case SlimeMode.MiningMode.SILK_TOUCH: case SlimeMode.MiningMode.DEFAULT:
                // 個別スライムのマイニングモード更新
                updateMiningModeForInventory(stackUuid, mode, player);
                break;
        }
    }
}