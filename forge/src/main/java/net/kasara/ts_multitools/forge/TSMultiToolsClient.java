package net.kasara.ts_multitools.forge;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.client.option.ModKeyMappings;
import net.kasara.ts_multitools.client.render.block.PedestalRenderRegistry;
import net.kasara.ts_multitools.client.render.entity.SlimeArrowRenderer;
import net.kasara.ts_multitools.forge.client.ModClientEvents;
import net.kasara.ts_multitools.forge.entity.ModEntities;
import net.kasara.ts_multitools.forge.item.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = TSMultiTools.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TSMultiToolsClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // クライアントイベント登録
            ModClientEvents.register();

            // 台座描画
            PedestalRenderRegistry.register();

            // SLIMEを弓として引いている間のモデル切替に使うitem propertyを登録
            // (バニラの"pulling"/"pull"はItems.BOW専用に登録されているため、SLIME用に別途登録が必要)
            ItemProperties.register(ModItems.SLIME.get(), new ResourceLocation("pulling"),
                    (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.SLIME.get(), new ResourceLocation("pull"),
                    (stack, level, entity, seed) -> entity == null || entity.getUseItem() != stack ? 0.0F
                            : (float) (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F);
        });
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        // Slimeのモード変更キー登録
        event.register(ModKeyMappings.MODE_TOGGLE);

        // 登録完了ログを出力
        TSMultiToolsCommon.LOGGER.info("Registering addon Mod Key Mappings for " + TokorotenSlimeAPI.getModId() + " (from " + TSMultiToolsCommon.MOD_ID + ")");
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // スライム矢の描画
        event.registerEntityRenderer(ModEntities.SLIME_ARROW.get(), SlimeArrowRenderer::new);

        // 登録完了ログを出力
        TSMultiToolsCommon.LOGGER.info("Registering addon Mod Renderers for " + TokorotenSlimeAPI.getModId() + " (from " + TSMultiToolsCommon.MOD_ID + ")");
    }
}
