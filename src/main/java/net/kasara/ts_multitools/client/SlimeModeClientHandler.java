package net.kasara.ts_multitools.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kasara.ts_multitools.component.ModComponents;
import net.kasara.ts_multitools.component.SlimeModeComponent;
import net.kasara.ts_multitools.item.ModItems;
import net.kasara.ts_multitools.network.packet.c2s.ToggleSlimeModeC2SPacket;
import net.kasara.ts_multitools.client.option.ModKeyBindings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

/**
 * クライアント側のモード変更
 */
@Environment(EnvType.CLIENT)
public class SlimeModeClientHandler {

    // モード切替キーが押下中かどうか（押下判定の連打防止用）
    private static boolean modeTogglePressed = false;

    /**
     * モード切替キー入力処理
     * Ctrlキー併用で use_mode、それ以外は mining_mode に切り替える。
     */
    public static void handleModeToggle(MinecraftClient client) {
        boolean isPressed = ModKeyBindings.MODE_TOGGLE.isPressed();

        if (isPressed && !modeTogglePressed) {
            modeTogglePressed = true;

            ItemStack stack = client.player.getMainHandStack();
            if (stack.getItem() != ModItems.SLIME) return;

            UUID stackUuid = stack.get(ModComponents.SLIME_UUID);

            boolean ctrlPressed = GLFW.glfwGetKey(client.getWindow().getHandle(),
                    GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS;

            ToggleSlimeModeC2SPacket.send(stackUuid, ctrlPressed ? "use_mode" : "mining_mode");

        } else if (!isPressed) {
            // キーが離されてらリセット
            modeTogglePressed = false;
        }
    }

    /**
     * 現在のエンチャントに応じて望ましいモードを判定
     */
    public static void updateSlimeEnchantment(ItemStack stack, UUID uuid) {
        String desiredMode = getMiningModeFromEnchantments(stack);
        SlimeModeComponent mode = stack.get(ModComponents.SLIME_MODE);

        if (!desiredMode.equals(mode.miningMode())) {
            ToggleSlimeModeC2SPacket.send(uuid, desiredMode);
        }
    }

    /**
     * エンチャント状態から望ましい採掘モードを判定する。
     * @return "fortune" | "silk_touch" | "default"
     */
    private static String getMiningModeFromEnchantments(ItemStack stack) {
        ItemEnchantmentsComponent enchantments = stack.getEnchantments();
        if (enchantments == null || enchantments.isEmpty()) return "default";

        boolean hasFortune = enchantments.getEnchantmentEntries().stream()
                .anyMatch(e -> e.getKey().matchesKey(net.minecraft.enchantment.Enchantments.FORTUNE));
        boolean hasSilkTouch = enchantments.getEnchantmentEntries().stream()
                .anyMatch(e -> e.getKey().matchesKey(net.minecraft.enchantment.Enchantments.SILK_TOUCH));

        if (hasFortune) return "fortune";
        if (hasSilkTouch) return "silk_touch";
        return "default";
    }
}