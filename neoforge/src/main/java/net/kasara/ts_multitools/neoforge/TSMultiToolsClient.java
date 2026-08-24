package net.kasara.ts_multitools.neoforge;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.client.option.ModKeyMappingsCommon;
import net.kasara.ts_multitools.client.render.block.PedestalRenderRegistry;
import net.kasara.ts_multitools.client.render.entity.SlimeArrowRenderer;
import net.kasara.ts_multitools.network.ModPacketsCommon;
import net.kasara.ts_multitools.neoforge.client.ModClientEvents;
import net.kasara.ts_multitools.neoforge.client.option.ModKeyMappings;
import net.kasara.ts_multitools.neoforge.entity.ModEntities;
import net.kasara.ts_multitools.neoforge.network.ModPackets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod(value = TSMultiTools.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = TSMultiTools.MOD_ID, value = Dist.CLIENT)
public class TSMultiToolsClient {

    public TSMultiToolsClient(IEventBus modEventBus) {
        // クライアント→サーバー送信ブリッジ
        ModPacketsCommon.SEND_TO_SERVER = ModPackets::sendToServer;

        ModKeyMappingsCommon.MODE_TOGGLE = ModKeyMappings.MODE_TOGGLE;

        modEventBus.addListener(this::onClientSetup);

        // クライアントイベント登録
        ModClientEvents.register();

        // キーマッピング登録
        modEventBus.addListener(this::registerKeys);

        // レンダリング登録
        modEventBus.addListener(this::registerRenderers);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        // 台座描画
        PedestalRenderRegistry.register();
    }

    private void registerKeys(RegisterKeyMappingsEvent event) {
        // Slimeのモード変更キー登録
        event.register(ModKeyMappings.MODE_TOGGLE);

        // 登録完了ログを出力
        TSMultiTools.LOGGER.info("Registering addon Mod Key Mappings for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // スライム矢の描画
        event.registerEntityRenderer(ModEntities.SLIME_ARROW.get(), SlimeArrowRenderer::new);

        // 登録完了ログを出力
        TSMultiTools.LOGGER.info("Registering addon Mod Renderers for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }
}
