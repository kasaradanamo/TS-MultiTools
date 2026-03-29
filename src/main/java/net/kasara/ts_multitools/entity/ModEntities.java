package net.kasara.ts_multitools.entity;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultitools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModEntities {

    // スライム矢エンティティのEntityType
    public static final EntityType<SlimeArrowEntity> SLIME_ARROW = registerEntity(
            "slime_arrow",
            EntityType.Builder.<SlimeArrowEntity>create(SlimeArrowEntity::new, SpawnGroup.MISC)
                    .dropsNothing()                         // 死亡時にドロップなし
                    .dimensions(0.5F, 0.5F)     // ヒットボックスの幅・高さ
                    .eyeHeight(0.13F)                       // 視点の高さ
                    .maxTrackingRange(4)                    // サーバーとの同期範囲
                    .trackingTickInterval(20)               // 同期間隔
    );

    private static <T extends Entity> EntityType<T> registerEntity(String name, EntityType.Builder<T> builder) {
        RegistryKey<EntityType<?>> key = RegistryKey.of(Registries.ENTITY_TYPE.getKey(), Identifier.of(TokorotenSlimeAPI.getModId(), name));
        return Registry.register(
                Registries.ENTITY_TYPE,
                key.getValue(),
                builder.build(key)
        );
    }

    /**
     * エンティティ登録処理を初期化時に呼び出す
     */
    public static void registerModEntities() {
        TSMultitools.LOGGER.info("Registering addon Entities for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultitools.MOD_ID + ")");
    }
}
