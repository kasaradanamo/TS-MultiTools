package net.kasara.ts_multitools.client.option;

import com.mojang.blaze3d.platform.InputConstants;
import net.kasara.tokorotenslime.api.TokorotenSlimeClientAPI;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ModKeyMappings {

    // モード切替用のキー
    public static final KeyMapping MODE_TOGGLE = new KeyMapping(
            "key.tokorotenslime.mode_toggle",   // キー名（翻訳用）
            InputConstants.Type.KEYSYM,               // キーの種類
            GLFW.GLFW_KEY_X,                          // デフォルトのキー
            TokorotenSlimeClientAPI.getKeyMappingCategory() // カテゴリ名（オプション画面で表示されるグループ）
    );
}