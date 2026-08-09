package net.kasara.ts_multitools.forge.entity;

import net.kasara.tokorotenslime.TokorotenSlimeCommon;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.entity.ModEntitiesCommon;
import net.kasara.ts_multitools.entity.SlimeArrowEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TokorotenSlimeCommon.MOD_ID);

    // スライム矢エンティティのEntityType
    public static final RegistryObject<EntityType<SlimeArrowEntity>> SLIME_ARROW =
            ENTITIES.register("slime_arrow", () -> EntityType.Builder.<SlimeArrowEntity>of(SlimeArrowEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)          // ヒットボックスの幅・高さ
                    .clientTrackingRange(4)   // サーバーとの同期範囲
                    .updateInterval(20)                     // 同期間隔
                    .build(TokorotenSlimeCommon.MOD_ID + ":slime_arrow")
            );

    /**
     * エンティティ登録処理を初期化時に呼び出す
     */
    public static void register(IEventBus modEventBus) {
        ENTITIES.register(modEventBus);

        TSMultiToolsCommon.LOGGER.info("Registering addon Mod Entities for " + TokorotenSlimeAPI.getModId() + " (from " + TSMultiToolsCommon.MOD_ID + ")");
    }

    public static void initCommonHolder() {
        ModEntitiesCommon.SLIME_ARROW = SLIME_ARROW.get();
    }
}
