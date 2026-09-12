package net.kasara.ts_multitools.neoforge.entity;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.entity.SlimeArrowEntity;
import net.kasara.ts_multitools.neoforge.TSMultiTools;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, TokorotenSlimeAPI.getModId());

    // スライム矢エンティティのEntityType
    public static final DeferredHolder<EntityType<?>, EntityType<SlimeArrowEntity>> SLIME_ARROW =
            ENTITIES.register("slime_arrow", () ->
                    EntityType.Builder.<SlimeArrowEntity>of(SlimeArrowEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)          // ヒットボックスの幅・高さ
                            .eyeHeight(0.13F)                       // 視点の高さ
                            .clientTrackingRange(4)   // サーバーとの同期範囲
                            .updateInterval(20)                     // 同期間隔
                            .build(TokorotenSlimeAPI.getModId() + ":slime_arrow")
            );

    /**
     * エンティティ登録処理を初期化時に呼び出す
     */
    public static void register(IEventBus modEventBus) {
        ENTITIES.register(modEventBus);

        TSMultiTools.LOGGER.info("Registering addon Mod Entities for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }
}
