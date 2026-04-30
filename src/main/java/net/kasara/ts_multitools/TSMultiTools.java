package net.kasara.ts_multitools;

import com.mojang.logging.LogUtils;
import net.kasara.ts_multitools.command.ModCommands;
import net.kasara.ts_multitools.component.ModComponents;
import net.kasara.ts_multitools.entity.ModEntities;
import net.kasara.ts_multitools.item.ModItems;
import net.kasara.ts_multitools.network.ModPackets;
import net.kasara.ts_multitools.recipe.ModRecipeSerializers;
import net.kasara.ts_multitools.server.ModServerEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(TSMultiTools.MOD_ID)
public class TSMultiTools {

    public static final String MOD_ID = "ts_multitools";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TSMultiTools(IEventBus modEventBus) {
        // カスタムコンポーネント登録
        ModComponents.register(modEventBus);

        // アイテム登録
        ModItems.register(modEventBus);

        // エンティティ登録
        ModEntities.register(modEventBus);

        // レシピシリアライザー登録
        ModRecipeSerializers.register(modEventBus);

        // ペイロード登録
        ModPackets.register(modEventBus);

        // コマンドイベント登録
        ModCommands.register();

        // サーバーイベント登録
        ModServerEvents.register();
    }
}
