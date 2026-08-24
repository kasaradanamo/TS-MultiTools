package net.kasara.ts_multitools.fabric.entity;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.entity.SlimeArrowEntity;
import net.kasara.ts_multitools.fabric.TSMultiTools;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {

    // スライム矢エンティティのEntityType
    public static final EntityType<SlimeArrowEntity> SLIME_ARROW =
            registerEntity(
                    "slime_arrow",
                    EntityType.Builder.<SlimeArrowEntity>of(SlimeArrowEntity::new, MobCategory.MISC)
                            .noLootTable()                          // 死亡時にドロップなし
                            .sized(0.5F, 0.5F)          // ヒットボックスの幅・高さ
                            .eyeHeight(0.13F)                       // 視点の高さ
                            .clientTrackingRange(4)   // サーバーとの同期範囲
                            .updateInterval(20)                     // 同期間隔
            );

    private static <T extends Entity> EntityType<T> registerEntity(String name, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(TokorotenSlimeAPI.getModId(), name));
        return Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                key,
                builder.build(key)
        );
    }

    /**
     * エンティティ登録処理を初期化時に呼び出す
     */
    public static void register() {
        TSMultiTools.LOGGER.info("Registering addon Mod Entities for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }
}
