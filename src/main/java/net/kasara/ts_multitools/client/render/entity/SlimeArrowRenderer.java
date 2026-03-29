package net.kasara.ts_multitools.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.entity.SlimeArrowEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.render.entity.state.ArrowEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SlimeArrowRenderer extends ProjectileEntityRenderer<SlimeArrowEntity, ArrowEntityRenderState> {

    public SlimeArrowRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public ArrowEntityRenderState createRenderState() {
        return new ArrowEntityRenderState();
    }

    /**
     * 矢を描画する際に使用するテクスチャを指定
     */
    @Override
    protected Identifier getTexture(ArrowEntityRenderState state) {
        return Identifier.of(TokorotenSlimeAPI.getModId(), "textures/entity/projectiles/slime_arrow.png");
    }
}
