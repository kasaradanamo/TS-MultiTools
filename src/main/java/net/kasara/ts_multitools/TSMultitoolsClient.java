package net.kasara.ts_multitools;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.kasara.ts_multitools.client.ModClientEvents;
import net.kasara.ts_multitools.client.render.block.ModPedestalRenderers;
import net.kasara.ts_multitools.entity.ModEntities;
import net.kasara.ts_multitools.client.render.entity.SlimeArrowRenderer;
import net.kasara.ts_multitools.network.ModPackets;
import net.kasara.ts_multitools.client.option.ModKeyBindings;

public class TSMultitoolsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModKeyBindings.register();  // キーイベント登録
        ModClientEvents.registerEvents();   // クライアントイベント登録

        EntityRendererRegistry.register(ModEntities.SLIME_ARROW, SlimeArrowRenderer::new);  // スライム矢の描画

        ModPedestalRenderers.register();   // 台座描画ハンドラ登録

        ModPackets.registerS2CPackets();    // サーバー→クライアントパケット登録
    }
}
