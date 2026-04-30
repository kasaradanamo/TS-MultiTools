package net.kasara.ts_multitools.client.option;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.tokorotenslime.api.TokorotenSlimeClientAPI;
import net.kasara.ts_multitools.TSMultiTools;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ModKeyMappings {

    // モード切替用のキー
    public static KeyMapping MODE_TOGGLE;

    /**
     * GLFWのキーコードとキー名を指定して登録
     */
    public static void register() {
        MODE_TOGGLE = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.tokorotenslime.mode_toggle",   // キー名（翻訳用）
                InputConstants.Type.KEYSYM,               // キーの種類
                GLFW.GLFW_KEY_X,                          // デフォルトのキー
                TokorotenSlimeClientAPI.getKeyMappingCategory() // カテゴリ名（オプション画面で表示されるグループ）
        ));

        // 登録完了ログを出力
        TSMultiTools.LOGGER.info("Registering addon Mod Key Mappings for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }
}