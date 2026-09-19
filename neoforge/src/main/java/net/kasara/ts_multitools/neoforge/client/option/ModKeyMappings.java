package net.kasara.ts_multitools.neoforge.client.option;

import com.mojang.blaze3d.platform.InputConstants;
import net.kasara.tokorotenslime.api.TokorotenSlimeClientAPI;
import net.minecraft.client.KeyMapping;

public class ModKeyMappings {

    // モード切替用のキー
    public static final KeyMapping MODE_TOGGLE = new KeyMapping(
            "key.tokorotenslime.mode_toggle",   // キー名（翻訳用）
            InputConstants.Type.KEYBOARD,              // キーの種類
            InputConstants.KEY_X,                     // デフォルトのキー
            TokorotenSlimeClientAPI.getKeyMappingCategory() // カテゴリ名（オプション画面で表示されるグループ）
    );
}
