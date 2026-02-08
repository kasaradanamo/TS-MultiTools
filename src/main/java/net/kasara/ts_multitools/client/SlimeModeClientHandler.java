package net.kasara.ts_multitools.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kasara.ts_multitools.component.ModComponents;
import net.kasara.ts_multitools.component.SlimeModeComponent;
import net.kasara.ts_multitools.item.SlimeItem;
import net.kasara.ts_multitools.network.packet.c2s.NewSlimeStackC2SPacket;
import net.kasara.ts_multitools.network.packet.c2s.ToggleSlimeModeC2SPacket;
import net.kasara.ts_multitools.client.option.ModKeyBindings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


/**
 * SlimeItem のクライアント側の動作を制御するヘルパークラス。
 *
 * 主な責務：
 * - キー入力による SlimeItem のモード切替（Ctrl同時押し対応）
 * - インベントリ内の SlimeItem のエンチャントに応じたモード更新
 *
 * このクラスは ModClientEvents から呼び出されることを前提としており、
 * 毎Tickの監視やキー入力検知は ModClientEvents 側で行う。
 */
@Environment(EnvType.CLIENT)
public class SlimeModeClientHandler {

    /** モード切替キーが押下中かどうか（押下判定の連打防止用） */
    private static boolean modeTogglePressed = false;

    /**
     * モード切替キー入力処理
     * Ctrlキー併用で use_mode、それ以外は mining_mode に切り替える。
     */
    public static void handleModeToggle(MinecraftClient client) {
        boolean isPressed = ModKeyBindings.MODE_TOGGLE.isPressed();

        if (isPressed && !modeTogglePressed) {
            modeTogglePressed = true;

            ItemStack stack = Objects.requireNonNull(client.player).getMainHandStack();
            if (stack.getItem() instanceof SlimeItem) {
                UUID stackUuid = stack.get(ModComponents.SLIME_UUID);

                boolean ctrlPressed = GLFW.glfwGetKey(client.getWindow().getHandle(),
                        GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS;

                ToggleSlimeModeC2SPacket.send(stackUuid, ctrlPressed ? "use_mode" : "mining_mode");

            }
        } else if (!isPressed) {
            // キーが離されてらリセット
            modeTogglePressed = false;
        }
    }

    /**
     * インベントリ全体を走査し、各 SlimeItem の状態を確認。
     * - UUID が無ければサーバーに新規生成を要求
     * - 現在のエンチャントに応じて望ましいモードを判定
     * - サーバーにモード変更リクエストを送信
     *
     * この処理は毎Tickではなく、一定間隔ごとに呼び出すことを想定してる
     */
    public static void updateInventoryEnchantments(MinecraftClient client) {
        if(client.player == null) return;
        PlayerInventory inv = client.player.getInventory();

        Set<UUID> seenUUIDs = new HashSet<>();

        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof SlimeItem)) continue;

            UUID stackUuid = stack.get(ModComponents.SLIME_UUID);

            if (stackUuid == null || seenUUIDs.contains(stackUuid)) {
                // 新しいSlimeItem → サーバーにUUID生成を要求
                NewSlimeStackC2SPacket.send();
                continue;
            }

            seenUUIDs.add(stackUuid);

            // エンチャントに応じてモードを設定
            String desiredMode = getMiningModeFromEnchantments(stack);
            SlimeModeComponent mode = stack.getOrDefault(ModComponents.SLIME_MODE, SlimeModeComponent.DEFAULT);

            if (!desiredMode.equals(mode.miningMode())) {
                ToggleSlimeModeC2SPacket.send(stackUuid, desiredMode);
            }
        }
    }

    /**
     * エンチャント状態から望ましい採掘モードを判定する。
     * @return "fortune" | "silk_touch" | "default"
     */
    private static String getMiningModeFromEnchantments(ItemStack stack) {
        var enchantments = stack.getEnchantments();
        if (enchantments == null) return "default";

        boolean hasFortune = enchantments.getEnchantmentEntries().stream()
                .anyMatch(e -> e.getKey().matchesKey(net.minecraft.enchantment.Enchantments.FORTUNE));
        boolean hasSilkTouch = enchantments.getEnchantmentEntries().stream()
                .anyMatch(e -> e.getKey().matchesKey(net.minecraft.enchantment.Enchantments.SILK_TOUCH));

        if (hasFortune) return "fortune";
        if (hasSilkTouch) return "silk_touch";
        return "default";
    }
}
