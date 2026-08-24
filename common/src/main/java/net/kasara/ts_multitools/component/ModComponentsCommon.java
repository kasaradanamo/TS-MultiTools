package net.kasara.ts_multitools.component;

import net.minecraft.core.component.DataComponentType;

import java.util.UUID;

/**
 * ローダー側で登録されたDataComponentTypeをcommonから参照するための保持クラス
 */
public class ModComponentsCommon {

    public static DataComponentType<SlimeModeComponent> SLIME_MODE;
    public static DataComponentType<MiningEnchantLevelComponent> MINING_ENCHANT_LEVEL;
    public static DataComponentType<String> SLIME_STATE;
    public static DataComponentType<UUID> SLIME_UUID;

    private ModComponentsCommon() {}
}
