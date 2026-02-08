package net.kasara.ts_multitools.entity;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultitools;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

/**
 * Mod 内で使用するエンティティの登録を行うクラス。
 * - SlimeArrowEntity のエンティティタイプを定義している。
 */
public class ModEntities {

    /**
     * slime_arrow エンティティの RegistryKey。
     * EntityType を登録する際の一意な識別子として利用する。
     */
    public static final RegistryKey<EntityType<?>> key = RegistryKey.of(Registries.ENTITY_TYPE.getKey(),
            Identifier.of(TokorotenSlimeAPI.getModId(), "slime_arrow"));

    /**
     * スライム矢エンティティの EntityType。
     * - SpawnGroup.MISC に分類（Mob ではないため）。
     * - アイテムをドロップしない。
     * - 当たり判定の大きさは 0.5 x 0.5。
     * - 視点の高さは 0.13f（矢なので地面に近い位置）。
     * - クライアントへの同期範囲は 4 チャンク。
     * - 同期間隔は 20tick（1秒ごと）。
     */
    public static final EntityType<SlimeArrowEntity> SLIME_ARROW = Registry.register(
            Registries.ENTITY_TYPE,
            key.getValue(),
            EntityType.Builder.<SlimeArrowEntity>create(SlimeArrowEntity::new, SpawnGroup.MISC)
                    .dropsNothing()                     // 死亡時にドロップなし
                    .dimensions(0.5F, 0.5F) // ヒットボックスの幅・高さ
                    .eyeHeight(0.13F)                   // 視点の高さ
                    .maxTrackingRange(4)                // サーバーとの同期範囲
                    .trackingTickInterval(20)           // 同期間隔
                    .build(key)
    );

    /**
     * エンティティ登録処理を初期化時に呼び出す。
     * 実際の登録は static フィールド初期化で行われる。
     */
    public static void registerModEntities() {
        TSMultitools.LOGGER.info("Registering addon Entities for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultitools.MOD_ID + ")");
    }
}
