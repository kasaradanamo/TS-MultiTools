package net.kasara.ts_multitools.client.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultitools;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class ModKeyBindings {

    // モード切替用のキー
    public static KeyBinding MODE_TOGGLE;

    /**
     * GLFWのキーコードとキー名を指定して登録
     */
    public static void register() {
        MODE_TOGGLE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.tokorotenslime.mode_toggle",   // キー名（翻訳用）
                InputUtil.Type.KEYSYM,                          // キーの種類
                GLFW.GLFW_KEY_X,                                // デフォルトのキー
                TokorotenSlimeAPI.getKeyBindingCategory()       // カテゴリ名（オプション画面で表示されるグループ）
        ));

        // 登録完了ログを出力
        TSMultitools.LOGGER.info("Registering addon Mod Key Bindings for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultitools.MOD_ID + ")");
    }
}
