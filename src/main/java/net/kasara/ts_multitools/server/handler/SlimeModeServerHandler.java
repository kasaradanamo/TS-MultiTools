package net.kasara.ts_multitools.server.handler;

import net.kasara.ts_multitools.component.ModComponents;
import net.kasara.ts_multitools.component.SlimeModeComponent;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * サーバー側でSlimeItemのモード切替処理を担当するユーティリティクラス
 *
 * 主な役割:
 * - use_mode（弓/ツール）切替
 * - mining_mode（fortune/silk_touch）切替
 * - インベントリ内の特定SlimeItemのマイニングモード更新
 */
public class SlimeModeServerHandler {

    public static final String USE_BOW = "bow";
    public static final String USE_TOOL = "tool";


    /**
     * SlimeItemの使用モード（弓/ツール）を切り替え
     * @param player 対象プレイヤー
     */
    public static void handleUseMode(ServerPlayerEntity player) {
        ItemStack stack = player.getMainHandStack();
        SlimeModeComponent comp = stack.get(ModComponents.SLIME_MODE);
        if (comp == null) return;

        // 現在のモードを反転
        String newUseMode = comp.useMode().equals(USE_BOW) ? USE_TOOL : USE_BOW;

        // モードを更新
        stack.set(ModComponents.SLIME_MODE, comp.withUseMode(newUseMode));

        // プレイヤーにメッセージを表示
        Text modeText = Text.translatable("mode.tokorotenslime.use." + newUseMode).setStyle(Style.EMPTY.withColor(Formatting.GOLD));
        player.sendMessage(Text.translatable("message.tokorotenslime.use_mode", modeText), false);
    }

    /**
     * SlimeItemのマイニングモード（fortune/silk_touch）を切り替え
     * @param player 対象プレイヤー
     */
    public static void handleMiningMode(ServerPlayerEntity player) {
        var stack = player.getMainHandStack();
        var comp = stack.get(ModComponents.SLIME_MODE);
        if (comp == null) return;

        // 現在のマイニングモードを切替
        String newMiningMode = switch (comp.miningMode()) {
            case "fortune" -> "silk_touch";
            case "silk_touch" -> "fortune";
            default -> "default";
        };

        if (newMiningMode.equals("default")) return;

        // サーバーのレジストリからエンチャントを取得
        var registryManager = Objects.requireNonNull(player.getEntityWorld().getServer()).getRegistryManager();
        Optional<Registry<Enchantment>> enchantmentRegistryOpt =
                registryManager.getOptional(RegistryKeys.ENCHANTMENT);
        if (enchantmentRegistryOpt.isEmpty()) return;

        Registry<Enchantment> enchantmentRegistry = enchantmentRegistryOpt.get();

        Optional<RegistryEntry.Reference<Enchantment>> fortuneEntryOpt =
                enchantmentRegistry.getEntry(Enchantments.FORTUNE.getValue());
        Optional<RegistryEntry.Reference<Enchantment>> silkEntryOpt =
                enchantmentRegistry.getEntry(Enchantments.SILK_TOUCH.getValue());
        if (fortuneEntryOpt.isEmpty() || silkEntryOpt.isEmpty()) return;

        RegistryEntry<Enchantment> fortuneEntry = fortuneEntryOpt.get();
        RegistryEntry<Enchantment> silkEntry = silkEntryOpt.get();

        // 現在のエンチャントを取得
        ItemEnchantmentsComponent current = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

        int currentFortune = current.getLevel(fortuneEntry);
        int currentSilk = current.getLevel(silkEntry);

        var miningComp = stack.get(ModComponents.MINING_ENCHANT_LEVEL);
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

        // コンポーネントを更新
        stack.set(ModComponents.MINING_ENCHANT_LEVEL, miningComp);

        // エンチャントを再構築（fortune/silk_touchのみ付与）
        ItemEnchantmentsComponent.Builder enchBuilder = new ItemEnchantmentsComponent.Builder(current);
        enchBuilder.remove(entry -> entry.equals(fortuneEntry) || entry.equals(silkEntry));
        if (newMiningMode.equals("fortune") && miningComp.fortuneLevel() > 0) {
            enchBuilder.add(fortuneEntry, miningComp.fortuneLevel());
        } else if (newMiningMode.equals("silk_touch") && miningComp.silkTouchLevel() > 0) {
            enchBuilder.add(silkEntry, miningComp.silkTouchLevel());
        }
        stack.set(DataComponentTypes.ENCHANTMENTS, enchBuilder.build());

        // モードを更新
        stack.set(ModComponents.SLIME_MODE, comp.withMiningMode(newMiningMode));

        // プレイヤーに切替結果を通知
        Text miningText = Text.translatable("mode.tokorotenslime.mining." + newMiningMode).setStyle(Style.EMPTY.withColor(Formatting.AQUA));
        player.sendMessage(Text.translatable("message.tokorotenslime.mining_mode", miningText), false);
    }

    /**
     * UUIDで特定したSlimeItemのマイニングモードをインベントリ内で更新
     * @param stackUuid 対象SlimeItemのUUID
     * @param mode 適用するモード ("fortune"/"silk_touch"/"default")
     * @param player 対象プレイヤー
     */
    public static void updateMiningModeForInventory(UUID stackUuid, String mode, ServerPlayerEntity player) {
        var inventory = player.getInventory();

        for (int i = 0; i < inventory.size(); i++) {
            var stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;

            var uuidComp = stack.get(ModComponents.SLIME_UUID);
            if (uuidComp == null || !uuidComp.equals(stackUuid)) continue;

            var comp = stack.get(ModComponents.SLIME_MODE);
            if (comp == null) continue;

            // マイニングモードを更新
            SlimeModeComponent newComp = comp.withMiningMode(mode);
            stack.set(ModComponents.SLIME_MODE, newComp);
        }
    }

    /**
     * SlimeItemのモード切替や更新処理をまとめて呼び出す
     * C2Sパケットで受信した情報に基づき、用途モード・マイニングモード・個別モードを適用
     */
    public static void toggleSlimeModeHandle(UUID stackUuid, String mode, ServerPlayerEntity player) {
        switch (mode) {
            case "use_mode":
                // 使用モード（弓/ツール）切替
                SlimeModeServerHandler.handleUseMode(player);
                break;
            case "mining_mode":
                // マイニングモード（fortune/silk_touch）切替
                SlimeModeServerHandler.handleMiningMode(player);
                break;
            case "fortune": case "silk_touch": case "default":
                // 個別スライムのマイニングモード更新
                SlimeModeServerHandler.updateMiningModeForInventory(stackUuid, mode, player);
                break;
        }
    }
}
