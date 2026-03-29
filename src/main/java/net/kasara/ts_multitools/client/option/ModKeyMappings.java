package net.kasara.ts_multitools.client.option;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultitools;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
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
                TokorotenSlimeAPI.getKeyMappingCategory() // カテゴリ名（オプション画面で表示されるグループ）
        ));

        // 登録完了ログを出力
        TSMultitools.LOGGER.info("Registering addon Mod Key Mappings for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultitools.MOD_ID + ")");
    }
}