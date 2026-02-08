package net.kasara.ts_multitools;

import net.fabricmc.api.ModInitializer;

import net.kasara.ts_multitools.command.ModCommand;
import net.kasara.ts_multitools.component.ModComponents;
import net.kasara.ts_multitools.entity.ModEntities;
import net.kasara.ts_multitools.item.ModItems;
import net.kasara.ts_multitools.network.ModPackets;
import net.kasara.ts_multitools.recipe.ModRecipes;
import net.kasara.ts_multitools.server.ModServerEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TSMultitools implements ModInitializer {
    public static final String MOD_ID = "ts_multitools";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        ModComponents.registerDataComponentTypes(); // カスタムコンポーネント登録

        ModItems.registerModItems();    // アイテムの登録

        ModEntities.registerModEntities();  // エンティティ登録

        ModRecipes.register();  // レシピ登録

        ModCommand.register();	// コマンド登録

        ModPackets.registerPayloadTypes();
        ModPackets.registerC2SPackets();	// クライアント→サーバーパケット登録

        ModServerEvents.registerEvents();   // サーバーイベント登録
    }
}