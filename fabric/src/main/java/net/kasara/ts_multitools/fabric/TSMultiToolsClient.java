package net.kasara.ts_multitools.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.client.option.ModKeyMappings;
import net.kasara.ts_multitools.client.render.block.PedestalRenderRegistry;
import net.kasara.ts_multitools.client.render.entity.SlimeArrowRenderer;
import net.kasara.ts_multitools.fabric.client.ModClientEvents;
import net.kasara.ts_multitools.fabric.client.network.ClientModPackets;
import net.kasara.ts_multitools.fabric.entity.ModEntities;
import net.kasara.ts_multitools.fabric.item.ModItems;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class TSMultiToolsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // クライアント専用ネットワーキング登録
        ClientModPackets.register();

        // クライアントイベント登録
        ModClientEvents.register();

        // 台座描画
        PedestalRenderRegistry.register();

        // Slimeのモード変更キー登録
        KeyBindingHelper.registerKeyBinding(ModKeyMappings.MODE_TOGGLE);

        // スライム矢の描画
        EntityRendererRegistry.register(ModEntities.SLIME_ARROW, SlimeArrowRenderer::new);

        // SLIMEを弓として引いている間のモデル切替に使うitem propertyを登録
        ItemProperties.register(ModItems.SLIME, new ResourceLocation("pulling"),
                (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
        ItemProperties.register(ModItems.SLIME, new ResourceLocation("pull"),
                (stack, level, entity, seed) -> entity == null || entity.getUseItem() != stack ? 0.0F
                        : (float) (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F);

        // 登録完了ログを出力
        TSMultiToolsCommon.LOGGER.info("Registering addon Mod Key Mappings/Renderers for " + TokorotenSlimeAPI.getModId() + " (from " + TSMultiToolsCommon.MOD_ID + ")");
    }
}
