package net.kasara.ts_multitools.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.entity.SlimeArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class SlimeArrowRenderer extends ArrowRenderer<SlimeArrowEntity, ArrowRenderState> {

    public SlimeArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ArrowRenderState createRenderState() {
        return new ArrowRenderState();
    }

    /**
     * 矢を描画する際に使用するテクスチャを指定
     */
    @Override
    protected Identifier getTextureLocation(ArrowRenderState state) {
        return Identifier.fromNamespaceAndPath(TokorotenSlimeAPI.getModId(), "textures/entity/projectiles/slime_arrow.png");
    }
}