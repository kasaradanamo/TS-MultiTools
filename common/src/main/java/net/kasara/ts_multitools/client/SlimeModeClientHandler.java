package net.kasara.ts_multitools.client;

import net.kasara.ts_multitools.client.option.ModKeyMappings;
import net.kasara.ts_multitools.component.SlimeModeComponent;
import net.kasara.ts_multitools.constant.SlimeMode;
import net.kasara.ts_multitools.data.SlimeItemData;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.kasara.ts_multitools.network.packet.c2s.ToggleSlimeModeC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

/**
 * クライアント側のモード変更
 */
public class SlimeModeClientHandler {

    // モード切替キーが押下中かどうか(押下判定の連打防止用)
    private static boolean modeTogglePressed = false;

    /**
     * モード切替キー入力処理
     * Ctrlキー併用で use_mode、それ以外は mining_mode に切り替える。
     */
    public static void handleModeToggle(Minecraft minecraft) {
        boolean isPressed = ModKeyMappings.MODE_TOGGLE.isDown();

        if (isPressed && !modeTogglePressed) {
            modeTogglePressed = true;

            ItemStack stack = minecraft.player.getMainHandItem();
            if (stack.getItem() != ModItemsCommon.SLIME) return;

            UUID stackUuid = SlimeItemData.getUuid(stack);

            boolean ctrlPressed = GLFW.glfwGetKey(minecraft.getWindow().getWindow(),
                    GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS;

            ToggleSlimeModeC2SPacket.send(stackUuid, ctrlPressed ? SlimeMode.Type.USE : SlimeMode.Type.MINING);

        } else if (!isPressed) {
            // キーが離されてたらリセット
            modeTogglePressed = false;
        }
    }

    /**
     * 現在のエンチャントに応じて望ましいモードを判定
     */
    public static void updateSlimeEnchantment(ItemStack stack, UUID uuid) {
        String desiredMode = getMiningModeFromEnchantments(stack);
        SlimeModeComponent mode = SlimeItemData.getMode(stack);

        if (!desiredMode.equals(mode.miningMode())) {
            ToggleSlimeModeC2SPacket.send(uuid, desiredMode);
        }
    }

    /**
     * エンチャント状態から望ましい採掘モードを判定する。
     * @return "fortune" | "silk_touch" | "default"
     */
    private static String getMiningModeFromEnchantments(ItemStack stack) {
        boolean hasFortune = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, stack) > 0;
        boolean hasSilkTouch = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0;

        if (hasFortune) return SlimeMode.MiningMode.FORTUNE;
        if (hasSilkTouch) return SlimeMode.MiningMode.SILK_TOUCH;
        return SlimeMode.MiningMode.DEFAULT;
    }
}
