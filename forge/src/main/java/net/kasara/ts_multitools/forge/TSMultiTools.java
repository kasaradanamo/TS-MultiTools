package net.kasara.ts_multitools.forge;

import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.forge.command.ModCommands;
import net.kasara.ts_multitools.forge.entity.ModEntities;
import net.kasara.ts_multitools.forge.item.ModItems;
import net.kasara.ts_multitools.forge.network.ModPackets;
import net.kasara.ts_multitools.forge.recipe.ModRecipeSerializers;
import net.kasara.ts_multitools.forge.server.ModServerEvents;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TSMultiTools.MOD_ID)
public class TSMultiTools {

    public static final String MOD_ID = TSMultiToolsCommon.MOD_ID;

    public TSMultiTools() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // アイテム登録
        ModItems.register(modEventBus);

        // エンティティ登録
        ModEntities.register(modEventBus);

        // レシピシリアライザー登録
        ModRecipeSerializers.register(modEventBus);

        // ネットワーキング登録
        ModPackets.register();

        // コマンドイベント登録
        ModCommands.register();

        // サーバーイベント登録
        ModServerEvents.register();

        // common側の共有ホルダーへRegistryObjectの実体を反映(RegisterEvent発火後でないとRegistryObject#get()が使えないため)
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        ModItems.initCommonHolder();
        ModEntities.initCommonHolder();
    }
}
