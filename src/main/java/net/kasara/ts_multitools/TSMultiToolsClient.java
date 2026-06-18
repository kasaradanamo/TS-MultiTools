package net.kasara.ts_multitools;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.client.ModClientEvents;
import net.kasara.ts_multitools.client.render.block.PedestalRenderRegistry;
import net.kasara.ts_multitools.entity.ModEntities;
import net.kasara.ts_multitools.client.render.entity.SlimeArrowRenderer;
import net.kasara.ts_multitools.network.ModPackets;
import net.kasara.ts_multitools.client.option.ModKeyMappings;

public class TSMultiToolsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // S2Cパケット登録
        ModPackets.registerS2CPackets();

        // クライアントイベント登録
        ModClientEvents.register();

        // キーマッピング登録
        ModKeyMappings.register();

        // レンダリング登録
        registerRenderers();

        // 台座描画登録
        PedestalRenderRegistry.register();
    }

    private void registerRenderers() {
        // スライム矢の描画登録
        EntityRendererRegistry.register(ModEntities.SLIME_ARROW, SlimeArrowRenderer::new);

        // 登録完了ログを出力
        TSMultiTools.LOGGER.info("Registering addon Mod Renderers for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }
}