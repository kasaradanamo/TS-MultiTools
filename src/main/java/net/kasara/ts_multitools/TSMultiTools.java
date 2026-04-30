package net.kasara.ts_multitools;

import net.fabricmc.api.ModInitializer;

import net.kasara.ts_multitools.command.ModCommands;
import net.kasara.ts_multitools.component.ModComponents;
import net.kasara.ts_multitools.entity.ModEntities;
import net.kasara.ts_multitools.item.ModItems;
import net.kasara.ts_multitools.network.ModPackets;
import net.kasara.ts_multitools.recipe.ModRecipeSerializers;
import net.kasara.ts_multitools.server.ModServerEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TSMultiTools implements ModInitializer {

    public static final String MOD_ID = "ts_multitools";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // カスタムコンポーネント登録
        ModComponents.register();

        // アイテム登録
        ModItems.register();

        // エンティティ登録
        ModEntities.register();

        // レシピシリアライザー登録
        ModRecipeSerializers.register();

        // C2Sパケット登録
        ModPackets.registerPayloadTypes();
        ModPackets.registerC2SPackets();

        // コマンドイベント登録
        ModCommands.register();

        // サーバーイベント登録
        ModServerEvents.register();
    }
}