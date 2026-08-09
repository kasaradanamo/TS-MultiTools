package net.kasara.ts_multitools.fabric;

import net.fabricmc.api.ModInitializer;
import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.fabric.command.ModCommands;
import net.kasara.ts_multitools.fabric.entity.ModEntities;
import net.kasara.ts_multitools.fabric.item.ModItems;
import net.kasara.ts_multitools.fabric.network.ModPackets;
import net.kasara.ts_multitools.fabric.recipe.ModRecipeSerializers;
import net.kasara.ts_multitools.fabric.server.ModServerEvents;

public class TSMultiTools implements ModInitializer {

    public static final String MOD_ID = TSMultiToolsCommon.MOD_ID;

    @Override
    public void onInitialize() {
        // アイテム登録
        ModItems.register();

        // エンティティ登録
        ModEntities.register();

        // レシピシリアライザー登録
        ModRecipeSerializers.register();

        // ネットワーキング登録
        ModPackets.register();

        // コマンドイベント登録
        ModCommands.register();

        // サーバーイベント登録
        ModServerEvents.register();
    }
}
