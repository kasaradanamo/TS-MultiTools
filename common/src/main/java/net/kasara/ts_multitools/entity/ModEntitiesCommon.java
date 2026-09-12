package net.kasara.ts_multitools.entity;

import net.minecraft.world.entity.EntityType;

/**
 * ローダー側で登録されたEntityTypeをcommonから参照するための保持クラス
 */
public class ModEntitiesCommon {

    public static EntityType<SlimeArrowEntity> SLIME_ARROW;

    private ModEntitiesCommon() {}
}
