package net.kasara.ts_multitools.fabric.entity;

import net.kasara.tokorotenslime.TokorotenSlimeCommon;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.entity.ModEntitiesCommon;
import net.kasara.ts_multitools.entity.SlimeArrowEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {

    // スライム矢エンティティのEntityType
    public static final EntityType<SlimeArrowEntity> SLIME_ARROW = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(TokorotenSlimeCommon.MOD_ID, "slime_arrow"),
            EntityType.Builder.<SlimeArrowEntity>of(SlimeArrowEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)          // ヒットボックスの幅・高さ
                    .clientTrackingRange(4)   // サーバーとの同期範囲
                    .updateInterval(20)                     // 同期間隔
                    .build(TokorotenSlimeCommon.MOD_ID + ":slime_arrow")
    );

    /**
     * エンティティ登録処理を初期化時に呼び出す。
     */
    public static void register() {
        ModEntitiesCommon.SLIME_ARROW = SLIME_ARROW;

        TSMultiToolsCommon.LOGGER.info("Registering addon Mod Entities for " + TokorotenSlimeAPI.getModId() + " (from " + TSMultiToolsCommon.MOD_ID + ")");
    }
}
