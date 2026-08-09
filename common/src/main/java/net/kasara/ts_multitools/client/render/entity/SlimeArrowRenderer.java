package net.kasara.ts_multitools.client.render.entity;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.entity.SlimeArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class SlimeArrowRenderer extends ArrowRenderer<SlimeArrowEntity> {

    public SlimeArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    /**
     * 矢を描画する際に使用するテクスチャを指定
     */
    @Override
    public ResourceLocation getTextureLocation(SlimeArrowEntity entity) {
        return new ResourceLocation(TokorotenSlimeAPI.getModId(), "textures/entity/projectiles/slime_arrow.png");
    }
}
