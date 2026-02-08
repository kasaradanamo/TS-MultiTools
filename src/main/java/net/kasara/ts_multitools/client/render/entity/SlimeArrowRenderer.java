package net.kasara.ts_multitools.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.entity.SlimeArrowEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.render.entity.state.ArrowEntityRenderState;
import net.minecraft.util.Identifier;

/**
 * カスタム矢エンティティ {@link SlimeArrowEntity} の描画処理を担当するクラス。
 * 通常の矢レンダラーをベースにしているが、テクスチャを差し替えている。
 * クライアントサイド専用クラス。
 */
@Environment(EnvType.CLIENT)
public class SlimeArrowRenderer extends ProjectileEntityRenderer<SlimeArrowEntity, ArrowEntityRenderState> {

    /**
     * コンストラクタ。
     * EntityRendererFactory.Context を利用してレンダラーを初期化する。
     *
     * @param context レンダラーの描画コンテキスト
     */
    public SlimeArrowRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    /**
     * 描画に使用するステートオブジェクトを生成。
     * 矢の回転やヒット時の表現などの状態を保持する。
     *
     * @return 新しい ArrowEntityRenderState インスタンス
     */
    @Override
    public ArrowEntityRenderState createRenderState() {
        return new ArrowEntityRenderState();
    }

    /**
     * 矢を描画する際に使用するテクスチャを指定。
     *
     * @param state 矢の描画ステート
     * @return カスタム矢のテクスチャの Identifier
     */
    @Override
    protected Identifier getTexture(ArrowEntityRenderState state) {
        return Identifier.of(TokorotenSlimeAPI.getModId(), "textures/entity/projectiles/slime_arrow.png");
    }
}
